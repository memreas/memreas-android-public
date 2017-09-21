
package com.memreas.sax.handler;

import android.app.Activity;
import android.content.Context;
import android.util.Pair;

import com.memreas.base.Common;
import com.memreas.base.SessionManager;
import com.memreas.member.FriendBean;
import com.memreas.memreas.MemreasShareBean;
import com.memreas.notifications.NotificationItem;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class XMLGenerator {

	private static String sid;
	private static Context context;

	public XMLGenerator(Activity _context) {
		context = _context;
	}

	public static String registrationXML(String email, String username,
			String password, String device_id, String invited_by,
			String event_id, String profile_photo, String secret) {

		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<registration>").trim());
		xml.append(("		<email>").trim());
		xml.append(email);
		xml.append(("		</email>").trim());
		xml.append(("		<username>").trim());
		xml.append(username);
		xml.append(("		</username>").trim());
		xml.append(("		<password>").trim());
		xml.append(password);
		xml.append(("		</password>").trim());
		xml.append(("		<device_id>").trim());
		xml.append(device_id);
		xml.append(("		</device_id>").trim());
		xml.append(("		<device_type>").trim());
		xml.append(Common.DEVICE_TYPE);
		xml.append(("		</device_type>").trim());
		xml.append(("		<invited_by>").trim());
		xml.append(invited_by);
		xml.append(("		</invited_by>").trim());
		xml.append(("		<event_id>").trim());
		xml.append(event_id);
		xml.append(("		</event_id>").trim());
		xml.append(("		<profile_photo>").trim());
		xml.append(profile_photo);
		xml.append(("		</profile_photo>").trim());
        xml.append(("		<secret>").trim());
        xml.append(secret);
        xml.append(("		</secret>").trim());
		xml.append(("	</registration>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String loginXML(String userName, String password,
			String deviceId) {

		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<login>").trim());
		xml.append(("		<username>").trim());
		xml.append(userName);
		xml.append(("		</username>").trim());
		xml.append(("		<password>").trim());
		xml.append(password);
		xml.append(("		</password>").trim());
		xml.append(("		<device_id>").trim());
		xml.append(deviceId);
		xml.append(("		</device_id>").trim());
		xml.append(("		<device_type>").trim());
		xml.append(Common.DEVICE_TYPE);
		xml.append(("		</device_type>").trim());
		xml.append(("	</login>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String logoutXML() {

		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("</sid>").trim());
		xml.append(("<logout>").trim());
		xml.append(("<user_id>").trim());
		xml.append(SessionManager.getInstance().getUser_id());
		xml.append(("</user_id>").trim());
		xml.append(("</logout>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String fetchMemreasTVMXML() {

		StringBuilder xml = new StringBuilder();
        xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<username>").trim());
		xml.append(SessionManager.getInstance().getUser_name());
		xml.append(("	</username>").trim());
		xml.append(("	<type>xml</type>").trim());
		xml.append(("	<memreas_tvm></memreas_tvm>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();

	}

	public static String fetchCopyRightBatchXML() {

		StringBuilder xml = new StringBuilder();

		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<username>").trim());
		xml.append(SessionManager.getInstance().getUser_name());
		xml.append(("	</username>").trim());
		xml.append(("	<fetchcopyrightbatch></fetchcopyrightbatch>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();

	}

	public static String loadAllMediaXML() {

		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<listallmedia>").trim());
		xml.append(("		<event_id>").trim());
		xml.append(("		</event_id>").trim());
		xml.append(("		<user_id>").trim());
		xml.append(SessionManager.getInstance().getUser_id());
		xml.append(("		</user_id>").trim());
		xml.append(("		<device_id>").trim());
		xml.append(SessionManager.getInstance().getDevice_id());
		xml.append(("		</device_id>").trim());
		xml.append(("		<metadata>").trim());
		xml.append(("true").trim());
		xml.append(("		</metadata>").trim());
		xml.append(("		<limit>").trim());
		xml.append(("100").trim());
		xml.append(("		</limit>").trim());
		xml.append(("		<page>").trim());
		xml.append(("1").trim());
		xml.append(("		</page>").trim());
		xml.append(("	</listallmedia>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();

	}

	public static String getUserDetails(String userId) {

		SessionManager sMgr = SessionManager.getInstance();
		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<getuserdetails>").trim());
		xml.append(("		<user_id>").trim());
		xml.append(SessionManager.getInstance().getUser_id());
		xml.append(("		</user_id>").trim());
		xml.append(("	</getuserdetails>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String forgotUserPassXML(String email) {

		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<forgotpassword>").trim());
		xml.append(("		<email>").trim());
		xml.append(email);
		xml.append(("		</email>").trim());
		xml.append(("	</forgotpassword>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String changePassXML(String pass, String verify, String code,
			boolean bReset) {

		if (bReset) {
			StringBuilder xml = new StringBuilder();
			xml.append(("<xml>").trim());
			xml.append(("	<changepassword>").trim());
			xml.append(("		<username>").trim());
			xml.append(("		</username>").trim());
			xml.append(("		<password>").trim());
			xml.append(("		</password>").trim());
			xml.append(("		<new>").trim());
			xml.append(pass);
			xml.append(("		</new>").trim());
			xml.append(("		<retype>").trim());
			xml.append(verify);
			xml.append(("		</retype>").trim());
			xml.append(("		<token>").trim());
			xml.append(code);
			xml.append(("		</token>").trim());
			xml.append(("	</changepassword>").trim());
			xml.append(("</xml>").trim());

			return xml.toString();
		} else {
			return "";
		}
	}

	public static String checkUserNameXML(String userName) {

		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<checkusername>").trim());
		xml.append(("		<username>").trim());
		xml.append(userName);
		xml.append(("		</username>").trim());
		xml.append(("	</checkusername>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String updateMediaLocationXML(String mediaId, String lat,
			String lng, String address) {
		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<updatemedia>").trim());
		xml.append(("		<media>").trim());
		xml.append(("			<media_id>").trim());
		xml.append(mediaId);
		xml.append(("			</media_id>").trim());
		xml.append(("			<location>").trim());
		xml.append(("				<address>").trim());
		xml.append(address);
		xml.append(("				</address>").trim());
		xml.append(("				<latitude>").trim());
		xml.append(lat);
		xml.append(("				</latitude>").trim());
		xml.append(("				<longitude>").trim());
		xml.append(lng);
		xml.append(("				</longitude>").trim());
		xml.append(("			</location>").trim());
		xml.append(("		</media>").trim());
		xml.append(("	</updatemedia>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String ListNotificationXML() {

		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<listnotification>").trim());
		xml.append(("		<receiver_uid>").trim());
		xml.append(SessionManager.getInstance().getUser_id());
		xml.append(("		</receiver_uid>").trim());
		xml.append(("	</listnotification>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String UpdateNotificationXML(
			List<NotificationGetterSetter> listNotification) {
		String str = "";
		for (int i = 0; i < listNotification.size(); i++) {
			if (listNotification.get(i).isCheck()) {
				str += "<notification>" + "<notification_id>"
						+ listNotification.get(i).getNotificationId()
						+ "</notification_id><status>1</status></notification>";
			} else {
				str += "<notification><notification_id>"
						+ listNotification.get(i).getNotificationId()
						+ "</notification_id><status>2</status></notification>";
			}
		}
		return "<xml>" + "<sid>" + SessionManager.getInstance().getSession_id()
				+ "</sid>" + "<updatenotification>" + str
				+ "</updatenotification></xml>";
	}

	public static String UpdateNotificationXML(NotificationItem item) {
		StringBuilder xml = new StringBuilder();

		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<updatenotification>").trim());
		xml.append(("		<notification>").trim());
		xml.append(("			<notification_id>").trim());
		xml.append(item.getNotificationId());
		xml.append(("			</notification_id>").trim());
		xml.append(("			<status>").trim());
		xml.append(item.getType());
		xml.append(("			</status>").trim());
		xml.append(("			<message>").trim());
		xml.append(item.getMessage());
		xml.append(("			</message>").trim());
		xml.append(("		</notification>").trim());
		xml.append(("	</updatenotification>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String UpdateNotificationListXML(
			List<Pair<NotificationItem, String>> acceptItems,
			List<Pair<NotificationItem, String>> declineItems,
			List<Pair<NotificationItem, String>> blockItems) {
		String str = "";
		for (int i = 0; i < acceptItems.size(); i++) {
			str += "<notification><notification_id>"
					+ acceptItems.get(i).first.getNotificationId()
					+ "</notification_id><status>1</status><message>"
					+ acceptItems.get(i).second + "</message></notification>";
		}
		for (int i = 0; i < declineItems.size(); i++) {
			str += "<notification><notification_id>"
					+ declineItems.get(i).first.getNotificationId()
					+ "</notification_id><status>0</status><message>"
					+ declineItems.get(i).second + "</message></notification>";
		}
		for (int i = 0; i < blockItems.size(); i++) {
			str += "<notification><notification_id>"
					+ blockItems.get(i).first.getNotificationId()
					+ "</notification_id><status>2</status><message>"
					+ blockItems.get(i).second + "</message></notification>";
		}
		return "<xml>" + "<sid>" + SessionManager.getInstance().getSession_id()
				+ "</sid>" + "<updatenotification>" + str
				+ "</updatenotification></xml>";
	}

	public static String viewAllFriendsXML() {

		StringBuilder xml = new StringBuilder();
        xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<viewallfriends>").trim());
		xml.append(("		<user_id>").trim());
		xml.append(SessionManager.getInstance().getUser_id());
		xml.append(("		</user_id>").trim());
		xml.append(("	</viewallfriends>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String removeEventMediaStart(String eventId) {

		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<removeeventmedia>").trim());
		xml.append(("		<event_id>").trim());
		xml.append(eventId);
		xml.append(("		</event_id>").trim());
		xml.append(("		<media_ids>").trim());

		return xml.toString();

	}

	public static String removeEventMediaEntry(String mediaId) {
		StringBuilder xml = new StringBuilder();
		xml.append(("			<media_id>").trim());
		xml.append(mediaId);
		xml.append(("			</media_id>").trim());

		return xml.toString();

	}

	public static String removeEventMediaEnd() {
		StringBuilder xml = new StringBuilder();
		xml.append(("		</media_ids>").trim());
		xml.append(("	</removeeventmedia>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();

	}

	public static String viewMediaDetailXML(String mediaId) {
		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<viewmediadetails>").trim());
		xml.append(("		<media_id>").trim());
		xml.append(mediaId);
		xml.append(("		</media_id>").trim());
		xml.append(("	</viewmediadetails>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String viewEventXML(int isMyEvent, int isFriendEvent,
			int isPublicEvent) {

		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<viewevent>").trim());
		xml.append(("		<user_id>").trim());
		xml.append(SessionManager.getInstance().getUser_id());
		xml.append(("		</user_id>").trim());
		xml.append(("		<is_my_event>").trim());
		xml.append(isMyEvent);
		xml.append(("		</is_my_event>").trim());
		xml.append(("		<is_friend_event>").trim());
		xml.append(isFriendEvent);
		xml.append(("		</is_friend_event>").trim());
		xml.append(("		<is_public_event>").trim());
		xml.append(isPublicEvent);
		xml.append(("		</is_public_event>").trim());
		xml.append((" 		<page>1</page> ").trim());
		xml.append(("		<limit>10000</limit>").trim());
		xml.append(("	</viewevent>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();

	}

	public static String addMediaToEvent(
			String eventId,
			String mediaId,
			boolean isServerImage,
			String s3url,
			String contentType,
			String fileName,
			boolean isProfilePicture,
			boolean isRegistration,
			String location,
			String copyright) {

		/* IOS version
		generateAddMediaEventXML:(NSString *)sid
                            withUserId:(NSString *)user_id
                       andWithDeviceId:(NSString *)device_id
                     andWithDeviceTYPE:(NSString *)device_type
                        andWithEventId:(NSString *)event_id
                        andWithMediaId:(NSString *)media_id
                          andWithS3Url:(NSString *)s3url
                    andWithContentType:(NSString *)content_type
                     andWithS3FileName:(NSString *)s3file_name
                  andWithIsServerImage:(NSString *)is_server_image
                   andWithIsProfilePic:(NSString *)is_profile_pic
                       andWithLocation:(NSString *)location
                      andWithCopyRight:(NSString *)copyright;

		 */


		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<addmediaevent>").trim());
		xml.append(("		<user_id>").trim());
		xml.append(SessionManager.getInstance().getUser_id());
		xml.append(("		</user_id>").trim());
		xml.append(("		<device_id>").trim());
		xml.append(SessionManager.getInstance().getDevice_id());
		xml.append(("		</device_id>").trim());
		xml.append(("		<device_type>").trim());
		xml.append(Common.DEVICE_TYPE);
		xml.append(("		</device_type>").trim());
		xml.append(("		<event_id>").trim());
		xml.append(eventId);
		xml.append(("		</event_id>").trim());
		xml.append(("		<media_id>").trim());
		xml.append(mediaId);
		xml.append(("		</media_id>").trim());
		xml.append(("		<is_server_image>").trim());
		xml.append((isServerImage ? "1" : "0"));
		xml.append(("		</is_server_image>").trim());
		xml.append(("		<content_type>").trim());
		xml.append(contentType);
		xml.append(("		</content_type>").trim());
		xml.append(("		<s3url>").trim());
		xml.append(s3url);
		xml.append(("		</s3url>").trim());
		xml.append(("		<s3file_name>").trim());
		xml.append(fileName);
		xml.append(("		</s3file_name>").trim());
		xml.append(("		<is_profile_pic>").trim());
		xml.append((isProfilePicture ? "1" : "0"));
		xml.append(("		</is_profile_pic>").trim());
		xml.append(("		<is_registration>").trim());
		xml.append((isRegistration ? "1" : "0"));
		xml.append(("		</is_registration>").trim());
		xml.append(("		<location>").trim());
		xml.append(location);
		xml.append(("		</location>").trim());
		xml.append(("		<copyright>").trim());
		xml.append(copyright);
		xml.append(("		</copyright>").trim());
		xml.append(("	</addmediaevent>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String addEventXML(MemreasShareBean mMemreasShareBean) {

		/*
		 * <!-- Sample XML --> <?xml version="1.0" encoding="UTF-8"?>
		 * <xml><addevent> <user_id>1</user_id> <event_name>Event 1</event_name>
		 * <event_date>22/02/2013</event_date>
		 * <event_location>Ahmedabad</event_location>
		 * <event_from>22/02/2013</event_from> <event_to>28/02/2013</event_to>
		 * <is_friend_can_add_friend>yes</is_friend_can_add_friend>
		 * <is_friend_can_post_media>no</is_friend_can_post_media>
		 * <event_self_destruct>02/03/2013</event_self_destruct>
		 * <is_public>1</is_public> </addevent> </xml>
		 */

		return addEventXML(mMemreasShareBean.getName(),
				mMemreasShareBean.getDate(), mMemreasShareBean.getLocation(),
				mMemreasShareBean.getViewableFrom(),
				mMemreasShareBean.getViewableTo(),
				(mMemreasShareBean.isFriendsCanAddFriends() ? "1" : "0"),
				(mMemreasShareBean.isFriendsCanPost() ? "1" : "0"),
				mMemreasShareBean.getGhostEndDate(),
				(mMemreasShareBean.isPublicShare() ? "1" : "0"));
	}

	public static String addEventXML(String eventName, String eventDate,
			String eventLocation, String eventFrom, String eventTo,
			String isFriendCanAdd, String isFriendCanPost,
			String eventSelfDestruct, String isPublic) {

		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<addevent>").trim());
		xml.append(("		<user_id>").trim());
		xml.append((SessionManager.getInstance().getUser_id()).trim());
		xml.append(("		</user_id>").trim());
		xml.append(("		<device_id>").trim());
		xml.append((SessionManager.getInstance().getDevice_id()).trim());
		xml.append(("		</device_id>").trim());
		xml.append(("		<device_type>").trim());
		xml.append((Common.DEVICE_TYPE).trim());
		xml.append(("		</device_type>").trim());
		xml.append(("		<event_name>").trim());
		xml.append((eventName).trim());
		xml.append(("		</event_name>").trim());
		xml.append(("		<event_date>").trim());
		xml.append((eventDate).trim());
		xml.append(("		</event_date>").trim());
		xml.append(("		<event_location>").trim());
		xml.append((eventLocation).trim());
		xml.append(("		</event_location>").trim());
		xml.append(("		<event_from>").trim());
		xml.append((eventFrom).trim());
		xml.append(("		</event_from>").trim());
		xml.append(("		<event_to>").trim());
		xml.append((eventTo).trim());
		xml.append(("		</event_to>").trim());
		xml.append(("		<is_friend_can_add_friend>").trim());
		xml.append((isFriendCanAdd).trim());
		xml.append(("		</is_friend_can_add_friend>").trim());
		xml.append(("		<is_friend_can_post_media>").trim());
		xml.append((isFriendCanPost).trim());
		xml.append(("		</is_friend_can_post_media>").trim());
		xml.append(("		<event_self_destruct>").trim());
		xml.append((eventSelfDestruct).trim());
		xml.append(("		</event_self_destruct>").trim());
		xml.append(("		<is_public>").trim());
		xml.append((isPublic).trim());
		xml.append(("		</is_public>").trim());
		xml.append(("	</addevent>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String getCommentXML(String eventId, String mediaId,
			int page, int limit) {

		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<listcomments>").trim());
		xml.append(("		<event_id>").trim());
		xml.append((eventId).trim());
		xml.append(("		</event_id>").trim());
		xml.append(("		<media_id>").trim());
		xml.append((mediaId).trim());
		xml.append(("		</media_id>").trim());
		xml.append(("<		limit>").trim());
		xml.append(limit);
		xml.append(("		</limit>").trim());
		xml.append(("		<page>").trim());
		xml.append(page);
		xml.append(("		</page>").trim());
		xml.append(("	</listcomments>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String addCommentXML(String comment) {

		/*
		 * TODO: Fix audio url and media id...
		 */
		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<addcomment>").trim());
		xml.append(("		<event_id>").trim());
		xml.append((Common.EVENTID).trim());
		xml.append(("		</event_id>").trim());
		xml.append(("		<media_id>").trim());
		xml.append(1);
		xml.append(("		</media_id>").trim());
		xml.append(("		<comments>").trim());
		xml.append((comment).trim());
		xml.append(("		</comments>").trim());
		xml.append(("		<audio_url>url</audio_url>").trim());
		xml.append(("	</addcomment>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String addCommentXML(String comment, String mediaId,
			String audioMediaUrl) {
		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<addcomment>").trim());
		xml.append(("		<event_id>").trim());
		xml.append((Common.EVENTID).trim());
		xml.append(("		</event_id>").trim());
		xml.append(("		<media_id>").trim());
		xml.append((mediaId).trim());
		xml.append(("		</media_id>").trim());
		xml.append(("		<user_id>").trim());
		xml.append((SessionManager.getInstance().getUser_id()).trim());
		xml.append(("		</user_id>").trim());
		xml.append(("		<comments>").trim());
		xml.append((comment).trim());
		xml.append(("		</comments>").trim());
		xml.append(("		<audio_media_id>").trim());
		xml.append((audioMediaUrl).trim());
		xml.append(("		</audio_media_id>").trim());
		xml.append(("	</addcomment>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String addCommentXML(String comment, String eventId,
			String mediaId, String audioMediaId) {

		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<addcomment>").trim());
		xml.append(("		<event_id>").trim());
		xml.append((eventId).trim());
		xml.append(("		</event_id>").trim());
		xml.append(("		<media_id>").trim());
		xml.append((mediaId).trim());
		xml.append(("		</media_id>").trim());
		xml.append(("		<user_id>").trim());
		xml.append((SessionManager.getInstance().getUser_id()).trim());
		xml.append(("		</user_id>").trim());
		xml.append(("		<comments>").trim());
		xml.append((comment).trim());
		xml.append(("		</comments>").trim());
		xml.append(("		<audio_media_id>").trim());
		xml.append((audioMediaId).trim());
		xml.append(("		</audio_media_id>").trim());
		xml.append(("	</addcomment>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String createGroupStartXML(String groupName,
			LinkedList<FriendBean> listFriendBean) {
		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<creategroup>").trim());
		xml.append(("		<group_name>").trim());
		xml.append((groupName).trim());
		xml.append(("		</group_name>").trim());
		xml.append(("		<user_id>").trim());
		xml.append((SessionManager.getInstance().getUser_id()).trim());
		xml.append(("		</user_id>").trim());
		xml.append(("		<friends>").trim());
		Iterator iterator = listFriendBean.iterator();
		while (iterator.hasNext()) {
			FriendBean friend = (FriendBean) iterator.next();
			// Start friend entry
			xml.append(("			<friend>").trim());

			// friend_name
			xml.append(("			<friend_name>").trim());
			xml.append(friend.getFriendName());
			xml.append(("			</friend_name>").trim());

			// friend network - only memreas users allowed in groups
			xml.append(("			<network_name>").trim());
			xml.append("memreas");
			xml.append(("			</network_name>").trim());

			// friend profile pic url
			xml.append(("			<profile_pic_url>").trim());
			xml.append(friend.getProfileImgUrl());
			xml.append(("			</profile_pic_url>").trim());

			// Finish friend entry
			xml.append(("			</friend>").trim());
		}
		xml.append(("		</friends>").trim());
		xml.append(("	</creategroup>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String addFriendXML(String friendId) {

		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<addfriend>").trim());
		xml.append(("		<user_id>").trim());
		xml.append((SessionManager.getInstance().getUser_id()).trim());
		xml.append(("		</user_id>").trim());
		xml.append(("		<friend_id>").trim());
		xml.append((friendId).trim());
		xml.append(("		</friend_id>").trim());
		xml.append(("	</addfriend>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String addFriendToEventStart(String eventId) {
		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<addfriendtoevent>").trim());
		xml.append(("		<user_id>").trim());
		xml.append((SessionManager.getInstance().getUser_id()).trim());
		xml.append(("		</user_id>").trim());
		xml.append(("		<event_id>").trim());
		xml.append((eventId).trim());
		xml.append(("		</event_id>").trim());

		return xml.toString();
	}

	public static String addEmailXML(String email) {
		StringBuilder xml = new StringBuilder();
		xml.append(("<email>").trim());
		xml.append((email).trim());
		xml.append(("</email>").trim());

		return xml.toString();
	}

	public static String addFriendsXML(String friendId, String friendName,
			String networkName, String profilePicUrl) {

		StringBuilder xml = new StringBuilder();

		xml.append(("<friend>").trim());
		xml.append(("	<friend_id>").trim());
		xml.append((friendId).trim());
		xml.append(("	</friend_id>").trim());
		xml.append(("	<friend_name>").trim());
		xml.append((friendName).trim());
		xml.append(("	</friend_name>").trim());
		xml.append(("	<network_name>").trim());
		xml.append((networkName).trim());
		xml.append(("	</network_name>").trim());
		xml.append(("	<profile_pic_url><![CDATA[" + profilePicUrl + "]]>")
				.trim());
		xml.append(("	</profile_pic_url>").trim());
		xml.append(("</friend>").trim());

		return xml.toString();
	}

	public static String addGroupsXML(String groupId) {
		StringBuilder xml = new StringBuilder();

		xml.append(("<group>").trim());
		xml.append(("	<group_id>").trim());
		xml.append((groupId).trim());
		xml.append(("	</group_id>").trim());
		xml.append(("</group>").trim());
		return xml.toString();

	}

	public static String downloadXML(String mediaId) {

		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<download>").trim());
		xml.append(("	<user_id>").trim());
		xml.append((SessionManager.getInstance().getUser_id()).trim());
		xml.append(("	</user_id>").trim());
		xml.append(("	<media_id>").trim());
		xml.append((mediaId).trim());
		xml.append(("	</media_id>").trim());
		xml.append(("	<device_id>").trim());
		xml.append((Common.DEVICEID).trim());
		xml.append(("	</device_id>").trim());
		xml.append(("	</download>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String likeMediaXML(String mEventId, String mMediaId,
			boolean isLike) {
		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());

		xml.append(("	<likemedia>").trim());
		xml.append(("		<event_id>").trim());
		xml.append((mEventId).trim());
		xml.append(("		</event_id>").trim());
		xml.append(("		<media_id>").trim());
		xml.append((mMediaId).trim());
		xml.append(("		</media_id>").trim());
		xml.append(("		<user_id>").trim());
		xml.append((SessionManager.getInstance().getUser_id()).trim());
		xml.append(("		</user_id>").trim());
		xml.append(("		<is_like>").trim());
		xml.append(isLike);
		xml.append(("		</is_like>").trim());
		xml.append(("	</likemedia>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String inappropriateXMLStart(String event_id, String user_id,
			String media_id) {
		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());

		xml.append(("	<mediainappropriate>").trim());
		xml.append(("		<event_id>").trim());
		xml.append((event_id).trim());
		xml.append(("		</event_id>").trim());
		xml.append(("		<user_id>").trim());
		xml.append((user_id).trim());
		xml.append(("		</user_id>").trim());
		xml.append(("		<media_id>").trim());
		xml.append((media_id).trim());
		xml.append(("		</media_id>").trim());
		xml.append(("		<inappropriate>").trim());
		xml.append(("1").trim());
		xml.append(("		</inappropriate>").trim());
		xml.append(("		<reason_types>").trim());

		return xml.toString();
	}

	public static String inappropriateXMLReason(String reason_type) {
		StringBuilder xml = new StringBuilder();

		xml.append(("			<reason_type>").trim());
		xml.append((reason_type).trim());
		xml.append(("			<reason_type>").trim());

		return xml.toString();
	}

	public static String inappropriateXMLEnd() {
		StringBuilder xml = new StringBuilder();

		xml.append(("		</reason_types>").trim());
		xml.append(("	</mediainappropriate").trim());
		xml.append(("</xml").trim());

		return xml.toString();
	}

	public static String changePassword(String userId, String oldPassword,
			String newPassword, String verifyPassword) {

		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<updatepassword>").trim());
		xml.append(("		<user_id>").trim());
		xml.append((userId).trim());
		xml.append(("		</user_id>").trim());
		xml.append(("		<old>").trim());
		xml.append((oldPassword).trim());
		xml.append(("		</old>").trim());
		xml.append(("		<new>").trim());
		xml.append((newPassword).trim());
		xml.append(("		</new>").trim());
		xml.append(("		<retype>").trim());
		xml.append((verifyPassword).trim());
		xml.append(("		</retype>").trim());
		xml.append(("	</updatepassword>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String updateUserEmail(String emailId) {

		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<saveuserdetails>").trim());
		xml.append(("		<user_id>").trim());
		xml.append((SessionManager.getInstance().getUser_id()).trim());
		xml.append(("		</user_id>").trim());
		xml.append(("		<email>").trim());
		xml.append((emailId).trim());
		xml.append(("		</email>").trim());
		xml.append(("	</saveuserdetails>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String getTags(String tags) {

		StringBuilder xml = new StringBuilder();

		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<findtag> ").trim());
		xml.append(("		<tag>").trim());
		xml.append((tags).trim());
		xml.append(("		</tag>").trim());
		xml.append(("		<user_id>").trim());
		xml.append((SessionManager.getInstance().getUser_id()).trim());
		xml.append(("		</user_id>").trim());
		xml.append(("	</findtag>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String getEventDetail(String eventId) {

		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<geteventdetails>").trim());
		xml.append(("		<event_id>").trim());
		xml.append((eventId).trim());
		xml.append(("		</event_id>").trim());
		xml.append(("	</geteventdetails>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String registerDeviceXML(String user_id, String device_id,
			String device_token) {

		SessionManager sMgr = SessionManager.getInstance();
		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<registerdevice>").trim());
		xml.append(("		<user_id>").trim());
		xml.append((user_id).trim());
		xml.append(("		</user_id>").trim());
		xml.append(("		<device_id>").trim());
		xml.append((device_id).trim());
		xml.append(("		</device_id>").trim());
		xml.append(("		<device_type>").trim());
		xml.append((Common.DEVICE_TYPE).trim());
		xml.append(("		</device_type>").trim());
		xml.append(("		<device_token>").trim());
		xml.append((device_token).trim());
		xml.append(("		</device_token>").trim());
		xml.append(("	</registerdevice>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String registerDeviceByCanonicalRegistrationIdXML(
			String device_id, String device_token) {

		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<registercanonicaldevice>").trim());
		xml.append(("		<device_id>").trim());
		xml.append((device_id).trim());
		xml.append(("		</device_id>").trim());
		xml.append(("		<device_type>").trim());
		xml.append((Common.DEVICE_TYPE).trim());
		xml.append(("		</device_type>").trim());
		xml.append(("		<canonical_device_token>").trim());
		xml.append((device_token).trim());
		xml.append(("		</canonical_device_token>").trim());
		xml.append(("	</registercanonicaldevice>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}
	
	
	public static String generateMediaIdXML() {

		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<generatemediaid>").trim());
		xml.append(("		<media_id>0</media_id>").trim());
		xml.append(("		<media_id_batch>1</media_id_batch>").trim());
		xml.append(("	</generatemediaid>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}

	public static String updateProfilePicXML() {

		StringBuilder xml = new StringBuilder();
		xml.append(("<xml>").trim());
		xml.append(("	<sid>").trim());
		xml.append(SessionManager.getInstance().getSession_id());
		xml.append(("	</sid>").trim());
		xml.append(("	<updateProfilePic>").trim());
		xml.append(("		<user_id>").trim());
		xml.append(("		</user_id>").trim());
		xml.append(("		<media_id>").trim());
		xml.append(("		</media_id>").trim());
		xml.append(("	</updateProfilePic>").trim());
		xml.append(("</xml>").trim());

		return xml.toString();
	}


} // end class XMLGenerator
