package top.easyboot.springboot.restfulapi.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import top.easyboot.core.rowraw.RowRawEntity;
import top.easyboot.core.rowraw.RowRawUtil;
import top.easyboot.springboot.restfulapi.gateway.core.WebSocketSessionBase;
import top.easyboot.springboot.restfulapi.gateway.exception.SessionException;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.IRowRawApiService;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties;
import top.easyboot.springboot.restfulapi.util.ConnectionIdUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Date;

@Service
public class SessionService extends SessionAbstractService {
    /**
     * 链接id工具
     */
    protected ConnectionIdUtil connectionIdUtil;

    @Autowired
    private IRowRawApiService rowRawApiService;


    public SessionService(RestfulApiGatewayProperties.WebSocket webSocket) {

        String connectionIdPrefix = webSocket.getConnectionIdPrefix();
        if (connectionIdPrefix == null || connectionIdPrefix.isEmpty()){
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
                return containsKey(connectionId);
            }
        };
        connectionIdUtil.setConnectionIdPrefixByIpV4(connectionIdPrefix);
    }

    @Override
    public void start(){
        System.out.println("start-start");
        super.start();
    }
    @Override
    public void stop(){
        System.out.println("stop-stop");
        super.stop();
    }

    @Override
    public void refreshBindUid(String connectionId, String uid) {

    }

    @Override
    public String generateConnectionId() throws SessionException {
        if (connectionIdUtil == null){
            throw new SessionException("connectionIdUtil is null");
        }
        try {
            return connectionIdUtil.generateConnectionId();
        }catch (ConnectionIdUtil.Exception e){
            throw new SessionException(e.getMessage(), e);
        }
    }

    protected void onWebSocketMessage(String connectionId, WebSocketMessage message){
        if (isRowRawEntityAndHandler(connectionId, message)){
            return;
        }
    }

    protected boolean isRowRawEntityAndHandler(String connectionId, WebSocketMessage webSocketMessage){
        WebSocketMessage.Type type = webSocketMessage.getType();
        RowRawEntity entity;
        if (type == WebSocketMessage.Type.TEXT){
            entity = RowRawUtil.parse(webSocketMessage.getPayloadAsText().getBytes());
        }else if (type == WebSocketMessage.Type.BINARY){
            DataBuffer buffer = webSocketMessage.getPayload();
            byte[] pos = new byte[buffer.readableByteCount()];
            buffer.read(pos);
            entity = RowRawUtil.parse(pos);
        }else{
            return false;
        }
        if (entity.getProtocol() == null){
            return false;
        }
        String method = entity.getMethod();
        String status = entity.getStatus();
        if (method!=null){
            if (restfulProtocol.equals(entity.getProtocol())){
                InetSocketAddress remoteAddress = get(connectionId).getRemoteAddress();

                rowRawApiService.rpcApi(entity, connectionId, remoteAddress, null, null, type != WebSocketMessage.Type.TEXT);
//                rpcApi(entity, connectionId, type);
            }else if (signalProtocol.equals(entity.getProtocol())){
                // 如果是一个ping
                if (SIGNAL.equals(entity.getMethod()) && pingPath.equals(entity.getPath())){
                    // 调用pong回应
                    pong(connectionId);
                }
            }

        }else if (status!=null){
            System.out.println("响应");

        }

        return true;
    }


    protected void taskRun(){
        long now = new Date().getTime()/1000;
        for (String connectionId : keySet()) {
            WebSocketSessionBase session = get(connectionId);
            long pingInterval = now - (session.getUpdatedAt().getTime()/1000);
            if (pingInterval>45){
                ping(connectionId);
            } else if (pingInterval>60*5){
                session.close();
                continue;
            }
            Date authAccessAt = session.getAuthAccessAt();
            // 每2分钟刷新一次授权信息
            if (authAccessAt == null || (now - (authAccessAt.getTime()/1000))>120){
                pingAuth(connectionId);
            }
        }
    }


}
