package gulimall.product.feign.fallback;

import gulimall.common.exception.BizCodeEnume;
import gulimall.common.utils.R;
import gulimall.product.feign.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;




/**
 * 远程调用失败的降级处理方法
 * @author 孙启新
 * <br>FileName: SeckillFeignServiceFallbackHandleImpl
 * <br>Date: 2020/08/20 16:35:25
 */
@Component
@Slf4j
public class SeckillFeignServiceFallbackHandleImpl implements SeckillFeignService {
    /**
     * 根据skuId获取该商品是否有秒杀活动
     *
     * @param skuId skuId
     * @return R
     */
    @Override
    public R getSkuSeckillInfoById(Long skuId) {
        log.error("--------------------------调用远程服务方法 getSkuSeckillInfoById 失败,返回降级信息-------------------------");
        return R.error(BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getCode(), BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getMsg());
    }
}
