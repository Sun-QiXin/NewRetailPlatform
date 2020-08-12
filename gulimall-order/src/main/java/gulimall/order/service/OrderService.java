package gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gulimall.common.utils.PageUtils;
import gulimall.order.entity.OrderEntity;
import gulimall.order.vo.OrderConfirmVo;
import gulimall.order.vo.OrderSubmitVo;
import gulimall.order.vo.SubmitOrderResponseVo;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:31:21
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 跳转结算页，并展示当前需要展示的信息
     *
     * @return OrderConfirmVo
     * @throws ExecutionException ExecutionException
     * @throws InterruptedException InterruptedException
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    /**
     * 更改当前的默认地址为新指定的
     * @param memberId     用户id
     * @param defaultStatus 要更改成的信息
     * @param addressId 要更改成默认地址的列id
     */
    void updateAddress(Long memberId, Integer defaultStatus, Long addressId);

    /**
     * 提交订单
     * @param orderSubmitVo  orderSubmitVo
     * @return SubmitOrderResponseVo
     */
    SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo);

    /**
     * 根据订单号获取订单的详细信息
     * @param orderSn 订单号
     * @return 订单的详细信息
     */
    OrderEntity getOrderByOrderSn(String orderSn);
}

