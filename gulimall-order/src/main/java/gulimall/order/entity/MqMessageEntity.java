package gulimall.order.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * (MqMessage)表实体类
 *
 * @author @孙启新
 * @since 2020-08-13 16:26:06
 */
@SuppressWarnings("serial")
@TableName("mq_message")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MqMessageEntity implements Serializable {
    private static final long serialVersionUID = 326014288322139451L;

    @TableId("id")
    private Integer id;
    /**
     * 消息id
     */
    private String messageId;
    /**
     * json数据
     */
    private String content;
    /**
     * 发送到哪个交换机
     */
    private String toExchane;
    /**
     * 路由键
     */
    private String routingKey;
    /**
     * 发送数据的类型
     */
    private String classType;
    /**
     * 0-已抵达 1-错误抵达
     */
    private Integer messageStatus;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}