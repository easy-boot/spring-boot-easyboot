package top.easyboot.springboot.restfulapi.gateway.configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.*;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.client.RestTemplate;
import top.easyboot.springboot.restfulapi.gateway.core.WebSocketRestfulSession;
import top.easyboot.springboot.restfulapi.gateway.property.WebSocketGatewayProperties;
import top.easyboot.springboot.restfulapi.gateway.property.WebSocketGatewayProperties.RpcCall;
import top.easyboot.springboot.restfulapi.util.ConnectionIdUtil;

@Configuration
@EnableConfigurationProperties(WebSocketGatewayProperties.class)
@ConditionalOnProperty(prefix = "easyboot.restfulapi.gateway", name = {"enabled", "websocket.enabled"}, havingValue = "true")
public class WebSocketGatewayConfiguration implements ApplicationListener<WebServerInitializedEvent> {
    /**
     * ping定时器
     */
    private Timer pingTimer;
    /**
     * 配置文件
     */
    @Autowired
    private WebSocketGatewayProperties properties;
    /**
     * 会话连接池
     */
    private HashMap<String, WebSocketRestfulSession> sessions = new HashMap<>();
    /**
     * 链接id前缀
     */
    private ConnectionIdUtil connectionIdUtil;


    @Bean
    @ConditionalOnMissingBean(name = "easybootRestfulApiWebSocketHandler")
    public WebSocketHandler easybootRestfulApiWebSocketHandler() {

        connectionIdUtil = new ConnectionIdUtil(){
            @Override
            protected boolean isUseIng(String connectionId) {
                return sessions.containsKey(connectionId);
            }
        };
        connectionIdUtil.setConnectionIdPrefixByIpV4("127.0.0.1");

        RestTemplate restGateawyTemplate = new RestTemplate();

        return (final WebSocketSession session) -> {
            String connectionId;
            try {
                connectionId = connectionIdUtil.generateConnectionId();
            }catch (Throwable e){
                return session.close();
            }

            System.out.println("connectionId");
            System.out.println(connectionId);
//            ResponseEntity<Object> exchange = restTemplate.exchange(serverUri+"/v1.0/api", HttpMethod.GET, new HttpEntity<>(null), Object.class);
//
//            System.out.println(exchange.getStatusCode());
//            System.out.println(exchange.getStatusCodeValue());
//            System.out.println(exchange.getBody().toString());
//            System.out.println(exchange.getHeaders().toSingleValueMap().toString());

            WebSocketRestfulSession restfulSession = new WebSocketRestfulSession(connectionId, session);
            restfulSession.onClose(()->{
                if (sessions.containsKey(connectionId)){
                    sessions.remove(connectionId);
                }
            });


            sessions.put(connectionId, restfulSession);

            return session.send(restfulSession.getFlux()).doAfterSuccessOrError((res, throwable)->{
                if (sessions.containsKey(connectionId)){
                    sessions.remove(connectionId);
                }
                if (throwable != null){
                    System.out.println("throwable");
                    throwable.printStackTrace();
                }
            });
        };
    }
    //每隔2秒执行一次
    protected void pingTaskRun() {
        for (String connectionId : sessions.keySet()) {
            sessions.get(connectionId).ping();
        }
    }

    @Bean
    @ConditionalOnMissingBean(name = "easybootRestfulApiHandlerMapping")
    public HandlerMapping easybootRestfulApiHandlerMapping(WebSocketHandler easybootRestfulApiWebSocketHandler) {
        Map<String, WebSocketHandler> map = new HashMap<String, WebSocketHandler>();
        /**
         * 默认地址
         */
        if (properties.getPath() == null){
            properties.setPath(new String[]{"/easyboot-restful-api/websocket"});
        }
        /**
         * 循环加入监听器
         */
        for (String path : properties.getPath()) {
            map.put(path, easybootRestfulApiWebSocketHandler);
        }
        /**
         * 创建
         */
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        /**
         * 设置地图
         */
        mapping.setUrlMap(map);
        if (properties.getOrder() == null){
            mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        }else{
            mapping.setOrder(properties.getOrder());
        }

        return mapping;
    }
    /**
     * 服务器初始化事件
     * @param event
     */
    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        if (properties.getRpcCall() == null){
            properties.setRpcCall(new RpcCall());
        }
        RpcCall rpcCall = properties.getRpcCall();
        if (rpcCall.getPort() == 0){
            rpcCall.setPort(event.getWebServer().getPort());
        }
        if (rpcCall.getAddress() == null || rpcCall.getAddress().isEmpty()){
            try {
                rpcCall.setAddress(InetAddress.getLocalHost().getHostAddress());
            }catch (UnknownHostException e){
            }
        }
        if (rpcCall.getAddress() == null || rpcCall.getAddress().isEmpty()){
            rpcCall.setAddress("127.0.0.1");
        }
        // 启动定时器
        pingTaskInit();
    }
    protected void pingTaskInit(){
        try {
            if (pingTimer!=null){
                pingTimer.cancel();
            }
        }catch (Throwable e){
        }
        pingTimer = new Timer();
        pingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                pingTaskRun();
            }
        }, new Date(), 5000);
    }
    @Bean
    @ConditionalOnMissingBean(name = "webSocketHandlerAdapter")
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
