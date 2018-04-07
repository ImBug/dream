package com.water.util.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ping 仍旧不够准
 * 有可能压力太大。
 * 只提供ping确认可达功能
 * 调用操作系统ping
 * 
 * @author honghm
 *
 */
public class OsPing {
	
	private final static Log logger = LogFactory.getLog(OsPing.class);
	
	private static int max = 12;
	private static OsPing vipForMonitor;//专供给monitor调用
	private static OsPing user;
	
	private AtomicInteger ping_size;
	
	private OsPing(int size){
		ping_size = new AtomicInteger(size);
	}
	
	static {
		try {
			max = Integer.valueOf(System.getProperty("probe.ping.maxSize", "12"));
		} catch (NumberFormatException e) {
			logger.warn(e);
		}
	}
	
	public OsPing() {
		ping_size.set(4);
	}
	
	
	
	public static OsPing getVipForMonitor() {
		if(vipForMonitor == null) vipForMonitor = new OsPing(max / 4);
		return vipForMonitor;
	}



	public static OsPing getUser() {
		if(user == null) user = new OsPing(max * 3 / 4);
		return user;
	}

	
	public boolean isFull(){
		return ping_size.get() < 1;
	}

	public static void main(String[] args) {
		System.out.println("延时=" + getUser().pingWindowResp("127.0.0.1"));
	}

	private boolean isWindows(){
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.indexOf("windows") > -1)return true;
		return false;
	}
	
	public int getResp(String ip){
		if(!isWindows()){
			return pingLinuxResp(ip);
		}else{
			return pingWindowResp(ip);
		}
	}
	
	public int pingWindowResp(String ip){
		String pingCommand = "ping " + ip + " -n " + 3 + " -w " + 2000;
		ping_size.getAndDecrement();
		BufferedReader in = null;
		Runtime r = Runtime.getRuntime(); // 将要执行的ping命令,此命令是windows格式的命令
		List<Double> resps = new ArrayList<Double>(3);
		try {
			Process p = r.exec(pingCommand);
			in = new BufferedReader(new InputStreamReader(p.getInputStream(),"gbk"));
			String line = null;
			while ((line = in.readLine()) != null) {
				if(logger.isDebugEnabled())logger.debug(line);
				int index = line.toLowerCase().indexOf("ttl");
				if ( index > -1) {
					if(line.indexOf("<") > -1){
						resps.add(1.0);
					}else{
						String resp = line.substring(0,line.indexOf("ms"));
						resp = resp.substring(resp.lastIndexOf("=") + 1);
						resps.add(Double.valueOf(resp));
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}finally{
			try {
				in.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
		ping_size.getAndIncrement();
		double sum = 0;
		if(resps.size() > 0){
			for(double resp:resps){
				sum += resp;
			}
			sum += 0.5;
			int res =  (int)(sum/resps.size());
			if(res < 1) return 1;
			return res;
		}
		return -1;
	}
	
	public int pingLinuxResp(String ip){
		String pingCommand = "ping " + ip + " -c " + 3 + " -w " + 2;
		ping_size.getAndDecrement();
		BufferedReader in = null;
		Runtime r = Runtime.getRuntime(); // 将要执行的ping命令,此命令是windows格式的命令
		List<Double> resps = new ArrayList<Double>(3);
		try {
			Process p = r.exec(pingCommand);
			in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				if(logger.isDebugEnabled())logger.debug(line);
				int index = line.toLowerCase().indexOf("time=");
				if ( index > -1) {
					String resp = line.substring(index+5);
					resp = resp.substring(0,resp.indexOf("ms")).trim();
					resps.add(Double.valueOf(resp));
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}finally{
			try {
				in.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
		ping_size.getAndIncrement();
		double sum = 0;
		if(resps.size() > 0){
			for(double resp:resps){
				sum += resp;
			}
			sum += 0.5;
			int res =  (int)(sum/resps.size());
			if(res < 1) return 1;
			return res;
		}
		return -1;
	}
	
	public  boolean ping(String ip){
		return ping(ip,3);
	}
	
	/**
	 * 不到非常时期不用这方法
	 * @param ip
	 * @return
	 */
	public   boolean ping(String ip,int count){
		if(logger.isDebugEnabled())logger.debug("size=" + ping_size.get());
		while(ping_size.get() < 1){
			try {
				Thread.sleep(500);
				if(logger.isDebugEnabled())logger.debug("ping " + ip + " wait");
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}
		if(count < 1) count = 1;
		if(count > 5) count = 3;
		return ping(ip,count,2000);
	}
	
	
	private  boolean ping(String ipAddress, int pingTimes, int timeOut) {

		ping_size.getAndDecrement();

		BufferedReader in = null;
		Runtime r = Runtime.getRuntime(); // 将要执行的ping命令,此命令是windows格式的命令
		String pingCommand = "";
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.indexOf("windows") > -1)// 验证windows版本
			pingCommand = "ping " + ipAddress + " -n " + pingTimes + " -w " + timeOut;
		else
			// linux版本
			pingCommand = "ping " + ipAddress + " -c " + pingTimes + " -w " + timeOut/1000;
		boolean reach = false;
		try {
			Process p = r.exec(pingCommand);
			in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				if(logger.isDebugEnabled())logger.debug(line);
				if (line.toUpperCase().indexOf("TTL=") > -1) {
					reach = true;
					break;
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}finally{
			try {
				in.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}

		ping_size.getAndIncrement();
		return reach;

	}
	
}
