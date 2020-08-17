package gulimall.seckill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * 开启异步任务所需环境
 * @author 孙启新
 * <br>FileName: MyScheduledConfig
 * <br>Date: 2020/08/17 09:00:11
 */
@Configuration
@EnableScheduling
@EnableAsync
public class MyScheduledConfig {

}
