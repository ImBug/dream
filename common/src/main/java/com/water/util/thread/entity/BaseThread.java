package com.water.util.thread.entity;

import com.water.util.thread.Finishable;

/**
 * 提供开始和结束标识
 * @author honghm
 *
 */
public class BaseThread extends Thread implements Finishable {

	private boolean start,end;
	private Runnable runer;
	
	public BaseThread(){
		super();
	}
	
	public BaseThread(Runnable target) {
		super(target);
		this.runer = target;
	}

	public void setRuner(Runnable runer) {
		this.runer = runer;
	}
	
	@Override
	public void run() {
		start = true;
		runer.run();
		end = true;
	}

	public boolean isStart() {
		return start;
	}

	public boolean isEnd() {
		return end;
	}
	
	public boolean isRunning(){
		return start && !end;
	}
	
	protected void setEnd(boolean end) {
		this.end = end;
	}
	
	@Override
	public void finish() {
		if(runer instanceof Finishable){
			((Finishable)runer).finish();
		}else{
			this.interrupt();
		}
	}
}
