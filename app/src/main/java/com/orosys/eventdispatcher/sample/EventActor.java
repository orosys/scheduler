package com.orosys.eventdispatcher.sample;

import android.content.Context;
import android.util.Log;

import com.oro.scheduler.AbstractActionTask;
import com.oro.scheduler.ActionEvent;

/**
 * Created by oro on 2017. 6. 23..
 */

public class EventActor extends AbstractActionTask {

    @Override
    public void onReceiveActionEvent(Context context, ActionEvent actionEvent) {
        Log.i("EventActor", "Trigger by " + actionEvent.getEventType());
    }
}
