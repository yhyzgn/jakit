package com.yhy.jakit.core.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * IP 工具
 * <p>
 * Created on 2021-08-01 01:30
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class IPUtils {

    /**
     * 获取本地IP
     *
     * @return 本地有效IP
     */
    public static String localIP() {
        try {
            Enumeration<NetworkInterface> allInterface = NetworkInterface.getNetworkInterfaces();
            InetAddress address = null;
            while (allInterface.hasMoreElements()) {
                NetworkInterface netInterface = allInterface.nextElement();
                // 排除 回文地址、虚拟网卡地址、非使用中网卡地址
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    address = addresses.nextElement();
                    if (address instanceof Inet4Address) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "0.0.0.0";
    }
}
