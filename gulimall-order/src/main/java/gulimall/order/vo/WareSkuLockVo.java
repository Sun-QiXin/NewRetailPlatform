package gulimall.order.vo;

import java.util.List;

/**
 * 库存锁定vo
 * @author 孙启新
 * <br>FileName: WareSkuLockVo
 * <br>Date: 2020/08/10 16:05:46
 */
public class WareSkuLockVo {
    /**
     * 订单号
     */
    private String orderSn;
    /**
     * 需要锁定的商品信息
     */
    private List<OrderItemVo> orderItemVos;

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public List<OrderItemVo> getOrderItemVos() {
        return orderItemVos;
    }

    public void setOrderItemVos(List<OrderItemVo> orderItemVos) {
        this.orderItemVos = orderItemVos;
    }
}
