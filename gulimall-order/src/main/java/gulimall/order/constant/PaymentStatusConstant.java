package gulimall.order.constant;

/**
 * 支付宝异步通知的支付状态
 * @author 孙启新
 * <br>FileName: PaymentStatusConstant
 * <br>Date: 2020/08/16 10:29:15
 */
public class PaymentStatusConstant {
    /**
     * 交易创建，等待买家付款
     */
    public static final String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
    /**
     * 未付款交易超时关闭，或支付完成后全额退款
     */
    public static final String TRADE_CLOSED = "TRADE_CLOSED";
    /**
     * 交易支付成功
     */
    public static final String TRADE_SUCCESS = "TRADE_SUCCESS";
    /**
     * 交易结束，不可退款
     */
    public static final String TRADE_FINISHED = "TRADE_FINISHED";
}
