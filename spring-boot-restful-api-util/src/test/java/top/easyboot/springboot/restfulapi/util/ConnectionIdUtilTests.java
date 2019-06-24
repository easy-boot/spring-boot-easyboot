package top.easyboot.springboot.restfulapi.util;

import org.junit.Test;
import static org.junit.Assert.assertFalse;

public class ConnectionIdUtilTests {
    @Test
    public void hexToIpv4() {
        assertFalse("10.0.0.1", !"10.0.0.1".equals(ConnectionIdUtil.hexToIpv4("0a000001")));
        assertFalse("255.1.2.255", !"255.1.2.255".equals(ConnectionIdUtil.hexToIpv4("ff0102ff")));
        assertFalse("10.0.0.1", !"10.0.0.1".equals(ConnectionIdUtil.hexToIpv4("a000001")));
        assertFalse("0.0.0.1", !"0.0.0.1".equals(ConnectionIdUtil.hexToIpv4("1")));
        assertFalse("0.0.0.17", !"0.0.0.17".equals(ConnectionIdUtil.hexToIpv4("11")));
        assertFalse("0.0.17.33", !"0.0.17.33".equals(ConnectionIdUtil.hexToIpv4("1121")));
        assertFalse("0.1.18.65", !"0.1.18.65".equals(ConnectionIdUtil.hexToIpv4("11241")));
        assertFalse("1.18.65.33", !"1.18.65.33".equals(ConnectionIdUtil.hexToIpv4("1124121")));
        assertFalse("0.0.0.1", !"0.0.0.1".equals(ConnectionIdUtil.hexToIpv4("00000001")));
    }
    @Test
    public void ipV4ToHex() {
        assertFalse("0a000001", !"0a000001".equals(ConnectionIdUtil.ipV4ToHex("10.0.0.1")));
        assertFalse("ff0102ff", !"ff0102ff".equals(ConnectionIdUtil.ipV4ToHex("255.1.2.255")));
        assertFalse("ffffffff", !"ffffffff".equals(ConnectionIdUtil.ipV4ToHex("255.255.255.255")));
        assertFalse("00000001", !"00000001".equals(ConnectionIdUtil.ipV4ToHex("0.0.0.1")));
        assertFalse("000000ff", !"000000ff".equals(ConnectionIdUtil.ipV4ToHex("0.0.0.255")));
    }
    @Test
    public void ipToHex() {
        assertFalse("0a000001", !"0a000001".equals(ConnectionIdUtil.ipToHex("10.0.0.1")));
        assertFalse("ff0102ff", !"ff0102ff".equals(ConnectionIdUtil.ipToHex("255.1.2.255")));
        assertFalse("ffffffff", !"ffffffff".equals(ConnectionIdUtil.ipToHex("255.255.255.255")));
        assertFalse("00000001", !"00000001".equals(ConnectionIdUtil.ipToHex("0.0.0.1")));
        assertFalse("000000ff", !"000000ff".equals(ConnectionIdUtil.ipToHex("0.0.0.255")));
    }
    @Test
    public void parseHex() {
        try {
            System.out.println("==8:"+  ConnectionIdUtil.parse("40a030310000003"));
        }catch (ConnectionIdUtil.Exception e){
            e.printStackTrace();
        }
    }
}
