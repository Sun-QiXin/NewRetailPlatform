package gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 孙启新
 * <br>FileName: MergeVo
 * <br>Date: 2020/07/22 10:55:14
 */
@Data
public class MergeVo {
    /**
     * 整单id
     */
    private Long purchaseId;
    /**
     * 合并项集合
     */
    private List<Long> items;
}
