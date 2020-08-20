package gulimall.product.feign;

import gulimall.common.to.es.SkuEsModel;
import gulimall.common.utils.R;
import gulimall.product.feign.fallback.SearchFeignServiceFallbackHandleImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author 孙启新
 * <br>FileName: SearchFeignService
 * <br>Date: 2020/07/25 17:00:32
 */
@Component
@FeignClient(value = "gulimall-search",fallback = SearchFeignServiceFallbackHandleImpl.class)
public interface SearchFeignService {
    /**
     * 上架商品
     *
     * @param skuEsModels
     * @return
     */
    @PostMapping("/search/save/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
