package gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import gulimall.product.entity.AttrAttrgroupRelationEntity;
import gulimall.product.entity.AttrEntity;
import gulimall.product.service.AttrAttrgroupRelationService;
import gulimall.product.service.AttrService;
import gulimall.product.service.CategoryService;
import gulimall.product.vo.AttrGroupRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import gulimall.product.entity.AttrGroupEntity;
import gulimall.product.service.AttrGroupService;
import gulimall.common.utils.PageUtils;
import gulimall.common.utils.R;


/**
 * 属性分组
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:33:30
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService relationService;

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params, @PathVariable("catelogId") Long catelogId) {
        PageUtils page = attrGroupService.queryPage(params, catelogId);

        return R.ok().put("page", page);
    }

    /**
     * 添加分组与属性关联
     *
     * @return
     */
    @PostMapping("/attr/relation")
    public R saveAttrRelation(@RequestBody List<AttrGroupRelationVo> attrGroupRelationVos) {
        relationService.saveAttrBatch(attrGroupRelationVos);

        return R.ok();
    }

    /**
     * 获取分组与属性关联的属性
     *
     * @return
     */
    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrNoRelation(@PathVariable("attrgroupId") Long groupId) {
        List<AttrEntity> attrEntities = attrService.getAttrRelation(groupId);

        return R.ok().put("data", attrEntities);
    }

    /**
     * 获取分组没有与属性关联的属性
     *
     * @return
     */
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@PathVariable("attrgroupId") Long attrgroupId,
                            @RequestParam Map<String, Object> params){
        PageUtils page = attrService.getNoAttrRelation(params,attrgroupId);
        return R.ok().put("page",page);
    }

    /**
     * 删除分组与属性的关系
     *
     * @return
     */
    @RequestMapping("/attr/relation/delete")
    public R DeleteAttrRelation(@RequestBody AttrGroupRelationVo[] groupRelationVo) {
        attrService.DeleteAttrRelation(groupRelationVo);

        return R.ok();
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        Long catelogId = attrGroup.getCatelogId();
        //查出完整路径【父/子/孙】
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        attrGroup.setCatelogPath(catelogPath);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
