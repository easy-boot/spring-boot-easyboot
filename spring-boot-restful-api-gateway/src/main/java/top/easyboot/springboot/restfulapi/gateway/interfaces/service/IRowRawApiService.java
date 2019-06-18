package top.easyboot.springboot.restfulapi.gateway.interfaces.service;

import org.springframework.http.server.reactive.SslInfo;
import org.springframework.lang.Nullable;
import org.springframework.web.server.session.WebSessionManager;
import top.easyboot.core.rowraw.RowRawEntity;

import java.net.InetSocketAddress;

public interface IRowRawApiService {
    void rpcApi(RowRawEntity entity, String connectionId, InetSocketAddress remoteAddress, @Nullable SslInfo sslInfo, WebSessionManager sessionManager, boolean isByte);
}
