package top.easyboot.springboot.restfulapi.gateway.core;


import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.Date;

public class WebSocketRestfulSession extends WebSocketRestfulSessionBase {
    private String connectionId;
    public WebSocketRestfulSession(String connectionId, WebSocketSession webSocketSession){
        super(webSocketSession);
        this.connectionId = connectionId;
        init();
    }
    protected void init(){

    }
    public void ping(){
        long time = new Date().getTime()/1000;
        System.out.println("定时任务执行时间：" + time);
        textMessage("time:"+time);
    }
    @Override
    protected void onWebSocketMessage(WebSocketMessage webSocketMessage){

        System.out.println("ss");
        System.out.println(webSocketMessage.getType());
        String aa = webSocketMessage.getPayloadAsText();
        System.out.println(aa);
        System.out.println(webSocketMessage.getPayloadAsText());
        if (aa.equals("close1")){
            close();
        }else{
            this.textMessage("sss+++:"+aa);
        }
    }
}