package gulimall.common.to.mq;

import lombok.Data;


/**
 * 库存锁定成功的信息（发送给RabbitMQ）
 * @author 孙启新
 * <br>FileName: StockLockedTo
 * <br>Date: 2020/08/12 14:04:01
 */
@Data
public class StockLockedTo {
    /**
     * 库存工作单id
     */
    private Long taskId;
    /**
     * 工作单详情的id
     */
    private StockLockedDetailTo lockedDetailTo;
}
