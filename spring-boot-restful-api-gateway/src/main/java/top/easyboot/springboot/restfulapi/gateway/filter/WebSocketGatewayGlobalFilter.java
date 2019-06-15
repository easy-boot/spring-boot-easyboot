package top.easyboot.springboot.restfulapi.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties.WebSocket;
import top.easyboot.springboot.restfulapi.gateway.service.WebSocketGatewaySessionService;

@Component
public class WebSocketGatewayGlobalFilter implements GlobalFilter, Ordered {
    @Autowired(required = false)
    private RestfulApiGatewayProperties properties;
    private WebSocket webSocket;
    /**
     * 链接id的头的key
     */
    private static String connectionIdHeaderKey;
    /**
     * 会话连接池
     */
    @Autowired
    protected WebSocketGatewaySessionService sessionService;
    /**
     * 忽略属性
     */
    public final static String ATTRIBUTE_IGNORE_TEST_GLOBAL_FILTER = "@ignoreEasybootWebSocketGatewayGlobalFilter";

    /**
     * 过滤器
     * @param exchange 交互数据
     * @param chain chain
     * @return 处理器
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 跳过过滤器
        if (!isEnabled(exchange)) {
            return chain.filter(exchange);
        }

        // 取得请求对象
        ServerHttpRequest serverHttpRequest = exchange.getRequest();

        // 试图获取长连接的connectionId[连接id]
        String connectionId = serverHttpRequest.getHeaders().getFirst(getConnectionIdKey());
        System.out.println("connectionId");
        System.out.println(connectionId);

        // 如果没有找到连接id跳过
        if (connectionId == null || connectionId.isEmpty()){

            // 跳过
            return chain.filter(exchange);
        }

        // 判断当前会话服务中是否还存在 connectionId[连接id]
        if (!sessionService.containsKey(connectionId)){
            // 会话池没有该连接的时候，修改请求，请求头中移除connectionId[连接id]
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .headers(httpHeaders -> httpHeaders.remove(getConnectionIdKey()))
                    .build();
            // 返回过滤器
            return chain.filter(exchange.mutate().request(request).build());
        }
        // 继续
        return chain.filter(exchange);
    }

    /**
     * 判断是否启动过滤器
     * @param exchange 交互数据
     * @return 是否需要过滤
     */
    protected boolean isEnabled(ServerWebExchange exchange){
        if (connectionIdHeaderKey == null || exchange.getAttribute(ATTRIBUTE_IGNORE_TEST_GLOBAL_FILTER) != null){
            return false;
        }
        return getWebSocket().isEnabled();
    }

    /**
     * 取得连接id
     * @return 连接id
     */
    protected String getConnectionIdKey(){
        if (connectionIdHeaderKey == null){
            WebSocket webSocket = getWebSocket();
            if (webSocket != null){
                connectionIdHeaderKey = webSocket.getConnectionIdHeaderKey();
            }
        }
        return connectionIdHeaderKey;
    }

    /**
     * 取得WebSocket配置
     * @return WebSocket
     */
    protected WebSocket getWebSocket() {
        if (webSocket == null){
            webSocket = properties == null ? null : properties.getWebSocket();
        }
        return webSocket;
    }

    @Override
    public int getOrder() {
        //在GatewayFilter之后执行
        return 10;
    }
}
