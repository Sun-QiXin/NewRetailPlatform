package gulimall.ware.vo;

import lombok.Data;

/**
 * @author 孙启新
 * <br>FileName: PurchaseDoneVo
 * <br>Date: 2020/07/22 13:35:27
 */
@Data
public class PurchaseItemDoneVo {
    /**
     * 采购项id
     */
    private Long itemId;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 失败原因
     */
    private String reason;
}
