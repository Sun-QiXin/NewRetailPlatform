package gulimall.product.feign;

import gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 孙启新
 * <br>FileName: SeckillfeignService
 * <br>Date: 2020/08/18 16:47:14
 */
@Component
@FeignClient("gulimall-seckill")
public interface SeckillFeignService {
    /**
     * 根据skuId获取该商品是否有秒杀活动
     * @param skuId skuId
     * @return R
     */
    @GetMapping("/sku/seckill/{skuId}")
    R getSkuSeckillInfoById(@PathVariable("skuId") Long skuId);
}
