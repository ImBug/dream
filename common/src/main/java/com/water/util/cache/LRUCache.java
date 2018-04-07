package com.water.util.cache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU(Least Recently Used ，最近最少使用淘汰机制)
	算法根据数据的最近访问记录来淘汰数据，其原理是如果数据最近被访问过，将来被访问的几概率相对比较高，
	最常见的实现是使用一个链表保存缓存数据，详细具体算法如下：
		1. 新数据插入到链表头部；
		2. 每当缓存数据命中，则将数据移到链表头部；
		3. 当链表满的时候，将链表尾部的数据丢弃；
 * @author Wen
 *
 * @param <K>
 * @param <V>
 */
public class LRUCache<K, V> extends AbstractCacheMap<K, V> {

	private final static int BAESE_SIZE = 50;
	
	public LRUCache(int cacheSize, long defaultExpire) {
		
		super(cacheSize , defaultExpire) ;

		//linkedHash已经实现LRU算法 是通过双向链表来实现，当某个位置被命中，通过调整链表的指向将该位置调整到头位置，新加入的内容直接放在链表头，如此一来，最近被命中的内容就向链表头移动，需要替换时，链表最后的位置就是最近最少使用的位置
		this.cacheMap = new LinkedHashMap<K, CacheObject<K, V>>( BAESE_SIZE , 1f,true ) {

			@Override
			protected boolean removeEldestEntry(
					Map.Entry<K, CacheObject<K, V>> eldest) {

				return LRUCache.this.removeEldestEntry(eldest);
			}

		};
	}
	
	public LRUCache(int cacheSize) {
		
		super(cacheSize , 0) ;
		
		//linkedHash已经实现LRU算法 是通过双向链表来实现，当某个位置被命中，通过调整链表的指向将该位置调整到头位置，新加入的内容直接放在链表头，如此一来，最近被命中的内容就向链表头移动，需要替换时，链表最后的位置就是最近最少使用的位置
		this.cacheMap = new LinkedHashMap<K, CacheObject<K, V>>( BAESE_SIZE , 1f,true ) {
			
			@Override
			protected boolean removeEldestEntry(
					Map.Entry<K, CacheObject<K, V>> eldest) {
				
				return LRUCache.this.removeEldestEntry(eldest);
			}
			
		};
	}

	private boolean removeEldestEntry(Map.Entry<K, CacheObject<K, V>> eldest) {

		if (cacheSize == 0)
			return false;

		return size() > cacheSize;
	}

	/**
	 * 只需要实现清除过期对象就可以了,linkedHashMap已经实现LRU
	 */
	@Override
	protected int eliminateCache() {

		if(!isNeedClearExpiredObject()){ return 0 ;}
		
		Iterator<CacheObject<K, V>> iterator = cacheMap.values().iterator();
		int count  = 0 ;
		while(iterator.hasNext()){
			CacheObject<K, V> cacheObject = iterator.next();
			
			if(cacheObject.isExpired() ){
				iterator.remove(); 
				count++ ;
			}
		}
		
		return count;
	}

}