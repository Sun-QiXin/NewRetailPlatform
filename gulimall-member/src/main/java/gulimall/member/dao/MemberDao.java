package gulimall.member.dao;

import gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:29:48
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
