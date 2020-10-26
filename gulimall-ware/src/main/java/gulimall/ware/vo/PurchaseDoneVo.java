package gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 孙启新
 * <br>FileName: PurchaseDoneVo
 * <br>Date: 2020/07/22 13:35:27
 */
@Data
public class PurchaseDoneVo {
    /**
     * 采购单id
     */
    private Long id;
    /**
     * 采购项集合
     */
    private List<PurchaseItemDoneVo> items;
}
