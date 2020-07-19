package gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gulimall.common.utils.PageUtils;
import gulimall.product.entity.AttrEntity;
import gulimall.product.vo.AttrGroupRelationVo;
import gulimall.product.vo.AttrRespVo;
import gulimall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:33:31
 */
public interface AttrService extends IService<AttrEntity> {

    /**
     * 保存基本信息以及关联信息
     * @param attr
     */
    void saveAttr(AttrVo attr);

    /**
     * 分页根据条件查询列表
     * @param params
     * @param catelogId
     * @param type
     * @return
     */
    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    /**
     * 根据Id查询出详细信息
     * @param attrId
     * @return
     */
    AttrRespVo getAttrInfo(Long attrId);

    /**
     * 修改规格参数
     * @param attr
     */
    void updateAttr(AttrVo attr);

    /**
     * 根据分组id获取分组与属性的关系
     * @param groupId
     * @return
     */
    List<AttrEntity> getAttrRelation(Long groupId);

    /**
     * 根据属性id和分组id删除分组与属性的关系
     * @param groupRelationVo
     */
    void DeleteAttrRelation(AttrGroupRelationVo[] groupRelationVo);

    /**
     * 根据id获取分组没有与属性关联的属性
     * @param params
     * @param groupId
     * @return
     */
    PageUtils getNoAttrRelation(Map<String, Object> params, Long attrgroupId);
}

