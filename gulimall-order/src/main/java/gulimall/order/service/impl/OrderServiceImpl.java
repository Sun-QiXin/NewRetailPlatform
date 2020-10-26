package gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;

import gulimall.common.exception.NoStockException;
import gulimall.common.to.SkuHasStockVo;
import gulimall.common.to.mq.OrderTo;
import gulimall.common.to.mq.SeckillOrderTo;
import gulimall.common.to.mq.SkuInfoTo;
import gulimall.common.utils.R;
import gulimall.common.vo.MemberRespVo;
import gulimall.common.vo.ShoppingCart;
import gulimall.common.vo.ShoppingCartItem;
import gulimall.order.config.AlipayConfig;
import gulimall.order.config.MyRabbitMqConfig;
import gulimall.order.constant.OrderConstant;
import gulimall.order.constant.PaymentStatusConstant;
import gulimall.order.entity.OrderItemEntity;
import gulimall.common.enume.OrderStatusEnum;
import gulimall.order.entity.PaymentInfoEntity;
import gulimall.order.feign.MemberFeignService;
import gulimall.order.feign.ProductFeignService;
import gulimall.order.feign.ShoppingCartFeignService;
import gulimall.order.feign.WareFeignService;
import gulimall.order.interceptor.LoginUserInterceptor;
import gulimall.order.service.OrderItemService;
import gulimall.order.service.PaymentInfoService;
import gulimall.order.to.OrderCreateTo;
import gulimall.order.vo.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import gulimall.common.utils.PageUtils;
import gulimall.common.utils.Query;

