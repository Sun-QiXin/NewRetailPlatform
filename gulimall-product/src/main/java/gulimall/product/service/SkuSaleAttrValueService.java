package gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gulimall.common.utils.PageUtils;
import gulimall.product.entity.SkuSaleAttrValueEntity;
import gulimall.product.vo.SkuItemSaleAttrsVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:33:30
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * spu的销售属性组合
     * @param spuId
     * @return
     */
    List<SkuItemSaleAttrsVo> getSaleAttrsBySpuId(Long spuId);

    /**
     * 根据skuId获取销售属性值
     * @param skuId skuId
     * @return List<String>
     */
    List<String> getSkuSaleAttrValues(Long skuId);
}

