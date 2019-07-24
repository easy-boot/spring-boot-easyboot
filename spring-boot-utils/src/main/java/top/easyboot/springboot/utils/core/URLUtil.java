package top.easyboot.springboot.utils.core;

import org.springframework.http.HttpHeaders;
import sun.net.util.IPAddressUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;

public class URLUtil {
    private static String DEFAULT_ENCODING = "UTF-8";
    private static BitSet URI_UNRESERVED_CHARACTERS = new BitSet();
    private static String[] PERCENT_ENCODED_STRINGS = new String[256];
    private static final BuilderURLStreamHandler urlStreamHandler = new BuilderURLStreamHandler();
    public static Builder parse(URL url) throws MalformedURLException  {
        return new Builder(url);
    }

    public static URL build(Builder builder) {
        return builder.build();
    }

    public static Map parseQuery(URL url) {
        return parseQuery(url.getQuery());
    }
    public static Map parseQuery(String query) {
        return null;
//        return new QueryBuilder(query);
    }

    public static String buildQuery(Map builder) {
        return String.join("&", buildQuery(builder, ""));
    }

    protected static List<String> buildQuery(Map data, String prefix) {
        List<String> parameterStrings = new ArrayList();
        if (data != null){
            for (Object key : data.keySet()) {
                addParameter(parameterStrings, (prefix == null || prefix.isEmpty()) ? String.valueOf(key) : (prefix + "[" + key + "]"), data.get(key));
            }
        }
        return parameterStrings;
    }
    protected static List<String> buildQuery(List data, String prefix) {
        List<String> parameterStrings = new ArrayList();
        if (data != null){
            for (int i = 0; i < data.size(); i++) {
                Object value = data.get(i);
                addParameter(parameterStrings, (prefix == null || prefix.isEmpty()) ? String.valueOf(i) : (prefix + "[" + ((value instanceof Map|value instanceof Set|value instanceof List) ? i : "") + "]"), value);
            }
        }
        return parameterStrings;
    }
    protected static void addParameter(List<String> parameterStrings,String name, Object value){
        if (value == null || value instanceof String || value instanceof Number || value instanceof Boolean) {
            final String v;
            if (value == null){
                v = "";
            }else if (value instanceof String){
                v = (String)value;
            }else if (value instanceof Number){
                v = String.valueOf(value);
            }else if (value instanceof Boolean){
                v = (Boolean)value ? "true" : "false";
            }else{
                v = "";
            }
            parameterStrings.add(urlEncode(name) + "=" + urlEncode(v));
        } else if (value instanceof Map){
            parameterStrings.addAll(buildQuery((Map)value, name));
        } else if (value instanceof List){
            parameterStrings.addAll(buildQuery((List)value, name));
        } else if (value instanceof Set){
            parameterStrings.addAll(buildQuery(new ArrayList((List)value), name));
        } else if (value.getClass().isArray()){
            parameterStrings.addAll(buildQuery(Arrays.asList(value), name));
        }
    }

    public static class Builder{
        private final URL url;
        public Builder(URL url) throws MalformedURLException {
            this.url = new URL(url, "", urlStreamHandler);
        }

