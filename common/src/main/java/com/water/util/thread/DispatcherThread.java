package com.water.util.thread;

import java.util.Queue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.water.util.thread.entity.BaseThread;
import com.water.util.thread.entity.CountLatch;
import com.water.util.thread.runner.AutoQueueRunner;

/**
 * 
 * @author honghm
 *
 */
public abstract class DispatcherThread<T> extends BaseThread implements Finishable {
	
	protected Log logger = LogFactory.getLog(DispatcherThread.class);
	
	private ThreadPoolLevel poolLevel;
	protected Queue<T> queue;
	private int maxRemain;//队列剩余最大值
	private int dealNumPerThread = 20;
	private CountLatch counter = new CountLatch(0);
	private AtomicLong startTime = new AtomicLong(0);
	private long lastCost = 0;//上次执行消耗时间 ms
	
	public DispatcherThread(ThreadPoolLevel poolLevel, Queue<T> queue, int dealNumPerThread) {
		super();
		this.poolLevel = poolLevel;
		this.queue = queue;
		this.dealNumPerThread = dealNumPerThread;
		super.setRuner(target);
	}

	private Runnable target = ()->{
		while(!interrupted()){
			if(queue.isEmpty() || counter.getCount() < 1){
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						logger.warn("interrupt",e);
					}
				}
			}else{
				try {//对队列来讲任务清空即是完成，但还有可能正在执行中
					counter.await();
					lastCost = System.currentTimeMillis() - startTime.get();
					startTime.set(0);
					callBack();
				} catch (InterruptedException e) {
					logger.error(e);
					setEnd(true);
				}catch (Exception e) {
					logger.error("callback error",e);
				}
			}
		}
		
	};
	
	private ThreadPoolExecutor getPool(){
		return ThreadPoolFactory.getPool(poolLevel);
	}
	
	public abstract void excute(T task);
	public void callBack(){}
	
	public boolean hasFinish(){
		return counter.getCount() < 1 && queue.isEmpty();
	}
	
	public void offerTask(T task){
		if(!isEnd()){
			synchronized (queue) {
				queue.offer(task);
				dispatch();
			}
			synchronized (this) {
				this.notify();
			}
		}else{
			logger.warn("服务已经关闭，不接收任务");
		}
	}
	
	public int getSize(){
		return queue.size();
	}
	
	public int getMaxRemain(){
		return maxRemain;
	}
	
	public long getLastCost(){
		return lastCost;
	}
	
	public long getWorkerSize(){
		return counter.getCount();
	}
	
	private void dispatch(){
		int size = queue.size();
		if(maxRemain < size) maxRemain = size;
		if(size < 1)return;
		int currentThread = (int)counter.getCount();
		int threadNum = (size + dealNumPerThread/2)/dealNumPerThread;//需要线程数
		if(threadNum < 1)threadNum = 1;
		int needAdd = threadNum - currentThread;
		needAdd = needAdd > 0 ?needAdd:0;
		for(int i=0;i<needAdd;i++){
			getPool().execute(new TaskRunner());
			counter.countUp();
		}
	}
	
	@Override
	public void finish() {
		setEnd(true);
	}



	class TaskRunner extends AutoQueueRunner<T>{
		
		public TaskRunner(){
			super(queue,counter);
		}
		
		public TaskRunner(Queue<T> queue, CountLatch countLatch) {
			super(queue, countLatch);
		}

		@Override
		protected void excuteTask(T task) {
			if(startTime.get() < 1)startTime.set(System.currentTimeMillis());
			excute(task);
		}
		
	}
}
