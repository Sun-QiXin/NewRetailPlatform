package gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import gulimall.common.to.es.SkuEsModel;
import gulimall.search.config.ElasticsearchConfig;
import gulimall.search.constant.EsConstant;
import gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 孙启新
 * <br>FileName: ProductSaveService
 * <br>Date: 2020/07/25 16:35:20
 */
@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {
    @Resource
    private RestHighLevelClient highLevelClient;

    /**
     * 上架商品
     *
     * @param skuEsModels
     * @return
     */
    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        //保存到es中
        //1、在es中建立product的索引依旧类型映射
        //2、批量保存
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : skuEsModels) {
            //2.1构造保存请求
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            String jsonString = JSON.toJSONString(skuEsModel);
            indexRequest.source(jsonString, XContentType.JSON);

            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = highLevelClient.bulk(bulkRequest, ElasticsearchConfig.COMMON_OPTIONS);

        //TODO 如果出现错误
        boolean hasFailures = bulk.hasFailures();
        List<String> ids = Arrays.stream(bulk.getItems()).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        if (hasFailures){
            log.error("商品上架错误：{}"+ids);
        }
        return hasFailures;
    }
}
