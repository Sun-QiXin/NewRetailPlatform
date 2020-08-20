package gulimall.seckill.feign.fallback;

import gulimall.common.exception.BizCodeEnume;
import gulimall.common.utils.R;
import gulimall.seckill.feign.ProductFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 远程调用失败的降级处理方法
 *
 * @author 孙启新
 * <br>FileName: ProductFeignServiceFallbackHandleImpl
 * <br>Date: 2020/08/20 16:35:25
 */
@Component
@Slf4j
public class ProductFeignServiceFallbackHandleImpl implements ProductFeignService {

    /**
     * 根据skuId查询商品信息
     *
     * @param skuId 商品skuId
     * @return r对象
     */
    @Override
    public R getSkuInfoById(Long skuId) {
        log.error("--------------------------调用远程服务方法 getSkuInfoById 失败,返回降级信息-------------------------");
        return R.error(BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getCode(), BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getMsg());
    }
}
