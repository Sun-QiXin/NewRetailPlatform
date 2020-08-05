package gulimall.common.constant;

/**
 * 购物车相关的常量
 *
 * @author 孙启新
 * <br>FileName: ShoppingCartConstant
 * <br>Date: 2020/08/05 13:48:06
 */
public class ShoppingCartConstant {
    /**
     * 临时用户的cookie名称
     */
    public static final String TEMP_USER_COOKIE_NAME = "user-key";
    /**
     * 临时用户cookie的过期时间
     */
    public static final int TEMP_USER_COOKIE_TIMEOUT = 60 * 60 * 24 * 30;
    /**
     * 购物车保存进redis数据的前缀
     */
    public static final String CART_PREFIX = "gulimall:cart:";
}
