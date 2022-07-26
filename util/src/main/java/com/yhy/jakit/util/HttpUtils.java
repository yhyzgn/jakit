package com.yhy.jakit.util;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * HTTP 工具类
 * <p>
 * Created on 2022-07-26 14:12
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public interface HttpUtils {

    /**
     * 获取请求头，忽略大小写匹配
     *
     * @param request 请求对象
     * @param name    参数名
     * @return 参数值
     */
    static String getHeader(HttpServletRequest request, String name) {
        Enumeration<String> names = request.getHeaderNames();
        String temp;
        while (names.hasMoreElements()) {
            temp = names.nextElement();
            // 忽略大小写匹配
            if (temp.equalsIgnoreCase(name)) {
                return request.getHeader(temp);
            }
        }
        return null;
    }

    /**
     * 获取请求来源IP地址
     *
     * @param request 请求
     * @return IP地址
     */
    static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    if (inet != null) {
                        ipAddress = inet.getHostAddress();
                    }
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }
}
