package top.easyboot.springboot.restfulapi.util;

public class Application {
    public static void main(String[] args) {
        System.out.println("Welcome to easyboot");
        ConnectionIdUtil connectionId = new ConnectionIdUtil(){
            @Override
            protected boolean isUseIng(String connectionId) {
                return true;
            }
        };
        System.out.println("33ot");

        System.out.println("==1:"+ ConnectionIdUtil.ipV4ToHex("10.0.0.1"));
        System.out.println("==2:"+ ConnectionIdUtil.ipV4ToHex("255.1.2.255"));
        System.out.println("==3:"+ ConnectionIdUtil.hexToIpv4("0a000001"));
        System.out.println("==4:"+ ConnectionIdUtil.hexToIpv4("ff0102ff"));
        System.out.println("==5:"+ ConnectionIdUtil.hexToIpv4("a000001"));
        System.out.println("==6:"+ ConnectionIdUtil.hexToIpv4("1"));
        System.out.println("==6:"+ ConnectionIdUtil.hexToIpv4("11"));
        System.out.println("==6:"+ ConnectionIdUtil.hexToIpv4("1121"));
        System.out.println("==6:"+ ConnectionIdUtil.hexToIpv4("11241"));
        System.out.println("==6:"+ ConnectionIdUtil.hexToIpv4("1124121"));
        System.out.println("==8:"+ ConnectionIdUtil.ipV4ToHex("0.0.0.1"));
        System.out.println("==8:"+ ConnectionIdUtil.hexToIpv4("00000001"));
//        int aa = (255 * 65535) ;
//        String aaaa = "";
//        try {
//
//            for (int i = 0; i < aa ; i++) {
//                System.out.println(connectionId.generateConnectionId());
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

}