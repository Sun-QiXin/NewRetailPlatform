package gulimall.product.feign;

import gulimall.common.utils.R;
import gulimall.common.to.SkuHasStockVo;
import gulimall.product.feign.fallback.WareFeignServiceFallbackHandleImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author 孙启新
 * <br>FileName: WareFeignService
 * <br>Date: 2020/07/25 16:06:20
 */
@Component
@FeignClient(value = "gulimall-ware",fallback = WareFeignServiceFallbackHandleImpl.class)
public interface WareFeignService {
    /**
     * 查询是否有库存
     * @param skuIds
     * @return
     */
    @PostMapping("/ware/waresku/hasStock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);
}
