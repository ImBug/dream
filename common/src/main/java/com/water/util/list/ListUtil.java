package com.water.util.list;

import java.util.List;
import java.util.stream.Collectors;

public class ListUtil {
	
	@SuppressWarnings("rawtypes")
	public static boolean isNotEmpty(List list){
		if(list != null && list.size() > 0) return true;
		return false;
	}
	
	public static String join(List<?> list,String token){
		if(isNotEmpty(list)){
			if(token == null) token = ",";
			return list.stream().map(obj->String.valueOf(obj)).collect(Collectors.joining(token));
		}
		return "";
	}
	
}
