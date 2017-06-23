package com.orosys.eventdispatcher.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.oro.scheduler.AbstractEventDispatcherService;
import com.oro.scheduler.ActionEvent;
import com.oro.scheduler.IEvent;
import com.oro.scheduler.scheduling.SchedulingAlarmReceiver;

/**
 * Created by oro on 2017. 6. 23..
 */
public class LoopEvent extends BroadcastReceiver implements IEvent {
    private static final String TAG = LoopEvent.class.getSimpleName();
    private ActionEvent action = new ActionEvent("loop", new String[]{"loop_event"});
    private SchedulingAlarmReceiver loopReceiver;

    @Override
    public void onReceive(Context context, Intent intent) {
        AbstractEventDispatcherService.dispatchAction(context, this);
    }

    @Override
    public ActionEvent getActionEvent() {
        return action;
    }

    @Override
    public void startEvent(Context context) {
        // 60 second loop
        loopReceiver = new SchedulingAlarmReceiver(context, LoopEvent.class);
        loopReceiver.setAlarmRepeatingScheduling(context, 60 * 1000, true);
    }

    @Override
    public void stopEvent(Context context) {
        loopReceiver = new SchedulingAlarmReceiver(context, LoopEvent.class);
        loopReceiver.setAlarmRepeatingScheduling(context, 0, false);
    }
}
