package com.oro.scheduler;

import java.io.Serializable;

/**
 * Created by oro on 15. 7. 2..
 */
public class ActionEvent implements Serializable {
	private String eventType;
	private String[] actionTypes;
	private int actionType;

	public ActionEvent(String eventType, String[] actionTypes) {
		this.eventType = eventType;
		this.actionTypes = actionTypes;
	}

	public String getActionType() {
		return actionTypes[actionType];
	}

	public void setActionType(int type) {
		this.actionType = type;
	}

	public String[] getActionTypes() {
		return actionTypes;
	}

	public String getEventType() {
		return eventType;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		ActionEvent event = new ActionEvent(eventType, actionTypes);
		event.setActionType(actionType);
		return event;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		String types = "";
		for (String s:actionTypes) {
			if (types.length() == 0) {
				types += s;
			} else {
				types += ", " + s;
			}
		}
		sb.append("\n");
		sb.append("action types : " + types);
		sb.append("\n");
		sb.append("action type : " + getActionType() + " (" + actionType + ")");
		sb.append("\n");
		sb.append("event type : " + eventType);
		sb.append("\n");
		return sb.toString();
	}
}
