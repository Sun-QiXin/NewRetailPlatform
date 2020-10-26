package gulimall.coupon.service.impl;

import gulimall.coupon.entity.SeckillSkuRelationEntity;
import gulimall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import gulimall.common.utils.PageUtils;
import gulimall.common.utils.Query;

import gulimall.coupon.dao.SeckillSessionDao;
import gulimall.coupon.entity.SeckillSessionEntity;
import gulimall.coupon.service.SeckillSessionService;
import org.springframework.util.StringUtils;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {
    @Autowired
    private SeckillSkuRelationService seckillSkuRelationService;

    /**
     * 根据条件进行分页查询
     *
     * @param params 参数
     * @return PageUtils
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SeckillSessionEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.eq("name", key).or().eq("status", key).or().eq("id", key);
        }

        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }

    /**
     * 获取最近三天内的秒杀活动以及每个活动需要上架的商品
     *
     * @return 活动集合
     */
    @Override
    public List<SeckillSessionEntity> getLatestThreeDaysSessions() {
        //1、获取最近三天的起止时间
        String startTime = latestThreeStartTime();
        String endTime = latestThreeEndTime();

        //2、查询出最近三天的活动
        List<SeckillSessionEntity> sessionEntities = this.list(new QueryWrapper<SeckillSessionEntity>().between("start_time", startTime, endTime));

        if (sessionEntities != null && sessionEntities.size() > 0) {
            //3、根据活动查询出需要上架的商品
            return sessionEntities.stream().peek(session -> {
                List<SeckillSkuRelationEntity> relationEntities = seckillSkuRelationService.list(new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id", session.getId()));
                session.setRelationEntities(relationEntities);
            }).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 返回最近三天的起始时间
     *
     * @return 起始时间
     */
    private String latestThreeStartTime() {
        LocalDate localDate = LocalDate.now();
        LocalTime min = LocalTime.MIN;
        return LocalDateTime.of(localDate, min).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 返回最近三天的结束时间
     *
     * @return 结束时间
     */
    private String latestThreeEndTime() {
        LocalDate localDate = LocalDate.now().plusDays(2);
        LocalTime max = LocalTime.MAX;
        return LocalDateTime.of(localDate, max).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}