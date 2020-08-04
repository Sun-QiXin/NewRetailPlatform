package gulimall.product.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @author 孙启新
 * <br>FileName: MySpringSessionConfig
 * <br>Date: 2020/08/03 15:52:30
 */
@Configuration
@EnableRedisHttpSession //使用redis作为session存储
public class MySpringSessionConfig {

    /**
     * 自定义保存cookie
     * @return cookieSerializer
     */
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setCookieName("GULISESSION");
        cookieSerializer.setCookiePath("/");
        //设置存储cookie的域(保证在各个子域名下都可以访问)
        cookieSerializer.setDomainName("gulimall.com");
        return cookieSerializer;
    }

    /**
     * 指定序列化到redis是用什么方式
     * @return GenericFastJsonRedisSerializer
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
