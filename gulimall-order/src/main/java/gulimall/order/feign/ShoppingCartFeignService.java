package gulimall.order.feign;

import gulimall.common.vo.ShoppingCart;
import gulimall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author 孙启新
 * <br>FileName: MemberFeignService
 * <br>Date: 2020/08/09 10:38:17
 */
@Component
@FeignClient("gulimall-shopping-cart")
public interface ShoppingCartFeignService {
    /**
     * 获取当前登录用户的购物车数据返回
     * @return ShoppingCart
     * @throws ExecutionException ExecutionException
     * @throws InterruptedException InterruptedException
     */
    @GetMapping("/currentUserShoppingCart")
    ShoppingCart getCurrentUserShoppingCart();
}
