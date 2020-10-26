package gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 返回给首页的分类vo
 * @author 孙启新
 * <br>FileName: catagory2Vo
 * <br>Date: 2020/07/26 10:17:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class catagory2Vo {
    /**
     * 1级父分类的id
     */
    private String catalog1Id;
    /**
     * 三级子分类
     */
    private List<catalog3Vo> catalog3List;
    /**
     * 2级分类的id
     */
    private Long id;
    /**
     * 2级分类名称
     */
    private String name;

    /**
     * 3级分类的Vo
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class catalog3Vo{
        /**
         * 2级父分类的id
         */
        private String catalog2Id;
        /**
         * 3级分类id
         */
        private Long id;
        /**
         * 3级分类名称
         */
        private String name;
    }
}
