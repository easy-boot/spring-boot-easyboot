package top.easyboot.springboot.authorization.utils;

import top.easyboot.springboot.authorization.Protocol;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.*;

import top.easyboot.springboot.authorization.interfaces.http.Headers;
import top.easyboot.springboot.utils.core.URLUtil;

public class HttpUtils {
    /**
     * Returns a host header according to the specified URI. The host header is generated with the same logic used by
     * apache http client, that is, append the port to hostname only if it is not the default port.
     *
     * @param uri the URI
     * @return a host header according to the specified URI.
     */
    public static String generateHostHeader(URI uri) {
        String host = uri.getHost();
        if (isUsingNonDefaultPort(uri)) {
            host += ":" + uri.getPort();
        }
        return host;
    }

    /**
     * Returns true if the specified URI is using a non-standard port (i.e. any port other than 80 for HTTP URIs or any
     * port other than 443 for HTTPS URIs).
     *
     * @param uri the URI
     * @return True if the specified URI is using a non-standard port, otherwise false.
     */
    public static boolean isUsingNonDefaultPort(URI uri) {
        String scheme = uri.getScheme().toLowerCase();
        int port = uri.getPort();
        if (port <= 0) {
            return false;
        }
        if (scheme.equals(Protocol.HTTP.toString())) {
            return port != Protocol.HTTP.getDefaultPort();
        }
        if (scheme.equals(Protocol.HTTPS.toString())) {
            return port != Protocol.HTTPS.getDefaultPort();
        }
        return false;
    }

    public static String getCanonicalURIPath(String path) {
        if (path == null) {
            return "/";
        } else if (path.startsWith("/")) {
            return URLUtil.urlEncodeExceptSlash(path);
        } else {
            return "/" + URLUtil.urlEncodeExceptSlash(path);
        }
    }

    public static String getCanonicalQueryString(String query, Set<String> noSignQueryKeys) {
        if (query == null || query.equals("")) {
            return "";
        }
        List<String> parameterStrings = new ArrayList();
        if (query != null && !query.equals("")) {
            for (String t : query.split("&")) {
                int index = t.indexOf("=");
                String key = t.substring(0, index);
                String value = t.substring(index + 1);
                if (key == null) {
                    throw new NullPointerException("parameter key should not be null");
                }
                if (noSignQueryKeys!= null && noSignQueryKeys.contains(key.toLowerCase())){
                    continue;
                }
                if (Headers.AUTHORIZATION.equalsIgnoreCase(key)) {
                    continue;
                }
                if (value == null) {
                    parameterStrings.add(URLUtil.urlEncode(URLUtil.urlDecode(key)) + '=');
                } else {
                    parameterStrings.add(URLUtil.urlEncode(URLUtil.urlDecode(key)) + '=' + URLUtil.urlEncode(URLUtil.urlDecode(value)));
                }
            }
        }
        Collections.sort(parameterStrings);
        return String.join("&", parameterStrings);
    }
    public static String getCanonicalHeaders(Map<String, String> headers, Set<String> noSignHeadersKeys,Set<String> signedHeaders) {
        if (headers.isEmpty()) {
            return "";
        }

        List<String> headerStrings = new ArrayList();

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            if (key == null) {
                continue;
            }
            String value = entry.getValue();
            if (value == null) {
                value = "";
            }
            if (noSignHeadersKeys!= null && noSignHeadersKeys.contains(key.toLowerCase())){
                continue;
            }
            if (Headers.AUTHORIZATION.equalsIgnoreCase(key)) {
                continue;
            }
            signedHeaders.add(key.trim());
            headerStrings.add(URLUtil.urlEncode(key.trim().toLowerCase()) + ':' + URLUtil.urlEncode(value.trim()));
        }
        Collections.sort(headerStrings);

        return  String.join("\n", headerStrings);
    }

}
