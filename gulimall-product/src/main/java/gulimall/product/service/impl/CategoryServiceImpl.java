package gulimall.product.service.impl;

import gulimall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import gulimall.common.utils.PageUtils;
import gulimall.common.utils.Query;

import gulimall.product.dao.CategoryDao;
import gulimall.product.entity.CategoryEntity;
import gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author x3626
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 分页查询
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询所有分类以及子分类，以树形列表展示
     *
     * @return
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        //1、查询出所有分类
        List<CategoryEntity> categoryEntityList = baseMapper.selectList(null);

        //2、组装成父子的树形结构
        //2.1)找到所有的一级分类
        List<CategoryEntity> level1Menus = categoryEntityList.stream().filter(categoryEntity -> categoryEntity.getParentCid() == 0
        ).map(menu -> {
            //2.2)找到所有的一级分类的子菜单
            menu.setChildren(getChildren(menu, categoryEntityList));
            return menu;
        }).sorted((menu1, menu2) -> {
            //2.3)找到所有的子菜单并排序
            return menu1.getSort() - menu2.getSort();
        }).collect(Collectors.toList());
        return level1Menus;
    }

    /**
     * 递归查找每个主菜单的子菜单
     *
     * @param root 当前主菜单
     * @param all  所有菜单
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid().equals(root.getCatId());
        }).map(categoryEntity -> {
            //再次查找当前菜单的子菜单
            categoryEntity.setChildren(getChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            //排序
            return menu1.getSort() - menu2.getSort();
        }).collect(Collectors.toList());
        return children;
    }

    /**
     * 删除时需要查看是否有其他地方使用它
     *
     * @param asList
     */
    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 删除时需要查看是否有其他地方使用它
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * 根据id查询整个分类的完整路径【父/子/孙】
     *
     * @param catelogId
     * @return
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        //调用方法
        paths = findParentPath(catelogId, paths);
        Collections.reverse(paths);
        return paths.toArray(new Long[paths.size()]);
    }
    /**
     * @param catelogId
     * @param paths
     * @return
     */
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //1、收集当前节点id
        paths.add(catelogId);
        CategoryEntity categoryEntity = this.getById(catelogId);
        if (categoryEntity.getParentCid() != 0) {
            findParentPath(categoryEntity.getParentCid(),paths);
        }
        return paths;
    }

    /**
     * 级联更新所有关联数据
     *
     * @param category
     */
    @Transactional(rollbackFor=Exception.class)
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
    }
}