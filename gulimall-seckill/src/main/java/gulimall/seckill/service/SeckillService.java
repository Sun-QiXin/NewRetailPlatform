package gulimall.seckill.service;

/**
 * @author 孙启新
 * <br>FileName: SeckillService
 * <br>Date: 2020/08/17 09:08:23
 */
public interface SeckillService {
    /**
     * 上架最近三天的秒杀商品
     */
    void uploadLatestThreeDaysSeckillSku();
}
