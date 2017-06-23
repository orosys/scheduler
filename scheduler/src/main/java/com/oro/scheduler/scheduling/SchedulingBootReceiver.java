package com.oro.scheduler.scheduling;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.oro.scheduler.Constants;

/**
 * This BroadcastReceiver automatically (re)starts the alarm when the device is
 * rebooted. This receiver is set to be disabled (android:enabled="false") in the
 * application's manifest file. When the user sets the alarm, the receiver is enabled.
 * When the user cancels the alarm, the receiver is disabled, so that rebooting the
 * device will not trigger this receiver.
 */
public class SchedulingBootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			SharedPreferences pref = context.getSharedPreferences(Constants.PREF_SCHEDULING_BOOT_RECEIVER, Context.MODE_MULTI_PROCESS);

			long intervalMillis = pref.getLong(Constants.KEY_INTERVAL_TIME, 0);
			String className = pref.getString(Constants.KEY_CLASS_NAME, "");

			try {
				Class<?> clazz = Class.forName(className);
				SchedulingAlarmReceiver alarm = new SchedulingAlarmReceiver(context, clazz);
				alarm.setAlarmRepeatingScheduling(context, intervalMillis, true);
				alarm.setBootReceiver(context, intervalMillis, true);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}