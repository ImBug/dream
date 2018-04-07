package com.water.dream.api.dic;

public enum Importance{
	
	low("低",1),normal("一般",2),high("关键",3);
	
	private String name;
	private int value;
	
	private Importance(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}
	
}
