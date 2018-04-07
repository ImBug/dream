package com.water.util.list;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.Stream;

/**
 * 从小到大排列的有序列表
 * 1. 支持正序和反序
 * 2. 如果元素不支持比较用hash值比较
 * @author honghm
 *
 * @param <E>
 */
public class OrderList<E> implements Iterable<E>{

	private LinkedList<E> datas = new LinkedList<>();
	private boolean isAsc = true;//正序
	private Comparator<E> comparator;
	private Method compareTo;
	
	public OrderList() {
		super();
	}

	public OrderList(boolean isAsc, Comparator<E> comparator) {
		super();
		this.isAsc = isAsc;
		this.comparator = comparator;
	}

	public void add(E e){
		if(comparator == null && compareTo == null && Comparable.class.isAssignableFrom(e.getClass())){
			try {
				compareTo = e.getClass().getMethod("compareTo", e.getClass());
			} catch (NoSuchMethodException | SecurityException e1) {
				e1.printStackTrace();
			}
		}
		datas.add(findIndexOfSmaller(e),e);
	}
	
	private int compare(E e1,E e2){
		int val = e1.hashCode() - e2.hashCode();
		if(comparator != null){
			return comparator.compare(e1, e2);
		}else{
			if(compareTo != null){
				try {
					val = (int)compareTo.invoke(e1, e2);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				}
			}
		}
		return isAsc ?val:-val;
	}
	/**
	 * 
	 * @param e
	 * @return
	 */
	private int findIndexOfSmaller(E e){
		if(datas.size() > 0){
			int index = Collections.binarySearch(datas, e, (E d1,E d2)->{
				if(compare(d1,d2) >= 0)return 0;
				else{
					return -1;
				}
			});//找一个大于e的位置递减
			if(index > -1){
				for(int i=index-1;i>-1;i--){
					if(compare(datas.get(i),e) <= 0){//第一个小于它的位置
						return i+1;
					}
				}
				return 0;
			}else{
				return datas.size();
			}
		}else{
			return 0;
		}
	}
	
	public static void main(String[] args) {
		OrderList list = new OrderList();
		Random random = new Random();
		for(int i=0;i<15;i++){
			list.add(random.nextInt(100));
		}
		System.out.println(list);
	}

	@Override
	public String toString() {
		return datas.toString();
	}

	@Override
	public Iterator<E> iterator() {
		return datas.iterator();
	}
	
	public Stream<E> stream(){
		return datas.stream();
	}
}
