package gulimall.order.feign.fallback;


import gulimall.common.exception.BizCodeEnume;
import gulimall.common.utils.R;

import gulimall.order.feign.ProductFeignService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

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
     * 根据skuId查询商品价格
     *
     * @param skuId 商品skuId
     * @return r对象
     */
    @Override
    public BigDecimal currentPrice(Long skuId) {
        log.error("--------------------------调用远程服务方法 currentPrice 失败,返回降级信息-------------------------");
        return null;
    }

    /**
     * 根据skuId查询spu信息
     *
     * @param skuId skuId
     * @return spu信息
     */
    @Override
    public R getSpuInfoBySkuId(Long skuId) {
        log.error("--------------------------调用远程服务方法 getSpuInfoBySkuId 失败,返回降级信息-------------------------");
        return R.error(BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getCode(), BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getMsg());
    }

    /**
     * 根据品牌id获取品牌信息
     *
     * @param brandId 品牌id
     * @return 品牌信息
     */
    @Override
    public R getBrandInfoById(Long brandId) {
        log.error("--------------------------调用远程服务方法 getBrandInfoById 失败,返回降级信息-------------------------");
        return R.error(BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getCode(), BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getMsg());
    }

    /**
     * 根据skuId获取销售属性值
     *
     * @param skuId skuId
     * @return List<String>
     */
    @Override
    public List<String> getSkuSaleAttrValues(Long skuId) {
        log.error("--------------------------调用远程服务方法 getSkuSaleAttrValues 失败,返回降级信息-------------------------");
        return null;
    }
}
