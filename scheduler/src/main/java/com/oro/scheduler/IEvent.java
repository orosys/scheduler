package com.oro.scheduler;

import android.content.Context;

/**
 * Created by oro on 15. 7. 1..
 */
public interface IEvent {
	public ActionEvent getActionEvent();

	public void startEvent(Context context);

	public void stopEvent(Context context);
}