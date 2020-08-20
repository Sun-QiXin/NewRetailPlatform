package gulimall.order.feign.fallback;

import gulimall.common.vo.ShoppingCart;

import gulimall.order.feign.ShoppingCartFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
/**
 * 远程调用失败的降级处理方法
 *
 * @author 孙启新
 * <br>FileName: ShoppingCartFeignServiceFallbackHandleImpl
 * <br>Date: 2020/08/20 16:35:25
 */
@Component
@Slf4j
public class ShoppingCartFeignServiceFallbackHandleImpl implements ShoppingCartFeignService {

    /**
     * 获取当前登录用户的购物车数据返回
     *
     * @return ShoppingCart
     */
    @Override
    public ShoppingCart getCurrentUserShoppingCart() {
        log.error("--------------------------调用远程服务方法 getCurrentUserShoppingCart 失败,返回降级信息-------------------------");
        return null;
    }
}
