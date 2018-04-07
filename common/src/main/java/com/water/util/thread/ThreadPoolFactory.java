package com.water.util.thread;

import java.util.concurrent.ThreadPoolExecutor;


/**
 * 统一线程池
 * 需根据繁忙情况自动分配TODO
 * @author honghm
 *
 */
public class ThreadPoolFactory {
	
	private static int maxPoolSize = 90;
	private static int large = 60;
	private static int big = 30;
	private static int small = 10;
	
	static {
		try {
			maxPoolSize = Integer.parseInt(System.getProperty("system.thread.pool.max","120"));
			large = maxPoolSize*2/3;
			big = large*2/3;
			small = maxPoolSize - large - big;
			if(small < 1) small =1;
			if(big < 2) big = 2;
			if(large < 3) large = 3;
		} catch (NumberFormatException e) {
		}
	}
	
	public static ThreadPoolExecutor LargePool = CommonThreadPoolExecutor.createDefault(large);
	public static ThreadPoolExecutor BiggPool = CommonThreadPoolExecutor.createDefault(big);
	public static ThreadPoolExecutor SmallPool = CommonThreadPoolExecutor.createDefault(small);
	
	public static ThreadPoolExecutor getPool(ThreadPoolLevel level){
		if(ThreadPoolLevel.large == level){
			return LargePool;
		}
		if(ThreadPoolLevel.big == level){
			return BiggPool;
		}
		return SmallPool;
	}
	
	
}
