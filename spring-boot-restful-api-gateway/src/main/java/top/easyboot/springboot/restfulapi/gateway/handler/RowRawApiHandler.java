package top.easyboot.springboot.restfulapi.gateway.handler;

import org.springframework.web.reactive.socket.WebSocketMessage;
import top.easyboot.core.rowraw.RowRawEntity;
import top.easyboot.springboot.restfulapi.gateway.interfaces.handler.IRowRawMessageHandler;

public class RowRawApiHandler implements IRowRawMessageHandler {
    @Override
    public void onRowRawMessage(String connectionId, RowRawEntity entity, WebSocketMessage message) {

    }
}
