package top.easyboot.springboot.restfulapi.gateway.service;

import top.easyboot.springboot.restfulapi.gateway.interfaces.service.IConnectionIdService;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.ISessionService;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties;
import top.easyboot.springboot.restfulapi.util.ConnectionIdUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class ConnectionIdAbstractService implements IConnectionIdService {
    /**
     * 链接id工具
     */
    protected ConnectionIdUtil connectionIdUtil;

    public ConnectionIdAbstractService(RestfulApiGatewayProperties.WebSocket webSocket, ISessionService webSocketSessionService) {

        String connectionIdPrefix = webSocket.getConnectionIdPrefix();
        if (webSocket.getConnectionIdPrefix() == null || webSocket.getConnectionIdPrefix().isEmpty()){
            try {
                connectionIdPrefix = InetAddress.getLocalHost().getHostAddress();
            }catch (UnknownHostException e){
            }
        }
        if (connectionIdPrefix == null || connectionIdPrefix.isEmpty()){
            connectionIdPrefix = "127.0.0.1";
        }
        webSocket.setConnectionIdPrefix(connectionIdPrefix);

        connectionIdUtil = new ConnectionIdUtil(){
            @Override
            protected boolean isUseIng(String connectionId) {
                return webSocketSessionService.containsKey(connectionId);
            }
        };
        connectionIdUtil.setConnectionIdPrefixByIpV4(connectionIdPrefix);
    }

    @Override
    public String generateConnectionId() throws Exception {
        return connectionIdUtil.generateConnectionId();
    }
}
