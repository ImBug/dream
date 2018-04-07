package com.water.dream.api.dic;

public enum TaskState {
	
	processing("处理中",1),resolved("已解决",2),deprecated("废弃的",-1),exist("未开始",0);
	
	private String name;
	private int value;
	
	private TaskState(String name, int value) {
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
