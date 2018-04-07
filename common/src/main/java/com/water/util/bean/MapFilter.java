package com.water.util.bean;

public interface MapFilter{
	
	/**
	 * true : 保留
	 * @param name
	 * @param val
	 * @return
	 */
	boolean filter(String name,Object val);

}
