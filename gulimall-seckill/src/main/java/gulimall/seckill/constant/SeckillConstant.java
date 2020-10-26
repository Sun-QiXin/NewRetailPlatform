package gulimall.seckill.constant;

/**
 * 秒杀服务所用常量
 *
 * @author 孙启新
 * <br>FileName: SeckillConstant
 * <br>Date: 2020/08/17 10:38:37
 */
public class SeckillConstant {
    /**
     * 秒杀活动保存进redis的键前缀
     */
    public static final String SESSIONS_PREFIX = "seckill:sessions:";

    /**
     * 秒杀商品保存进redis的键前缀
     */
    public static final String SKU_INFO_PREFIX = "seckill:skus:";
    /**
     * 商品的库存信号量（前缀+商品随机码）
     */
    public static final String SKU_STOCK_SEMAPHORE = "seckill:stock:";
    /**
     * 上架时的分布式锁名称
     */
    public static final String UPLOAD_LOCK_NAME = "seckill-upload-lock";
}
