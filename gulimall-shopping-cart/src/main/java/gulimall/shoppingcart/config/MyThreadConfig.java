package gulimall.shoppingcart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置
 *
 * @author 孙启新
 * <br>FileName: MyThreadConfig
 * <br>Date: 2020/08/01 10:42:58
 */
@Configuration
public class MyThreadConfig {
    /**
     * 自定义的线程池
     * @return 线程池
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(MyThreadConfigProperties threadConfigProperties) {
        return new org.apache.tomcat.util.threads.ThreadPoolExecutor(
                threadConfigProperties.getCorePoolSize(),
                threadConfigProperties.getMaximumPoolSize(),
                threadConfigProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
