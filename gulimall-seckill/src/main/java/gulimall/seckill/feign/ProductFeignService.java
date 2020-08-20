package gulimall.seckill.feign;


import gulimall.common.utils.R;
import gulimall.seckill.feign.fallback.ProductFeignServiceFallbackHandleImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 孙启新
 * <br>FileName: ProductFeignService
 * <br>Date: 2020/08/09 11:12:39
 */
@Component
@FeignClient(value = "gulimall-product", fallback = ProductFeignServiceFallbackHandleImpl.class)
public interface ProductFeignService {

    /**
     * 根据skuId查询商品信息
     *
     * @param skuId 商品skuId
     * @return r对象
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfoById(@PathVariable("skuId") Long skuId);
}
