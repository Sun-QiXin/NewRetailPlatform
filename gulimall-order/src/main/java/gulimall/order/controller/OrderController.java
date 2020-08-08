package gulimall.order.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import gulimall.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import gulimall.order.entity.OrderEntity;
import gulimall.order.service.OrderService;
import gulimall.common.utils.PageUtils;
import gulimall.common.utils.R;



/**
 * 订单
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:31:21
 */
@RestController
@RequestMapping("order/order")
@RefreshScope
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMsg")
    public R testRabbitMq(){
        /*
         * 发送消息
         * 参数一：交换机名称
         * 参数二：路由key
         * 参数三：发送的消息
         * 如果发送的消息是个对象，默认会使用序列化机制，将对象写出去。对象必须实现serializable,
         * 可以配置为json
         */
        OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
        orderReturnReasonEntity.setCreateTime(new Date());
        orderReturnReasonEntity.setName("麻辣粉可容纳付款啦");
        orderReturnReasonEntity.setId(111L);

        rabbitTemplate.convertAndSend("hello-java-exchane","hello-java-queue",orderReturnReasonEntity,new CorrelationData(UUID.randomUUID().toString()));
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("order:order:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("order:order:info")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("order:order:save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("order:order:update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("order:order:delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