        public URL getUrl() {
            return url;
        }
        public String getProtocol() {
            return url.getProtocol();
        }
        public String getHost() {
            return url.getHost();
        }
        public int getPort() {
            return url.getPort();
        }
        public String getPath() {
            return url.getPath();
        }
        public String getQuery() {
            return url.getQuery();
        }
        public Map getQueryMap() {
            return parseQuery(getQuery());
        }
        public String getRef() {
            return url.getRef();
        }
        public String getAuthority() {
            return url.getAuthority();
        }
        public String getUserInfo() {
            return url.getUserInfo();
        }
        public void setHost(String host) {
            final String authority = (getPort() == -1) ? host : host + ":" + getPort();
            urlStreamHandler.setURL(url, getProtocol(), host, getPort(), authority, getUserInfo(), getPath(), getQuery(), getRef());
        }
        public void setProtocol(String protocol){
            urlStreamHandler.setURL(url, protocol, getHost(), getPort(), getAuthority(), getUserInfo(), getPath(), getQuery(), getRef());
        }
        public void setPort(int port){
            final String authority = (port == -1) ? getHost() : getHost() + ":" + port;
            urlStreamHandler.setURL(url, getProtocol(), getHost(), port, authority, getUserInfo(), getPath(), getQuery(), getRef());
        }
        public void setPath(String path){
            urlStreamHandler.setURL(url, getProtocol(), getHost(), getPort(), getAuthority(), getUserInfo(), path, getQuery(), getRef());
        }
        public void setQuery(String query){
            urlStreamHandler.setURL(url, getProtocol(), getHost(), getPort(), getAuthority(), getUserInfo(), getPath(), query, getRef());
        }
        public void setQueryMap(Map queryMap){
            setQuery(buildQuery(queryMap));
        }
        public void setRef(String ref){
            urlStreamHandler.setURL(url, getProtocol(), getHost(), getPort(), getAuthority(), getUserInfo(), getPath(), getQuery(), ref);
        }
        public void setAuthority(String authority){
            int port = -1;
            String host;
            String userInfo;
            int ind = authority.indexOf('@');
            if (ind != -1) {
                if (ind != authority.lastIndexOf('@')) {
                    // more than one '@' in authority. This is not server based
                    userInfo = null;
                    host = null;
                } else {
                    userInfo = authority.substring(0, ind);
                    host = authority.substring(ind+1);
                }
            } else {
                host = authority;
                userInfo = null;
            }
            if (host != null) {
                // If the host is surrounded by [ and ] then its an IPv6
                // literal address as specified in RFC2732
                if (host.length()>0 && (host.charAt(0) == '[')) {
                    if ((ind = host.indexOf(']')) > 2) {

                        String nhost = host ;
                        host = nhost.substring(0,ind+1);
                        if (!IPAddressUtil.
                                isIPv6LiteralAddress(host.substring(1, ind))) {
                            throw new IllegalArgumentException(
                                    "Invalid host: "+ host);
                        }

                        port = -1 ;
                        if (nhost.length() > ind+1) {
                            if (nhost.charAt(ind+1) == ':') {
                                ++ind ;
                                // port can be null according to RFC2396
                                if (nhost.length() > (ind + 1)) {
                                    port = Integer.parseInt(nhost.substring(ind+1));
                                }
                            } else {
                                throw new IllegalArgumentException(
                                        "Invalid authority field: " + authority);
                            }
                        }
                    } else {
                        throw new IllegalArgumentException(
                                "Invalid authority field: " + authority);
                    }
                } else {
                    ind = host.indexOf(':');
                    port = -1;
                    if (ind >= 0) {
                        // port can be null according to RFC2396
                        if (host.length() > (ind + 1)) {
                            port = Integer.parseInt(host.substring(ind + 1));
                        }
                        host = host.substring(0, ind);
                    }
                }
            } else {
                host = "";
            }
            if (port < -1)
                throw new IllegalArgumentException("Invalid port number :" +
                        port);

            authority = (userInfo==null || userInfo.isEmpty() ? "" : (userInfo+"@")) + host +  ((port == -1) ? "" : ":" + port);
            urlStreamHandler.setURL(url, getProtocol(), host, port, authority, userInfo, getPath(), getQuery(), getRef());
        }
        public void setUserInfo(String userInfo){
            urlStreamHandler.setURL(url, getProtocol(), getHost(), getPort(), getAuthority(), userInfo, getPath(), getQuery(), getRef());
        }
        public URL build(){
            return url;
        }
        @Override
        public String toString() {
            return url.toString();
        }
    }

    private static class BuilderURLStreamHandler extends URLStreamHandler{
        @Override
        protected URLConnection openConnection(URL u) throws IOException {
            return null;
        }
        public void setURL(URL u, String protocol, String host, int port,
                            String authority, String userInfo, String path,
                            String query, String ref){
            super.setURL(u, protocol, host, port, authority, userInfo, path, query, ref);
        }
    }

    /**
     * 解密
     * @param value
     * @return
     */
    public static String urlDecode(String value) {
        try {
            return URLDecoder.decode(value, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 编码
     * @param value
     * @return
     */
    public static String urlEncode(String value) {
        try {
            StringBuilder builder = new StringBuilder();
            for (byte b : value.getBytes(DEFAULT_ENCODING)) {
                if (URI_UNRESERVED_CHARACTERS.get(b & 0xFF)) {
                    builder.append((char) b);
                } else {
                    builder.append(PERCENT_ENCODED_STRINGS[b & 0xFF]);
                }
            }
            return builder.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    public static String urlEncodeExceptSlash(String path) {
        return urlEncode(path).replace("%2F", "/");
    }
    static {
        for (int i = 'a'; i <= 'z'; i++) {
            URI_UNRESERVED_CHARACTERS.set(i);
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            URI_UNRESERVED_CHARACTERS.set(i);
        }
        for (int i = '0'; i <= '9'; i++) {
            URI_UNRESERVED_CHARACTERS.set(i);
        }
        URI_UNRESERVED_CHARACTERS.set('-');
        URI_UNRESERVED_CHARACTERS.set('.');
        URI_UNRESERVED_CHARACTERS.set('_');
        URI_UNRESERVED_CHARACTERS.set('~');

        for (int i = 0; i < PERCENT_ENCODED_STRINGS.length; ++i) {
            PERCENT_ENCODED_STRINGS[i] = String.format("%%%02X", i);
        }

    }
}
