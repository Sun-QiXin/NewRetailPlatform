package gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gulimall.common.utils.PageUtils;
import gulimall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:33:30
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存spu的规格参数;pms_product_attr_value
     * @param attrValueEntities
     */
    void saveProductAttr(List<ProductAttrValueEntity> attrValueEntities);
}

