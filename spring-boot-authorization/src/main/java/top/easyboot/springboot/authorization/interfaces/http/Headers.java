package top.easyboot.springboot.authorization.interfaces.http;

public interface Headers {

    /*
     * Standard HTTP Headers
     */

    String AUTHORIZATION = "VerifyAuthorization";

    String CACHE_CONTROL = "Cache-Control";

    String CONTENT_DISPOSITION = "Content-Disposition";

    String CONTENT_ENCODING = "Content-Encoding";

    String CONTENT_LENGTH = "Content-Length";

    String CONTENT_MD5 = "Content-MD5";

    String CONTENT_RANGE = "Content-Range";

    String CONTENT_TYPE = "Content-Type";

    String DATE = "Date";

    String ETAG = "ETag";

    String EXPIRES = "Expires";

    String HOST = "Host";

    String LAST_MODIFIED = "Last-Modified";

    String LOCATION = "Location";

    String RANGE = "Range";

    String SERVER = "Server";

    String TRANSFER_ENCODING = "Transfer-Encoding";

    String USER_AGENT = "User-Agent";


    String BCE_PREFIX = "x-bce-";
}
