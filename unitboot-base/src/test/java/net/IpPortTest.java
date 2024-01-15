package net;

import org.junit.Test;
import org.union.sbp.springbase.utils.IpPortUtils;

import java.io.IOException;
import java.net.InetAddress;

/**
 * ip与端口检测是否可用.
 *
 * @author youglu
 */
public class IpPortTest {

    @Test
    public void testIpPort() {
        // 查看端口号是否可用
        String ip = "192.168.5.230";
        int port = 8110;
        IpPortUtils iputils = new IpPortUtils();
        // 检测Ip是否可用
        boolean resultIp = iputils.checkIp(ip);
        System.out.print("检测的Ip结果为：");
        System.out.println(resultIp);
        // 检测Ip和端口号是否可用
        boolean resultIpPort = iputils.checkIpPort(ip, port);
        System.out.print("检测的Ip和端口结果为：");
        System.out.println(resultIpPort);
    }
}
