
package com.memreas.notifications;

import java.util.ArrayList;

public class NotificationList {
	
	private static NotificationList instance;
	private ArrayList<NotificationItem> notifications;
	
	protected NotificationList() {
		notifications = new ArrayList<NotificationItem>();
	}
	
	public static NotificationList getInstance() {
		if (instance == null) {
			instance = new NotificationList();
		}
		return instance;
	}
	
	public void addNotification(NotificationItem item) {
		notifications.add(item);
	}

	public void removeNotification(NotificationItem item) {

		notifications.remove(item);

		/*
		Iterator iterator = instance.getNotificationList().iterator();
		while (iterator.hasNext()) {
			NotificationItem listItem = (NotificationItem) iterator.next();
			if (item.getNotificationId().equalsIgnoreCase(listItem.getNotificationId())) {
				notifications.remove(item);
			}
		}
		*/

	}

	public ArrayList<NotificationItem> getNotificationList() {
		return notifications;
	}
}
