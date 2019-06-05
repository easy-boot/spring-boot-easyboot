package top.easyboot.springboot.restfulapi.util;

import java.util.ArrayList;
import java.util.Collections;

public abstract class ConnectionIdUtil {
    /**
     * connectionId 记录器
     */
    private int recorder =0;
    /**
     * 最大值
     */
    private int recorderMax = 65535;
    /**
     * 批次
     */
    private int batch = 0;
    /**
     * 批次最大值
     */
    private int batchMax = 255;

    private int batchMaxLen = 0;
    private int recorderMaxLen = 0;
    private String batchRecorderHexFisrt = "";
    /**
     * 链接id前缀
     */
    private String connectionIdPrefix;

    public ConnectionIdUtil(){
        batchRecorderHexFisrtInit();
    }

    protected void batchRecorderHexFisrtInit(){
        batchMaxLen = Integer.toHexString(batchMax).length();
        recorderMaxLen = Integer.toHexString(recorderMax).length();
        batchRecorderHexFisrt = getNextBatchRecorderHex();
    }
    public synchronized int[] getNextBatchRecorder(){

        if (++recorder>=recorderMax){
            recorder = 0;
            if (++batch>=batchMax){
                batch = 0;
            }
        }
        return new int[]{batch, recorder};

    }
    public String getNextBatchRecorderHex(){
        int[] batchRecorder = getNextBatchRecorder();
        return getFilling(Integer.toHexString(batchRecorder[0]), batchMaxLen, "0") + getFilling(Integer.toHexString(batchRecorder[1]), recorderMaxLen, "0");
    }

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
    public String generateConnectionId() throws Exception {
        String batchRecorderHex = getNextBatchRecorderHex();
        String connectionId;
        int times = 0;
        String v = connectionIdPrefix.length() == 8 ? "4":"6";
        while (true){
            connectionId = v + connectionIdPrefix + batchRecorderHex;
            if (!isUseIng(connectionId)){
                return connectionId;
            }
            batchRecorderHex = getNextBatchRecorderHex();
            if (batchRecorderHexFisrt.equals(batchRecorderHex) && (++times)>=2){
                throw new Exception("没有找到");
            }
        }
    }
    public static Entity parse(String connectionId){
        if (connectionId == null || connectionId.isEmpty()){
            return null;
        }
        String t = String.valueOf(connectionId.charAt(0));
        String ip;
        String ipHex;
        String batchHex;
        String recorderHex;
        if (t.equals("4") && connectionId.length() == 15){
            ipHex =  connectionId.substring(1, 9);
            ip =  hexToIpv4(ipHex);
            batchHex = connectionId.substring(10, 12);
            recorderHex = connectionId.substring(13);
        }else if (t.equals("6") && connectionId.length() == 135){
            ipHex = connectionId.substring(1, 129);
            ip = hexToIpv6(ipHex);
            batchHex = connectionId.substring(130, 132);
            recorderHex = connectionId.substring(133);
        }else{
            return null;
        }
        return new Entity(ip, ipHex, Integer.parseInt(batchHex, 16), Integer.parseInt(recorderHex, 16));
    }

    public int getRecorder() {
        return recorder;
    }

    public void setRecorder(int recorder) {
        this.recorder = recorder;
    }

    public int getRecorderMax() {
        return recorderMax;
    }

    public void setRecorderMax(int recorderMax) {
        this.recorderMax = recorderMax;
    }

    public int getBatch() {
        return batch;
    }

    public void setBatch(int batch) {
        this.batch = batch;
    }

    public int getBatchMax() {
        return batchMax;
    }

    public void setBatchMax(int batchMax) {
        this.batchMax = batchMax;
    }

    public String getConnectionIdPrefix() {
        return connectionIdPrefix;
    }

    public void setConnectionIdPrefix(String connectionIdPrefix) {
        this.connectionIdPrefix = connectionIdPrefix;
    }

    public void setConnectionIdPrefix() {
        this.connectionIdPrefix = connectionIdPrefix;
    }

    public void setConnectionIdPrefixByIpV4(String ip) {
        this.connectionIdPrefix = ipV4ToHex(ip);
    }

    public static String ipToHex(String ip){
        return ipV4ToHex(ip);
    }
    public static String hexToIp(String hex){
        return hexToIpv4(hex);
    }
    public static String ipV4ToHex(String ip){
        ArrayList<String> ipList = new ArrayList<>();
        for (String s : ip.split("\\.")) {
            ipList.add(getFilling(Integer.toHexString(Integer.valueOf(s)), 2, "0"));
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

    protected abstract boolean isUseIng(String connectionId);
    public static class Entity{
        private String ip;
        private String ipHex;
        private int batch;
        private int recorder;

        public Entity(String ip, String ipHex, int batch, int recorder) {
            this.ip = ip;
            this.ipHex = ipHex;
            this.batch = batch;
            this.recorder = recorder;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getIpHex() {
            return ipHex;
        }

        public void setIpHex(String ipHex) {
            this.ipHex = ipHex;
        }

        public int getBatch() {
            return batch;
        }

        public void setBatch(int batch) {
            this.batch = batch;
        }

        public int getRecorder() {
            return recorder;
        }

        public void setRecorder(int recorder) {
            this.recorder = recorder;
        }

        @Override
        public String toString() {
            return "ip:"+ip+"ipHex:"+ipHex+"batch:"+batch+"recorder:"+recorder;
        }
    }
}
