package gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import gulimall.common.enume.OrderStatusEnum;
import gulimall.common.to.SkuHasStockVo;
import gulimall.common.to.mq.OrderTo;
import gulimall.common.to.mq.StockLockedDetailTo;
import gulimall.common.to.mq.StockLockedTo;
import gulimall.common.utils.R;
import gulimall.common.exception.NoStockException;
import gulimall.ware.config.MyRabbitMqConfig;
import gulimall.ware.entity.WareOrderTaskDetailEntity;
import gulimall.ware.entity.WareOrderTaskEntity;
import gulimall.ware.feign.OrderFeignService;
import gulimall.ware.feign.ProductFeignService;
import gulimall.ware.service.WareOrderTaskDetailService;
import gulimall.ware.service.WareOrderTaskService;
import gulimall.ware.vo.OrderItemVo;

import gulimall.ware.vo.OrderVo;
import gulimall.ware.vo.SkuWareHasStockVo;
import gulimall.ware.vo.WareSkuLockVo;
import org.apache.commons.lang.StringUtils;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import gulimall.common.utils.PageUtils;
import gulimall.common.utils.Query;

import gulimall.ware.dao.WareSkuDao;
import gulimall.ware.entity.WareSkuEntity;
import gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author x3626
 */
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    private WareOrderTaskService wareOrderTaskService;

    @Autowired
    private OrderFeignService orderFeignService;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 分页查询，带模糊条件查询
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();

        //封装查询参数
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            wrapper.eq("sku_id", skuId);
        }
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            wrapper.eq("ware_id", wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 将成功采购的进行入库
     *
     * @param skuId
     * @param wareId
     * @param skuNum
     */
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //如果没有这个库存记录那就是新增操作
        List<WareSkuEntity> wareSkuEntities = this.list(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (wareSkuEntities == null || wareSkuEntities.size() == 0) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //跨服务查询商品名字
            //TODO 还有什么办法让事务发生异常不回滚
            try {
                R info = productFeignService.info(skuId);
                if (info.getCode() == 0) {
                    Map<String, Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                    wareSkuEntity.setSkuName((String) skuInfo.get("skuName"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            baseMapper.insert(wareSkuEntity);
        } else {
            baseMapper.addStock(skuId, wareId, skuNum);
        }
    }

    /**
     * 查询是否有库存
     *
     * @param skuIds
     * @return
     */
    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        return skuIds.stream().map(skuId -> {
            SkuHasStockVo hasStockVo = new SkuHasStockVo();
            //查询当前sku的库存量
            Long count = baseMapper.getSkuStock(skuId);
            hasStockVo.setSkuId(skuId);
            hasStockVo.setHasStock(count != null && count > 0);
            return hasStockVo;
        }).collect(Collectors.toList());
    }

    /**
     * 根据传来的数据锁定某件商品的库存
     *
     * @param wareSkuLockVo wareSkuLockVo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void orderLockStock(WareSkuLockVo wareSkuLockVo) {
        //1、保存库存工作单的详情(用于追溯)
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(wareSkuLockVo.getOrderSn());
        wareOrderTaskService.save(wareOrderTaskEntity);

        List<OrderItemVo> orderItemVos = wareSkuLockVo.getOrderItemVos();
        //2、找到每个商品在哪个仓库都有库存
        List<SkuWareHasStockVo> wareHasStockVos = orderItemVos.stream().map(item -> {
            Long skuId = item.getSkuId();
            SkuWareHasStockVo wareHasStockVo = new SkuWareHasStockVo();
            wareHasStockVo.setSkuId(skuId);
            wareHasStockVo.setSkuName(item.getTitle());
            wareHasStockVo.setNum(item.getCount());
            List<Long> wareIds = this.baseMapper.listWareIdHasSkuStock(skuId);
            wareHasStockVo.setWareIds(wareIds);
            return wareHasStockVo;
        }).collect(Collectors.toList());

        //3、锁定库存
        for (SkuWareHasStockVo wareHasStockVo : wareHasStockVos) {
            boolean skuStocked = false;
            Long skuId = wareHasStockVo.getSkuId();
            Integer num = wareHasStockVo.getNum();
            List<Long> wareIds = wareHasStockVo.getWareIds();
            if (wareIds == null || wareIds.size() == 0) {
                //没有仓库有该商品的库存
                throw new NoStockException(wareHasStockVo.getSkuName());
            }
            for (Long wareId : wareIds) {
                //锁定库存
                int count = this.baseMapper.lockSkuStock(skuId, num, wareId);
                if (count == 1) {
                    //锁定成功
                    skuStocked = true;
                    //1) 保存锁定成功的详情
                    WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity();
                    taskDetailEntity.setLockStatus(1);
                    taskDetailEntity.setSkuId(skuId);
                    taskDetailEntity.setSkuName(wareHasStockVo.getSkuName());
                    taskDetailEntity.setWareId(wareId);
                    taskDetailEntity.setTaskId(wareOrderTaskEntity.getId());
                    taskDetailEntity.setSkuNum(num);
                    wareOrderTaskDetailService.save(taskDetailEntity);

                    //2) 向RabbitMQ发送库存锁定成功的消息
                    StockLockedTo stockLockedTo = new StockLockedTo();
                    StockLockedDetailTo stockLockedDetailTo = new StockLockedDetailTo();
                    BeanUtils.copyProperties(taskDetailEntity, stockLockedDetailTo);
                    stockLockedTo.setTaskId(wareOrderTaskEntity.getId());
                    stockLockedTo.setLockedDetailTo(stockLockedDetailTo);
                    rabbitTemplate.convertAndSend(MyRabbitMqConfig.WARE_EVENT_EXCHANGE, MyRabbitMqConfig.WARE_DELAY_KEY, stockLockedTo, new CorrelationData(UUID.randomUUID().toString()));
                    break;
                }
                //当前仓库锁定失败，重试下一个仓库
            }
            if (!skuStocked) {
                //当前遍历的商品从所有仓库扣减库存失败(一个扣减失败导致数据全部回滚抛出异常)
                throw new NoStockException(wareHasStockVo.getSkuName());
            }
        }
    }

    /**
     * 操作数据库解锁库存
     *
     * @param stockLockedTo stockLockedTo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unLockStock(StockLockedTo stockLockedTo) {
        StockLockedDetailTo lockedDetailTo = stockLockedTo.getLockedDetailTo();
        //1、查询数据库看有没有关于这个这个订单的锁定库存信息
        //有：查询订单,如果没有这个订单，然后再判断当前订单的状态（创建，发货。。）
        //没有：库存锁定失败，导致数据回滚，无须解锁
        WareOrderTaskDetailEntity taskDetailEntity = wareOrderTaskDetailService.getById(lockedDetailTo.getId());
        if (taskDetailEntity != null) {
            //2、获取当前订单的信息
            WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskService.getById(stockLockedTo.getTaskId());
            String orderSn = wareOrderTaskEntity.getOrderSn();
            //3、远程查询订单状态
            R r = orderFeignService.getOrder(orderSn);
            OrderVo orderVo = r.getData(new TypeReference<OrderVo>() {
            });
            //订单被取消或者订单不存在，解锁库存
            if (orderVo == null || OrderStatusEnum.CANCLED.getCode().equals(orderVo.getStatus())) {
                //4、查询当前详情工作单的状态，锁定状态（1）的工作单才需要解锁
                WareOrderTaskDetailEntity orderTaskDetailEntity = wareOrderTaskDetailService.getById(lockedDetailTo.getId());
                if (orderTaskDetailEntity.getLockStatus() == 1) {
                    //1、解锁库存
                    this.baseMapper.unLockStock(orderTaskDetailEntity);
                    //2、更新详情工作单的状态，变为2已解锁状态
                    orderTaskDetailEntity.setLockStatus(2);
                    wareOrderTaskDetailService.updateById(orderTaskDetailEntity);
                }
            }
        }
    }

    /**
     * 操作数据库解锁库存
     * <br>防止网络延迟等问题导致库存服务解锁库存时关闭订单被阻塞或没执行完查询一直是待付款状态，库存一直解锁不了
     *
     * @param orderTo orderTo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unLockStock(OrderTo orderTo) {
        //1、远程查询订单状态
        R r = orderFeignService.getOrder(orderTo.getOrderSn());
        OrderVo orderVo = r.getData(new TypeReference<OrderVo>() {
        });
        //订单被取消或者订单不存在，解锁库存
        if (orderVo == null || OrderStatusEnum.CANCLED.getCode().equals(orderVo.getStatus())) {
            //2、根据订单号查询库存工作单的信息
            WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskService.getOne(new QueryWrapper<WareOrderTaskEntity>().eq("order_sn", orderTo.getOrderSn()));

            //3、根据库存工作单id查询锁定状态（1）所有详情工作单
            List<WareOrderTaskDetailEntity> orderTaskDetailEntities = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>().eq("task_id", wareOrderTaskEntity.getId()).eq("lock_status", 1));
            for (WareOrderTaskDetailEntity orderTaskDetailEntity : orderTaskDetailEntities) {
                //4、解锁库存
                this.baseMapper.unLockStock(orderTaskDetailEntity);
                //5、更新详情工作单的状态，变为2已解锁状态
                orderTaskDetailEntity.setLockStatus(2);
                wareOrderTaskDetailService.updateById(orderTaskDetailEntity);
            }
        }
    }
}