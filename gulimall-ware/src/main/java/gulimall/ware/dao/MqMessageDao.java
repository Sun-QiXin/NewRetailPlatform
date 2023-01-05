package gulimall.ware.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import gulimall.ware.entity.MqMessageEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * (MqMessage)表数据库访问层
 *
 * @author @孙启新
 * @since 2020-11-13 17:05:08
 */
@Mapper
public interface MqMessageDao extends BaseMapper<MqMessageEntity> {

}