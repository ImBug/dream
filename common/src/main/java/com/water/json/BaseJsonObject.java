package com.water.json;

/**
 * 提供默认的json转换
 * @author honghm
 *
 */
public abstract class BaseJsonObject {

	public String toJson() throws JsonEncodeException{
		try {
			return CustomObjectMapper.encodeJson(this);
		} catch (RuntimeException e) {
			throw new JsonEncodeException(e);
		}
	}
	
}
