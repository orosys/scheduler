package com.oro.scheduler;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.oro.scheduler.core.IEndTask;
import com.oro.scheduler.scheduling.SchedulingAlarmReceiver;


/**
 * Created by oro on 15. 7. 8..
 */
public abstract class AbstractActionTask implements IAction {
	private static final String TAG = AbstractActionTask.class.getSimpleName();
	private Context context;
	private IEndTask endTaskCallBack;
	private Handler warningHandler;
	private Handler destoryHandler;
	private Runnable warningRun;
	private Runnable destoryRun;
	private Intent intent;

	public void init(final Context context) {
		this.context = context;
		onCreate(context);
		warningHandler = new Handler(Looper.getMainLooper());
		destoryHandler = new Handler(Looper.getMainLooper());

		warningRun = new Runnable() {
			@Override
			public void run() {
				onWarningDestory(context);
				destoryHandler.postDelayed(destoryRun, Constants.VAL_TIME_DESTROY);
			}
		};
		destoryRun = new Runnable() {
			@Override
			public void run() {
				onDestory(context);
			}
		};
	}

	public void onCreate(Context context) {

	}

	public void setActionEvent(Context context, ActionEvent actionEvent) {
		if (intent != null) {
			SchedulingAlarmReceiver.completeWakefulIntent(intent);
			intent = null;
		}

		intent = new Intent();
		try {
			SchedulingAlarmReceiver.startWakefulIntent(context, intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		onReceiveActionEvent(context, actionEvent);

		warningHandler.removeCallbacks(warningRun);
		destoryHandler.removeCallbacks(destoryRun);
		warningHandler.postDelayed(warningRun, Constants.VAL_TIME_WARNING);
	}

	public void requestExtendTime() {
		warningHandler.removeCallbacks(warningRun);
		warningHandler.postDelayed(warningRun, Constants.VAL_TIME_EXTEND_DESTROY);
	}

	public IEndTask getEndTaskCallBack() {
		return endTaskCallBack;
	}

	public void setEndTaskCallBack(IEndTask endTaskCallBack) {
		this.endTaskCallBack = endTaskCallBack;
	}

	public void onWarningDestory(Context context) {

	}

	public void onDestory(Context context) {
		warningHandler.removeCallbacks(warningRun);
		destoryHandler.removeCallbacks(destoryRun);
		if (getEndTaskCallBack() != null) {
			getEndTaskCallBack().onEndTask(this);
		}
		SchedulingAlarmReceiver.completeWakefulIntent(intent);
	}
}
