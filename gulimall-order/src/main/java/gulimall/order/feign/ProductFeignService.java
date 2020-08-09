package gulimall.order.feign;


import gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import java.math.BigDecimal;

/**
 * @author 孙启新
 * <br>FileName: ProductFeignService
 * <br>Date: 2020/08/09 11:12:39
 */
@Component
@FeignClient("gulimall-product")
public interface ProductFeignService {
    /**
     * 根据skuId查询商品价格
     * @param skuId 商品skuId
     * @return r对象
     */
    @GetMapping("/product/skuinfo/price/{skuId}")
    BigDecimal currentPrice(@PathVariable("skuId") Long skuId);
}
