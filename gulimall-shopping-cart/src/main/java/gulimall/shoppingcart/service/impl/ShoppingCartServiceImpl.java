package gulimall.shoppingcart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import gulimall.common.constant.ShoppingCartConstant;
import gulimall.common.utils.R;
import gulimall.shoppingcart.feign.ProductFeignService;
import gulimall.shoppingcart.interceptor.ShoppingCartInterceptor;
import gulimall.shoppingcart.service.ShoppingCartService;
import gulimall.shoppingcart.to.UserInfoTo;
import gulimall.common.vo.ShoppingCart;
import gulimall.common.vo.ShoppingCartItem;
import gulimall.shoppingcart.vo.SkuInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

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

        //判断购物车中有无该商品，没有就添加，有就增加数量
        String catItem = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(catItem)) {
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
                //2、将新商品添加到购物车
                if (skuInfo != null) {
                    shoppingCartItem.setSkuId(skuId);
                    shoppingCartItem.setTitle(skuInfo.getSkuTitle());
                    shoppingCartItem.setImage(skuInfo.getSkuDefaultImg());
                    shoppingCartItem.setCheck(false);
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
            CompletableFuture.allOf(getSkuInfo, getSaleAttrValues).get();
            //4、保存进redis指定的hash中
            String jsonString = JSON.toJSONString(shoppingCartItem);
            cartOps.put(skuId.toString(), jsonString);
            return shoppingCartItem;
        } else {
            //1、增加数量
            ShoppingCartItem redisShoppingCartItem = JSON.parseObject(catItem, ShoppingCartItem.class);
            redisShoppingCartItem.setCount(redisShoppingCartItem.getCount() + num);
            //2、保存进redis指定的hash中
            String jsonString = JSON.toJSONString(redisShoppingCartItem);
            cartOps.put(skuId.toString(), jsonString);
            return redisShoppingCartItem;
        }
    }

    /**
     * 获取购物车数据
     *
     * @return 购物车数据
     */
    @Override
    public ShoppingCart getCartList() throws ExecutionException, InterruptedException {
        ShoppingCart shoppingCart = new ShoppingCart();
        UserInfoTo userInfoTo = ShoppingCartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() != null) {
            //如果用户登录了
            //1、如果临时购物车有数据就需要将数据合并到登录用户的购物车中，并清空临时购物车
            String tempCartKey = ShoppingCartConstant.CART_PREFIX + userInfoTo.getUserKey();
            List<ShoppingCartItem> tempCartItemList = getShoppingCartItems(tempCartKey);
            if (tempCartItemList != null && tempCartItemList.size() > 0) {
                //有数据,合并购物车
                for (ShoppingCartItem tempCartItem : tempCartItemList) {
                    //直接调用添加购物车方法，由于进入这个分支肯定是登录了，所以肯定是添加到用户购物车
                    addToCart(tempCartItem.getSkuId(), tempCartItem.getCount());
                }
                //合并完后清空临时购物车
                redisTemplate.delete(tempCartKey);
            }

            //2、获取上面合并后的用户购物车
            String userCartKey = ShoppingCartConstant.CART_PREFIX + userInfoTo.getUserId();
            List<ShoppingCartItem> userCartItemList = getShoppingCartItems(userCartKey);
            if (userCartItemList != null && userCartItemList.size() > 0) {
                shoppingCart.setItems(userCartItemList);
            }

        } else {
            //没登录就使用临时购物车的数据展示
            String tempCartKey = ShoppingCartConstant.CART_PREFIX + userInfoTo.getUserKey();
            List<ShoppingCartItem> cartItemList = getShoppingCartItems(tempCartKey);
            if (cartItemList != null && cartItemList.size() > 0) {
                shoppingCart.setItems(cartItemList);
            }
        }
        return shoppingCart;
    }

    /**
     * 删除购物车中的商品
     *
     * @param skuIds 商品的skuIds集合
     */
    @Override
    public void clearCartProduct(String skuIds) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        if (cartOps != null) {
            String[] currentSkuIds = skuIds.split(",");
            for (String skuId : currentSkuIds) {
                cartOps.delete(skuId);
            }
        }
    }

    /**
     * 修改购物车商品的选中状态
     *
     * @param skuId skuId
     * @param check 当前选中状态
     */
    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        ShoppingCartItem shoppingCartItem = getOneShoppingCartItem(skuId);
        shoppingCartItem.setCheck(check == 1);
        String jsonString = JSON.toJSONString(shoppingCartItem);
        cartOps.put(skuId.toString(), jsonString);
    }

    /**
     * 修改购物车商品的件数
     *
     * @param skuId skuId
     * @param count 要修改成的件数
     */
    @Override
    public void countItem(Long skuId, Integer count) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        ShoppingCartItem shoppingCartItem = getOneShoppingCartItem(skuId);
        shoppingCartItem.setCount(count);
        String jsonString = JSON.toJSONString(shoppingCartItem);
        cartOps.put(skuId.toString(), jsonString);
    }

    /**
     * 获取当前登录用户的购物车数据返回
     * @return ShoppingCart
     * @throws ExecutionException ExecutionException
     * @throws InterruptedException InterruptedException
     */
    @Override
    public ShoppingCart getCurrentUserShoppingCart() throws ExecutionException, InterruptedException {
        return getCartList();
    }

    /**
     * 根据skuId获取当前购物项
     *
     * @param skuId skuId
     * @return 购物项
     */
    @Override
    public ShoppingCartItem getOneShoppingCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String cartItem = (String) cartOps.get(skuId.toString());
        return JSON.parseObject(cartItem, ShoppingCartItem.class);
    }

    /**
     * 根据用户登没登录来判断是使用临时购物车的hash还是用户购物车的hash存储数据
     *
     * @return 商品数据要存入的hash
     */
    @Override
    public BoundHashOperations<String, Object, Object> getCartOps() {
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

    /**
     * 传入要获取的购物车hash键返回该购物车商品数据
     *
     * @param cartKey 购物车hash键
     * @return 指定购物车的数据
     */
    @Override
    public List<ShoppingCartItem> getShoppingCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> cartItems = boundHashOps.values();
        List<ShoppingCartItem> cartItemList = null;
        if (cartItems != null && cartItems.size() > 0) {
            cartItemList = cartItems.stream().map(cartItem -> {
                String stringCartItem = (String) cartItem;
                return JSON.parseObject(stringCartItem, ShoppingCartItem.class);
            }).collect(Collectors.toList());
        }
        return cartItemList;
    }
}
