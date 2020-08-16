package gulimall.coupon.service.impl;

import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import gulimall.common.utils.PageUtils;
import gulimall.common.utils.Query;

import gulimall.coupon.dao.SeckillSkuRelationDao;
import gulimall.coupon.entity.SeckillSkuRelationEntity;
import gulimall.coupon.service.SeckillSkuRelationService;
import org.springframework.util.StringUtils;


/**
 * @author x3626
 */
@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {

    /**
     * 根据条件进行分页查询
     *
     * @param params 参数
     * @return PageUtils
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SeckillSkuRelationEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.eq("promotion_id", key).or().eq("sku_id", key).or().eq("id", key).or().eq("seckill_price", key);
        }

        String promotionSessionId = (String) params.get("promotionSessionId");
        if (!StringUtils.isEmpty(promotionSessionId)) {
            wrapper.eq("promotion_session_id", promotionSessionId);
        }

        IPage<SeckillSkuRelationEntity> page = this.page(
                new Query<SeckillSkuRelationEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }
}