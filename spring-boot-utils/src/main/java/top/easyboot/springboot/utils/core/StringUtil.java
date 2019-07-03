package top.easyboot.springboot.utils.core;

import java.util.ArrayList;

public class StringUtil {
    public static String getFilling(String str, int length , String filling){
        if (str == null || str.isEmpty()){
            str = "";
        }
        int len = length - str.length();
        ArrayList<String> strList = new ArrayList();
        for (int i = 0; i < len; i++) {
            strList.add(filling);
        }
        strList.add(str);
        return String.join("", strList);
    }
}
