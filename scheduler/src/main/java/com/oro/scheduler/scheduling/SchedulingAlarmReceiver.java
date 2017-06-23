package com.oro.scheduler.scheduling;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Parcelable;

import com.oro.scheduler.Constants;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class SchedulingAlarmReceiver extends AbstractWakefulBroadcastReceiver {
	private static final String TAG = SchedulingAlarmReceiver.class.getSimpleName();
	private AlarmManager alarmMgr;
	private String className;
	private Intent intent;
	private PendingIntent pendingIntent;

	public SchedulingAlarmReceiver() {
		super();
	}

	public SchedulingAlarmReceiver(Context context, Class<?> cls) {
		super();
		init(context, cls);
	}

	private void init(Context context, Class<?> cls) {
		this.className = cls.getName();

		alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		intent = new Intent(context, SchedulingAlarmReceiver.class);
		intent.putExtra("target_service_name", this.className);
		pendingIntent = PendingIntent.getBroadcast(context, className.hashCode(), intent, 0);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String className = intent.getStringExtra("target_service_name");
		try {
			Class<?> clazz = Class.forName(className);
			Object obj = clazz.newInstance();
			if (obj != null && obj instanceof BroadcastReceiver) {
				((BroadcastReceiver) obj).onReceive(context, intent);
				return;
			}

			intent.setClass(context, clazz);
			startWakefulIntent(context, intent);
			ComponentName result = context.startService(intent);
			if (result == null) {
				completeWakefulIntent(intent);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void setBootReceiver(Context context, long intervalMillis, boolean enableReboot) {
		if (enableReboot) {
			ComponentName receiver = new ComponentName(context, SchedulingBootReceiver.class);
			PackageManager pm = context.getPackageManager();

			pm.setComponentEnabledSetting(receiver,
					PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
					PackageManager.DONT_KILL_APP);

			SharedPreferences pref = context.getSharedPreferences(Constants.PREF_SCHEDULING_BOOT_RECEIVER, Context.MODE_MULTI_PROCESS);
			SharedPreferences.Editor editer = pref.edit();
			editer.putLong(Constants.KEY_INTERVAL_TIME, intervalMillis);
			editer.putString(Constants.KEY_CLASS_NAME, className);
			editer.commit();

		} else {
			ComponentName receiver = new ComponentName(context, SchedulingBootReceiver.class);
			PackageManager pm = context.getPackageManager();

			pm.setComponentEnabledSetting(receiver,
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
					PackageManager.DONT_KILL_APP);
		}
	}

	public void setAlarmRepeatingScheduling(Context context, long intervalMillis, boolean enable) {
		if (enable) {
			if (alarmMgr != null) {
				alarmMgr.cancel(pendingIntent);
				pendingIntent.cancel();
				pendingIntent = PendingIntent.getBroadcast(context, className.hashCode(), intent, 0);
				alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, 0, intervalMillis, pendingIntent);
			}

		} else {
			if (alarmMgr != null) {
				alarmMgr.cancel(pendingIntent);
			}
		}
	}

	public void setAlarmScheduling(Context context, boolean enable) {
		if (enable) {
			if (alarmMgr != null) {
				alarmMgr.cancel(pendingIntent);
				pendingIntent.cancel();
				try {
					pendingIntent = PendingIntent.getBroadcast(context, className.hashCode(), intent, 0);
					pendingIntent.send();
				} catch (PendingIntent.CanceledException e) {
					e.printStackTrace();
				}
			}

		} else {
			if (alarmMgr != null) {
				alarmMgr.cancel(pendingIntent);
			}
		}
	}

	public String getClassName() {
		return className;
	}

	public void appendIntentExtra(Map<String, Object> params) {
		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			Object obj = params.get(key);
			if (obj instanceof Integer) {
				intent.putExtra(key, (Integer) obj);
			} else if (obj instanceof Long) {
				intent.putExtra(key, (Long) obj);
			} else if (obj instanceof Float) {
				intent.putExtra(key, (Float) obj);
			} else if (obj instanceof Double) {
				intent.putExtra(key, (Double) obj);
			} else if (obj instanceof String) {
				intent.putExtra(key, (String) obj);
			} else if (obj instanceof Serializable) {
				intent.putExtra(key, (Serializable) obj);
			} else if (obj instanceof Parcelable) {
				intent.putExtra(key, (Parcelable) obj);
			}
		}
	}
}
