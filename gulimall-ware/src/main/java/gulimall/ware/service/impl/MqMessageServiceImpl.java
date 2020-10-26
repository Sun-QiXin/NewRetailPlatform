package gulimall.ware.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import gulimall.ware.dao.MqMessageDao;
import gulimall.ware.entity.MqMessageEntity;
import gulimall.ware.service.MqMessageService;
import org.springframework.stereotype.Service;

/**
 * (MqMessage)表服务实现类
 *
 * @author @孙启新
 * @since 2020-08-13 17:05:08
 */
@Service("mqMessageService")
public class MqMessageServiceImpl extends ServiceImpl<MqMessageDao, MqMessageEntity> implements MqMessageService {

}