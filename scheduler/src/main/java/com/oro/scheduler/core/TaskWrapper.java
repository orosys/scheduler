package com.oro.scheduler.core;

import android.content.Context;

import com.oro.scheduler.ActionEvent;
import com.oro.scheduler.AbstractActionTask;

import java.util.Map;

/**
 * Created by oro on 15. 7. 8..
 */
public class TaskWrapper implements Runnable, IEndTask {
	private AbstractActionTask task;
	private Context context;
	private ActionEvent actionEvent;
	private Class<? extends AbstractActionTask> taskClazz;
	private IEndService endRunnableCallBack;

	public TaskWrapper(Context context, ActionEvent actionEvent, AbstractActionTask task) {
		this.context = context;
		this.actionEvent = actionEvent;
		this.task = task;
	}

	public TaskWrapper(Context context, ActionEvent actionEvent, Class<? extends AbstractActionTask> clazz) {
		this.context = context;
		this.actionEvent = actionEvent;
		this.taskClazz = clazz;
	}

	@Override
	public void run() {
		createTask();
		task.setActionEvent(context, actionEvent);
	}

	public AbstractActionTask getTask() {
		return task;
	}

	public void setTask(AbstractActionTask task) {
		this.task = task;
	}

	private void createTask() {
		if (task == null && taskClazz != null) {
			try {
				Object obj = taskClazz.newInstance();
				if (obj != null && obj instanceof AbstractActionTask) {
					this.task = (AbstractActionTask) obj;
					this.task.setEndTaskCallBack(this);
					this.task.init(context);
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onEndTask(AbstractActionTask job) {
		if (getEndRunnableCallBack() != null) {
			getEndRunnableCallBack().onEndRunnable(this);
		}
	}

	public IEndService getEndRunnableCallBack() {
		return endRunnableCallBack;
	}

	public void setEndRunnableCallBack(IEndService endRunnableCallBack) {
		this.endRunnableCallBack = endRunnableCallBack;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public ActionEvent getActionEvent() {
		return actionEvent;
	}

	public void setAction(Map<String, Object> params) {
		ActionEvent actionEvent = (ActionEvent) params.get("dispatch_action");
		setAction(actionEvent);
	}

	public void setAction(ActionEvent actionEvent) {
		this.actionEvent = actionEvent;
	}
}
