package gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gulimall.common.utils.PageUtils;
import gulimall.product.entity.SpuInfoDescEntity;
import gulimall.product.entity.SpuInfoEntity;
import gulimall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:33:30
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    /**
     * 多表保存
     * @param spuSaveVo
     */
    void saveSpuInfo(SpuSaveVo spuSaveVo);

    /**
     * 保存spu基本信息
     * @param spuInfoEntity
     */
    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

    /**
     * 按照多条件进行查询
     * @param params
     * @return
     */
    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * 商品上架功能（将数据查询出来保存到es）
     * @param spuId spu的id
     */
    void spuUp(Long spuId);
}

