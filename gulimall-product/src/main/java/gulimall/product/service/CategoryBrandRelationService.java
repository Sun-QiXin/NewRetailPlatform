package gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gulimall.common.utils.PageUtils;
import gulimall.product.entity.BrandEntity;
import gulimall.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:33:31
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存详细信息
     * @param categoryBrandRelation
     */
    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    /**
     * 同步更新品牌数据
     * @param brandId
     * @param name
     */
    void updateBrand(Long brandId, String name);

    /**
     * 同步更新分类数据
     * @param catId
     * @param name
     */
    void updateCategory(Long catId, String name);

    /**
     * 获取当前分类下的所有品牌
     * @param catId
     * @return
     */
    List<BrandEntity> getBrandsByCatId(Long catId);
}

