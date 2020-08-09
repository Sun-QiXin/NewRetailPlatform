package gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import gulimall.common.to.SkuHasStockVo;
import gulimall.common.utils.R;
import gulimall.common.vo.MemberRespVo;
import gulimall.common.vo.ShoppingCart;
import gulimall.common.vo.ShoppingCartItem;
import gulimall.order.feign.MemberFeignService;
import gulimall.order.feign.ProductFeignService;
import gulimall.order.feign.ShoppingCartFeignService;
import gulimall.order.feign.WareFeignService;
import gulimall.order.interceptor.LoginUserInterceptor;
import gulimall.order.vo.MemberAddressVo;
import gulimall.order.vo.OrderConfirmVo;
import gulimall.order.vo.OrderItemVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import gulimall.common.utils.PageUtils;
import gulimall.common.utils.Query;

import gulimall.order.dao.OrderDao;
import gulimall.order.entity.OrderEntity;
import gulimall.order.service.OrderService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private ShoppingCartFeignService shoppingCartFeignService;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 跳转结算页，并展示当前需要展示的信息
     *
     * @return OrderConfirmVo
     * @throws ExecutionException   ExecutionException
     * @throws InterruptedException InterruptedException
     */
    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        //获取到当前登录的用户id(能来到这都是已经登录的)
        MemberRespVo memberRespVo = LoginUserInterceptor.threadLocal.get();
        //拿到当前主线程的request数据
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        /*异步无返回值执行*/
        CompletableFuture<Void> getAddresses = CompletableFuture.runAsync(() -> {
            //1、远程查询当前用户的收货地址列表
            /*这个远程调用是不需要请求头的,所以不用同步*/
            List<MemberAddressVo> addresses = memberFeignService.getAddresses(memberRespVo.getId());
            orderConfirmVo.setAddressVos(addresses);
        }, executor);

        /*异步无返回值执行*/
        CompletableFuture<Void> getOrderItemVos = CompletableFuture.runAsync(() -> {
            //2、远程查询当前用户的购物车数据
            /*feign的远程调用会丢失请求头，所以会导致远程调用没有cookie导致商品服务无法查到用户购物车,
            这一步如果使用异步方式运行,我们配置了feign的拦截器来同步cookie，使用RequestContextHolder获取之前请求参数需要在同一个线程中,那么就需要将主线程的数据同步到该线程*/
            //将主线程request数据同步到该线程
            RequestContextHolder.setRequestAttributes(requestAttributes);
            ShoppingCart shoppingCart = shoppingCartFeignService.getCurrentUserShoppingCart();
            //过滤出当前选中的购物项
            List<OrderItemVo> orderItemVos = shoppingCart.getItems().stream()
                    .filter(ShoppingCartItem::getCheck)
                    .map(item -> {
                        //远程查询商品服务,更新为现在的最新价格
                        BigDecimal currentPrice = productFeignService.currentPrice(item.getSkuId());
                        item.setPrice(currentPrice);
                        //将数据封装到我们指定的vo
                        OrderItemVo orderItemVo = new OrderItemVo();
                        BeanUtils.copyProperties(item, orderItemVo);
                        return orderItemVo;
                    }).collect(Collectors.toList());
            orderConfirmVo.setOrderItemVos(orderItemVos);
        }, executor).thenRunAsync(() -> {
            //3、远程批量查询库存信息,并封装进orderItemVos
            List<OrderItemVo> orderItemVos = orderConfirmVo.getOrderItemVos();
            //拿到当前选中商品的id集合
            List<Long> skuIds = orderItemVos.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            R r = wareFeignService.getSkuHasStock(skuIds);
            if (r.getCode() == 0) {
                List<SkuHasStockVo> skuHasStockVos = r.getData(new TypeReference<List<SkuHasStockVo>>() {
                });
                if (skuHasStockVos != null && skuHasStockVos.size() > 0) {
                    for (OrderItemVo orderItemVo : orderItemVos) {
                        for (SkuHasStockVo skuHasStockVo : skuHasStockVos) {
                            if (orderItemVo.getSkuId().equals(skuHasStockVo.getSkuId())) {
                                orderItemVo.setHasStock(skuHasStockVo.getHasStock());
                            }
                        }
                    }
                }
            }
        }, executor);

        /*异步无返回值执行*/
        CompletableFuture<Void> setIntegration = CompletableFuture.runAsync(() -> {
            //4、保存用户积分
            orderConfirmVo.setIntegration(memberRespVo.getIntegration());
        }, executor);

        //TODO 5、防重令牌

        //等待都执行完返回
        CompletableFuture.allOf(getAddresses, getOrderItemVos, setIntegration).get();
        return orderConfirmVo;
    }

    /**
     * 更改当前的默认地址为新指定的
     *  @param memberId      id
     * @param defaultStatus 要更改成的信息
     * @param addressId 要更改成默认地址的列id
     */
    @Override
    public void updateAddress(Long memberId, Integer defaultStatus, Long addressId) {
        memberFeignService.updateAddress(memberId, defaultStatus, addressId);
    }
}