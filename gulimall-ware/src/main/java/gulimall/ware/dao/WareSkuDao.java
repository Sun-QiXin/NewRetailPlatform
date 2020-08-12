package gulimall.ware.dao;

import gulimall.common.to.mq.StockLockedDetailTo;
import gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:32:25
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    /**
     * 将成功采购的进行入库
     * @param skuId
     * @param wareId
     * @param skuNum
     */
    void addStock(@Param("skuId") Long skuId,@Param("wareId") Long wareId,@Param("skuNum") Integer skuNum);

    /**
     * 查询当前sku的库存量
     * @param skuId
     * @return
     */
    Long getSkuStock(Long skuId);

    /**
     * 根据SkuId查出该商品在哪些仓库有库存
     * @param skuId 商品id
     * @return 仓库id
     */
    List<Long> listWareIdHasSkuStock(@Param("skuId") Long skuId);

    /**
     * 锁定库存
     * @param skuId 商品id
     * @param num 锁定的数量
     * @param wareId 仓库id
     * @return 受影响行数
     */
    int lockSkuStock(@Param("skuId") Long skuId, @Param("num") Integer num, @Param("wareId") Long wareId);

    /**
     * 操作数据库解锁库存
     * @param lockedDetailTo lockedDetailTo
     */
    void unLockStock(@Param("lockedDetailTo") StockLockedDetailTo lockedDetailTo);
}
