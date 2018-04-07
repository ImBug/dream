package com.water.util.list;

import java.util.Arrays;

public class LimitList<T>{

	private int limit;
	private Object[] datas;
	private int size;
	
	public LimitList(int limit) {
		super();
		this.limit = limit;
		datas = new Object[limit];
	}
	
	public boolean add(T t){
		if(size < limit){
			datas[size] = t;
			size ++;
			return true;
		}
		return false;
	}
	
	public T[] toArray(T[] a) {
    if (a.length < size)return (T[]) Arrays.copyOf(datas, size, a.getClass());
    System.arraycopy(datas, 0, a, 0, size);
    if (a.length > size)a[size] = null;
    return a;
	} 
}
