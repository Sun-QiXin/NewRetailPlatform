package gulimall.order.vo;


import java.math.BigDecimal;
import java.util.List;

/**
 * 订单确认页数据
 *
 * @author 孙启新
 * <br>FileName: OrderConfirmVo
 * <br>Date: 2020/08/09 10:13:18
 */
public class OrderConfirmVo {
    /**
     * 收货地址列表
     */
    private List<MemberAddressVo> addressVos;
    /**
     * 所有选中商品
     */
    private List<OrderItemVo> orderItemVos;
    /**
     * 会员积分
     */
    private Integer integration;
    /**
     * 原商品总价格
     */
    private BigDecimal totalOriginalPrice;
    /**
     * 应付总价
     */
    private BigDecimal totalPricePayable;
    /**
     * 所有商品总件数
     */
    private Integer totalCount;
    /**
     * 防重复提交令牌
     */
    private String orderToken;

    public List<MemberAddressVo> getAddressVos() {
        return addressVos;
    }

    public void setAddressVos(List<MemberAddressVo> addressVos) {
        this.addressVos = addressVos;
    }

    public List<OrderItemVo> getOrderItemVos() {
        return orderItemVos;
    }

    public void setOrderItemVos(List<OrderItemVo> orderItemVos) {
        this.orderItemVos = orderItemVos;
    }

    public Integer getIntegration() {
        return integration;
    }

    public void setIntegration(Integer integration) {
        this.integration = integration;
    }

    public BigDecimal getTotalOriginalPrice() {
        this.totalOriginalPrice = new BigDecimal(0);

        //计算原商品价格
        if (orderItemVos != null && orderItemVos.size() > 0) {
            for (OrderItemVo orderItemVo : orderItemVos) {
                BigDecimal multiply = orderItemVo.getPrice().multiply(new BigDecimal(orderItemVo.getCount()));
                this.totalOriginalPrice = this.totalOriginalPrice.add(multiply);
            }
        }
        return totalOriginalPrice;
    }

    public void setTotalOriginalPrice(BigDecimal totalOriginalPrice) {
        this.totalOriginalPrice = totalOriginalPrice;
    }

    public BigDecimal getTotalPricePayable() {
        this.totalPricePayable = new BigDecimal(0);
        //计算应付价格
        if (orderItemVos != null && orderItemVos.size() > 0) {
            for (OrderItemVo orderItemVo : orderItemVos) {
                BigDecimal multiply = orderItemVo.getPrice().multiply(new BigDecimal(orderItemVo.getCount()));
                this.totalPricePayable = this.totalPricePayable.add(multiply);
            }
        }
        if (this.integration!=null){
            BigDecimal reduction = new BigDecimal(this.integration/2);
            this.totalPricePayable = this.totalPricePayable.subtract(reduction);
        }
        return totalPricePayable;
    }

    public void setTotalPricePayable(BigDecimal totalPricePayable) {
        this.totalPricePayable = totalPricePayable;
    }

    public Integer getTotalCount() {
        this.totalCount = 0;
        //计算应付价格
        if (orderItemVos != null && orderItemVos.size() > 0) {
            for (OrderItemVo orderItemVo : orderItemVos) {
                this.totalCount = this.totalCount+orderItemVo.getCount();
            }
        }
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public String getOrderToken() {
        return orderToken;
    }

    public void setOrderToken(String orderToken) {
        this.orderToken = orderToken;
    }
}
