package gulimall.member.feign;

import gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 孙启新
 * <br>FileName: OrderFeignService
 * <br>Date: 2020/07/13 12:02:08
 */
@Component
@FeignClient("gulimall-order")
public interface OrderFeignService {
    @RequestMapping("/order/order/test")
    public R test();
}
