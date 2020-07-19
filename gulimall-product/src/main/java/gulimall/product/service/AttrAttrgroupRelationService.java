package gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gulimall.common.utils.PageUtils;
import gulimall.product.entity.AttrAttrgroupRelationEntity;
import gulimall.product.vo.AttrGroupRelationVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:33:31
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 一次性批量删除
     * @param relationEntities
     */
    void deleteBatchRelation(List<AttrAttrgroupRelationEntity> relationEntities);

    /**
     * 添加分组与属性关联（可批量）
     * @param attrGroupRelationVos
     */
    void saveAttrBatch(List<AttrGroupRelationVo> attrGroupRelationVos);
}

