package com.memreas.notifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.memreas.R;
import com.memreas.base.Common;
import com.memreas.base.SessionManager;
import com.memreas.gallery.GalleryActivity;
import com.memreas.sax.handler.RegisterDeviceHandler;
import com.memreas.sax.handler.SaxParser;
import com.memreas.sax.handler.XMLGenerator;
import com.memreas.util.IdGenerator;

public class GcmIntentService extends IntentService {

	public GcmIntentService() {
		super("com.memreas.notifications.GCMService");
	}

	private GoogleCloudMessaging gcm = null;
	private Context context = null;
	private NotificationManager mNotificationManager;
	private NotificationCompat.Builder builder;

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);
		int resource = 0;
		String typeName;

		if (!extras.isEmpty()) {
			if (extras.containsKey("name")
					&& extras.getString("name").equalsIgnoreCase("register")) {
				// for GCMRegID...
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				/*
				 * Handle inbound GCM messages here...
				 */

				/*
				 * Message types for memreas app...
				 * Server Constants for types...
				 * const ADD_FRIEND = '1';
				 * const ADD_FRIEND_TO_EVENT = '2';
				 * const ADD_COMMENT = '3';
				 * const ADD_MEDIA = '4';
				 * const ADD_EVENT = '5';
				 * const ADD_FRIEND_RESPONSE = '6';
				 * const ADD_FRIEND_TO_EVENT_RESPONSE = '7';
				 */

				/*
				 * Sample data from server... Bundle[ {from=107574586693,
				 * type=1, event_id=, message=jmeah2 has sent friend request.,
				 * android.support.content.wakelockid=1,
				 * collapse_key=do_not_collapse}]
				 */

				String from = extras.getString("from");
				//int type = extras.getInt("type");
				String type = extras.getString("type");
				String message = extras.getString("message");
				String event_id = extras.getString("event_id");
				String media_id = extras.getString("media_id");
				String collapse_key = extras.getString("collapse_key");
				String message_id = extras.getString("message_id");
				String registration_id = extras.getString("registration_id");

				if (message_id != null) {
					// Handle canonical registration ID
					String device_id = Secure.getString(
							this.getContentResolver(), Secure.ANDROID_ID);
					RegisterDeviceHandler handler = new RegisterDeviceHandler();
					String registerDeviceByCanonicalRegistrationIdXML = XMLGenerator
							.registerDeviceByCanonicalRegistrationIdXML(
									device_id, registration_id);
					SaxParser.parse(Common.SERVER_URL
							+ Common.REGISTER_DEVICE_ACTION,
							registerDeviceByCanonicalRegistrationIdXML, "xml");

					if (handler.getStatus().equalsIgnoreCase("success")) {
						SessionManager.getInstance().setDevice_token(
								registration_id);
					}
				}

				// Post notification of received message.
				sendNotification(type, message);
				Log.i(getClass().getName(), "Received: " + extras.toString());
			}
		}
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNotification(String resource, String msg) {
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, GalleryActivity.class), 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.logo_img)
				.setContentTitle("memreas notification")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(IdGenerator.getID(), mBuilder.build());
	}
}