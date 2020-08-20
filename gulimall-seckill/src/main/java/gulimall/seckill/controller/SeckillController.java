package gulimall.seckill.controller;

import gulimall.common.to.mq.SeckillOrderTo;
import gulimall.common.utils.R;
import gulimall.seckill.service.SeckillService;
import gulimall.seckill.to.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @author 孙启新
 * <br>FileName: SeckillController
 * <br>Date: 2020/08/18 09:44:26
 */
@Controller
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * 返回当前时间可以参与的秒杀商品
     *
     * @return 秒杀商品
     */
    @GetMapping("/currentSeckillSkus")
    @ResponseBody
    public R getCurrentSeckillSkus() {
        List<SeckillSkuRedisTo> skuRedisTos = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(skuRedisTos);
    }


    /**
     * tesxt
     *
     * @return 秒杀商品
     */
    @GetMapping("/test")
    @ResponseBody
    public R getCurrentSeckillSkus1() {
        return R.ok();
    }

    /**
     * 根据skuId获取该商品是否有秒杀活动
     *
     * @param skuId skuId
     * @return R
     */
    @GetMapping("/sku/seckill/{skuId}")
    @ResponseBody
    public R getSkuSeckillInfoById(@PathVariable("skuId") Long skuId) {
        List<SeckillSkuRedisTo> skuRedisTos = seckillService.getSkuSeckillInfoById(skuId);
        return R.ok().setData(skuRedisTos);
    }

    /**
     * 秒杀商品加入购物车
     *
     * @param seckillId 商品在redis中的key
     * @param num       数量
     * @param code      随机码
     * @return R
     */
    @GetMapping("/seckill")
    public String seckill(@RequestParam("seckillId") String seckillId, @RequestParam("num") String num, @RequestParam("code") String code, Model model) {
        //1、判断是否登录(拦截器做)
        /*经过登录判断，合法效验，获取信号量后不走购物车流程，直接发送rabbitmq消息，快速提醒用户秒杀成功，等待一段时间后跳到付款页面，订单服务监听秒杀成功队列，慢慢处理*/
        SeckillOrderTo seckillOrderTo = seckillService.seckill(seckillId, num, code);
        if (seckillOrderTo == null) {
            model.addAttribute("seckillErrorMsg", "秒杀失败了,下次加油奥！");
        } else {
            model.addAttribute("seckillSuccessMsg", "恭喜您秒杀成功,系统正在为您准备订单,10S后可选择支付方式去付款！");
            model.addAttribute("seckillOrderTo", seckillOrderTo);
        }
        return "success";
    }
}
