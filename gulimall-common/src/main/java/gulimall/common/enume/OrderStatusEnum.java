package gulimall.common.enume;

/**
 * @author 孙启新
 */

public enum  OrderStatusEnum {
    /**
     * 待付款
     */
    CREATE_NEW(0,"待付款"),
    /**
     * 已付款
     */
    PAYED(1,"已付款"),
    /**
     * 已发货
     */
    SENDED(2,"已发货"),
    /**
     * 已完成
     */
    RECIEVED(3,"已完成"),
    /**
     * 已取消
     */
    CANCLED(4,"已取消"),
    /**
     * 售后中
     */
    SERVICING(5,"售后中"),
    /**
     * 售后完成
     */
    SERVICED(6,"售后完成");
    private Integer code;
    private String msg;

    OrderStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
