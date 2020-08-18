package gulimall.seckill.service;

import gulimall.seckill.to.SeckillSkuRedisTo;

import java.util.List;

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

    /**
     * 返回当前时间可以参与的秒杀商品
     * @return 秒杀商品
     */
    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    /**
     * 根据skuId获取该商品是否有秒杀活动
     * @param skuId skuId
     * @return R
     */
    List<SeckillSkuRedisTo> getSkuSeckillInfoById(Long skuId);
}
