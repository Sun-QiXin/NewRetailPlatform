package gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gulimall.common.utils.PageUtils;
import gulimall.coupon.entity.SeckillSessionEntity;

import java.util.List;
import java.util.Map;

/**
 * 秒杀活动场次
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 09:59:22
 */
public interface SeckillSessionService extends IService<SeckillSessionEntity> {

    /**
     * 根据条件进行分页查询
     * @param params 参数
     * @return PageUtils
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取最近三天内的秒杀活动以及每个活动需要上架的商品
     * @return 活动集合
     */
    List<SeckillSessionEntity> getLatestThreeDaysSessions();
}

