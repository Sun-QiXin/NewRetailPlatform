package gulimall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import gulimall.common.utils.R;
import gulimall.seckill.constant.SeckillConstant;
import gulimall.seckill.feign.CouponFeignService;
import gulimall.seckill.feign.ProductFeignService;
import gulimall.seckill.service.SeckillService;
import gulimall.seckill.to.SeckillSkuRedisTo;
import gulimall.seckill.to.SkuInfoTo;
import gulimall.seckill.vo.SeckillSessionsWithSkusVo;

import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author 孙启新
 * <br>FileName: SeckillServiceImpl
 * <br>Date: 2020/08/17 09:08:50
 */
@Service
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 上架最近三天的秒杀商品
     */
    @Override
    public void uploadLatestThreeDaysSeckillSku() {
        //1、远程查询数据库最近三天内需要秒杀的活动以及商品
        R r = couponFeignService.getLatestThreeDaysSessions();
        List<SeckillSessionsWithSkusVo> sessionsWithSkusVos = r.getData(new TypeReference<List<SeckillSessionsWithSkusVo>>() {
        });

        if (sessionsWithSkusVos != null && sessionsWithSkusVos.size() > 0) {
            //2、将秒杀活动以及商品保存到redis
            //2.1、保存秒杀活动信息
            saveSessionInfos(sessionsWithSkusVos);

            //2.2、保存每个活动的商品信息
            saveSessionSkuInfos(sessionsWithSkusVos);
        }
    }

    /**
     * 返回当前时间可以参与的秒杀商品
     *
     * @return 秒杀商品
     */
    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        //1、查看当前时间属于哪个秒杀场次
        long time = System.currentTimeMillis();
        Set<String> keys = redisTemplate.keys(SeckillConstant.SESSIONS_PREFIX + "*");
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                String replace = key.replace(SeckillConstant.SESSIONS_PREFIX, "");
                String[] split = replace.split("-");
                long startTime = Long.parseLong(split[0]);
                long endTime = Long.parseLong(split[1]);
                if (time > startTime && time <= endTime) {
                    //2、获取当前这个秒杀场次的所有商品信息
                    List<String> skuKeys = redisTemplate.opsForList().range(key, -100, 100);
                    BoundHashOperations<String, String, Object> hashOps = redisTemplate.boundHashOps(SeckillConstant.SKU_INFO_PREFIX);
                    if (skuKeys != null) {
                        List<Object> objects = hashOps.multiGet(skuKeys);
                        if (objects != null && objects.size() > 0) {
                            return objects.stream().map(item -> {
                                return JSON.parseObject(item.toString(), SeckillSkuRedisTo.class);
                            }).collect(Collectors.toList());
                        }
                    }
                    break;
                }
            }
        }
        return null;
    }

    /**
     * 根据skuId获取该商品是否有秒杀活动
     *
     * @param skuId skuId
     * @return R
     */
    @Override
    public List<SeckillSkuRedisTo> getSkuSeckillInfoById(Long skuId) {
        List<SeckillSkuRedisTo> skuRedisTos = new ArrayList<>();

        //1、获取redis中所有参与秒杀的key信息
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SeckillConstant.SKU_INFO_PREFIX);
        Set<String> keys = hashOps.keys();
        if (keys != null && keys.size() > 0) {
            //定义正则
            String regx = "\\d-" + skuId;
            for (String key : keys) {
                if (key.matches(regx)) {
                    //正则匹配成功返回数据
                    String json = hashOps.get(key);
                    SeckillSkuRedisTo skuRedisTo = JSON.parseObject(json, SeckillSkuRedisTo.class);
                    //2、处理随机码，只有到商品秒杀时间才能显示随机码
                    if (skuRedisTo != null) {
                        Long startTime = skuRedisTo.getStartTime();
                        long currentTime = System.currentTimeMillis();
                        if (currentTime < startTime) {
                            //秒杀还未开始，将随机码置空
                            skuRedisTo.setRandomCode(null);
                        }
                    }
                    skuRedisTos.add(skuRedisTo);
                }
            }
            return skuRedisTos;
        }
        return null;
    }

    /**
     * 保存秒杀活动信息
     *
     * @param sessionsWithSkusVos 需要保存的信息
     */
    private void saveSessionInfos(List<SeckillSessionsWithSkusVo> sessionsWithSkusVos) {
        sessionsWithSkusVos.forEach(session -> {
            long startTime = session.getStartTime().getTime();
            long entTime = session.getEndTime().getTime();
            String key = SeckillConstant.SESSIONS_PREFIX + startTime + "-" + entTime;
            //将活动信息保存进redis
            Boolean flag = redisTemplate.hasKey(key);
            if (flag != null && !flag) {
                //redis中没有该数据再存
                List<String> skuIds = session.getRelationEntities().stream().map(item -> item.getPromotionSessionId().toString() + "-" + item.getSkuId().toString()).collect(Collectors.toList());
                redisTemplate.opsForList().leftPushAll(key, skuIds);
            }
        });

    }

    /**
     * 保存每个活动的商品信息
     *
     * @param sessionsWithSkusVos 需要保存的信息
     */
    private void saveSessionSkuInfos(List<SeckillSessionsWithSkusVo> sessionsWithSkusVos) {
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(SeckillConstant.SKU_INFO_PREFIX);
        sessionsWithSkusVos.forEach(session -> {
            session.getRelationEntities().forEach(skuRelationInfo -> {
                //先判断有没有存过，存过就不用再存了
                Boolean isSkuInfo = hashOps.hasKey(skuRelationInfo.getPromotionSessionId() + "-" + skuRelationInfo.getSkuId().toString());
                if (isSkuInfo != null && !isSkuInfo) {
                    //1、根据skuId远程查询出当前商品的详细信息保存
                    SeckillSkuRedisTo seckillSkuRedisTo = new SeckillSkuRedisTo();
                    R r = productFeignService.getSkuInfoById(skuRelationInfo.getSkuId());
                    if (r.getCode() == 0) {
                        SkuInfoTo skuInfoTo = r.getData("skuInfo", new TypeReference<SkuInfoTo>() {
                        });
                        seckillSkuRedisTo.setSkuInfoTo(skuInfoTo);
                    }
                    //2、保存该商品的秒杀信息
                    BeanUtils.copyProperties(skuRelationInfo, seckillSkuRedisTo);

                    //3、设置当前商品的秒杀时间信息
                    seckillSkuRedisTo.setStartTime(session.getStartTime().getTime());
                    seckillSkuRedisTo.setEndTime(session.getEndTime().getTime());

                    //4、设置秒杀随机码，防止恶意抢单
                    String token = UUID.randomUUID().toString().replace("_", "");
                    seckillSkuRedisTo.setRandomCode(token);

                    //5、使用当前商品的库存作为分布式的信号量
                    Boolean isSemaphore = redisTemplate.hasKey(SeckillConstant.SKU_STOCK_SEMAPHORE + token);
                    if (isSemaphore != null && !isSemaphore) {
                        RSemaphore semaphore = redissonClient.getSemaphore(SeckillConstant.SKU_STOCK_SEMAPHORE + token);
                        semaphore.trySetPermits(skuRelationInfo.getSeckillCount().intValue());
                    }

                    //6、将所有信息保存进redis
                    hashOps.put(skuRelationInfo.getPromotionSessionId() + "-" + skuRelationInfo.getSkuId().toString(), JSON.toJSONString(seckillSkuRedisTo));
                }
            });
        });
    }
}
