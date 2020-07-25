package gulimall.common.constant;

/**
 * 商品系统常量定义
 *
 * @author 孙启新
 * <br>FileName: ProductConstant
 * <br>Date: 2020/07/19 13:34:13
 */
public class ProductConstant {
    public enum AttrEnum {
        ATTR_BASE_TYPE(1, "基本属性"), ATTR_SALE_TYPE(0, "销售属性");
        private int code;
        private String msg;

        AttrEnum(int code, String msg) {
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

    public enum StatusEnum {
        NEW_SPU(0, "商品新建"), SPU_UP(1, "商品上架"), SPU_DOWN(2, "商品下架");
        private int code;
        private String msg;

        StatusEnum(int code, String msg) {
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
}
