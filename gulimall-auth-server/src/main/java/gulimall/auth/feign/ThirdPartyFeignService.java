package gulimall.auth.feign;

import gulimall.auth.feign.fallback.ThirdPartyFeignServiceFallbackHandleImpl;
import gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 孙启新
 * <br>FileName: ThirdPartyFeignService
 * <br>Date: 2020/08/02 09:22:33
 */
@Component
@FeignClient(value = "gulimall-third-party", fallback = ThirdPartyFeignServiceFallbackHandleImpl.class)
public interface ThirdPartyFeignService {

    /**
     * 调用第三方服务发送验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @return R对象
     */
    @GetMapping("/sms/sendCode")
    R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
