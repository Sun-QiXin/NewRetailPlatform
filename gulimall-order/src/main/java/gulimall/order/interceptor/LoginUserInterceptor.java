package gulimall.order.interceptor;

import gulimall.common.constant.AuthServerConstant;
import gulimall.common.constant.ShoppingCartConstant;
import gulimall.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 用户登录的拦截器
 *
 * @author 孙启新
 * <br>FileName: LoginUserInterceptor
 * <br>Date: 2020/08/08 15:33:27
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {
    /**
     * 用来共享数据
     */
    private static ThreadLocal<MemberRespVo> threadLocal = new ThreadLocal<>();

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
        MemberRespVo memberRespVo = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (memberRespVo != null) {
            threadLocal.set(memberRespVo);
            //用户已经登录
            return true;
        } else {
            //没有登录就跳转登录
            session.setAttribute("redirectMsg", "请先进行登录再进行订单的相关操作");
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
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
        try {
            //必须回收自定义的ThreadLocal变量，尤其在线程池场景下，线程经常会被复用，如果不清理自定义的 ThreadLocal变量，可能会影响后续业务逻辑和造成内存泄露等问题。尽量在代理中使用try-finally块进行回收
        } finally {
            threadLocal.remove();
        }
    }
}
