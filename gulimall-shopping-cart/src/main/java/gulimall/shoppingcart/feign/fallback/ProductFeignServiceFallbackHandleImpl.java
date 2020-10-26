package gulimall.shoppingcart.feign.fallback;

import gulimall.common.exception.BizCodeEnume;
import gulimall.common.utils.R;
import gulimall.shoppingcart.feign.ProductFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
     * 根据skuId查询商品信息
     *
     * @param skuId 商品skuId
     * @return r对象
     */
    @Override
    public R info(Long skuId) {
        log.error("--------------------------调用远程服务方法 info 失败,返回降级信息-------------------------");
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
