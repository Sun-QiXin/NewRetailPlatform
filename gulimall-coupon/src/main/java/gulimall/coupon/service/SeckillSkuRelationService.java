package gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gulimall.common.utils.PageUtils;
import gulimall.coupon.entity.SeckillSkuRelationEntity;

import java.util.Map;

/**
 * 秒杀活动商品关联
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 09:59:22
 */
public interface SeckillSkuRelationService extends IService<SeckillSkuRelationEntity> {

    /**
     * 根据条件进行分页查询
     * @param params 参数
     * @return  PageUtils
     */
    PageUtils queryPage(Map<String, Object> params);
}

