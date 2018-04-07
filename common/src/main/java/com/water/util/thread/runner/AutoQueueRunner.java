package com.water.util.thread.runner;

import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.water.util.thread.entity.CountLatch;

/**
 * 线程不固定
 * @author honghm
 *
 * @param <T>
 */
public abstract class AutoQueueRunner<T> implements Runnable {
	
	private Queue<T> queue;//src
	private CountLatch countLatch;
	protected Log logger = LogFactory.getLog(AutoQueueRunner.class);
	
	public AutoQueueRunner(Queue<T> queue, CountLatch countLatch) {
		super();
		this.queue = queue;
		this.countLatch = countLatch;
	}

	/**
	 * 一般用于线程内部
	 * @param task
	 */
	protected abstract void excuteTask(T task);
	
	@Override
	public void run() {
		while(true){
			synchronized (queue) {
				if(queue.isEmpty()){
					countLatch.countDown();
					return;
				}
				T task = null;
				try {
					task = queue.poll();
					excuteTask(task);
				} catch (Throwable e) {
					logger.error(task,e);
				}
			}
		}
	}

}
