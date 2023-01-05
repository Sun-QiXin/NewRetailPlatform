package gulimall.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.servlet.CommonFilter;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.alibaba.fastjson.JSON;
import gulimall.common.exception.BizCodeEnume;
import gulimall.common.utils.R;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.servlet.Filter;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;


/**
 * 秒杀服务的sentinel配置
 *
 * @author 孙启新
 * <br>FileName: SeckillSentinelConfig
 * <br>Date: 2020/10/20 12:27:50
 */
@Configuration
public class GatewaySentinelConfig {
    public GatewaySentinelConfig() {
        //重新指定网关被规则限制后的默认提示信息
        GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {
                //根据异常类型返回不同的提示信息
                R error = null;

                //1、限流异常
                if (t instanceof FlowException) {
                    error = R.error(BizCodeEnume.SENTINEL_FLOW_EXCEPTION.getCode(), "网关：" + BizCodeEnume.SENTINEL_FLOW_EXCEPTION.getMsg());
                }
                //2、降级异常
                else if (t instanceof DegradeException) {
                    error = R.error(BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getCode(), "网关：" + BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getMsg());
                }
                //3、参数热点异常
                else if (t instanceof ParamFlowException) {
                    error = R.error(BizCodeEnume.SENTINEL_PARAM_FLOW_EXCEPTION.getCode(), "网关：" + BizCodeEnume.SENTINEL_PARAM_FLOW_EXCEPTION.getMsg());
                }
                //4、系统异常
                else if (t instanceof SystemBlockException) {
                    error = R.error(BizCodeEnume.SENTINEL_SYSTEM_BLOCK_EXCEPTION.getCode(), "网关：" + BizCodeEnume.SENTINEL_SYSTEM_BLOCK_EXCEPTION.getMsg());
                }
                //5、授权异常
                else if (t instanceof AuthorityException) {
                    error = R.error(BizCodeEnume.SENTINEL_AUTHORITY_EXCEPTION.getCode(), "网关：" + BizCodeEnume.SENTINEL_AUTHORITY_EXCEPTION.getMsg());
                }
                return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(JSON.toJSONString(error)));
            }
        });
    }

    /**
     * <br>1.7.0版本开始，官方在CommonFilter中引入了一个WEB_CONTEXT_UNIFY参数，用于控制是否收敛context。默认为true(默认收敛所有)，配置为false则可根据不同URL进行链路的限流操作。
     * <br>不配置的话，上面通用返回通用提示信息不生效
     *
     * @return registration
     */
    @Bean
    public FilterRegistrationBean sentinelFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CommonFilter());
        registration.addUrlPatterns("/*");

        //入口资源关闭聚合
        registration.addInitParameter(CommonFilter.WEB_CONTEXT_UNIFY, "false");
        registration.setName("sentinelFilter");
        registration.setOrder(1);
        return registration;
    }
}
