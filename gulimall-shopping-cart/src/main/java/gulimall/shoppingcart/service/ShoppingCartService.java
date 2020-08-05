package gulimall.shoppingcart.service;

import gulimall.shoppingcart.vo.ShoppingCartItem;

import java.util.concurrent.ExecutionException;

/**
 * @author 孙启新
 * <br>FileName: ShoppingCartService
 * <br>Date: 2020/08/05 11:34:29
 */
public interface ShoppingCartService {
    /**
     * 加入购物车
     * @param skuId 商品的id
     * @param num 商品数量
     * @return 当前商品的详细信息
     */
    ShoppingCartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;
}
