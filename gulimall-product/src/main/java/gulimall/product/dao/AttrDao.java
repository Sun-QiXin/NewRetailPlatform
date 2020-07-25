package gulimall.product.dao;

import gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:33:31
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    /**
     * 取出当前id集合中可以被检索的id集合
     * @param attrValueIds
     * @return
     */
    List<Long> selectSearchAttrIds(@Param("attrValueIds") List<Long> attrValueIds);
}
