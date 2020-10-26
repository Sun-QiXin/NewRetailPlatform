package gulimall.auth.feign.fallback;

import gulimall.auth.feign.MemberFeignService;
import gulimall.auth.vo.SocialUserVo;
import gulimall.auth.vo.UserLoginVo;
import gulimall.auth.vo.UserRegisterVo;
import gulimall.common.exception.BizCodeEnume;
import gulimall.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 远程调用失败的降级处理方法
 *
 * @author 孙启新
 * <br>FileName: MemberFeignServiceFallbackHandleImpl
 * <br>Date: 2020/08/20 16:35:25
 */
@Component
@Slf4j
public class MemberFeignServiceFallbackHandleImpl implements MemberFeignService {

    /**
     * 注册用户
     *
     * @param userRegisterVo 用户信息
     * @return R对象
     */
    @Override
    public R register(UserRegisterVo userRegisterVo) {
        log.error("--------------------------调用远程服务方法 register 失败,返回降级信息-------------------------");
        return R.error(BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getCode(), BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getMsg());
    }

    /**
     * 登录用户
     *
     * @param userLoginVo 登录信息
     * @return R对象
     */
    @Override
    public R login(UserLoginVo userLoginVo) {
        log.error("--------------------------调用远程服务方法 login 失败,返回降级信息-------------------------");
        return R.error(BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getCode(), BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getMsg());
    }

    /**
     * 社交登录
     *
     * @param socialUserVo 登录信息
     * @return R对象
     */
    @Override
    public R OAuth2login(SocialUserVo socialUserVo) {
        log.error("--------------------------调用远程服务方法 OAuth2login 失败,返回降级信息-------------------------");
        return R.error(BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getCode(), BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getMsg());
    }
}
