package com.feiyu.base.utils;



import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetUtil {
    private static String addressStr = null;
    public static String getAddress() {
        if (addressStr != null) {
            return addressStr;
        }
        String inet4Address = null;
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.getName().startsWith("eth")) {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        if (inetAddress instanceof Inet4Address) {
                            inet4Address = inetAddress.getHostAddress();
                        }
                    }
                    break;
                }
            }
        } catch (SocketException ignore) {
        }
        addressStr = inet4Address;
        return inet4Address;
    }
}
