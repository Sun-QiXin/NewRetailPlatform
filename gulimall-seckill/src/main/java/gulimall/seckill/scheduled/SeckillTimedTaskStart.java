package gulimall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 秒杀服务的定时任务
 * <li>定时任务
 *     <ul>
 *         <li>1、@EnabLeScheduling 开启定时任务功能</li>
 *         <li>2、@Scheduled 开启一个定时任务</li>
 *         <li>3、自动配置类 TaskSchedulingAutoConfiguration</li>
 *     </ul>
 * </li>
 * <li>异步任务
 *     <ul>
 *         <li>1、@EnableAsync 开启异步任务功能</li>
 *         <li>2、@Async 给希望异步执行的方法上标注</li>
 *         <li>3、自动配置类 TaskExecutionAutoConfiguration</li>
 *     </ul>
 * </li>
 *
 * @author 孙启新
 * <br>FileName: SeckillTimedTasks
 * <br>Date: 2020/08/16 16:25:31
 */
@Component
@Slf4j
public class SeckillTimedTaskStart {
    @Autowired
    private RedissonClient redissonClient;
    /**
     * <br>1、Spring中cron表达式由6位组成，不允许第7位的年
     * <br>2、在周几的位置，1-7代表周一到周日或者MON-SUN（quartz中的cron表达式是0-6）
     * <li>3、定时任务不应该阻塞。这里默认是阻塞的，只有上一个执行完下一个才执行
     *     <ul>
     *         <li>1）、可以让业务运行以异步的方式，自己提交到线程池</li>
     *         <li>2）、支持定时任务线程池;设置TaskSchedulingProperties配置
     *          spring.task.scheduling.pool.size=5（有时因为版本问题可能不好用）
     *         </li>
     *         <li>3) 、让定时任务异步执行(推荐)</li>
     *     </ul>
     * </li>
     */
    @Scheduled(cron = "* * * * 1 ?")
    @Async
    public void test() throws InterruptedException {
        log.info("hello........");
        Thread.sleep(3000);
    }
}
