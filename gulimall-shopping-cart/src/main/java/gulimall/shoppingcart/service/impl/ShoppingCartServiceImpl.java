package gulimall.shoppingcart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import gulimall.common.constant.ShoppingCartConstant;
import gulimall.common.utils.R;
import gulimall.shoppingcart.feign.ProductFeignService;
import gulimall.shoppingcart.interceptor.ShoppingCartInterceptor;
import gulimall.shoppingcart.service.ShoppingCartService;
import gulimall.shoppingcart.to.UserInfoTo;
import gulimall.shoppingcart.vo.ShoppingCartItem;
import gulimall.shoppingcart.vo.SkuInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 孙启新
 * <br>FileName: ShoppingCartServiceImpl
 * <br>Date: 2020/08/05 11:34:47
 */
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private ThreadPoolExecutor executor;


    /**
     * 加入购物车
     *
     * @param skuId 商品的id
     * @param num   商品数量
     * @return 当前商品的详细信息
     */
    @Override
    public ShoppingCartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        /*异步执行1*/
        CompletableFuture<Void> getSkuInfo = CompletableFuture.runAsync(() -> {
            //1、远程查询当前要添加的商品的信息
            R r = productFeignService.info(skuId);
            SkuInfoVo skuInfo = null;
            if (r.getCode() == 0) {
                skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
            }
            //2、将商品添加到购物车
            if (skuInfo != null) {
                shoppingCartItem.setSkuId(skuId);
                shoppingCartItem.setTitle(skuInfo.getSkuTitle());
                shoppingCartItem.setImage(skuInfo.getSkuDefaultImg());
                shoppingCartItem.setCheck(true);
                shoppingCartItem.setCount(num);
                shoppingCartItem.setPrice(skuInfo.getPrice());
            }
        }, executor);

        /*异步执行2*/
        CompletableFuture<Void> getSaleAttrValues = CompletableFuture.runAsync(() -> {
            //3、调用远程服务查询sku的销售组合信息
            List<String> saleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
            shoppingCartItem.setSkuAttr(saleAttrValues);
        }, executor);

        /*等所有异步都执行完毕*/
        CompletableFuture.allOf(getSkuInfo,getSaleAttrValues).get();
        //4、保存进redis
        String jsonString = JSON.toJSONString(shoppingCartItem);
        cartOps.put(skuId.toString(), jsonString);
        return shoppingCartItem;
    }

    /**
     * 根据key获取redis中相应的购物车数据
     *
     * @return 购物车数据
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = ShoppingCartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {
            //如果用户登录了
            cartKey = ShoppingCartConstant.CART_PREFIX + userInfoTo.getUserId();
        } else {
            //没登录就使用临时购物车
            cartKey = ShoppingCartConstant.CART_PREFIX + userInfoTo.getUserKey();
        }
        return redisTemplate.boundHashOps(cartKey);
    }
}
