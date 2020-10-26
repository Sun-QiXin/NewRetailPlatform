package gulimall.product.dao;

import gulimall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import gulimall.product.vo.SkuItemVo;
import gulimall.product.vo.SpuItemBaseGroupAttrsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:33:30
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    /**
     * 查出当前spu对应的所有属性的分组信息以及当前分组下的所有属性对应的值
     * @param catalogId
     * @param spuId
     */
    List<SpuItemBaseGroupAttrsVo> getAttrGroupWithAttrsBySpuId(@Param("catalogId") Long catalogId, @Param("spuId") Long spuId);
}
