package com.oro.scheduler;

import android.content.Context;
import android.content.SharedPreferences;

import com.oro.scheduler.scheduling.SchedulingAlarmReceiver;
import com.oro.scheduler.core.AbstractThreadPoolService;
import com.oro.scheduler.core.IEndService;
import com.oro.scheduler.core.TaskWrapper;
import com.oro.scheduler.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by oro on 15. 7. 17..
 */
public class EventDispatcherServiceImpl extends AbstractThreadPoolService implements IEndService {
	private Map<String, Object> actions;
	private Map<String, String> events;
	private long minDispatcherTime = 100;
	private long lastDispatcherTime = 0;
	private SharedPreferences dispatcherPref;

	@Override
	public void onCreate() {
		super.onCreate();

		dispatcherPref = getSharedPreferences(Constants.PREF_DISPATCHER, Context.MODE_MULTI_PROCESS);

		events = new HashMap<>();
		actions = new HashMap<>();

		initEvents(this);
		initActions(this);

		boolean isSDKRun = dispatcherPref.getBoolean(Constants.KEY_IS_DISPATCHER_RUN, false);
		if (isSDKRun) {
			startEvent();
		}
	}

	@Override
	public void onEndRunnable(TaskWrapper job) {
		removeTask(job);
	}

	@Override
	protected void addEvent(String className) {
		events.put(className, "");
		updateEvent(this);
	}

	@Override
	protected void removeEvent(String className) {
		events.remove(className);
		updateEvent(this);
	}

	@Override
	protected void addAction(String className) {
		try {
			Class<?> clazz = Class.forName(className);
			if (Util.typeOf(clazz, AbstractActionService.class)) {
				addActionService((Class<? extends AbstractActionService>) clazz);
			} else if (Util.typeOf(clazz, AbstractActionTask.class)) {
				addActionTask((Class<? extends AbstractActionTask>) clazz);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void removeAction(String className) {
		try {
			Class<?> clazz = Class.forName(className);
			removeAction(clazz);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void startEvent() {
		for (String className : events.keySet()) {
			try {
				Class<?> clazz = Class.forName(className);
				Object event = clazz.newInstance();
				if (event != null && event instanceof IEvent) {
					((IEvent) event).startEvent(this);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void stopEvent() {
		for (String className : events.keySet()) {
			try {
				Class<?> clazz = Class.forName(className);
				Object event = clazz.newInstance();
				if (event != null && event instanceof IEvent) {
					((IEvent) event).stopEvent(this);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private void addActionService(Class<? extends AbstractActionService> clazz) {
		SchedulingAlarmReceiver receiver = new SchedulingAlarmReceiver(this, clazz);
		actions.put(clazz.getName(), receiver);
		updateActions(this);
	}

	private void addActionTask(Class<? extends AbstractActionTask> clazz) {
		TaskWrapper job = new TaskWrapper(this, null, clazz);
		job.setEndRunnableCallBack(this);
		actions.put(clazz.getName(), job);
		updateActions(this);
	}

	private void removeAction(Class<?> clazz) {
		actions.remove(clazz.getName());
		updateActions(this);
	}

	protected boolean dispatchAction(Map<String, Object> params) {
		if (System.currentTimeMillis() - lastDispatcherTime < minDispatcherTime) {
			return false;
		}
		lastDispatcherTime = System.currentTimeMillis();

		for (String key : actions.keySet()) {
			Object action = actions.get(key);
			if (action instanceof SchedulingAlarmReceiver) {
				SchedulingAlarmReceiver receiver = (SchedulingAlarmReceiver) action;
				if (receiver != null) {
					receiver.appendIntentExtra(params);
					receiver.setAlarmScheduling(this, true);
				}
			} else if (action instanceof TaskWrapper) {
				TaskWrapper job = (TaskWrapper) action;
				if (job != null) {
					job.setAction(params);
				}
				executeTask(job);
			}
		}

		return true;
	}

	private void initActions(Context context) {
		String lastSdk = dispatcherPref.getString(Constants.KEY_LAST_ACTION, "");
		String[] sdk = null;
		if (lastSdk != null) {
			sdk = lastSdk.split(",");
		}
		if (sdk == null) {
			return;
		}

		for (String className : sdk) {
			if (className == null || className.length() == 0 || actions.get(className) != null) {
				continue;
			}
			try {
				Class<?> clazz = Class.forName(className);
				if (Util.typeOf(clazz, AbstractActionService.class)) {
					addActionService((Class<? extends AbstractActionService>) clazz);
				} else if (Util.typeOf(clazz, AbstractActionTask.class)) {
					addActionTask((Class<? extends AbstractActionTask>) clazz);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void initEvents(Context context) {
		String event = dispatcherPref.getString(Constants.KEY_LAST_EVENT, "");
		String[] eventArr = null;
		if (event != null) {
			eventArr = event.split(",");
		}
		if (eventArr == null) {
			return;
		}

		for (String className : eventArr) {
			if (className == null || className.length() == 0 || events.get(className) != null) {
				continue;
			}
			addEvent(className);
		}
	}

	private void updateActions(Context context) {
		String action = "";
		for (String key : actions.keySet()) {
			if (action.length() == 0) {
				action += key;
			} else {
				action += "," + key;
			}
		}
		SharedPreferences.Editor editer = dispatcherPref.edit();
		editer.putString(Constants.KEY_LAST_ACTION, action);
		editer.commit();
	}

	private void updateEvent(Context context) {
		String event = "";
		for (String key : events.keySet()) {
			if (event.length() == 0) {
				event += key;
			} else {
				event += "," + key;
			}
		}
		SharedPreferences.Editor editer = dispatcherPref.edit();
		editer.putString(Constants.KEY_LAST_EVENT, event);
		editer.commit();
	}
}
