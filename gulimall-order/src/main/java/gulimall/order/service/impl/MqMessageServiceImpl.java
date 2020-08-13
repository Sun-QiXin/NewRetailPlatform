package gulimall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import gulimall.order.dao.MqMessageDao;
import gulimall.order.entity.MqMessageEntity;
import gulimall.order.service.MqMessageService;
import org.springframework.stereotype.Service;

/**
 * (MqMessage)表服务实现类
 *
 * @author @孙启新
 * @since 2020-08-13 13:57:24
 */
@Service("mqMessageService")
public class MqMessageServiceImpl extends ServiceImpl<MqMessageDao, MqMessageEntity> implements MqMessageService {

}