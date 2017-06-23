package com.oro.scheduler.core;

import android.os.Handler;
import android.os.Message;

/**
 * Created by oro on 15. 7. 6..
 */
public abstract class TaskExecuter implements Runnable {
	private static final String TAG = TaskExecuter.class.getSimpleName();
	private Handler handler;

	public TaskExecuter() {
		super();
		handler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				onStopProcess();
				return false;
			}
		});
	}

	@Override
	public void run() {
		onStartProcess();
		handler.sendEmptyMessageDelayed(0, 10 * 1000);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	abstract protected void onStartProcess();

	abstract protected void onStopProcess();
}
