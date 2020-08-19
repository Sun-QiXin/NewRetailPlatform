package gulimall.order.feign;


import gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import java.math.BigDecimal;
import java.util.List;

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

    /**
     * 根据skuId查询spu信息
     * @param skuId skuId
     * @return spu信息
     */
    @GetMapping("/product/spuinfo/getSpuInfoBySkuId/{skuId}")
    R getSpuInfoBySkuId(@PathVariable("skuId") Long skuId);

    /**
     * 根据品牌id获取品牌信息
     * @param brandId 品牌id
     * @return 品牌信息
     */
    @RequestMapping("/product/brand/info/{brandId}")
    R getBrandInfoById(@PathVariable("brandId") Long brandId);


    /**
     * 根据skuId获取销售属性值
     * @param skuId skuId
     * @return List<String>
     */
    @GetMapping("/product/skusaleattrvalue/stringList/{skuId}")
    List<String> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);
}
