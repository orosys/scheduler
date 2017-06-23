package com.oro.scheduler;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.oro.scheduler.core.AbstractSchedulingService;


/**
 * Created by oro on 15. 6. 23..
 */
public abstract class AbstractActionService extends AbstractSchedulingService implements IAction {
	private static final String TAG = AbstractActionService.class.getSimpleName();
	private String name;
	private Intent intent;
	private Handler warningHandler;
	private Handler destoryHandler;

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 *
	 * @param name Used to name the worker thread, important only for debugging.
	 */
	public AbstractActionService(String name) {
		super(name);
		this.name = name;
		warningHandler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				onWarningDestory(AbstractActionService.this);
				destoryHandler.sendEmptyMessageDelayed(0, Constants.VAL_TIME_DESTROY);
				return false;
			}
		});
		destoryHandler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				onDestory(AbstractActionService.this);
				return false;
			}
		});
	}

	@Override
	public void onCreate() {
		super.onCreate();
		onCreate(this);
	}

	public void onCreate(Context context) {

	}

	protected void onHandleIntent(Intent intent) {
		this.intent = intent;

		ActionEvent actionEvent = (ActionEvent) intent.getSerializableExtra("dispatch_action");
		onReceiveActionEvent(this, actionEvent);
		warningHandler.removeMessages(0);
		destoryHandler.removeMessages(0);
		warningHandler.sendEmptyMessageDelayed(0, Constants.VAL_TIME_WARNING);
	}

	public void onWarningDestory(Context context) {

	}

	public void onDestory(Context context) {
		warningHandler.removeMessages(0);
		destoryHandler.removeMessages(0);
		stopScheduling(intent);
	}

	public void requestExtendTime() {
		warningHandler.removeMessages(0);
		warningHandler.sendEmptyMessageDelayed(0, Constants.VAL_TIME_EXTEND_DESTROY);
	}
}
