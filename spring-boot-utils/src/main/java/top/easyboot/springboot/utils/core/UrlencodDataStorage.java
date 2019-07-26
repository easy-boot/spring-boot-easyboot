package top.easyboot.springboot.utils.core;

import java.util.*;

public class UrlencodDataStorage {
    private static char keyStart = "[".charAt(0);
    private static char keyEnd = "]".charAt(0);
    private static char keyPoint = ".".charAt(0);
    private static char keySpace = " ".charAt(0);

    public static Map<String, Object> save(String key, String value){
        Map map = new HashMap<>();
        save(key, value, map);
        return map;
    }
    /**
     * 保存数据到map中，确保data是一个map
     * @param key
     * @param value
     * @param inputData
     */
    public static void save(String key, String value, Map<String, Object> inputData){
        if (key ==null || key.isEmpty()){
            return;
        }
        key = URLUtil.urlDecode(key.replaceAll("\\+", "%20").trim());
        int keyLen = key.indexOf("\u0000");
        if (keyLen>-1){
            key = key.substring(0, keyLen);
        }
        if (key.isEmpty() || key.charAt(0) == keyStart){
            return;
        }
        List<String> keyList = new ArrayList();
        int postLeftBracketPos = 0;
        for (int i = 0; i < key.length(); i++) {
            if (key.charAt(i) == keyStart && postLeftBracketPos == 0){
                postLeftBracketPos = i + 1;
            }else if (key.charAt(i) == keyEnd){
                if (postLeftBracketPos != 0){
                    if (keyList.isEmpty()){
                        keyList.add(key.substring(0, postLeftBracketPos-1));
                    }
                    keyList.add(key.substring(postLeftBracketPos, i));
                    postLeftBracketPos = 0;
                    if ((i + 1)>= key.length()|| key.charAt(i + 1) != keyStart){
                        break;
                    }
                }
            }
        }
        if (keyList.isEmpty()){
            keyList.add(key);
        }
        for (int i = 0; i < keyList.get(0).length(); i++) {
            char chr = keyList.get(0).charAt(i);
            if (chr == keySpace || chr == keyPoint || chr == keyStart){
                keyList.set(0, keyList.get(0).substring(0, i) + "_"  + keyList.get(0).substring(i+1));
            }
            if (chr == keyStart){
                break;
            }
        }
        
        Object obj = inputData;
        String objKey = "";
        Object lastObj = obj;
        String lastObjKey = "";

        final int keyListSize = keyList.size();
        for (int i = 0; i < keyListSize; i++) {
            // 试图获取key
            objKey = keyList.get(i).replaceAll("^[\'\"]", "").replaceAll("['\"]$", "").trim();
            Map objNew = null;
            // 数组和集合必须转list
            if (obj instanceof Set || obj.getClass().isArray()){
                objNew = new HashMap();
                final Object[] t;
                if (obj instanceof Set){
                    t = ((Set) obj).toArray();
                }else {
                    t = (Object[])obj;
                }
                for (int j = 0; j < t.length; j++) {
                    objNew.put(j, (t[j]));
                }
            }
            // 发现obj是一个list，但是key不是数字的时候，需要转换为map
            if ((!objKey.isEmpty()) && !((String) objKey).matches("\\d+") && (obj instanceof List || obj instanceof Set || obj.getClass().isArray())){
                objNew = new HashMap();
                if (obj instanceof List){
                    for (int j = 0; j < ((List) obj).size(); j++) {
                        objNew.put(j, ((List) obj).get(j));
                    }
                }
            }
            if (objNew != null){
                if (obj == inputData){
                    inputData = objNew;
                }else {
                    if (lastObj instanceof List){
                        ((List) lastObj).set(Integer.valueOf(lastObjKey), objNew);
                    }else if (lastObj instanceof Map){
                        ((Map) lastObj).put(lastObjKey, objNew);
                    }
                }
                obj = objNew;
                objNew = null;
            }
            // 不要修改lastObj变量的位置，避免异常
            lastObj = obj;
            // i!=0就是第一个[之后的参数name，然后如果为空考虑为数组
            if ((objKey.isEmpty() || objKey.equals(" ")) && i != 0){
                if (obj instanceof List) {
                    objKey = String.valueOf(((List) obj).size());
                } else if (obj instanceof Map){
                    int ct = -1;
                    for (Object oKey : ((Map) obj).keySet()) {
                        if (oKey instanceof Number){
                            ct = Integer.valueOf(String.valueOf(oKey));
                        }else if (oKey instanceof String && ((String) oKey).matches("\\d+")){
                            ct = Integer.valueOf(String.valueOf(oKey));
                        }
                    }
                    objKey = String.valueOf(ct + 1);
                }
            }
            // 不要修改lastObjKey变量的位置，避免异常
            lastObjKey = objKey;
            if (obj instanceof List){
                while (((List) obj).size() - Integer.valueOf(objKey)<=0){
                    ((List) obj).add(new ArrayList<>());
                }
                obj = ((List) obj).get(Integer.valueOf(objKey));
            }else if (obj instanceof Map){
                if (!((Map) obj).containsKey(objKey)){
                    ((Map) obj).put(objKey, new ArrayList<>());
                }
                obj = ((Map) obj).get(objKey);
            }
        }
        if (lastObj instanceof List){
            ((List) lastObj).set(Integer.valueOf(objKey), value);
        }else if (lastObj instanceof Map){
            ((Map) lastObj).put(objKey, value);
        }
    }
}
