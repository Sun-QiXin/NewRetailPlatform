package gulimall.seckill.feign;

import gulimall.common.utils.R;
import gulimall.seckill.feign.fallback.CouponFeignServiceFallbackHandleImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author 孙启新
 * <br>FileName: CouponFeignService
 * <br>Date: 2020/08/17 09:12:13
 */
@Component
@FeignClient(value = "gulimall-coupon", fallback = CouponFeignServiceFallbackHandleImpl.class)
public interface CouponFeignService {
    /**
     * 获取最近三天内的秒杀活动以及每个活动需要上架的商品
     *
     * @return R
     */
    @GetMapping("/coupon/seckillsession/latestThreeDaysSession")
    R getLatestThreeDaysSessions();
}
