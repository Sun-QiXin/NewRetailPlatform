package gulimall.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 视图映射
 * @author 孙启新
 * <br>FileName: GulimallWebConfig
 * <br>Date: 2020/08/01 14:16:47
 */
@Configuration
public class GulimallWebConfig implements WebMvcConfigurer {
    /**
     * 视图映射，对于只需要跳转页面的请求，就可以这样来写
     * @param registry 注册器
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/register.html").setViewName("register");
    }
}
