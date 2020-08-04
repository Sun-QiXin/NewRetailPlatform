package gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import gulimall.auth.feign.MemberFeignService;
import gulimall.auth.feign.ThirdPartyFeignService;
import gulimall.auth.vo.UserLoginVo;
import gulimall.auth.vo.UserRegisterVo;
import gulimall.common.constant.AuthServerConstant;
import gulimall.common.exception.BizCodeEnume;
import gulimall.common.utils.R;
import gulimall.common.vo.MemberRespVo;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * @author 孙启新
 * <br>FileName: LoginController
 * <br>Date: 2020/08/01 13:24:05
 */
@Controller
public class LoginController {
    @Autowired
    private ThirdPartyFeignService thirdPartyFeignService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MemberFeignService memberFeignService;

    /**
     * 点击登录按钮时的原始地址
     */
    public static String currentOriginUrl = null;

    /**
     * 供前端调用的发送验证码接口
     *
     * @param phone 手机号
     * @return R对象
     */
    @GetMapping("/sms/sendCode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone) {
        //TODO 1、接口防刷功能

        //2、防止同一个手机号在60秒内多次调用
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)) {
            long time = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - time < 60000) {
                return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(), BizCodeEnume.SMS_CODE_EXCEPTION.getMsg());
            }
        }

        //3、生成6位数字验证码
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 1; i <= 6; i++) {
            sb.append(random.nextInt(10));
        }
        //4、验证码的再次效验，存入redis
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, sb.toString() + "_" + System.currentTimeMillis(), 5, TimeUnit.MINUTES);
        return thirdPartyFeignService.sendCode(phone, sb.toString());
    }

    /**
     * 注册功能
     * TODO 需要解决分布式下的session问题。
     *
     * @param userRegisterVo     页面传来的数据
     * @param bindingResult      校验返回对象
     * @param redirectAttributes 重定向携带数据，利用session原理。 将数据放在session中。只要跳到下一个页面取出这个数据以后，session里 面的数据就会删掉
     * @return R对象
     */
    @PostMapping("/register")
    public String register(@Validated UserRegisterVo userRegisterVo, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        //1、数据效验
        if (bindingResult.hasErrors()) {
            bindingResult.hasGlobalErrors();
            //将错误信息封装为map传给页面
            Map<String, String> errors = new HashMap<>(10);
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            redirectAttributes.addFlashAttribute(AuthServerConstant.ERRORS, errors);
            //校验失败
            return "redirect:http://auth.gulimall.com/register.html";
        }

        //2、校验验证码
        String code = userRegisterVo.getCode();
        String redisCodeWithTime = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + userRegisterVo.getPhone());
        if (!StringUtils.isEmpty(redisCodeWithTime)) {
            String redisCode = redisCodeWithTime.split("_")[0];
            if (code.equals(redisCode)) {
                //删除验证码,令牌机制
                redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + userRegisterVo.getPhone());
                //验证码通过，真正注册，调用远程服务
                R r = memberFeignService.register(userRegisterVo);
                if (r.getCode() == 0) {
                    //注册成功，重定向到登录页面
                    return "redirect:http://auth.gulimall.com/login.html";
                } else if (r.getCode() == BizCodeEnume.PHONE_EXIST_EXCEPTION.getCode()) {
                    //注册失败,手机号已存在返回错误信息
                    Map<String, String> errors = new HashMap<>(10);
                    errors.put("phone", r.get("msg").toString());
                    redirectAttributes.addFlashAttribute(AuthServerConstant.ERRORS, errors);
                    return "redirect:http://auth.gulimall.com/register.html";
                } else if (r.getCode() == BizCodeEnume.USERNAME_EXIST_EXCEPTION.getCode()) {
                    //注册失败,用户名已存在返回错误信息
                    Map<String, String> errors = new HashMap<>(10);
                    errors.put("username", r.get("msg").toString());
                    redirectAttributes.addFlashAttribute(AuthServerConstant.ERRORS, errors);
                    return "redirect:http://auth.gulimall.com/register.html";
                } else {
                    //注册失败,返回错误信息
                    Map<String, String> errors = new HashMap<>(10);
                    errors.put("username", r.get("msg").toString());
                    redirectAttributes.addFlashAttribute(AuthServerConstant.ERRORS, errors);
                    return "redirect:http://auth.gulimall.com/register.html";
                }
            } else {
                return "redirect:http://auth.gulimall.com/register.html";
            }
        } else {
            Map<String, String> errors = new HashMap<>(10);
            errors.put("code", "验证码错误");
            redirectAttributes.addFlashAttribute(AuthServerConstant.ERRORS, errors);
            //校验失败
            return "redirect:http://auth.gulimall.com/register.html";
        }
    }

    /**
     * 跳转登录页
     *
     * @param session   session
     * @param originUrl 点击登录按钮时的原始地址
     * @return 登录或主页
     */
    @GetMapping("/login.html")
    public String loginPage(HttpSession session, @RequestParam(value = "originUrl", required = false) String originUrl) {
        currentOriginUrl = null;
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute == null) {
            //没有登录过
            if (!StringUtils.isEmpty(originUrl)) {
                currentOriginUrl = originUrl;
            }
            return "login";
        } else {
            //登录过跳转首页
            return "redirect:http://gulimall.com";
        }
    }

    /**
     * 登录功能
     *
     * @param userLoginVo        登录信息
     * @param redirectAttributes 重定向携带数据
     * @return 主页
     */
    @PostMapping("/login")
    public String login(UserLoginVo userLoginVo, RedirectAttributes redirectAttributes, HttpSession session) {
        //调用远程登录
        R r = memberFeignService.login(userLoginVo);
        if (r.getCode() == 0) {
            /*将返回的信息存入session(由于整合了springSession并将redis设置为存储对象，所以就存到了redis中)*/
            MemberRespVo memberRespVo = r.getData(new TypeReference<MemberRespVo>() {
            });
            session.setAttribute(AuthServerConstant.LOGIN_USER, memberRespVo);
            if (StringUtils.isEmpty(currentOriginUrl)) {
                //默认跳转首页
                return "redirect:http://gulimall.com";
            } else {
                //如果传了原始网址就跳回原始网址
                return "redirect:" + currentOriginUrl;
            }

        } else {
            redirectAttributes.addFlashAttribute(AuthServerConstant.LOGIN_ERROR_USER, r.getData("msg", new TypeReference<String>() {
            }));
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }

    /**
     * 退出登录
     *
     * @param session session
     * @return 返回当前页
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, @RequestParam("domainName") String domainName) {
        session.removeAttribute(AuthServerConstant.LOGIN_USER);
        return "redirect:" + domainName;
    }
}
