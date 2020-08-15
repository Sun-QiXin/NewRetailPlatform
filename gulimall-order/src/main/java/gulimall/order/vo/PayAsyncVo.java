package gulimall.order.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 支付宝支付成功异步通知Vo
 * @author x3626
 */
@ToString
@Data
public class PayAsyncVo {
    /**
     * 该笔交易创建的时间。格式为 yyyy-MM-dd HH:mm:ss
     */
    private String gmt_create;
    /**
     * 编码格式，如utf-8、gbk、gb2312等
     */
    private String charset;
    /**
     * 该笔交易的买家付款时间。格式为 yyyy-MM-dd HH:mm:ss
     */
    private String gmt_payment;
    /**
     * 该笔交易的退款时间。格式为 yyyy-MM-dd HH:mm:ss.S
     */
    private String gmt_refund;
    /**
     * 该笔交易结束时间。格式为 yyyy-MM-dd HH:mm:ss
     */
    private String gmt_close;
    /**
     * 通知的发送时间。格式为yyyy-MM-dd HH:mm:ss
     */
    private String notify_time;
    /**
     * 商品的标题/交易标题/订单标题/订单关键字等，是请求时对应的参数，原样通知回来
     */
    private String subject;
    /**
     * 签名
     */
    private String sign;
    /**
     * 买家支付宝账号对应的支付宝唯一用户号。以 2088 开头的纯 16 位数字
     */
    private String buyer_id;
    /**
     * 该订单的备注、描述、明细等。对应请求时的body参数，原样通知回来
     */
    private String body;
    /**
     * 用户在交易中支付的可开发票的金额，单位为元，精确到小数点后2位
     */
    private String invoice_amount;
    /**
     * 调用的接口版本，固定为：1.0
     */
    private String version;
    /**
     * 通知校验ID
     */
    private String notify_id;
    /**
     * 支付成功的各个渠道金额信息
     * <li>fundChannel->支付渠道
     *      <ul>
     *          <li>COUPON->支付宝红包</li>
     *          <li>ALIPAYACCOUNT->支付宝余额</li>
     *          <li>POINT->集分宝</li>
     *          <li>DISCOUNT->预付卡</li>
     *          <li>PCARD->折扣券</li>
     *          <li>FINANCEACCOUNT->余额宝</li>
     *          <li>MCARD->商家储值卡</li>
     *          <li>MDISCOUNT->商户优惠券</li>
     *          <li>MCOUPON->商户红包</li>
     *          <li>PCREDIT->蚂蚁花呗</li>
     *      </ul>
     * </li>
     * <li>amount->使用指定支付渠道支付的金额，单位为元。</li>
     */
    private String fund_bill_list;
    /**
     * 通知类型； trade_status_sync
     */
    private String notify_type;
    /**
     * 原支付请求的商户订单号
     */
    private String out_trade_no;
    /**
     * 商户业务 ID，主要是退款通知中返回退款申请的流水号
     */
    private String out_biz_no;
    /**
     * 本次交易支付的订单金额，单位为人民币（元），精确到小数点后2位
     */
    private String total_amount;
    /**
     * 交易目前所处的状态
     * <li>WAIT_BUYER_PAY->交易创建，等待买家付款</li>
     * <li>TRADE_CLOSED->未付款交易超时关闭，或支付完成后全额退款</li>
     * <li>TRADE_SUCCESS->交易支付成功</li>
     * <li>TRADE_FINISHED->交易结束，不可退款</li>
     */
    private String trade_status;
    /**
     * 支付宝交易凭证号
     */
    private String trade_no;
    /**
     * 授权方的appid，由于本接口暂不开放第三方应用授权，因此auth_app_id=app_id
     */
    private String auth_app_id;
    /**
     * 本交易支付时所使用的所有优惠券信息
     * <li>name->券名称</li>
     * <li>type->券类型，当前支持三种类型：ALIPAY_FIX_VOUCHER - 全场代金券ALIPAY_DISCOUNT_VOUCHER - 折扣券ALIPAY_ITEM_VOUCHER - 单品优惠注：不排除将来新增其他类型的可能，商家接入时请注意兼容性，避免硬编码</li>
     * <li>amount->优惠券面额，它应该等于商家出资加上其他出资方出资</li>
     * <li>merchant_contribute->商家出资（特指发起交易的商家出资金额）</li>
     * <li>other_contribute->其他出资方出资金额，可能是支付宝，可能是品牌商，或者其他方，也可能是他们的共同出资</li>
     * <li>memo->优惠券备注信息</li>
     */
    private String voucher_detail_list;
    /**
     * 退款通知中，返回总退款金额，单位为元，精确到小数点后2位
     */
    private String refund_fee;
    /**
     * 商家在交易中实际收到的款项，单位为元，精确到小数点后2位
     */
    private String receipt_amount;
    /**
     * 使用集分宝支付的金额，单位为元，精确到小数点后2位
     */
    private String point_amount;
    /**
     * 支付宝分配给开发者的应用 ID
     */
    private String app_id;
    /**
     * 用户在交易中支付的金额，单位为元，精确到小数点后2位
     */
    private String buyer_pay_amount;
    /**
     * 公共回传参数，如果请求时传递了该参数，则返回给商户时会在异步通知时将该参数原样返回。本参数必须进行UrlEncode之后才可以发送给支付宝
     */
    private String passback_params;
    /**
     * 签名算法类型，目前支持RSA2和RSA，推荐使用RSA2
     */
    private String sign_type;
    /**
     * 卖家支付宝用户号
     */
    private String seller_id;

}
