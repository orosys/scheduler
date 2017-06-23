package com.oro.scheduler.core;


import com.oro.scheduler.AbstractEventDispatcherService;
import com.oro.scheduler.ActionEvent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by oro on 15. 7. 6..
 */
public abstract class AbstractThreadPoolService extends AbstractEventDispatcherService implements IEndService {
	private ThreadPoolExecutor threadPool = null;

	@Override
	public void onCreate() {
		super.onCreate();
		BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		threadPool = new ThreadPoolExecutor(3, 6, 20, TimeUnit.SECONDS, queue);
	}

	protected void executeTask(TaskWrapper job) {
		job.setContext(this);
		job.setEndRunnableCallBack(this);
		threadPool.submit(job);
	}

	protected void removeTask(TaskWrapper job) {
		threadPool.remove(job);
		job.setTask(null);
		job.setContext(null);
		job.setEndRunnableCallBack(null);
		job.setAction((ActionEvent) null);
	}

	@Override
	public void onDestroy() {
		threadPool.shutdownNow();
		if (threadPool.getQueue() != null) {
			threadPool.getQueue().clear();
		}

		super.onDestroy();
	}

	@Override
	public void onEndRunnable(TaskWrapper job) {
		removeTask(job);
	}
}

