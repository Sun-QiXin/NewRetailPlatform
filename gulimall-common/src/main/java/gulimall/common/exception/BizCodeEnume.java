package gulimall.common.exception;

/**
 * <br>错误码和错误信息定义类
 * <br>1. 错误码定义规则为5位数字
 * <br>2. 前两位表示业务场景，最后三位表示错误码。例如：100001。10:通用 001:系统未知异常
 * <br>3. 维护错误码后需要维护错误描述，将他们定义为枚举形式
 * <br>错误码列表：
 *  <br>10: 通用
 *      <br>001：参数格式校验
 *      <br>002：短信验证码频率太高
 *  <br>11: 商品
 *  <br>12: 订单
 *  <br>13: 购物车
 *  <br>14: 物流
 *  <br>15: 用户
 *  <br>21: 库存
 * @author x3626
 */
public enum BizCodeEnume {
    /**
     *系统未知异常
     */
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    /**
     * 参数格式校验失败
     */
    VAILD_EXCEPTION(10001,"参数格式校验失败"),
    /**
     * 短信验证码频率太高，请稍后再试
     */
    SMS_CODE_EXCEPTION(10002,"短信验证码频率太高，请稍后再试"),
    /**
     * 用户已经存在
     */
    USERNAME_EXIST_EXCEPTION(15001,"用户已经存在"),
    /**
     * 手机号已经存在
     */
    PHONE_EXIST_EXCEPTION(15002,"手机号已经存在"),
    /**
     * 邮箱已经存在
     */
    EMAIL_EXIST_EXCEPTION(15003,"邮箱已经存在"),
    /**
     * 帐号或密码错误
     */
    USERNAME_PASSWORD_ERROR_EXCEPTION(15004,"帐号或密码错误"),
    /**
     * 库存不足异常
     */
   NO_STOCK_EXCEPTION(21000,"商品库存不足"),
    /**
     * 商品上架异常
     */
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常");

    private final int code;
    private final String msg;
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
