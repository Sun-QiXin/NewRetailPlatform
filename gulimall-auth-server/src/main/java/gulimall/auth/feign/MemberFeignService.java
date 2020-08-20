package gulimall.auth.feign;

import gulimall.auth.feign.fallback.MemberFeignServiceFallbackHandleImpl;
import gulimall.auth.vo.SocialUserVo;
import gulimall.auth.vo.UserLoginVo;
import gulimall.auth.vo.UserRegisterVo;
import gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 远程调用member服务
 *
 * @author 孙启新
 * <br>FileName: MemberFeignService
 * <br>Date: 2020/08/02 13:42:12
 */
@Component
@FeignClient(value = "gulimall-member", fallback = MemberFeignServiceFallbackHandleImpl.class)
public interface MemberFeignService {

    /**
     * 注册用户
     *
     * @param userRegisterVo 用户信息
     * @return R对象
     */
    @PostMapping("/member/member/register")
    R register(@RequestBody UserRegisterVo userRegisterVo);

    /**
     * 登录用户
     *
     * @param userLoginVo 登录信息
     * @return R对象
     */
    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo userLoginVo);

    /**
     * 社交登录
     *
     * @param socialUserVo 登录信息
     * @return R对象
     */
    @PostMapping("/member/member/oauth2/login")
    R OAuth2login(@RequestBody SocialUserVo socialUserVo);
}
