package gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gulimall.common.to.SkuReductionTo;
import gulimall.common.utils.PageUtils;
import gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 09:59:22
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * sku的优惠、满减等信息；gulimall_sms->sms_sku_ladder
     * @param reductionTo
     */
    void saveSkuReduction(SkuReductionTo reductionTo);
}

