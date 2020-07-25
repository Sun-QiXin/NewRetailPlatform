package gulimall.search.controller;

import gulimall.common.exception.BizCodeEnume;
import gulimall.common.to.es.SkuEsModel;
import gulimall.common.utils.R;
import gulimall.search.service.ProductSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * 保存到Elasticsearch
 *
 * @author 孙启新
 * <br>FileName: ElasticSaveController
 * <br>Date: 2020/07/25 16:31:27
 */
@RestController
@RequestMapping("/search/save")
public class ElasticSaveController {
    @Autowired
    private ProductSaveService saveService;

    /**
     * 上架商品
     *
     * @param skuEsModels
     * @return
     */
    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {
        try {
            saveService.productStatusUp(skuEsModels);
            return R.ok();
        } catch (IOException e) {
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnume.PRODUCT_UP_EXCEPTION.getMsg());
        }
    }
}
