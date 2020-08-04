package gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import gulimall.auth.feign.MemberFeignService;
import gulimall.common.constant.AuthServerConstant;
import gulimall.common.vo.MemberRespVo;
import gulimall.auth.vo.SocialUserVo;
import gulimall.common.utils.HttpUtils;
import gulimall.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理社交登录请求
 *
 * @author 孙启新
 * <br>FileName: OAuth2Controller
 * <br>Date: 2020/08/03 11:46:54
 */
@Controller
public class OAuth2Controller {
    @Autowired
    private MemberFeignService memberFeignService;

    /**
     * 微博社交登录
     *
     * @param code               code
     * @param redirectAttributes 重定向携带数据
     * @param session session对象
     * @return 首页
     */
    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, RedirectAttributes redirectAttributes, HttpSession session) throws UnsupportedEncodingException {
        //1、根据code发送请求换取accessToken
        Map<String, String> params = new HashMap<>(16);
        params.put("client_id", "1406408040");
        params.put("client_secret", "856a6bd2406d202d20d4f46daeeae965");
        params.put("grant_type", "authorization_code");
        params.put("redirect_uri", "http://auth.gulimall.com/oauth2.0/weibo/success");
        params.put("code", code);
        String response = HttpUtils.post("https://api.weibo.com/oauth2/access_token", null, params);
        if (response != null) {
            //将json字符串转为vo对象
            SocialUserVo socialUserVo = JSON.parseObject(response, SocialUserVo.class);
            //知道当前是哪个社交用户
            //1)、当前用户如果是第一次进网站，自动注册进来(为当前社交用户生成一个会员信息账号，以后这个社交账号就对应指定的会员)
            //调用远程服务进行登录或注册
            R r = memberFeignService.OAuth2login(socialUserVo);
            if (r.getCode() == 0) {
                //登录成功就跳回首页
                MemberRespVo memberRespVo = r.getData(new TypeReference<MemberRespVo>() {
                });
                /*将返回的信息存入session(由于整合了springSession并将redis设置为存储对象，所以就存到了redis中)*/
                session.setAttribute(AuthServerConstant.LOGIN_USER, memberRespVo);
                if (StringUtils.isEmpty(LoginController.currentOriginUrl)) {
                    //默认跳转首页
                    return "redirect:http://gulimall.com";
                } else {
                    //如果传了原始网址就跳回原始网址
                    return "redirect:" + LoginController.currentOriginUrl;
                }
            } else {
                //登录失败，重新登录
                String msg = r.getData("msg", new TypeReference<String>() {
                });
                redirectAttributes.addFlashAttribute(AuthServerConstant.LOGIN_ERROR_USER, msg);
                return "redirect:http://auth.gulimall.com/login.html";
            }

        } else {
            //获取失败，重新登录
            redirectAttributes.addFlashAttribute(AuthServerConstant.LOGIN_ERROR_USER, "获取信息失败，请重新登录");
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }
}
