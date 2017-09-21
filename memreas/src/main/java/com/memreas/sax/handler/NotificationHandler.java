
package com.memreas.sax.handler;

import android.util.Log;

import com.memreas.notifications.NotificationItem;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * Sample Notification xml
 * <!-- Request -->
 * <xml>
 * 	<updatenotification>
 * 		<notification>
 * 			<notification_id>5f173f40-2d87-11e3-b8a8-27e1f11594a6</notification_id>
 * 			<!-- status 0 - accept, 1 - decline, 2 - ignore, 3 - rejected(not used) -->
 * 			<status>2</status>
 * 			<message> optional </message>
 * 		</notification>
 * 	</updatenotification>
 * </xml>
 * 
 * <!-- Response -->
 * <xml>
 * <listnotificationresponse>
 * <status>success</status>
 * <notifications>
 *	 <notification>
 * 		<notification_id>e5285106-47be-11e3-85d4-22000a8a1935</notification_id>
 * 		<meta>vinod Has commented on yuu event</meta>
 * 		<notification_type>3</notification_type>
 * 		</notification> 
 * 	<notification>
 * 		<notification_id>ae0232fa-47b9-11e3-85d4-22000a8a1935</notification_id>
 * 		<meta>vinod want to add you to yuu event</meta>
 * 		<notification_type>2</notification_type>
 * 	</notification>
 * </notifications>
 * </listnotificationresponse>
 * </xml>
 */
public class NotificationHandler extends DefaultHandler {

	Boolean curElement;
	String curValue = "";
	int i = 0;
	private String status;
	public static NotificationGetterSetter getterSetter;
	private NotificationGetterSetter notificationGetterSetter;
	private List<NotificationItem> notifications;
	private List<NotificationGetterSetter> notificationList;
	private NotificationItem mNotification;
	private SimpleDateFormat dateFormater = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm:ss");

	public NotificationHandler() {
		notifications = new ArrayList<NotificationItem>();
	}

	public List<NotificationItem> getNotifications() {
		return notifications;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		i++;
		curElement = true;
		curValue = "";
		Log.w("ViewEventHandler " + i, " Sax Parser startElement =>"
				+ localName + " =>" + curValue);
		if (localName.equals("listnotificationresponse")) {
			getterSetter = new NotificationGetterSetter();
		} else if (localName.equals("notifications")) {
			notificationList = new ArrayList<NotificationGetterSetter>();
		} else if (localName.equals("notification")) {
			mNotification = new NotificationItem();
			notificationGetterSetter = new NotificationGetterSetter();
		} else if (localName.equals("updatenotification")) {
			getterSetter = new NotificationGetterSetter();
		} else if (localName.equals("clearallnotification")) {
			getterSetter = new NotificationGetterSetter();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		Log.i("ViewEventHandler " + i, " Sax Parser endElement =>" + localName
				+ " =>" + curValue);
		if (localName.equals("notification_id")) {
			mNotification.setNotificationId(curValue);
			if (notificationGetterSetter != null)
				notificationGetterSetter.setNotificationId(curValue);
		} else if (localName.equals("notification_type")) {
			mNotification.setNotificationType(curValue);
			if (notificationGetterSetter != null)
				notificationGetterSetter.setNotificationType(curValue);
		} else if (localName.equals("media_id")) {
			mNotification.setMediaId(curValue);
		} else if (localName.equals("profile_username")) {
			mNotification.setProfileName(curValue);
		} else if (localName.equals("profile_pic")) {
			mNotification.setProfilePicture(CommonHandler
					.parseMemJSON(curValue));
		} else if (localName.equals("profile_pic_79x80")) {
			mNotification.setProfilePicture79x80(CommonHandler
					.parseMemJSON(curValue));
		} else if (localName.equals("profile_pic_448x306")) {
			mNotification.setProfilePicture448x306(CommonHandler
					.parseMemJSON(curValue));
		} else if (localName.equals("profile_pic_98x78")) {
			mNotification.setProfilePicture98x78(CommonHandler
					.parseMemJSON(curValue));
		} else if (localName.equals("notification_status")) {
			mNotification.setNotificationStatus(curValue);
		} else if (localName.equals("updated_about")) {
			mNotification.setTimeString(curValue);
		} else if (localName.equals("profile_pic")) {
			mNotification.setProfilePicture(CommonHandler
					.parseMemJSON(curValue));
		} else if (localName.equals("event_media_url")) {
			mNotification.setMediaUrl(CommonHandler.parseMemJSON(curValue));
		} else if (localName.equals("media_path")) {
			mNotification.setMediaUrl(CommonHandler.parseMemJSON(curValue));
		} else if (localName.equals("notification_updated")) {
			try {
				mNotification.setTime(dateFormater.parse(curValue).getTime());
			} catch (Exception e) {
				mNotification.setTime(new Date().getTime());
			}
		} else if (localName.equals("event_id")) {
			mNotification.setEventId(curValue);
		} else if (localName.equals("event_name")) {
			mNotification.setEventName(curValue);
		} else if (localName.equals("notification")) {
			notifications.add(mNotification);
			if (notificationList != null) {
				notificationList.add(notificationGetterSetter);
			}
		} else if (localName.equals("notifications")) {
			if (getterSetter != null) {
				getterSetter.setNotificationList(notificationList);
			}
		} else if (localName.equals("status")) {
			if (getterSetter != null) {
				getterSetter.setStatus(curValue);
			}
			status = curValue;
		} else if (localName.equals("message")) {
			if ((getterSetter != null) && (curValue != null)) {
				getterSetter.setMessage(curValue);
				mNotification.setMessage(curValue);
			}
		}
		curElement = false;
		curValue = "";
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		Log.i("ViewEventHandler " + i, "characters =>"
				+ new String(ch, start, length));
		if (curElement) {
			curValue += new String(ch, start, length);
		}
	}

}
