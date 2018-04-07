package com.water.util.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * 排名队列
 * @author honghm
 *
 */
public class TopoList<T> implements Iterable<T>{
	
	private int topoSize;
	private List<T> datas;
	private Comparator<T> comparator;
	private int index=0;
	
	public TopoList(int topoSize, Comparator<T> comparator) {
		super();
		this.topoSize = topoSize;
		this.comparator = comparator;
		datas = new ArrayList<>(topoSize);
	}
	
	public int getSize(){
		return index;
	}
	
	public boolean push(T data){
		if(index < topoSize){
			datas.add(data);
			index++;
			return true;
		}else{
			Collections.sort(datas, comparator);
			for(int i=0;i<index;i++){
				int dis = comparator.compare(datas.get(i), data);
				if(dis < 0){//data 大 保留
					datas.set(i, data);
					return true;
				}
			}
		}
		return false;
	}
	
	public List<T> getDatas(){
		Collections.sort(datas, (o1,o2)->comparator.compare(o2, o1));//倒叙排列
		return datas;
	}
	
	public Stream<T> stream(){
		return getDatas().stream();
	}
	
	@Override
	public Iterator<T> iterator() {
		return getDatas().iterator();
	}
	
	public void clear(){
		datas.clear();
		index = 0;
	}
	
	public static void main(String[] args) {
		/**使用说明**/
		Comparator<Integer> comparator = (i,j)->i-j;
		TopoList<Integer> list = new TopoList<>(3,comparator);
		System.out.println(list.push(1));
		System.out.println(list.push(5));
		System.out.println(list.push(9));
		System.out.println(list.push(4));
		System.out.println(list.push(6));
		System.out.println(list.push(3));
		System.out.println(list.getDatas());
	}
}
