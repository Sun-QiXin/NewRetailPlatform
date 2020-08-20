package gulimall.shoppingcart.feign;

import gulimall.common.utils.R;
import gulimall.shoppingcart.feign.fallback.ProductFeignServiceFallbackHandleImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author 孙启新
 * <br>FileName: ProductFeignService
 * <br>Date: 2020/08/05 15:26:26
 */
@FeignClient(value = "gulimall-product", fallback = ProductFeignServiceFallbackHandleImpl.class)
@Component
public interface ProductFeignService {

    /**
     * 根据skuId查询商品信息
     *
     * @param skuId 商品skuId
     * @return r对象
     */
    @GetMapping("/product/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);

    /**
     * 根据skuId获取销售属性值
     *
     * @param skuId skuId
     * @return List<String>
     */
    @GetMapping("/product/skusaleattrvalue/stringList/{skuId}")
    List<String> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);
}
