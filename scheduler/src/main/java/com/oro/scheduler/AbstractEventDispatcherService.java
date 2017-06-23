package com.oro.scheduler;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.oro.scheduler.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by oro on 15. 7. 1..
 */
public abstract class AbstractEventDispatcherService extends Service {
	public static final String INTENT_ACTION_EVENT_START = "i_a_event_start";
	public static final String INTENT_ACTION_EVENT_STOP = "i_a_event_stop";
	public static final String INTENT_ACTION_EVENT_ADD = "i_a_event_add";
	public static final String INTENT_ACTION_EVENT_REMOVE = "i_a_event_remove";
	public static final String INTENT_ACTION_ACTION_ADD = "i_a_action_add";
	public static final String INTENT_ACTION_ACTION_REMOVE = "i_a_action_remove";
	public static final String INTENT_ACTION_ACTION_DISPATCH = "i_a_action_trigger";

	public static final String INTENT_DATA_CLASS_NAME = "i_d_class_name";
	public static final String INTENT_DATA_ACTION = "i_d_action";

	private static final String TAG = AbstractEventDispatcherService.class.getSimpleName();


	public static void init(Context context) {
		SharedPreferences pref = context.getSharedPreferences(Constants.PREF_DISPATCHER, Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editer = pref.edit();
		editer.putString(Constants.KEY_LAST_EVENT, "");
		editer.putString(Constants.KEY_LAST_ACTION, "");
		editer.commit();
	}

	public static void startEvent(Context context) {
		SharedPreferences pref = context.getSharedPreferences(Constants.PREF_DISPATCHER, Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editer = pref.edit();
		editer.putBoolean(Constants.KEY_IS_DISPATCHER_RUN, true);
		editer.commit();

		Intent intent = new Intent(context, Util.getEventDispatcherService(context));
		intent.setAction(AbstractEventDispatcherService.INTENT_ACTION_EVENT_START);
		try {
			context.startService(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void stopEvent(Context context) {
		SharedPreferences pref = context.getSharedPreferences(Constants.PREF_DISPATCHER, Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editer = pref.edit();
		editer.putBoolean(Constants.KEY_IS_DISPATCHER_RUN, false);
		editer.commit();

		Intent intent = new Intent(context, Util.getEventDispatcherService(context));
		intent.setAction(AbstractEventDispatcherService.INTENT_ACTION_EVENT_STOP);
		try {
			context.startService(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addAction(Context context, Class<? extends IAction> clazz) {
		Intent intent = new Intent(context, Util.getEventDispatcherService(context));
		intent.setAction(AbstractEventDispatcherService.INTENT_ACTION_ACTION_ADD);
		intent.putExtra(AbstractEventDispatcherService.INTENT_DATA_CLASS_NAME, clazz.getName());
		try {
			context.startService(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void removeAction(Context context, Class<? extends IAction> clazz) {
		Intent intent = new Intent(context, Util.getEventDispatcherService(context));
		intent.setAction(AbstractEventDispatcherService.INTENT_ACTION_ACTION_REMOVE);
		intent.putExtra(AbstractEventDispatcherService.INTENT_DATA_CLASS_NAME, clazz.getName());
		try {
			context.startService(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addEvent(Context context, Class<? extends IEvent> clazz) {
		Intent intent = new Intent(context, Util.getEventDispatcherService(context));
		intent.setAction(AbstractEventDispatcherService.INTENT_ACTION_EVENT_ADD);
		intent.putExtra(AbstractEventDispatcherService.INTENT_DATA_CLASS_NAME, clazz.getName());
		try {
			context.startService(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void removeEvent(Context context, Class<? extends IEvent> clazz) {
		Intent intent = new Intent(context, Util.getEventDispatcherService(context));
		intent.setAction(AbstractEventDispatcherService.INTENT_ACTION_EVENT_REMOVE);
		intent.putExtra(AbstractEventDispatcherService.INTENT_DATA_CLASS_NAME, clazz.getName());
		try {
			context.startService(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void dispatchAction(Context context, IEvent event) {
		Intent intent = new Intent(context, Util.getEventDispatcherService(context));
		intent.setAction(AbstractEventDispatcherService.INTENT_ACTION_ACTION_DISPATCH);
		intent.putExtra(AbstractEventDispatcherService.INTENT_DATA_CLASS_NAME, event.getClass().getName());
		intent.putExtra(AbstractEventDispatcherService.INTENT_DATA_ACTION, event.getActionEvent());
		try {
			context.startService(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isRunDispatcher(Context context) {
		SharedPreferences pref = context.getSharedPreferences(Constants.PREF_DISPATCHER, Context.MODE_MULTI_PROCESS);
		return pref.getBoolean(Constants.KEY_IS_DISPATCHER_RUN, false);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null || intent.getAction() == null) {
			return super.onStartCommand(intent, flags, startId);
		}

		if (INTENT_ACTION_EVENT_ADD.equals(intent.getAction())) {
			addEvent(intent.getStringExtra(AbstractEventDispatcherService.INTENT_DATA_CLASS_NAME));

		} else if (INTENT_ACTION_EVENT_REMOVE.equals(intent.getAction())) {
			removeEvent(intent.getStringExtra(AbstractEventDispatcherService.INTENT_DATA_CLASS_NAME));

		} else if (INTENT_ACTION_ACTION_ADD.equals(intent.getAction())) {
			addAction(intent.getStringExtra(AbstractEventDispatcherService.INTENT_DATA_CLASS_NAME));

		} else if (INTENT_ACTION_ACTION_REMOVE.equals(intent.getAction())) {
			removeAction(intent.getStringExtra(AbstractEventDispatcherService.INTENT_DATA_CLASS_NAME));

		} else if (INTENT_ACTION_ACTION_DISPATCH.equals(intent.getAction())) {
			Map<String, Object> params = new HashMap<>();
			params.put("dispatch_action", intent.getSerializableExtra(AbstractEventDispatcherService.INTENT_DATA_ACTION));
			dispatchAction(params);

		} else if (INTENT_ACTION_EVENT_START.equals(intent.getAction())) {
			startEvent();

		} else if (INTENT_ACTION_EVENT_STOP.equals(intent.getAction())) {
			stopEvent();

		}

		return super.onStartCommand(intent, flags, startId);
	}

	protected abstract void addEvent(String className);

	protected abstract void removeEvent(String className);

	protected abstract void addAction(String className);

	protected abstract void removeAction(String className);

	protected abstract boolean dispatchAction(Map<String, Object> params);

	protected abstract void startEvent();

	protected abstract void stopEvent();
}
