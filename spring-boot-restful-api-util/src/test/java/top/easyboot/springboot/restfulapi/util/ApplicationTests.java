package top.easyboot.springboot.restfulapi.util;

import org.junit.Test;

public class ApplicationTests {
    private ConnectionIdUtilTests connectionIdUtilTests = new ConnectionIdUtilTests();

    @Test
    public void contextLoads() {
        connectionIdUtilTests.hexToIpv4();
        connectionIdUtilTests.ipToHex();
        connectionIdUtilTests.ipV4ToHex();
        connectionIdUtilTests.parseHex();
    }
}
