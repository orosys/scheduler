package com.oro.scheduler;

import android.content.Context;

/**
 * Created by oro on 15. 7. 1..
 */
public interface IAction {
	void onReceiveActionEvent(Context context, ActionEvent actionEvent);
}