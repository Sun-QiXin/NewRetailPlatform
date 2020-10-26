package gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gulimall.common.utils.PageUtils;
import gulimall.ware.entity.PurchaseEntity;
import gulimall.ware.vo.MergeVo;
import gulimall.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:32:25
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    /**
     * 分页查询加条件查询
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询未领取的采购单列表
     * @param params
     * @return
     */
    PageUtils queryPageUnreceivePurchase(Map<String, Object> params);

    /**
     * 合并采购单
     * @param mergeVo
     * @return
     */
    boolean mergePurchase(MergeVo mergeVo);

    /**
     * 领取采购单
     * @param ids
     * @return
     */
    void receivedPurchase(List<Long> ids);

    /**
     * 完成采购单
     * @param purchaseDoneVo
     */
    void donePurchase(PurchaseDoneVo purchaseDoneVo);
}

