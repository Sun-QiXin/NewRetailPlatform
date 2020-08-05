package gulimall.shoppingcart.config;

import gulimall.shoppingcart.interceptor.ShoppingCartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * springWEB自定义配置
 *
 * @author 孙启新
 * <br>FileName: GuLiMallWebConfig
 * <br>Date: 2020/08/05 14:04:11
 */
@Configuration
public class GuLiMallWebConfig implements WebMvcConfigurer {
    /**
     * 添加自定义的拦截器
     * @param registry 注册
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ShoppingCartInterceptor()).addPathPatterns("/**");
    }
}
