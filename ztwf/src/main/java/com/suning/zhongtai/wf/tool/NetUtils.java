package com.suning.zhongtai.wf.tool;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Network Utils
 */
public abstract class NetUtils {

    /**
     * Pre-loaded local address
     */
    public static InetAddress localAddress;

    static {
        try {
            localAddress = getLocalInetAddress();
        } catch (SocketException e) {
            throw new RuntimeException("fail to get local ip.");
        }
    }

    /**
     * Retrieve the first validated local ip address(the Public and LAN ip addresses are validated).
     *
     * @return the local address
     * @throws SocketException the socket exception
     */
    public static InetAddress getLocalInetAddress() throws SocketException {
        // enumerates all network interfaces
        Enumeration<NetworkInterface> enu = NetworkInterface.getNetworkInterfaces();

        while (enu.hasMoreElements()) {
            NetworkInterface ni = enu.nextElement();
            if (ni.isLoopback()) {
                continue;
            }

            Enumeration<InetAddress> addressEnumeration = ni.getInetAddresses();
            while (addressEnumeration.hasMoreElements()) {
                InetAddress address = addressEnumeration.nextElement();

                // ignores all invalidated addresses
                if (address.isLinkLocalAddress() || address.isLoopbackAddress() || address.isAnyLocalAddress()) {
                    continue;
                }

                return address;
            }
        }

        throw new RuntimeException("No validated local address!");
    }

    /**
     * Retrieve local address
     * 
     * @return the string local address
     */
    public static String getLocalAddress() {
        return localAddress.getHostAddress();
    }

    /**
     * 判断IP地址的合法性，这里采用了正则表达式的方法来判断
     * return true，合法
     * */
    public static boolean ipCheck(String text) {
        if (text != null && !text.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."+
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."+
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."+
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            if (text.matches(regex)) {
                // 返回判断信息
                return true;
            } else {
                // 返回判断信息
                return false;
            }
        }
        return false;
    }
}
