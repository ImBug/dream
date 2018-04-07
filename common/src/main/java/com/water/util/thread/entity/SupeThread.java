package com.water.util.thread.entity;

import com.water.util.thread.Finishable;

public abstract class SupeThread extends Thread implements Finishable {
	
	/**
	 * 别名
	 * @return
	 */
	public abstract String getAisName();
	
	@Override
	public void finish() {

	}

}
