package gulimall.auth.feign.fallback;

import gulimall.auth.feign.ThirdPartyFeignService;

import gulimall.common.exception.BizCodeEnume;
import gulimall.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 远程调用失败的降级处理方法
 *
 * @author 孙启新
 * <br>FileName: ThirdPartyFeignServiceFallbackHandleImpl
 * <br>Date: 2020/08/20 16:35:25
 */
@Component
@Slf4j
public class ThirdPartyFeignServiceFallbackHandleImpl implements ThirdPartyFeignService {
    /**
     * 调用第三方服务发送验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @return R对象
     */
    @Override
    public R sendCode(String phone, String code) {
        log.error("--------------------------调用远程服务方法 sendCode 失败,返回降级信息-------------------------");
        return R.error(BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getCode(), BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getMsg());
    }
}
