package gulimall.shoppingcart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 线程池的参数配置
 * @author 孙启新
 * <br>FileName: MyThreadConfigProperties
 * <br>Date: 2020/11/01 10:48:53
 */
@Component
@ConfigurationProperties(prefix = "gulimall.thread")
@Data
public class MyThreadConfigProperties {
    /**
     * 核心线程数
     */
    private Integer corePoolSize;
    /**
     * 最大线程数
     */
    private Integer maximumPoolSize;
    /**
     * 休眠时长
     */
    private Integer keepAliveTime;
}
