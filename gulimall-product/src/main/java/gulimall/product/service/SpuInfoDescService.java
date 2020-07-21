package gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gulimall.common.utils.PageUtils;
import gulimall.product.entity.SpuInfoDescEntity;

import java.util.Map;

/**
 * spu信息介绍
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:33:30
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存Spu的描述图片 pms_spu_info_desc
     * @param spuInfoDescEntity
     */
    void saveSpuInfoDesc(SpuInfoDescEntity spuInfoDescEntity);
}

