package top.easyboot.springboot.authorization.component;

import top.easyboot.springboot.authorization.drives.EasyBootAuthV1;
import top.easyboot.springboot.authorization.entity.Authorization;
import top.easyboot.springboot.authorization.entity.AuthorizationInput;
import top.easyboot.springboot.authorization.entity.AuthorizationSign;
import top.easyboot.springboot.authorization.interfaces.AuthSignDrive;
import top.easyboot.springboot.authorization.exception.AuthSignException;
import top.easyboot.springboot.authorization.interfaces.core.IAuthClient;
import top.easyboot.springboot.authorization.utils.Str;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthClient implements IAuthClient {
    /**
     * 授权信息存储操作对象
     */
    protected Storage storage;
    /**
     * 缓存授权驱动对象
     */
    protected Map<String, AuthSignDrive> authSignDriveMap;

    private Pattern linePattern = Pattern.compile("-(\\w)");

    public AuthClient(Storage storage){
        this(storage, new HashMap<>());
        EasyBootAuthV1 easyBootAuthV1 = new EasyBootAuthV1();
        authSignDriveMap.put("easyBootAuthV1", easyBootAuthV1);
        authSignDriveMap.put("easybootAuthV1", easyBootAuthV1);
        authSignDriveMap.put("easyboot-auth-v1", easyBootAuthV1);
        authSignDriveMap.put("easy-boot-auth-v1", easyBootAuthV1);
    }
    public AuthClient(Storage s, Map<String, AuthSignDrive> driveMap){
        storage = s;
        authSignDriveMap = driveMap;
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
        Map<String, String> headers = authorizationInput.getHeaders();
        /**
         * 试图获取授权头
         */
        String authorizationOrigin = headers.get("authorization");


        if (authorizationOrigin == null || authorizationOrigin.isEmpty()){
            /**
             * 试图不区分大小写获取
             */
            for (String key : headers.keySet()) {
                if (key != null && !key.isEmpty() && key.toLowerCase().equals("authorization")){
                    authorizationOrigin = headers.get(key);
                }
            }
        }


        if (authorizationOrigin == null || authorizationOrigin.isEmpty()){
            throw new AuthSignException(AuthSignException.E_AUTHENTICATION_INFO_ERROR);
        }
        authorizationOrigin = authorizationOrigin.trim();
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
        if (authVersion==null || authVersion.isEmpty()){
            throw new AuthSignException(AuthSignException.E_AUTHENTICATION_VERSION_ERROR);
        }

        HashMap md = new HashMap();
        md.put("className", authVersion);
        /**
         * 获取授权签名服务
         */
        AuthSignDrive drive;
        if (authSignDriveMap.containsKey(authVersion)){
            drive = authSignDriveMap.get(authVersion);
        }else{
            Matcher matcher = linePattern.matcher(authVersion);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
            }
            matcher.appendTail(sb);
            String className = sb.toString();
            if (authSignDriveMap.containsKey(className)){
                drive = authSignDriveMap.get(className);
            }else if(authSignDriveMap.containsKey(Str.toUpperCaseFirstOne(className))){
                drive = authSignDriveMap.get(Str.toUpperCaseFirstOne(className));
            }else{
                throw new AuthSignException(AuthSignException.E_AUTHENTICATION_VERSION_CLASS_NOT_FIND, md);
            }
        }
        if (!(drive instanceof AuthSignDrive)){
            throw new AuthSignException(AuthSignException.E_AUTHENTICATION_CLASS_INSTANCE_ERROR, md);
        }


        /**
         * 实例化授权模型
         */
        AuthorizationSign authorizationSign = new AuthorizationSign();

        /**
         * 设置授权信息
         */
        authorizationSign.setDelimiter(delimiter);
        authorizationSign.setAuthVersion(authVersion);
        authorizationSign.setAuthValue(authValue);
        authorizationSign.setInput(authorizationInput);
        authorizationSign.setStorage(storage);
        /**
         * 检查授权信息
         */
        drive.runAuthSign(authorizationSign);

        /**
         * 返回授权信息
         */
        return authorizationSign.getAuthorization();
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
