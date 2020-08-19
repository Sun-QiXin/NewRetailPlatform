package gulimall.order.service;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.extension.service.IService;
import gulimall.common.to.mq.SeckillOrderTo;
import gulimall.common.utils.PageUtils;
import gulimall.order.entity.OrderEntity;
import gulimall.order.vo.*;

import java.text.ParseException;
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

    /**
     * 关闭订单
     * @param orderEntity orderEntity
     * @throws AlipayApiException AlipayApiException
     */
    void closeOrder(OrderEntity orderEntity) throws AlipayApiException;

    /**
     * 根据订单号查询需要的信息
     * @param orderSn 订单号
     * @return PayVo
     */
    PayVo getOrderPayInfo(String orderSn);

    /**
     * 支付成功后支付宝也会默认访问该请求
     * <br>查询出已经支付的订单信息
     *
     * @param params 查询参数
     * @return 订单数据
     */
    PageUtils queryPageWithItem(Map<String, Object> params);

    /**
     * 根据支付宝返回的支付成功信息，修改订单的状态
     * @param payAsyncVo 支付宝通知信息
     * @return 成功or失败
     */
    Boolean handlePayResult(PayAsyncVo payAsyncVo) throws ParseException;

    /**
     * 保存秒杀的订单信息
     * @param seckillOrderTo seckillOrderTo
     */
    void createSeckillOrder(SeckillOrderTo seckillOrderTo);
}

