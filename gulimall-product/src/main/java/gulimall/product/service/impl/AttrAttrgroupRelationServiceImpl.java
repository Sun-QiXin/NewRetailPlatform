package gulimall.product.service.impl;

import gulimall.product.vo.AttrGroupRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import gulimall.common.utils.PageUtils;
import gulimall.common.utils.Query;

import gulimall.product.dao.AttrAttrgroupRelationDao;
import gulimall.product.entity.AttrAttrgroupRelationEntity;
import gulimall.product.service.AttrAttrgroupRelationService;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 一次性批量删除
     *
     * @param relationEntities
     */
    @Override
    public void deleteBatchRelation(List<AttrAttrgroupRelationEntity> relationEntities) {
        this.baseMapper.deleteBatchRelation(relationEntities);
    }

    /**
     * 添加分组与属性关联（可批量）
     *
     * @param attrGroupRelationVos
     */
    @Override
    public void saveAttrBatch(List<AttrGroupRelationVo> attrGroupRelationVos) {
        List<AttrAttrgroupRelationEntity> relationEntities = attrGroupRelationVos.stream().map(attrGroupRelationVo -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(attrGroupRelationVo.getAttrId());
            relationEntity.setAttrGroupId(attrGroupRelationVo.getAttrGroupId());
            return relationEntity;
        }).collect(Collectors.toList());

        this.saveBatch(relationEntities);
    }

}