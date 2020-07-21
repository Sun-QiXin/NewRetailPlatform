package gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gulimall.common.utils.PageUtils;
import gulimall.product.entity.SkuInfoEntity;

import java.util.Map;

/**
 * sku信息
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:33:30
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    /**
     * sku的基本信息；pms_sku_info
     * @param skuInfoEntity
     */
    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    /**
     * 根据传来的参数进行查询
     * @param params
     * @return
     */
    PageUtils queryPageByCondition(Map<String, Object> params);
}

