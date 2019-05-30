package top.easyboot.springboot.gateway.configuration;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.*;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.client.RestTemplate;
import top.easyboot.springboot.gateway.lib.WebSocketRestfulSession;
import top.easyboot.springboot.gateway.property.WebSocketGatewayProperties;


@Configuration
@EnableConfigurationProperties(WebSocketGatewayProperties.class)
@ConditionalOnProperty(name = "easyboot.gateway.websocket.gateway.enabled", matchIfMissing = true)
public class WebSocketGatewayConfiguration implements ApplicationListener<WebServerInitializedEvent> {
    private int serverPort;
    private String serverAddress;
    private URI serverUri;
    @Autowired
    private WebSocketGatewayProperties properties;

    private HashMap<String, WebSocketRestfulSession> sessions = new HashMap<>();

    /**
     * 服务器初始化事件
     * @param event
     */
    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        serverPort = event.getWebServer().getPort();
        try {
            serverAddress = InetAddress.getLocalHost().getHostAddress();
        }catch (UnknownHostException e){
            serverAddress = "127.0.0.1";
        }
        serverUri = URI.create("http://"+serverAddress+":"+serverPort);
    }


//    @Value(value = "${webSocketHandlerPath}")
    private String webSocketHandlerPath = "/8";

    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    //每隔2秒执行一次
    @Scheduled(fixedRate = 5000)
    public void testTasks() {
        long time = new Date().getTime()/1000;
        System.out.println("定时任务执行时间：" + time);

        for (String sid : sessions.keySet()) {
            WebSocketRestfulSession session = sessions.get(sid);
            session.textMessage("time:"+time);
        }
    }
    @Bean
    public WebSocketHandler webSocketHandler() {


        RestTemplate restGateawyTemplate = new RestTemplate();

        return (final WebSocketSession session) -> {
            String sid = session.getId();

//            ResponseEntity<Object> exchange = restTemplate.exchange(serverUri+"/v1.0/api", HttpMethod.GET, new HttpEntity<>(null), Object.class);
//
//            System.out.println(exchange.getStatusCode());
//            System.out.println(exchange.getStatusCodeValue());
//            System.out.println(exchange.getBody().toString());
//            System.out.println(exchange.getHeaders().toSingleValueMap().toString());

            WebSocketRestfulSession restfulSession = new WebSocketRestfulSession(session);
            restfulSession.onClose(()->{
                if (sessions.containsKey(sid)){
                    sessions.remove(sid);
                }
            });


            sessions.put(sid, restfulSession);

            return session.send(restfulSession.getFlux().map(t->{
                System.out.println("send");
                return t;
            })).doAfterSuccessOrError((res, throwable)->{
                if (sessions.containsKey(sid)){
                    sessions.remove(sid);
                }
                if (throwable != null){
                    System.out.println("throwable");
                    throwable.printStackTrace();
                }
            });
        };
    }

    @Bean
    public HandlerMapping handlerMapping(WebSocketHandler webSocketHandler) {
        Map<String, WebSocketHandler> map = new HashMap<String, WebSocketHandler>();
        map.put(webSocketHandlerPath, webSocketHandler);

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return mapping;
    }


}
