package top.easyboot.springboot.restfulapi.gateway.core;

import top.easyboot.springboot.restfulapi.gateway.interfaces.WebSocketGatewayIHandler;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties.WebSocket;
import top.easyboot.springboot.restfulapi.util.ConnectionIdUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class WebSocketGatewayConnidHandler extends WebSocketGatewayPingHandler implements WebSocketGatewayIHandler {
    /**
     * 链接id工具
     */
    private ConnectionIdUtil connectionIdUtil;
    /**
     * 链接id的头的key
     */
    protected static String connectionIdHeaderKey;
    @Override
    protected void init(WebSocket webSocket) {
        connectionIdPrefixInit(webSocket);
    }
    @Override
    protected String generateConnectionId() throws Exception {
        return connectionIdUtil.generateConnectionId();
    }

    protected void connectionIdPrefixInit(WebSocket webSocket) {
        connectionIdHeaderKey = webSocket.getConnectionIdHeaderKey();
        // 初始化 链接id 前缀初始化

        if (webSocket.getConnectionIdPrefix() == null || webSocket.getConnectionIdPrefix().isEmpty()){
            try {
                webSocket.setConnectionIdPrefix(InetAddress.getLocalHost().getHostAddress());
            }catch (UnknownHostException e){
            }
        }
        if (webSocket.getConnectionIdPrefix() == null || webSocket.getConnectionIdPrefix().isEmpty()){
            webSocket.setConnectionIdPrefix("127.0.0.1");
            System.out.println("easyboot.restfulapi.gateway.websocket.connectionIdPrefix must is string");
        }

        connectionIdUtil = new ConnectionIdUtil(){
            @Override
            protected boolean isUseIng(String connectionId) {
                return containsConnectionId(connectionId);
            }
        };
        connectionIdUtil.setConnectionIdPrefixByIpV4(webSocket.getConnectionIdPrefix());
    }
}
