package gulimall.seckill.scheduled;

import gulimall.seckill.constant.SeckillConstant;
import gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 商品上架的定时任务
 * @author 孙启新
 * <br>FileName: SeckillSkuScheduled
 * <br>Date: 2020/08/17 08:58:33
 */
@Service
@Slf4j
public class SeckillSkuScheduled {
    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 上架最近三天的秒杀商品
     */
    @Scheduled(cron = "0/30 * * * * ?")
    @Async
    public void uploadLatestThreeDaysSeckillSku(){
        log.info("开始上架商品。。。。。。");
        //防止以后在多机器下部署该服务导致多个定时任务同时执行，这里需要加入分布式锁
        RLock lock = redissonClient.getLock(SeckillConstant.UPLOAD_LOCK_NAME);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            seckillService.uploadLatestThreeDaysSeckillSku();
        } finally {
            //解锁
            lock.unlock();
        }
    }
}
