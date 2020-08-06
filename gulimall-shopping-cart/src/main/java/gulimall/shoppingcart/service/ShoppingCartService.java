package gulimall.shoppingcart.service;

import gulimall.common.vo.ShoppingCart;
import gulimall.common.vo.ShoppingCartItem;
import org.springframework.data.redis.core.BoundHashOperations;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author 孙启新
 * <br>FileName: ShoppingCartService
 * <br>Date: 2020/08/05 11:34:29
 */
public interface ShoppingCartService {
    /**
     * 加入购物车
     *
     * @param skuId 商品的id
     * @param num   商品数量
     * @return 当前商品的详细信息
     * @throws ExecutionException   ExecutionException
     * @throws InterruptedException InterruptedException
     */
    ShoppingCartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 获取购物车数据
     *
     * @return 购物车数据
     * @throws ExecutionException   ExecutionException
     * @throws InterruptedException InterruptedException
     */
    ShoppingCart getCartList() throws ExecutionException, InterruptedException;

    /**
     * (方法)根据用户登没登录来判断是使用临时购物车的hash还是用户购物车的hash存储数据
     *
     * @return 商品数据要存入的hash
     */
    BoundHashOperations<String, Object, Object> getCartOps();

    /**
     * 传入要获取的购物车hash键返回该购物车商品数据
     *
     * @param cartKey 购物车hash键
     * @return 指定购物车的数据
     */
    List<ShoppingCartItem> getShoppingCartItems(String cartKey);

    /**
     * 根据skuId获取当前购物项
     * @param skuId skuId
     * @return 购物项
     */
    ShoppingCartItem getOneShoppingCartItem(Long skuId);

    /**
     * 删除购物车中的商品
     *
     * @param skuIds 商品的skuIds集合
     */
    void clearCartProduct(String skuIds);

    /**
     * 修改购物车商品的选中状态
     * @param skuId skuId
     * @param check 当前选中状态
     */
    void checkItem(Long skuId, Integer check);

    /**
     * 修改购物车商品的件数
     * @param skuId skuId
     * @param count 要修改成的件数
     */
    void countItem(Long skuId, Integer count);
}
