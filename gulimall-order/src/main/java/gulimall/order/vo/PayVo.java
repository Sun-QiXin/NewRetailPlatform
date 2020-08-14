package gulimall.order.vo;

public class PayVo {
    /**
     * 商户订单号 必填(字段名不可改，支付宝只接受此字段名)
     */
    private String out_trade_no;
    /**
     * 订单名称 必填(字段名不可改，支付宝只接受此字段名)
     */
    private String subject;
    /**
     * 付款金额 必填(字段名不可改，支付宝只接受此字段名)
     */
    private String total_amount;
    /**
     * 商品描述 可空(字段名不可改，支付宝只接受此字段名)
     */
    private String body;

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
