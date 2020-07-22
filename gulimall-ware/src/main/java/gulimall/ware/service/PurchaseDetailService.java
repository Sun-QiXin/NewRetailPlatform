package gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gulimall.common.utils.PageUtils;
import gulimall.ware.entity.PurchaseDetailEntity;

import java.util.Map;

/**
 *
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:32:25
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    /**
     * 分页查询并且带条件的模糊查询
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

