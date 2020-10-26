package gulimall.seckill.config;


import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * RabbitMQ全局配置
 *
 * @author 孙启新
 * <br>FileName: MyRabbitMqGlobalConfig
 * <br>Date: 2020/08/07 14:41:44
 */
@Configuration
public class MyRabbitMqJsonConfig {
    /**
     * 将默认序列化机制设置json
     *
     * @return Jackson2JsonMessageConverter
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
