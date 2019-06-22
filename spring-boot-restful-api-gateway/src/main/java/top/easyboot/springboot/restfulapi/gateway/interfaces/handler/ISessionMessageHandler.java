package top.easyboot.springboot.restfulapi.gateway.interfaces.handler;

import top.easyboot.core.rowraw.RowRawEntity;

public interface ISessionMessageHandler {
    void create(String connectionId);
    boolean onRowRawMessage(String connectionId, RowRawEntity entity, boolean isBinary);
}
