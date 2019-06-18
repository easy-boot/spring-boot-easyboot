package top.easyboot.springboot.restfulapi.gateway.interfaces.handler;

import org.springframework.web.reactive.socket.WebSocketMessage;
import top.easyboot.core.rowraw.RowRawEntity;

public interface IRowRawMessageHandler {
    void onRowRawMessage(String connectionId, RowRawEntity entity, WebSocketMessage message);
}
