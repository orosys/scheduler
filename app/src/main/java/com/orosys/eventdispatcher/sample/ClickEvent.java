package com.orosys.eventdispatcher.sample;

import android.content.Context;
import android.widget.Toast;

import com.oro.scheduler.AbstractEventDispatcherService;
import com.oro.scheduler.ActionEvent;
import com.oro.scheduler.IEvent;

/**
 * Created by oro on 2017. 6. 23..
 */

public class ClickEvent implements IEvent {
    ActionEvent actionEvent = new ActionEvent("click", new String[]{"click"});
    @Override
    public ActionEvent getActionEvent() {
        return actionEvent;
    }

    @Override
    public void startEvent(Context context) {
        Toast.makeText(context, this.getClass().getSimpleName() + " startEvent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void stopEvent(Context context) {
        Toast.makeText(context, this.getClass().getSimpleName() + " stopEvent", Toast.LENGTH_SHORT).show();
    }

    public void trigger(Context context) {
        AbstractEventDispatcherService.dispatchAction(context, this);
    }
}
