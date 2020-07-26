package gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gulimall.common.utils.PageUtils;
import gulimall.product.entity.CategoryEntity;
import gulimall.product.vo.catagory2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:33:31
 */
public interface CategoryService extends IService<CategoryEntity> {

    /**
     * 分页查询
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询所有分类以及子分类，以树形列表展示
     * @return
     */
    List<CategoryEntity> listWithTree();

    /**
     * 删除时需要查看是否有其他地方使用它
     * @param asList
     */
    void removeMenuByIds(List<Long> asList);

    /**
     * 根据id查询整个分类的完整路径【父/子/孙】
     * @param catelogId
     * @return
     */
    Long[] findCatelogPath(Long catelogId);

    /**
     * 级联更新所有数据
     * @param category
     */
    void updateCascade(CategoryEntity category);

    /**
     * 查出所有的1级分类
     * @return
     */
    List<CategoryEntity> getLeve1Categorys();

    /**
     * 获取2级3级分类的json
     * @return
     */
    Map<String, List<catagory2Vo>> getCatalogJson();
}

