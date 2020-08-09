package gulimall.order;


import gulimall.order.entity.OrderEntity;
import gulimall.order.entity.OrderReturnReasonEntity;
import gulimall.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.Date;
import java.util.List;

@SpringBootTest
class GulimallOrderApplicationTests {
    @Autowired
    private OrderService orderService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Test
    void contextLoads() {
        List<OrderEntity> list = orderService.list();
        System.out.println(list);
    }

    /**
     * 创建交换机
     */
    @Test
    void createExchane(){
        //创建一个直接交换机
        /*
            String name 交换机名称
            boolean durable 是否持久化
            boolean autoDelete 是否自动删除
            Map<String, Object> arguments 指定参数
        */
        DirectExchange directExchange = new DirectExchange("hello-java-exchane",true,false,null);
        amqpAdmin.declareExchange(directExchange);
        System.out.println("hello-java-exchane交换机创建完成");
    }

    /**
     * 创建队列
     */
    @Test
    void createQueue(){
        //创建一个普通队列
        /*
            String name 队列名称
            boolean durable 是否持久化
            boolean exclusive 是否排他，队列将仅由声明他的对象使用
            boolean autoDelete 是否自动删除
            Map<String, Object> arguments 指定参数
        */
        Queue queue = new Queue("hello-java-queue",true,false,false,null);
        amqpAdmin.declareQueue(queue);
        System.out.println("hello-java-queue队列创建完成");
    }

    /**
     * 将交换机与队列绑定
     */
    @Test
    void binding(){
        /*
            String destination 目的队列
            DestinationType destinationType 目的地类型
            String exchange 要绑定的交换机
            String routingKey 路由键
			@Nullable Map<String, Object> arguments 参数
        */
        Binding binding = new Binding("hello-java-queue", Binding.DestinationType.QUEUE,"hello-java-exchane","hello-java-queue",null);
        amqpAdmin.declareBinding(binding);
        System.out.println("绑定完成");
    }

    /**
     * 发送消息
     */
    @Test
    void sendMsg(){
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

        rabbitTemplate.convertAndSend("hello-java-exchane","hello-java-queue",orderReturnReasonEntity);
        System.out.println("发送成功");
    }
}
