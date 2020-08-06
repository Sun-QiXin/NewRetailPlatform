package gulimall.shoppingcart.interceptor;

import gulimall.common.constant.AuthServerConstant;
import gulimall.common.constant.ShoppingCartConstant;
import gulimall.common.vo.MemberRespVo;
import gulimall.shoppingcart.to.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * 购物车拦截器，在执行目标方法之前，判断用户的登录状态。并封装传递给controller目标请求
 *
 * @author 孙启新
 * <br>FileName: ShoppingCartInterceptor
 * <br>Date: 2020/08/05 11:45:29
 */
@Component
public class ShoppingCartInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    /**
     * 目标方法执行之前拦截
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param handler  选择要执行的处理程序
     * @return true或false
     * @throws Exception 抛出异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        UserInfoTo userInfoTo = new UserInfoTo();
        MemberRespVo memberRespVo = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (memberRespVo != null) {
            //登录了就将用户id存进userInfoTo
            userInfoTo.setUserId(memberRespVo.getId());
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (ShoppingCartConstant.TEMP_USER_COOKIE_NAME.equals(name)) {
                    //携带了指定cookie存入userInfoTo
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setTempUser(true);
                }
            }
        }
        //如果没有临时用户就创建一个
        if (StringUtils.isEmpty(userInfoTo.getUserKey())) {
            String uuid = UUID.randomUUID().toString();
            userInfoTo.setUserKey(uuid);
        }
        //目标方法执行之前
        threadLocal.set(userInfoTo);
        return true;
    }

    /**
     * 业务执行之后
     * {@link AsyncHandlerInterceptor}.
     * <p>The default implementation is empty.
     *
     * @param request      current HTTP request
     * @param response     current HTTP response
     * @param handler      the handler (or {@link }) that started asynchronous
     *                     execution, for type and/or instance examination
     * @param modelAndView the {@code ModelAndView} that the handler returned
     *                     (can also be {@code null})
     * @throws Exception in case of errors
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        //如果是新建的临时用户再放入cookie
        if (!userInfoTo.getTempUser()){
            Cookie cookie = new Cookie(ShoppingCartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            //设置作用域
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(ShoppingCartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }
}
