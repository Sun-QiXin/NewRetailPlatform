package gulimall.order.feign;

import gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author 孙启新
 * <br>FileName: WareFeignService
 * <br>Date: 2020/08/09 16:08:01
 */
@Component
@FeignClient("gulimall-ware")
public interface WareFeignService {

    /**
     * 根据skuIds集合批量查询是否有库存
     *
     * @param skuIds id集合
     * @return R对象
     */
    @PostMapping("/ware/waresku/hasStock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);
}
