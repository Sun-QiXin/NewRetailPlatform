package gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gulimall.common.utils.PageUtils;
import gulimall.product.entity.SpuCommentEntity;

import java.util.Map;

/**
 * 商品评价
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:33:30
 */
public interface SpuCommentService extends IService<SpuCommentEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

