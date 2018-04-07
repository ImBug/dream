package com.water.util.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 反射工具
 * @author honghm
 *
 */
public class BeanUtil {
	
	public static Map<String, Object> getNotNullValMap(Object bean) throws IllegalArgumentException, IllegalAccessException{
		return getValMap(bean, (name,val)->{return val != null;});
	}
	
	public static Map<String, Object> getValMap(Object bean,MapFilter filter) throws IllegalArgumentException, IllegalAccessException{
		if(bean != null){
			return getSuperValMap(bean,bean.getClass(),filter);
		}
		return Collections.EMPTY_MAP;
	}
	
	private static Map<String, Object> getSuperValMap(Object bean,Class<?> superCls,MapFilter filter) throws IllegalArgumentException, IllegalAccessException{
		if(bean != null && superCls != null){
			if(superCls != Object.class){
				Field[] fieldlist = superCls.getDeclaredFields();
				Map<String, Object> valMap = new HashMap<>(fieldlist.length);
				for (int i = 0; i < fieldlist.length; i++) {
					Field fld = fieldlist[i];
					if(!Modifier.isFinal(fld.getModifiers())){
						fld.setAccessible(true);
						if(filter != null){
							if(filter.filter(fld.getName(), fld.get(bean)))
								valMap.put(fld.getName(), fld.get(bean));
						}else{
							valMap.put(fld.getName(), fld.get(bean));
						}
					}
				}
				valMap.putAll(getSuperValMap(bean,superCls.getSuperclass(),filter));
				return valMap;
			}
		}
		return Collections.EMPTY_MAP;
	}
	
	/**
	 * 不含父类属性
	 * @param classPath
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static HashMap<String,Class<?>> getFieldMap(String classPath) throws ClassNotFoundException{
		Class<?> cls = Class.forName(classPath);
		Field[] fieldlist = cls.getDeclaredFields();
		HashMap<String,Class<?>> fieldHashMap=new HashMap<String,Class<?>>(fieldlist.length);
		for (int i = 0; i < fieldlist.length; i++) {
			Field fld = fieldlist[i];
			fieldHashMap.put(fld.getName(), fld.getType());
		}
		return fieldHashMap;
  }
	
	/**
	 * @param bean
	 * @param map
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void setValue(Object bean,Map<String, Object> map) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException{
		setBeanValue(bean,bean.getClass(),map);
	}
	
	private static void setBeanValue(Object bean,Class<?> superCls,Map<String, Object> map) throws IllegalArgumentException, IllegalAccessException{
		if(superCls != Object.class){
			for(Field field:superCls.getDeclaredFields()){
				Object val = map.get(field.getName());
				if(val != null){
					field.setAccessible(true);
					if(field.getType().isAssignableFrom(val.getClass())){
						field.set(bean, val);
					}else{
						if(field.getType() == int.class){
							field.set(bean, Integer.valueOf(val.toString()));
						}else if(field.getType() == boolean.class){
							field.set(bean, Boolean.valueOf(val.toString()));
						}else if(field.getType() == long.class){
							field.set(bean, Long.valueOf(val.toString()));
						}
					}
				}
			}
			superCls = superCls.getSuperclass();
			setBeanValue(bean,superCls,map);
		}
	}
}
