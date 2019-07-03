package top.easyboot.springboot.utils.entity.core;

import top.easyboot.springboot.utils.core.HexIp;
import top.easyboot.springboot.utils.exception.WebSocketConnectionException;

public class WebSocketConnection {
    private String ip;
    private String ipHex;
    private int id;
    private int batch;

    private WebSocketConnection() {}
    public WebSocketConnection(String connectionId) throws WebSocketConnectionException {
        parse(connectionId, this);
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ip:"+ip+"ipHex:"+ipHex+"batch:"+batch+"id:"+id;
    }

    public static WebSocketConnection parse(String connectionId) throws WebSocketConnectionException {
        WebSocketConnection connection = new WebSocketConnection();
        parse(connectionId, connection);
        return connection;
    }
    public static void parse(String connectionId, WebSocketConnection connection) throws WebSocketConnectionException {
        if (connectionId == null || connectionId.isEmpty()){
            throw new WebSocketConnectionException(WebSocketConnectionException.E_EMPTY_CONNECTION_ID);
        }
        String t = String.valueOf(connectionId.charAt(0));
        String ip;
        String ipHex;
        String batchHex;
        String idHex;
        if (t.equals("4") && connectionId.length() == 15){
            ipHex =  connectionId.substring(1, 9);
            ip =  HexIp.hexToIpv4(ipHex);
            batchHex = connectionId.substring(10, 12);
            idHex = connectionId.substring(13);
        }else if (t.equals("6") && connectionId.length() == 135){
            ipHex = connectionId.substring(1, 129);
            ip = HexIp.hexToIpv6(ipHex);
            batchHex = connectionId.substring(130, 132);
            idHex = connectionId.substring(133);
        }else{
            throw new WebSocketConnectionException(WebSocketConnectionException.E_FORMAT_CONNECTION_ID);
        }
        connection.setIp(ip);
        connection.setIpHex(ipHex);
        connection.setBatch(Integer.parseInt(batchHex, 16));
        connection.setId(Integer.parseInt(idHex, 16));
    }
}
