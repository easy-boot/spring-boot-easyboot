package top.easyboot.springboot.restfulapi.gateway.interfaces.handler;

import top.easyboot.core.rowraw.RowRawEntity;

public interface ISessionMessageHandler {
    boolean onRowRawMessage(String connectionId, RowRawEntity entity, boolean isBinary);
}
