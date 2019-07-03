package top.easyboot.springboot.utils.core;

import java.util.ArrayList;
import java.util.Collections;

public class HexIp {

    public static String ipToHex(String ip){
        return ipV4ToHex(ip);
    }
    public static String hexToIp(String hex){
        return hexToIpv4(hex);
    }
    public static String ipV4ToHex(String ip){
        ArrayList<String> ipList = new ArrayList<>();
        for (String s : ip.split("\\.")) {
            ipList.add(StringUtil.getFilling(Integer.toHexString(Integer.valueOf(s)), 2, "0"));
        }
        return String.join("", ipList);
    }
    public static String hexToIpv6(String hex){
        return hex;
    }
    public static String hexToIpv4(String hex){
        ArrayList<String> ipList = new ArrayList<>();
        ArrayList<String> ipHexList = new ArrayList<>();
        hex.charAt(0);
        String t = "";
        for (int i = hex.length() - 1; i >= 0; i--) {
            if (t.length() == 0){
                t = String.valueOf(hex.charAt(i));
            }else if (t.length() == 1){
                ipHexList.add(String.valueOf(hex.charAt(i)) + t);
                t = "";
            }
        }
        if (t.length()>0){
            ipHexList.add(t);
        }
        for (String ipHex : ipHexList) {
            ipList.add(String.valueOf(Integer.valueOf(ipHex, 16)));
        }
        int len = 4-ipList.size();
        for (int i = 0; i < len; i++) {
            ipList.add("0");
        }

        Collections.reverse(ipList);
        return String.join(".", ipList);
    }
}
