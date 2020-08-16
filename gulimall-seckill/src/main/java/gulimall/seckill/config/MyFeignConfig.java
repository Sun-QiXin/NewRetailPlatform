package gulimall.seckill.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 配置feign的请求拦截器，如果不配置，在远程调用时会丢失请求头
 *
 * @author 孙启新
 * <br>FileName: MyFeignConfig
 * <br>Date: 2020/08/09 13:07:23
 */
@Configuration
public class MyFeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                //1、getRequestAttributes拿到刚进来的这个请求
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (requestAttributes != null) {
                    HttpServletRequest request = requestAttributes.getRequest();
                    String cookie = request.getHeader("Cookie");
                    //同步新请求与之前请求的cookie
                    requestTemplate.header("Cookie", cookie);
                }
            }
        };
    }
}
