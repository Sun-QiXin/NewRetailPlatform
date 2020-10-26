package gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gulimall.common.utils.PageUtils;
import gulimall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:33:30
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     *保证冗余字段的数据一致
     * @param brand
     */
    void updateDetail(BrandEntity brand);
}

