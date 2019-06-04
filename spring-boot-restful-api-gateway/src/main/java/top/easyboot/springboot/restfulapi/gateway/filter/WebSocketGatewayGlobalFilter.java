package top.easyboot.springboot.restfulapi.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import top.easyboot.springboot.restfulapi.gateway.property.WebSocketGatewayProperties;

@Component
public class WebSocketGatewayGlobalFilter implements GlobalFilter, Ordered {
    @Autowired(required = false)
    private WebSocketGatewayProperties properties;
    public final static String ATTRIBUTE_IGNORE_TEST_GLOBAL_FILTER = "@ignoreWebSocketGatewayGlobalFilter";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //跳过检测
        if (properties == null || properties.isEnabled() == false || exchange.getAttribute(ATTRIBUTE_IGNORE_TEST_GLOBAL_FILTER) != null) {
            return chain.filter(exchange);
        }
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String connectionId = serverHttpRequest.getHeaders().getFirst(properties.getConnectionIdPrefix());
        if (connectionId == null || connectionId.isEmpty()){
            return chain.filter(exchange);
        }
        System.out.println("connectionId");
        System.out.println(connectionId);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        //在GatewayFilter之后执行
        return 10;
    }
}
