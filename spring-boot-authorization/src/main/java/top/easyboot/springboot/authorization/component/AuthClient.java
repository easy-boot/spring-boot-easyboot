package top.easyboot.springboot.authorization.component;

import top.easyboot.springboot.authorization.entity.Authorization;
import top.easyboot.springboot.authorization.entity.AuthorizationInput;
import top.easyboot.springboot.authorization.entity.AuthorizationSign;
import top.easyboot.springboot.authorization.interfaces.AuthSignDrive;
import top.easyboot.springboot.authorization.exception.AuthSignException;
import top.easyboot.springboot.authorization.utils.Str;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthClient {
    /**
     * 授权信息存储操作对象
     */
    protected Storage storage;
    /**
     * 缓存授权驱动对象
     */
    protected HashMap<String, AuthSignDrive> authSignDriveMap = new HashMap<>();

    /**
     * 驱动
     */
    protected Set<String> drives = new TreeSet();

    public AuthClient(Storage storage){
        this.storage = storage;
        this.drives.add("top.easyboot.springboot.authorization.drives.");
    }
    /**
     * 获取一个授权对象
     * @param authorizationInput
     * @return
     * @throws AuthSignException
     */
    public Authorization getAuthorization(AuthorizationInput authorizationInput) throws AuthSignException {
        String delimiter;
        String authVersion;
        String authValue;
        /**
         * 试图获取授权头
         */
        String authorizationOrigin = authorizationInput.getHeaders().get("authorization");

        if (authorizationOrigin == null || authorizationOrigin.isEmpty()){
            /**
             * 试图不区分大小写获取
             */
            for (String key : authorizationInput.getHeaders().keySet()) {
                if (key != null && !key.isEmpty() && key.toLowerCase().equals("authorization")){
                    authorizationOrigin = authorizationInput.getHeaders().get(key);
                }
            }
        }

        if (authorizationOrigin == null || authorizationOrigin.isEmpty()){
            throw new AuthSignException(AuthSignException.E_AUTHENTICATION_INFO_ERROR);
        }
        int delimiterSlantingIndex = authorizationOrigin.indexOf("/");
        int delimiterSpaceIndex = authorizationOrigin.indexOf(" ");
        if (delimiterSlantingIndex < 0 && delimiterSpaceIndex < 0){
            throw new AuthSignException(AuthSignException.E_AUTHENTICATION_FORMAT_ERROR);
        }
        if (delimiterSlantingIndex < delimiterSpaceIndex || (delimiterSpaceIndex < 0 && delimiterSlantingIndex >= 0)){
            delimiter = "/";
            authVersion = authorizationOrigin.substring(0, delimiterSlantingIndex);
            authValue = authorizationOrigin.substring(delimiterSlantingIndex+1);
        }else{
            delimiter = " ";
            authVersion = authorizationOrigin.substring(0, delimiterSpaceIndex);
            authValue = authorizationOrigin.substring(delimiterSpaceIndex+1);
        }
        Pattern linePattern = Pattern.compile("-(\\w)");

        Matcher matcher = linePattern.matcher(authVersion);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        String className = Str.toUpperCaseFirstOne(sb.toString());

        /**
         * 实例化授权模型
         */
        AuthorizationSign authorizationSign = new AuthorizationSign();

        /**
         * 设置授权信息
         */
        authorizationSign.setDelimiter(delimiter);
        authorizationSign.setClassName(className);
        authorizationSign.setAuthVersion(authVersion);
        authorizationSign.setAuthValue(authValue);
        authorizationSign.setInput(authorizationInput);
        authorizationSign.setStorage(storage);

        /**
         * 获取授权签名服务
         */
        AuthSignDrive drive = getAuthSignDrive(className);
        /**
         * 检查授权信息
         */
        drive.runAuthSign(authorizationSign);

        /**
         * 返回授权信息
         */
        return authorizationSign.getAuthorization();
    }
    public AuthSignDrive getAuthSignDrive(String className) throws AuthSignException {
        if (className.isEmpty() || className == null){
            throw new AuthSignException(AuthSignException.E_AUTHENTICATION_INFO_ERROR);
        }
        if (!authSignDriveMap.containsKey(className)){
            HashMap md = new HashMap();
            md.put("className", className);
            Class authClass = null;

            for (String drive : this.drives) {
                try {
                    authClass = Class.forName(drive + className);
                    if (authClass!= null){
                        break;
                    }
                }catch (ClassNotFoundException e){}
            }
            if (authClass == null){
                throw new AuthSignException(AuthSignException.E_AUTHENTICATION_VERSION_CLASS_NOT_FIND, md);
            }

            if (!AuthSignDrive.class.isAssignableFrom(authClass)){
                throw new AuthSignException(AuthSignException.E_AUTHENTICATION_CLASS_INSTANCE_ERROR, md);
            }

            try {
                authSignDriveMap.put(className, (AuthSignDrive)authClass.newInstance());
            }catch (InstantiationException e){
                AuthSignException et = new AuthSignException(AuthSignException.E_AUTHENTICATION_CLASS_INSTANCE_ERROR, md);
                et.setStackTrace(e.getStackTrace());
                throw et;
            } catch (IllegalAccessException e) {
                AuthSignException et = new AuthSignException(AuthSignException.E_AUTHENTICATION_CLASS_INSTANCE_ERROR, md);
                et.setStackTrace(e.getStackTrace());
                throw et;
            }
        }

        return authSignDriveMap.get(className);
    }
    public interface Storage{
        /**
         * 获取一条授权数据
         * @param accessKeyId
         * @return
         */
        String get(String accessKeyId);

        /**
         * 存储一条授权数据
         * @param accessKeyId
         * @param data
         */
        void put(String accessKeyId, String data);
    }
}
