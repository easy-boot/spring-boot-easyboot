package top.easyboot.springboot.restfulapi.gateway.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import top.easyboot.core.rowraw.RowRawEntity;
import top.easyboot.core.rowraw.RowRawUtil;
import top.easyboot.springboot.restfulapi.gateway.exception.SessionException;
import top.easyboot.springboot.restfulapi.gateway.handler.RowRawApiHandler;
import top.easyboot.springboot.restfulapi.gateway.handler.RowRawPingHandler;
import top.easyboot.springboot.restfulapi.gateway.interfaces.handler.ISessionMessageHandler;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties.WebSocket;
import top.easyboot.springboot.restfulapi.util.ConnectionIdUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SessionService extends SessionAbstractService  implements ApplicationContextAware {
    /**
     * 链接id工具
     */
    protected ConnectionIdUtil connectionIdUtil;

    protected List<ISessionMessageHandler> messageHandlers;

    protected RowRawApiHandler rowRawApiHandler;


    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        if (rowRawApiHandler!=null){
            DispatcherHandler handler = context.getBean(DispatcherHandler.class);
            rowRawApiHandler.setDispatcherHandler(handler);
        }
    }

    public SessionService(WebSocket webSocket) {
        this(webSocket, new ArrayList<>());
        rowRawApiHandler = new RowRawApiHandler(this, webSocket);
        messageHandlers.add(rowRawApiHandler);
        messageHandlers.add(new RowRawPingHandler(this, webSocket));
    }
    public SessionService(WebSocket webSocket, List<ISessionMessageHandler> handlers) {
        messageHandlers = handlers;
        connectionIdUtilInit(webSocket);
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
        WebSocketMessage.Type type = message.getType();
        RowRawEntity entity;
        boolean isBinary;
        if (type == WebSocketMessage.Type.TEXT){
            isBinary = false;
            entity = RowRawUtil.parse(message.getPayloadAsText().getBytes());
        }else if (type == WebSocketMessage.Type.BINARY){
            isBinary = true;
            DataBuffer buffer = message.getPayload();
            byte[] pos = new byte[buffer.readableByteCount()];
            buffer.read(pos);
            entity = RowRawUtil.parse(pos);
        }else{
            return;
        }
        for (ISessionMessageHandler messageHandler : messageHandlers) {
            if (messageHandler.onRowRawMessage(connectionId, entity, isBinary)){
                return;
            }
        }
    }




    protected void connectionIdUtilInit(WebSocket webSocket){
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

}