import gulimall.order.dao.OrderDao;
import gulimall.order.entity.OrderEntity;
import gulimall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @author 孙启新
 * <br>FileName: OrderServiceImpl
 * <br>Date: 2020/08/08 15:30:23
 */
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
    private AlipayConfig alipayConfig;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private PaymentInfoService paymentInfoService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ThreadPoolExecutor executor;

    /**
     * 用于同线程共享数据
     */
    private ThreadLocal<OrderSubmitVo> orderSubmitVoThreadLocal = new ThreadLocal<>();

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<>()
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
            if (addresses != null && addresses.size() > 0) {
                orderConfirmVo.setAddressVos(addresses);
            }
        }, executor);

        /*异步无返回值执行*/
        CompletableFuture<Void> getOrderItemVos = CompletableFuture.runAsync(() -> {
            //2、远程查询当前用户的购物车数据
            /*feign的远程调用会丢失请求头，所以会导致远程调用没有cookie导致商品服务无法查到用户购物车,
            这一步如果使用异步方式运行,我们配置了feign的拦截器来同步cookie，使用RequestContextHolder获取之前请求参数需要在同一个线程中,那么就需要将主线程的数据同步到该线程*/
            //将主线程request数据同步到该线程
            RequestContextHolder.setRequestAttributes(requestAttributes);
            ShoppingCart shoppingCart = shoppingCartFeignService.getCurrentUserShoppingCart();
            if (shoppingCart != null) {
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
            }
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
            R r = memberFeignService.getInfoById(memberRespVo.getId());
            if (r.getCode() == 0) {
                MemberRespVo data = r.getData("member", new TypeReference<MemberRespVo>() {
                });
                orderConfirmVo.setIntegration(data.getIntegration());
            }
        }, executor);

        //5、使用防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        orderConfirmVo.setOrderToken(token);
        //保存进redis
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId(), token, 30, TimeUnit.MINUTES);

        //等待都执行完返回
        CompletableFuture.allOf(getAddresses, getOrderItemVos, setIntegration).get();
        return orderConfirmVo;
    }

    /**
     * 更改当前的默认地址为新指定的
     *
     * @param memberId      id
     * @param defaultStatus 要更改成的信息
     * @param addressId     要更改成默认地址的列id
     */
    @Override
    public void updateAddress(Long memberId, Integer defaultStatus, Long addressId) {
        memberFeignService.updateAddress(memberId, defaultStatus, addressId);
    }

    /**
     * 提交订单,这里不用seata分布式事务，这是个高并发操作，库存服务本身使用自动解锁模式,我们使用消息队列进行通知解锁，保证最终一致性即可
     *
     * @param orderSubmitVo orderSubmitVo
     * @return SubmitOrderResponseVo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo) {
        orderSubmitVoThreadLocal.set(orderSubmitVo);
        SubmitOrderResponseVo submitOrderResponseVo = new SubmitOrderResponseVo();
        //获取到当前登录的用户id(能来到这都是已经登录的)
        MemberRespVo memberRespVo = LoginUserInterceptor.threadLocal.get();

        //1、验证令牌（防重复提交，令牌的删除和对比必须保证原子性）
        String orderToken = orderSubmitVo.getOrderToken();
        //lua脚本，返回0代表令牌校验失败，1代表删除成功
        String script = "if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        //原子验证和删除令牌
        Long result = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Collections.singletonList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()), orderToken);
        if (result != null && result == 1L) {
            //验证通过
            submitOrderResponseVo.setCode(0);
            //2、创建订单、订单项等信息
            OrderCreateTo orderCreateTo = createOrder(null);

            //3、保存订单至数据库
            this.saveOrder(orderCreateTo);

            //4、远程锁定库存,有异常回滚订单数据
            WareSkuLockVo wareSkuLockVo = new WareSkuLockVo();
            List<OrderItemVo> itemVos = orderCreateTo.getOrderItemEntities().stream().map(item -> {
                OrderItemVo orderItemVo = new OrderItemVo();
                orderItemVo.setSkuId(item.getSkuId());
                orderItemVo.setCount(item.getSkuQuantity());
                orderItemVo.setTitle(item.getSkuName());
                return orderItemVo;
            }).collect(Collectors.toList());
            wareSkuLockVo.setOrderItemVos(itemVos);
            wareSkuLockVo.setOrderSn(orderCreateTo.getOrderEntity().getOrderSn());
            R r = wareFeignService.orderLockStock(wareSkuLockVo);
            if (r.getCode() == 0) {
                //锁定成功
                submitOrderResponseVo.setOrderEntity(orderCreateTo.getOrderEntity());
                //订单创建成功，给mq发送创建成功的消息
                rabbitTemplate.convertAndSend(MyRabbitMqConfig.ORDER_EVENT_EXCHANGE, MyRabbitMqConfig.ORDER_DELAY_KEY, orderCreateTo.getOrderEntity(), new CorrelationData(UUID.randomUUID().toString()));
            } else {
                //锁定失败,抛出异常
                throw new NoStockException(r.get("msg").toString());
            }
        } else {
            //不通过
            submitOrderResponseVo.setCode(1);
        }
        return submitOrderResponseVo;
    }

    /**
     * 根据订单号获取订单的详细信息
     *
     * @param orderSn 订单号
     * @return 订单的详细信息
     */
    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        return this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
    }

    /**
     * 关闭订单
     *
     * @param orderEntity orderEntity
     */
    @Override
    public void closeOrder(OrderEntity orderEntity) throws AlipayApiException {
        //1、查询当前订单的最新状态
        OrderEntity newOrder = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderEntity.getOrderSn()));
        if (newOrder != null && newOrder.getStatus().equals(OrderStatusEnum.CREATE_NEW.getCode())) {
            //2、关闭订单(更改订单状态为已取消)
            newOrder.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(newOrder);

            //3、防止支付宝由于网络延迟问题自动收单比我们订单关闭慢了，这里关闭订单时手动调用收单
            //获取下交易流水号
            PaymentInfoEntity paymentInfoEntity = paymentInfoService.getOne(new QueryWrapper<PaymentInfoEntity>().eq("order_sn", orderEntity.getOrderSn()));
            if (paymentInfoEntity != null) {
                //调用收单方法
                alipayConfig.alipayTradeClose(paymentInfoEntity.getAlipayTradeNo(), null);
            } else {
                //调用收单方法
                alipayConfig.alipayTradeClose(null, orderEntity.getOrderSn());
            }
            //4、<br>再次发送消息确认解锁，防止网络延迟等问题导致库存服务解锁库存时关闭订单被阻塞或没执行完查询一直是待付款状态，库存一直解锁不了
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(newOrder, orderTo);
            rabbitTemplate.convertAndSend(MyRabbitMqConfig.ORDER_EVENT_EXCHANGE, "ware.dead.order", orderTo, new CorrelationData(UUID.randomUUID().toString()));
        }
    }

    /**
     * 根据订单号查询需要的信息
     *
     * @param orderSn 订单号
     * @return PayVo
     */
    @Override
    public PayVo getOrderPayInfo(String orderSn) {
        PayVo payVo = null;
        //1、查询订单信息封装至payVo
        OrderEntity orderEntity = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        if (!OrderStatusEnum.CANCLED.getCode().equals(orderEntity.getStatus())) {
            //没超过支付时间
            payVo = new PayVo();
            payVo.setOut_trade_no(orderEntity.getOrderSn());
            //精确至小数点后两位（支付宝支付要求）,后面向上取值
            BigDecimal totalAmount = orderEntity.getTotalAmount().setScale(2, BigDecimal.ROUND_UP);
            payVo.setTotal_amount(totalAmount.toString());

            //2、查询订单项信息
            List<OrderItemEntity> orderItemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
            OrderItemEntity orderItemEntity = orderItemEntities.get(0);
            payVo.setSubject(orderItemEntity.getSkuName());
            payVo.setBody(orderItemEntity.getSkuAttrsVals());
        }
        return payVo;
    }

    /**
     * 支付成功后支付宝也会默认访问该请求
     * <br>查询出已经支付的订单信息
     *
     * @param params 查询参数
     * @return 订单数据
     */
    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {
        MemberRespVo memberRespVo = LoginUserInterceptor.threadLocal.get();

        //1、设置默认每页显示条数
        String limit = (String) params.get("limit");
        if (StringUtils.isEmpty(limit)) {
            params.put("limit", 5);
        }

        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<OrderEntity>().eq("member_id", memberRespVo.getId()).orderByDesc("id");

        //2、设置按照状态的检索条件
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("status", status);
        }

        //3、获取当前登录用户可用于显示的订单信息
        IPage<OrderEntity> page = this.page(new Query<OrderEntity>().getPage(params), wrapper);

        if (page != null && page.getRecords().size() > 0) {
            //4、获取每一个订单的订单项
            List<OrderEntity> orderEntities = page.getRecords().stream().peek(orderEntity -> {
                List<OrderItemEntity> itemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderEntity.getOrderSn()).orderByDesc("id"));
                orderEntity.setOrderItemEntities(itemEntities);
            }).collect(Collectors.toList());
            page.setRecords(orderEntities);
            return new PageUtils(page);
        } else {
            return null;
        }
    }

    /**
     * 根据支付宝返回的支付成功信息，修改订单的状态
     *
     * @param payAsyncVo 支付宝通知信息
     * @return 成功or失败
     */
    @Override
    public Boolean handlePayResult(PayAsyncVo payAsyncVo) throws ParseException {
        //1、保存交易信息至数据库表
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setAlipayTradeNo(payAsyncVo.getTrade_no());
        paymentInfoEntity.setCallbackContent(payAsyncVo.getBody());
        paymentInfoEntity.setCallbackTime(sdf.parse(payAsyncVo.getNotify_time()));
        paymentInfoEntity.setCreateTime(sdf.parse(payAsyncVo.getGmt_create()));
        //查询订单id
        OrderEntity orderEntity = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", payAsyncVo.getOut_trade_no()));
        paymentInfoEntity.setOrderId(orderEntity.getId());
        paymentInfoEntity.setOrderSn(payAsyncVo.getOut_trade_no());
        paymentInfoEntity.setPaymentStatus(payAsyncVo.getTrade_status());
        paymentInfoEntity.setSubject(payAsyncVo.getSubject());
        paymentInfoEntity.setTotalAmount(new BigDecimal(payAsyncVo.getBuyer_pay_amount()));
        paymentInfoService.save(paymentInfoEntity);

        if (PaymentStatusConstant.TRADE_SUCCESS.equals(payAsyncVo.getTrade_status()) || PaymentStatusConstant.TRADE_FINISHED.equals(payAsyncVo.getTrade_status())) {
            //2、支付成功,修改订单状态为已付款
            String orderSn = payAsyncVo.getOut_trade_no();
            OrderEntity updateOrderEntity = new OrderEntity();
            updateOrderEntity.setStatus(OrderStatusEnum.PAYED.getCode());
            this.update(updateOrderEntity, new UpdateWrapper<OrderEntity>().eq("order_sn", orderSn));

            //3、调用远程服务将下单时所使用的会员积分清零
            Long memberId = orderEntity.getMemberId();
            MemberRespVo memberRespVo = new MemberRespVo();
            memberRespVo.setId(memberId);
            memberRespVo.setIntegration(0);
            memberFeignService.updateById(memberRespVo);
            return true;
        }
        return false;
    }

    /**
     * 保存秒杀的订单信息
     *
     * @param seckillOrderTo seckillOrderTo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createSeckillOrder(SeckillOrderTo seckillOrderTo) {
        //获取用户的信息保存至threadLocal，创建订单需要获取使用
        R member = memberFeignService.getInfoById(seckillOrderTo.getMemberId());
        MemberRespVo memberRespVo = member.getData("member", new TypeReference<MemberRespVo>() {
        });
        LoginUserInterceptor.threadLocal.set(memberRespVo);

        //1、构建提交订单的基本信息
        OrderSubmitVo orderSubmitVo = new OrderSubmitVo();
        //根据会员id获取其默认收货地址
        List<MemberAddressVo> addresses = memberFeignService.getAddresses(seckillOrderTo.getMemberId());
        for (MemberAddressVo address : addresses) {
            if (address.getDefaultStatus() == 1) {
                orderSubmitVo.setAddressId(address.getId());
            }
        }
        orderSubmitVo.setIntegrationAmount(new BigDecimal(0));
        orderSubmitVo.setPayPrice(seckillOrderTo.getSeckillPrice());
        orderSubmitVo.setPayType(1);
        orderSubmitVo.setTotalAmount(seckillOrderTo.getSeckillPrice().multiply(new BigDecimal(seckillOrderTo.getSeckillCount())));
        orderSubmitVoThreadLocal.set(orderSubmitVo);

        //2、创建订单等信息
        OrderCreateTo orderCreateTo = createOrder(seckillOrderTo.getOrderSn());
        //2.1、由于，没有经过购物车所以需要单独设置订单项
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        SkuInfoTo skuInfoTo = seckillOrderTo.getSkuInfoTo();
        shoppingCartItem.setPrice(seckillOrderTo.getSeckillPrice());
        shoppingCartItem.setCheck(true);
        shoppingCartItem.setCount(seckillOrderTo.getSeckillCount());
        shoppingCartItem.setImage(skuInfoTo.getSkuDefaultImg());
        //远程查询商品套餐值
        List<String> attrValues = productFeignService.getSkuSaleAttrValues(seckillOrderTo.getSkuId());
        shoppingCartItem.setSkuAttr(attrValues);
        shoppingCartItem.setSkuId(seckillOrderTo.getSkuId());
        shoppingCartItem.setTitle(skuInfoTo.getSkuName());
        OrderItemEntity orderItemEntity = buildOrderItemEntity(shoppingCartItem);
        orderItemEntity.setOrderSn(seckillOrderTo.getOrderSn());
        orderCreateTo.setOrderItemEntities(Collections.singletonList(orderItemEntity));

        //3、保存订单至数据库
        this.saveOrder(orderCreateTo);

        //4、远程锁定库存,有异常回滚订单数据
        WareSkuLockVo wareSkuLockVo = new WareSkuLockVo();
        List<OrderItemVo> itemVos = orderCreateTo.getOrderItemEntities().stream().map(item -> {
            OrderItemVo orderItemVo = new OrderItemVo();
            orderItemVo.setSkuId(item.getSkuId());
            orderItemVo.setCount(item.getSkuQuantity());
            orderItemVo.setTitle(item.getSkuName());
            return orderItemVo;
        }).collect(Collectors.toList());
        wareSkuLockVo.setOrderItemVos(itemVos);
        wareSkuLockVo.setOrderSn(orderCreateTo.getOrderEntity().getOrderSn());
        R r = wareFeignService.orderLockStock(wareSkuLockVo);
        if (r.getCode() == 0) {
            //订单创建成功，给mq发送创建成功的消息
            rabbitTemplate.convertAndSend(MyRabbitMqConfig.ORDER_EVENT_EXCHANGE, MyRabbitMqConfig.ORDER_DELAY_KEY, orderCreateTo.getOrderEntity(), new CorrelationData(UUID.randomUUID().toString()));
        } else {
            //锁定失败,抛出异常
            throw new NoStockException(r.get("msg").toString());
        }
    }

    /**
     * 保存订单数据至数据库
     *
     * @param orderCreateTo orderCreateTo
     */
    private void saveOrder(OrderCreateTo orderCreateTo) {
        OrderEntity orderEntity = orderCreateTo.getOrderEntity();
        List<OrderItemEntity> orderItemEntities = orderCreateTo.getOrderItemEntities();
        //保存订单
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity);

        //保存订单项
        for (OrderItemEntity orderItemEntity : orderItemEntities) {
            orderItemEntity.setOrderId(orderEntity.getId());
        }
        orderItemService.saveBatch(orderItemEntities);
    }

    /**
     * 封装订单所有的信息
     *
     * @return orderCreateTo
     */
    private OrderCreateTo createOrder(String orderSn) {
        //如果事先指定了订单号就用事先指定的，没指定就自动生成
        if (!StringUtils.isEmpty(orderSn)) {
            OrderCreateTo orderCreateTo = new OrderCreateTo();
            //1、构建订单
            OrderEntity orderEntity = buildOrderEntity(orderSn);
            orderCreateTo.setOrderEntity(orderEntity);

            //2、构建购物项
            List<OrderItemEntity> orderItemEntities = buildOrderItemEntities(orderSn);
            orderCreateTo.setOrderItemEntities(orderItemEntities);

            return orderCreateTo;
        }

        OrderCreateTo orderCreateTo = new OrderCreateTo();
        //1、构建订单
        OrderEntity orderEntity = buildOrderEntity(null);
        orderCreateTo.setOrderEntity(orderEntity);

        //2、构建购物项
        List<OrderItemEntity> orderItemEntities = buildOrderItemEntities(orderEntity.getOrderSn());
        orderCreateTo.setOrderItemEntities(orderItemEntities);

        return orderCreateTo;
    }

    /**
     * 构建订单
     *
     * @return orderEntity
     */
    private OrderEntity buildOrderEntity(String orderSn) {
        MemberRespVo memberRespVo = LoginUserInterceptor.threadLocal.get();
        OrderEntity orderEntity = new OrderEntity();
        if (StringUtils.isEmpty(orderSn)) {
            //1、如果没传递订单号就生成一个订单号
            orderSn = IdWorker.getTimeId();
        }
        orderEntity.setOrderSn(orderSn);

        //2、获取并设置收货地址信息
        OrderSubmitVo orderSubmitVo = orderSubmitVoThreadLocal.get();
        Long id = orderSubmitVo.getAddressId();
        R r = memberFeignService.getAddressById(id);
        if (r.getCode() == 0) {
            MemberAddressVo memberAddressVo = r.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {
            });
            orderEntity.setReceiverName(memberAddressVo.getName());
            orderEntity.setReceiverPhone(memberAddressVo.getPhone());
            orderEntity.setReceiverPostCode(memberAddressVo.getPostCode());
            orderEntity.setReceiverProvince(memberAddressVo.getProvince());
            orderEntity.setReceiverCity(memberAddressVo.getCity());
            orderEntity.setReceiverRegion(memberAddressVo.getRegion());
            orderEntity.setReceiverDetailAddress(memberAddressVo.getDetailAddress());
        }

        //3、设置会员积分抵扣金额
        orderEntity.setIntegrationAmount(orderSubmitVo.getIntegrationAmount());

        //4、设置订单总额
        orderEntity.setTotalAmount(orderSubmitVo.getTotalAmount());

        //5、设置应付金额
        orderEntity.setPayAmount(orderSubmitVo.getPayPrice());

        //6、设置运费金额
        orderEntity.setFreightAmount(new BigDecimal(0));

        //7、设置促销优化金额（促销价、满减、阶梯价）
        orderEntity.setPromotionAmount(new BigDecimal(0));

        //8、设置后台调整订单使用的折扣金额
        orderEntity.setDiscountAmount(new BigDecimal(0));

        //9、设置支付方式
        orderEntity.setPayType(orderSubmitVo.getPayType());

        //10、设置可以获得的积分,成长值
        orderEntity.setIntegration((int) Math.ceil(orderSubmitVo.getTotalAmount().doubleValue() / 10));
        orderEntity.setGrowth((int) Math.ceil(orderSubmitVo.getTotalAmount().doubleValue() / 10));

        //11、设置订单状态
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());

        //12、设置自动确认时间
        orderEntity.setAutoConfirmDay(7);

        //13、设置订单的删除状态
        orderEntity.setDeleteStatus(0);

        //14、设置会员id
        orderEntity.setMemberId(memberRespVo.getId());

        //15、设置订单创建时间
        orderEntity.setCreateTime(new Date());

        return orderEntity;
    }

    /**
     * 构建所有订单项数据
     *
     * @param orderSn 订单号
     * @return 订单项列表
     */
    private List<OrderItemEntity> buildOrderItemEntities(String orderSn) {
        ShoppingCart shoppingCart = shoppingCartFeignService.getCurrentUserShoppingCart();
        if (shoppingCart.getItems() != null) {
            return shoppingCart.getItems().stream()
                    //过滤出当前选中的购物项
                    .filter(ShoppingCartItem::getCheck)
                    .map(item -> {
                        //远程查询商品服务,更新为现在的最新价格
                        BigDecimal currentPrice = productFeignService.currentPrice(item.getSkuId());
                        item.setPrice(currentPrice);
                        //将数据封装到我们指定的vo
                        OrderItemEntity orderItemEntity = buildOrderItemEntity(item);
                        orderItemEntity.setOrderSn(orderSn);
                        return orderItemEntity;
                    }).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 构建每一个订单项
     *
     * @param item 当前遍历的商品数据
     * @return 订单项
     */
    private OrderItemEntity buildOrderItemEntity(ShoppingCartItem item) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        //1、商品的spu信息
        Long skuId = item.getSkuId();
        //远程查询spu信息
        R r = productFeignService.getSpuInfoBySkuId(skuId);
        if (r.getCode() == 0) {
            SpuInfoVo spuInfoVo = r.getData(new TypeReference<SpuInfoVo>() {
            });
            orderItemEntity.setSpuId(spuInfoVo.getId());
            orderItemEntity.setSpuName(spuInfoVo.getSpuName());
            orderItemEntity.setCategoryId(spuInfoVo.getCatalogId());
            //远程查询品牌名字
            R brandInfoById = productFeignService.getBrandInfoById(spuInfoVo.getBrandId());
            if (brandInfoById.getCode() == 0) {
                BrandInfoVo brandInfoVo = brandInfoById.getData("brand", new TypeReference<BrandInfoVo>() {
                });
                orderItemEntity.setSpuBrand(brandInfoVo.getName());
                orderItemEntity.setSpuPic(brandInfoVo.getLogo());
            }
        }

        //2、商品的sku信息
        orderItemEntity.setSkuId(item.getSkuId());
        orderItemEntity.setSkuName(item.getTitle());
        orderItemEntity.setSkuPic(item.getImage());
        orderItemEntity.setSkuPrice(item.getPrice());
        orderItemEntity.setSkuQuantity(item.getCount());
        orderItemEntity.setSkuAttrsVals(StringUtils.collectionToDelimitedString(item.getSkuAttr(), ";"));

        //3、优惠信息
        orderItemEntity.setCouponAmount(new BigDecimal(0));
        orderItemEntity.setPromotionAmount(new BigDecimal(0));
        orderItemEntity.setIntegrationAmount(new BigDecimal(0));
        //当前订单项的实际金额。总额减各种优惠
        BigDecimal realAmount = item.getSubtotalPrice().subtract(orderItemEntity.getCouponAmount()).subtract(orderItemEntity.getIntegrationAmount()).subtract(orderItemEntity.getPromotionAmount());
        orderItemEntity.setRealAmount(realAmount);

        //4、积分信息
        orderItemEntity.setGiftIntegration((int) Math.ceil(item.getSubtotalPrice().doubleValue() / 10));
        orderItemEntity.setGiftGrowth((int) Math.ceil(item.getSubtotalPrice().doubleValue() / 10));

        return orderItemEntity;
    }
}