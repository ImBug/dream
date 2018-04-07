package com.water.util.net;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class NetUtil {
  
  /**
   * 端口->ip
   * @return
   * @throws SocketException 
   */
	public static Map<String, String> getLocalIPV4() throws SocketException {
		Map<String, String> map = new HashMap<String, String>();
		Enumeration<NetworkInterface> e1 = NetworkInterface.getNetworkInterfaces();
		while (e1.hasMoreElements()) {
			NetworkInterface ni = e1.nextElement();
			if(!ni.isLoopback() && !ni.isVirtual() && ni.isUp()){
				Enumeration<InetAddress> e2 = ni.getInetAddresses();
				while (e2.hasMoreElements()) {
					InetAddress ia = e2.nextElement();
					if (ia instanceof Inet6Address) {
						continue;
					}
					map.put(ni.getName(), ia.getHostAddress());
					//String mac = parseMac(NetworkInterface.getByInetAddress(ia).getHardwareAddress());
				}
			}
		}
		return map;
	}
	
	private static String parseMac(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			if (i != 0)sb.append("-");
			String tmp = Integer.toHexString(bytes[i] & 0xFF);// 将byte转为正整数。然后转为16进制数
			sb.append(tmp.length() == 1 ? 0 + tmp : tmp);
		}
		return sb.toString();
	}
}