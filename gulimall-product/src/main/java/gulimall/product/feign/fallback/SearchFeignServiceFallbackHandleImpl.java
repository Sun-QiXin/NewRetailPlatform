package gulimall.product.feign.fallback;

import gulimall.common.exception.BizCodeEnume;
import gulimall.common.to.es.SkuEsModel;
import gulimall.common.utils.R;
import gulimall.product.feign.SearchFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 远程调用失败的降级处理方法
 * @author 孙启新
 * <br>FileName: SearchFeignServiceFallbackHandleImpl
 * <br>Date: 2020/08/20 16:35:25
 */
@Component
@Slf4j
public class SearchFeignServiceFallbackHandleImpl implements SearchFeignService {
    /**
     * 上架商品
     *
     * @param skuEsModels skuEsModels
     * @return R
     */
    @Override
    public R productStatusUp(List<SkuEsModel> skuEsModels) {
        log.error("--------------------------调用远程服务方法 productStatusUp 失败,返回降级信息-------------------------");
        return R.error(BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getCode(), BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getMsg());
    }
}
