package top.easyboot.springboot.restfulapi.gateway.service;

import top.easyboot.springboot.restfulapi.gateway.interfaces.service.IConnectionIdService;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.ISessionService;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties;
import top.easyboot.springboot.restfulapi.util.ConnectionIdUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

public abstract class ConnectionIdAbstractService implements IConnectionIdService {

    protected HashMap<String, String> cTuMap = new HashMap();

    /**
     * 链接id工具
     */
    protected ConnectionIdUtil connectionIdUtil;


    /**
     * 绑定用户uid与连接id关系
     * @param connectionId 连接id
     * @param uid 用户uid
     */
    protected abstract void bind(String connectionId, String uid);

    /**
     * 解绑用户uid与连接id关系
     * @param connectionId 连接id
     * @param uid 用户uid
     */
    protected abstract void unbind(String connectionId, String uid);

    @Override
    public void refresh(String connectionId, String uid) {
        if (connectionId == null || connectionId.isEmpty()){
            return;
        }
        String oldUid = cTuMap.get(connectionId);
        if ((oldUid == null || oldUid.isEmpty()) && uid!= null && !uid.isEmpty()){
            cTuMap.put(connectionId, uid);
            bind(connectionId, uid);
        } else if (oldUid!= null && !oldUid.isEmpty() && (uid == null || uid.isEmpty())){
            cTuMap.remove(connectionId);
            unbind(connectionId, uid);
        } else if (oldUid != null && uid != null && !oldUid.equals(uid)){
            cTuMap.put(connectionId, uid);
            bind(connectionId, uid);
        }
    }

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
