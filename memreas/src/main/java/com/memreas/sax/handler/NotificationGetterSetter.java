package com.memreas.sax.handler;

import java.util.List;

public class NotificationGetterSetter {
	boolean check = false;

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public String getNotificationData() {
		return notificationData;
	}

	public void setNotificationData(String notificationData) {
		this.notificationData = notificationData;
	}

	public List<NotificationGetterSetter> getNotificationList() {
		return notificationList;
	}

	public void setNotificationList(
			List<NotificationGetterSetter> notificationList) {
		this.notificationList = notificationList;
	}

	private String status, message, notificationId, notificationType,
			notificationData;
	private List<NotificationGetterSetter> notificationList;
}
