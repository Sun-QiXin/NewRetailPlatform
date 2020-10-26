package gulimall.ware.service.impl;

import gulimall.common.constant.WareConstant;
import gulimall.ware.entity.PurchaseDetailEntity;
import gulimall.ware.service.PurchaseDetailService;
import gulimall.ware.service.WareSkuService;
import gulimall.ware.vo.MergeVo;
import gulimall.ware.vo.PurchaseDoneVo;
import gulimall.ware.vo.PurchaseItemDoneVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import gulimall.common.utils.PageUtils;
import gulimall.common.utils.Query;

import gulimall.ware.dao.PurchaseDao;
import gulimall.ware.entity.PurchaseEntity;
import gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;


    /**
     * 分页查询加条件查询
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> wrapper = new QueryWrapper<>();
        //封装查询条件
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(w -> {
                w.eq("id", key).or().like("assignee_name", key);
            });
        }
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("status", status);
        }

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 查询未领取的采购单列表
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageUnreceivePurchase(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);
    }

    /**
     * 合并采购单
     *
     * @param mergeVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean mergePurchase(MergeVo mergeVo) {
        //合并成功或失败的变量
        boolean flag = false;

        //查询可合并的采购单
        List<Long> items = mergeVo.getItems();
        List<PurchaseDetailEntity> detailEntities = purchaseDetailService.listByIds(items);
        //拿到采购项的状态不是0或者1的集合
        List<PurchaseDetailEntity> collect = detailEntities.stream().filter(detailEntity -> {
            //确定采购项的状态是0或者1才能合并
            if (detailEntity.getStatus() == WareConstant.PurchaseDetailStatusEnum.CREATED.getCode() || detailEntity.getStatus() == WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode()) {
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toList());

        //如果可更新的采购项不为空就合并采购单
        if (collect != null && collect.size() > 0) {
            flag = true;
            Long purchaseId = mergeVo.getPurchaseId();
            //如果采购单id为空
            if (purchaseId == null) {
                //新建采购单
                PurchaseEntity purchaseEntity = new PurchaseEntity();
                purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
                purchaseEntity.setPriority(1);
                purchaseEntity.setCreateTime(new Date());
                purchaseEntity.setUpdateTime(new Date());
                this.save(purchaseEntity);
                purchaseId = purchaseEntity.getId();
            }

            Long finalPurchaseId = purchaseId;
            List<PurchaseDetailEntity> purchaseDetailEntities = collect.stream().map(detailEntity -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(detailEntity.getId());
                purchaseDetailEntity.setPurchaseId(finalPurchaseId);
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());

            purchaseDetailService.updateBatchById(purchaseDetailEntities);

            //更新修改时间
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(purchaseId);
            purchaseEntity.setUpdateTime(new Date());
            this.updateById(purchaseEntity);
        }
        return flag;
    }

    /**
     * 领取采购单
     *
     * @param ids 采购单的id集合
     * @return
     */
    @Override
    public void receivedPurchase(List<Long> ids) {
        //1.确认当前采购单是新建或者已分配状态
        List<PurchaseEntity> purchaseEntities = ids.stream().map(id -> {
            PurchaseEntity purchaseEntity = this.getById(id);
            return purchaseEntity;
        }).filter(item -> {
            if (item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() || item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
                return true;
            }
            return false;
        }).map(item -> {
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        //查询出所有采购项
        purchaseEntities.forEach(item -> {
            List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", item.getId()));

            //采购项不为空再更新
            if (purchaseDetailEntities != null && purchaseDetailEntities.size() > 0) {
                List<PurchaseDetailEntity> collect = purchaseDetailEntities.stream().map(purchaseDetailEntity -> {
                    purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                    return purchaseDetailEntity;
                }).collect(Collectors.toList());
                //2、改变采购单的状态
                this.updateBatchById(purchaseEntities);
                //3、改变采购项的状态
                purchaseDetailService.updateBatchById(collect);
            }
        });
    }

    /**
     * 完成采购单
     *
     * @param purchaseDoneVo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void donePurchase(PurchaseDoneVo purchaseDoneVo) {
        //1、改变采购项的状态
        Boolean flag = true;
        List<PurchaseItemDoneVo> items = purchaseDoneVo.getItems();
        List<PurchaseDetailEntity> updates = new ArrayList<>();
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()) {
                detailEntity.setStatus(item.getStatus());
                flag = false;
            } else {
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                //2、将成功采购的进行入库
                PurchaseDetailEntity detailEntity1 = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(detailEntity1.getSkuId(),detailEntity1.getWareId(),detailEntity1.getSkuNum());
            }
            detailEntity.setId(item.getItemId());
            updates.add(detailEntity);
        }
        //批量更新
        purchaseDetailService.updateBatchById(updates);

        //3、改变采购单状态
        Long id = purchaseDoneVo.getId();
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag ? WareConstant.PurchaseStatusEnum.FINISH.getCode() : WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }
}