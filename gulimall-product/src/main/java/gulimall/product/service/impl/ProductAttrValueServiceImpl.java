package gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import gulimall.common.utils.PageUtils;
import gulimall.common.utils.Query;

import gulimall.product.dao.ProductAttrValueDao;
import gulimall.product.entity.ProductAttrValueEntity;
import gulimall.product.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存spu的规格参数;pms_product_attr_value
     *
     * @param attrValueEntities
     */
    @Override
    public void saveProductAttr(List<ProductAttrValueEntity> attrValueEntities) {
        this.saveBatch(attrValueEntities);
    }

    /**
     * 根据spuId查询规格参数
     *
     * @param spuId
     * @return
     */
    @Override
    public List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId) {
        List<ProductAttrValueEntity> attrValueEntitys = this.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        return attrValueEntitys;
    }

    /**
     * 批量修改规格参数
     *
     * @param spuId
     * @param attrValueEntities
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> attrValueEntities) {
        //1、删除spuId之前对应的属性
        this.remove(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));

        //2、重新插入新的数据
        List<ProductAttrValueEntity> valueEntityList = attrValueEntities.stream().map(attrValueEntity -> {
            attrValueEntity.setSpuId(spuId);
            return attrValueEntity;
        }).collect(Collectors.toList());

        this.saveBatch(valueEntityList);
    }
}