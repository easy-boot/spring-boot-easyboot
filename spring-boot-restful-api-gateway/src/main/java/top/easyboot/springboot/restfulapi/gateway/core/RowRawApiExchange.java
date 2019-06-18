package top.easyboot.springboot.restfulapi.gateway.core;

import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.server.adapter.DefaultServerWebExchange;
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.WebSessionManager;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public final class RowRawApiExchange extends DefaultServerWebExchange {
    private RowRawApiExchange(RowRawApiRequest request, RowRawApiResponse response,  WebSessionManager sessionManager) {
        super(request, response, sessionManager,
                ServerCodecConfigurer.create(), new AcceptHeaderLocaleContextResolver());
    }


    public static RowRawApiExchange create(RowRawApiRequest request, RowRawApiResponse response, WebSessionManager sessionManager) throws URISyntaxException, MalformedURLException {


        return new RowRawApiExchange(request, response,
                sessionManager != null ? sessionManager : new DefaultWebSessionManager());
    }

    @Override
    public RowRawApiResponse getResponse() {
        return (RowRawApiResponse) super.getResponse();
    }
}
