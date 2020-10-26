package gulimall.search.feign;

import gulimall.common.utils.R;
import gulimall.search.feign.fallback.ProductFeignServiceFallbackHandleImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 孙启新
 * <br>FileName: ProductFeignService
 * <br>Date: 2020/07/13 12:02:08
 */
@Component
@FeignClient(value = "gulimall-product", fallback = ProductFeignServiceFallbackHandleImpl.class)
public interface ProductFeignService {
    /**
     * 获取属性信息
     *
     * @param attrId 属性id
     * @return 属性的详细信息
     */
    @GetMapping("/product/attr/info/{attrId}")
    R getAttrInfo(@PathVariable("attrId") Long attrId);
}
