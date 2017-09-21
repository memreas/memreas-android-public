
package com.memreas.notifications;

import org.json.JSONObject;

import java.io.Serializable;

public class NotificationItem implements Serializable {

	public enum Type {
		ACCEPT, DECLINE, BLOCK, IGNORE
	}

	private static final long serialVersionUID = 6912683300515044727L;
	private Type type;
	private String profilePicture;
	private String profileName;
	private String profilePicture79x80;
	private String profilePicture448x306;
	private String profilePicture98x78;
	private String eventId;
	private String eventName;
	private String mediaId;
	private JSONObject meta;
	private String message = "";
	private String notificationType;
	private String notificationStatus;
	private String notificationId;
	private String mediaUrl;
	private String timeString;
	private long time;

	public Type getType() {
		return type;
	}

	public int getType(boolean asInt) {
		int iType = 3;
		if (type == Type.ACCEPT) {
			iType = 1;
		} else if (type == Type.DECLINE) {
			iType = 2;
		} else if (type == Type.IGNORE) {
			iType = 3;
		} else if (type == Type.BLOCK) {
			iType = 4;
		}
		return iType;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getProfilePicture79x80() {
		return profilePicture79x80;
	}

	public void setProfilePicture79x80(String profilePicture79x80) {
		this.profilePicture79x80 = profilePicture79x80;
	}

	public String getProfilePicture98x78() {
		return profilePicture98x78;
	}

	public void setProfilePicture98x78(String profilePicture98x78) {
		this.profilePicture98x78 = profilePicture98x78;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public JSONObject getMeta() {
		return meta;
	}

	public void setMeta(JSONObject meta) {
		this.meta = meta;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public String getNotificationStatus() {
		return notificationStatus;
	}

	public void setNotificationStatus(String notificationStatus) {
		this.notificationStatus = notificationStatus;
	}

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public String getProfilePicture448x306() {
		return profilePicture448x306;
	}

	public void setProfilePicture448x306(String profilePicture448x306) {
		this.profilePicture448x306 = profilePicture448x306;
	}

	public String getMediaUrl() {
		return mediaUrl;
	}

	public void setMediaUrl(String mediaUrl) {
		this.mediaUrl = mediaUrl;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getTimeString() {
		return timeString;
	}

	public void setTimeString(String timeString) {
		this.timeString = timeString;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

}
