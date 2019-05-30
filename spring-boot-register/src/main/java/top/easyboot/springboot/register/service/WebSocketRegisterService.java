package top.easyboot.springboot.register.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import top.easyboot.springboot.register.property.WebSocketRegisterProperties;
import top.easyboot.springboot.register.utils.Jackson;

import java.io.IOException;
import java.util.HashMap;

@Service
public class WebSocketRegisterService implements WebSocketHandler {
    @Autowired
    private WebSocketRegisterProperties properties;
    /**
     * 秘钥
     */
    private static String secretKey;
    /**
     * 会话池
     */
    private HashMap<String, Session> sessionHashMap = new HashMap();

//    private gatewayConnections


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if (secretKey == null || secretKey.isEmpty()){
            secretKey = properties.getSecretKey();
        }
        String sid = session.getId();
        sessionHashMap.put(sid, new Session(session));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String sid = session.getId();
        if (sessionHashMap.containsKey(sid)){
            sessionHashMap.get(sid).handleMessage(message);
        }else{
            session.close();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        try{
            String sid = session.getId();
            if (sessionHashMap.containsKey(sid)){
                sessionHashMap.get(sid).close();
            }
        }catch (Throwable e){
        }
        try{
            session.close();
        }catch (Throwable e){
        }
    }
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        try{
            String sid = session.getId();
            if (sessionHashMap.containsKey(sid)){
                sessionHashMap.get(sid).close();
            }
        }catch (Throwable e){
        }
    }
    public void broadcastAddresses(){

    }
    public final static class Session{
        /**
         * 是否为worker
         */
        private boolean isWorker = false;
        /**
         * 是否为网关
         */
        private boolean isGateway = false;
        /**
         * 长连接会话
         */
        private WebSocketSession session;
        public Session(WebSocketSession session){
            this.session = session;
            System.out.println("系统WebSocket连接已建立！");
        }

        /**
         * 接受消息
         * @param message 长连接会话消息
         */
        public void handleMessage(WebSocketMessage<?> message){
            if (message instanceof TextMessage || message instanceof BinaryMessage){
                SessionData data = SessionData.create(message.toString());
                String event = data.getEvent();
                if (event == null){
                    close();
                    return;
                }else if (event.equals("ping")){
                    return;
                }else if (event.equals("gateway_connect") || event.equals("worker_connect")){
                    if ((secretKey!= null && !secretKey.isEmpty())&&(data.getSecretKey() == null || (!data.getSecretKey().equals(secretKey)))){
                        close();
                        return;
                    }
                    if (event.equals("gateway_connect")){
                        String address = data.getAddress();
                        if (address == null || address.isEmpty()){
                            close();
                            return;
                        }
                        // 网关消息
                        isGateway = true;

                    }else{
                        // 进程消息
                        isWorker = true;
                    }
                    return;
                }
            }
            close();
        }

        public void close(){
            if (session!= null && session.isOpen()){
                try {
                    session.close();
                }catch (IOException e){
                }
            }
        }

        public boolean isWorker() {
            return isWorker;
        }

        public boolean isGateway() {
            return isGateway;
        }

        public WebSocketSession getSession() {
            return session;
        }
    }
    public final static class SessionData{
        /**
         * 事件
         */
        private String event;
        /**
         * 地址
         */
        private String address;
        /**
         * 秘钥
         */
        private String secretKey;

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public static SessionData create(String dataStr){
            SessionData data = null;
            if (dataStr != null && !dataStr.isEmpty() && dataStr != ""){
                try {
                    data = Jackson.getObjectMapper().readValue(dataStr, SessionData.class);
                }catch (Exception e){
                    // todo
                    e.printStackTrace();
                }
            }
            if (data == null){
                data = new SessionData();
            }
            return data;
        }
    }
}
