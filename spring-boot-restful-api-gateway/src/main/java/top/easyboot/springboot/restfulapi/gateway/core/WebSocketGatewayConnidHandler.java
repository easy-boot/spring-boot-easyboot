package top.easyboot.springboot.restfulapi.gateway.core;

import top.easyboot.springboot.restfulapi.gateway.interfaces.WebSocketGatewayIHandler;
import top.easyboot.springboot.restfulapi.util.ConnectionIdUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class WebSocketGatewayConnidHandler extends WebSocketGatewayPingHandler implements WebSocketGatewayIHandler {
    /**
     * 链接id工具
     */
    private ConnectionIdUtil connectionIdUtil;
    @Override
    protected void init() {
        super.init();
        // 初始化 链接id 前缀初始化
        connectionIdPrefixInit();
    }
    @Override
    protected String generateConnectionId() throws Exception {
        return connectionIdUtil.generateConnectionId();
    }

    protected void connectionIdPrefixInit() {
        if (properties.getConnectionIdPrefix() == null || properties.getConnectionIdPrefix().isEmpty()){
            try {
                properties.setConnectionIdPrefix(InetAddress.getLocalHost().getHostAddress());
            }catch (UnknownHostException e){
            }
        }
        if (properties.getConnectionIdPrefix() == null || properties.getConnectionIdPrefix().isEmpty()){
            properties.setConnectionIdPrefix("127.0.0.1");
            System.out.println("easyboot.restfulapi.gateway.websocket.connectionIdPrefix must is string");
        }

        connectionIdUtil = new ConnectionIdUtil(){
            @Override
            protected boolean isUseIng(String connectionId) {
                return containsConnectionId(connectionId);
            }
        };
        connectionIdUtil.setConnectionIdPrefixByIpV4(properties.getConnectionIdPrefix());
    }
}
