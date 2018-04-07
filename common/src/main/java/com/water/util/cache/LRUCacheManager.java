package com.water.util.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * 
 * @author honghm
 *
 */
public class LRUCacheManager {
	
	private final static int maxCacheSize = 10000;
	
	private static LRUCacheManager instance;
	private Map<String, LRUCache<String, String>> cacheMap;
	
	public static LRUCacheManager getDefault(){
		if(instance == null) instance = new LRUCacheManager();
		return instance;
	}
	
	private LRUCacheManager(){
		super();
		cacheMap = new HashMap<>(20);
	}
	
	public void addCache(String source,String type,String key,String value){
		String baseKey = String.format("%s>%s", source,type);
		LRUCache<String, String> cachemap = cacheMap.get(baseKey);
		if(cachemap == null){
			cachemap = new LRUCache<String,String>(maxCacheSize);
			cacheMap.put(baseKey, cachemap);
		}
		if(key != null && key.trim().length() > 0)cachemap.put(key, value);
	}
	
	
	public String getStoreIdByKey(String source,String classCode,String identify){
		String baseKey = String.format("%s>%s", source,classCode);
		LRUCache<String, String> cMap = cacheMap.get(baseKey);
		if(cMap != null)return cMap.get(identify);
		return null;
	}
	
}
