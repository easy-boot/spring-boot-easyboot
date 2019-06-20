package top.easyboot.springboot.restfulapi.gateway.service;

import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import top.easyboot.core.rowraw.RowRawEntity;
import top.easyboot.core.rowraw.RowRawUtil;
import top.easyboot.springboot.restfulapi.gateway.core.WebSocketSessionAbstract;
import top.easyboot.springboot.restfulapi.gateway.core.WebSocketSessionBase;
import top.easyboot.springboot.restfulapi.gateway.exception.SessionException;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.ISessionService;
import top.easyboot.springboot.restfulapi.util.ConnectionIdUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public abstract class SessionAbstractService extends HashMap<String, WebSocketSessionBase> implements ISessionService {
    /**
     * 是否正在运行
     */
    protected String signalProtocol = "EASYBOOTSIGNAL";
    protected final String SIGNAL = "SIGNAL";
    protected String restfulProtocol = "EASYBOOTRESTFUL";
    protected final String pingPath = "/ping";
    /**
     * 每秒任务定时器
     */
    private Timer taskTimer;

    abstract void taskRun();
    abstract void onWebSocketMessage(String connectionId, WebSocketMessage message);

    protected void pong(String connectionId) {
        // pong
        RowRawEntity rawEntity = new RowRawEntity();
        rawEntity.setProtocol(signalProtocol);
        rawEntity.setStatus("200");
        rawEntity.setStatusText("OK");
        if (containsKey(connectionId)){
            get(connectionId).textMessage(new String(RowRawUtil.stringify(rawEntity)));
        }
    }

    protected void ping(String connectionId) {
        // ping
        RowRawEntity rawEntity = new RowRawEntity();
        rawEntity.setProtocol(signalProtocol);
        rawEntity.setMethod(SIGNAL);
        rawEntity.setPath("/ping");
        if (containsKey(connectionId)){
            get(connectionId).textMessage(new String(RowRawUtil.stringify(rawEntity)));
        }
    }

    protected void pingAuth(String connectionId) {
        // ping
        RowRawEntity rawEntity = new RowRawEntity();
        rawEntity.setProtocol(signalProtocol);
        rawEntity.setMethod(SIGNAL);
        rawEntity.setPath("/ping/auth");
        if (containsKey(connectionId)){
            get(connectionId).textMessage(new String(RowRawUtil.stringify(rawEntity)));
        }
    }


    @Override
    public WebSocketSessionBase remove(Object key) {
        if (!containsKey(key)){
            return null;
        }
        WebSocketSessionBase session = super.remove(key);
        session.close();
        return session;
    }
    @Override
    public WebSocketSessionAbstract createSession(final WebSocketSession webSocketSession) throws SessionException {
        try {
            // 创建连接id
            String connectionId = generateConnectionId();
            // 会话服务
            SessionAbstractService sessionService = this;
            // 创建会话
            WebSocketSessionAbstract session = new WebSocketSessionAbstract(connectionId, webSocketSession){
                @Override
                public void onWebSocketMessage(WebSocketMessage message) {
                    if (sessionService.containsKey(connectionId)){
                        sessionService.get(connectionId).setUpdatedAt(new Date());
                        sessionService.onWebSocketMessage(connectionId, message);
                    }
                }
            };
            // 保存会话
            put(connectionId, session);
            // 绑定关闭事件
            session.onClose(()->{
                if (containsKey(connectionId)){
                    remove(connectionId);
                }
            });
            // 返回会话
            return session;
        }catch (ConnectionIdUtil.Exception e){
            throw new SessionException(e.getMessage(), e);
        }
    }
    @Override
    public void start() {
        System.out.println("444-start");
        taskStart();
    }

    @Override
    public void stop() {
        System.out.println("444-stop");
        taskStop();
    }
    protected void taskStart(){
        taskStop();
        taskTimer = new Timer();
        taskTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    taskRun();
                }catch (Throwable e){
                    e.printStackTrace();
                }
            }
        }, new Date(), 5000);
    }

    protected void taskStop(){
        try {
            if (taskTimer!=null){
                taskTimer.cancel();
            }
        }catch (Throwable e){
        }
    }

}
