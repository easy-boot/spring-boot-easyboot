package top.easyboot.springboot.restfulapi.gateway.service;

import org.springframework.beans.BeansException;
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
import top.easyboot.springboot.utils.core.HexIp;
import top.easyboot.springboot.utils.core.PortUniqueId;
import top.easyboot.springboot.utils.interfaces.core.IPortUniqueId;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SessionService extends SessionAbstractService  implements ApplicationContextAware {

    protected List<ISessionMessageHandler> messageHandlers;

    protected RowRawApiHandler rowRawApiHandler;

    protected String connectionIdPrefix;

    protected IPortUniqueId portUniqueId;

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
        portUniqueId = new PortUniqueId();
        connectionIdPrefixInit(webSocket);
    }

    @Override
    protected void onCreate(String connectionId) {
        try {
            for (ISessionMessageHandler messageHandler : messageHandlers) {
                try {
                    messageHandler.create(connectionId);
                }catch (Throwable throwable){
                }
            }
        }catch (Throwable throwable){
        }
    }

    @Override
    public void refreshBindUid(String connectionId, String uid) {

    }

    @Override
    public String generateConnectionId() throws SessionException {
        String batchRecorderHex;
        String connectionId;
        int times = 0;
        while (true){
            batchRecorderHex = portUniqueId.getNextUniqueId();
            connectionId = connectionIdPrefix + batchRecorderHex;
            if (!containsKey(connectionId)){
                return connectionId;
            }
            if (portUniqueId.getFisrtUniqueId().equals(batchRecorderHex) && (++times)>=2){
                throw new SessionException(SessionException.E_NOT_AVAILABLE_CONNECTION_ID);
            }
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

    protected void connectionIdPrefixInit(WebSocket webSocket){
        String ip = webSocket.getRpcIp();
        if (ip == null || ip.isEmpty()){
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            }catch (UnknownHostException e){
            }
        }
        if (ip == null || ip.isEmpty()){
            ip = "127.0.0.1";
        }
        String ipHex = HexIp.ipToHex(ip);
        connectionIdPrefix = (ipHex.length() == 8 ? "4":"6") + ipHex;
    }

}
