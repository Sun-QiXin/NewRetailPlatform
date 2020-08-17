package gulimall.seckill.vo;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 活动以及每个活动所需要上架的商品Vo
 * @author 孙启新
 * <br>FileName: SeckillSessionsWithSkus
 * <br>Date: 2020/08/17 10:08:31
 */
@Data
public class SeckillSessionsWithSkusVo {
    /**
     * id
     */
    private Long id;
    /**
     * 场次名称
     */
    private String name;
    /**
     * 每日开始时间
     */
    private Date startTime;
    /**
     * 每日结束时间
     */
    private Date endTime;
    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 当前活动关联得商品
     */
    private List<SeckillSkuRelationVo> relationEntities;
}
