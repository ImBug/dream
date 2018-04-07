package com.water.dream.api.dic;

public enum Priority {
	
	low("低",1),normal("一般",2),high("紧急",3);
	
	private String name;
	private int value;
	
	private Priority(String name, int value) {
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
