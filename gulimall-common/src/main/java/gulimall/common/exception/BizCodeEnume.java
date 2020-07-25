package gulimall.common.exception;

/**
 * <br>错误码和错误信息定义类
 * <br>1. 错误码定义规则为5位数字
 * <br>2. 前两位表示业务场景，最后三位表示错误码。例如：100001。10:通用 001:系统未知异常
 * <br>3. 维护错误码后需要维护错误描述，将他们定义为枚举形式
 * <br>错误码列表：
 *  <br>10: 通用
 *      <br>001：参数格式校验
 *  <br>11: 商品
 *  <br>12: 订单
 *  <br>13: 购物车
 *  <br>14: 物流
 */
public enum BizCodeEnume {
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VAILD_EXCEPTION(10001,"参数格式校验失败"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常");

    private int code;
    private String msg;
    BizCodeEnume(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
