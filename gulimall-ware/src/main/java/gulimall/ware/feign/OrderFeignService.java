package gulimall.ware.feign;

import gulimall.common.utils.R;
import gulimall.ware.feign.fallback.OrderFeignServiceFallbackHandleImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 孙启新
 * <br>FileName: OrderFeignService
 * <br>Date: 2020/08/12 15:16:40
 */
@Component
@FeignClient(value = "gulimall-order", fallback = OrderFeignServiceFallbackHandleImpl.class)
public interface OrderFeignService {

    /**
     * 根据订单号获取订单的详细信息
     *
     * @param orderSn 订单号
     * @return 订单的详细信息
     */
    @GetMapping("/order/order/orderInfo/{orderSn}")
    R getOrder(@PathVariable("orderSn") String orderSn);
}
