package gulimall.search.service;

import gulimall.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author 孙启新
 * <br>FileName: ProductSaveService
 * <br>Date: 2020/07/25 16:35:11
 */
public interface ProductSaveService {
    /**
     * 上架商品
     * @param skuEsModels
     * @return
     * @throws IOException
     */
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
