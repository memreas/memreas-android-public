package com.memreas.notifications;

import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.memreas.base.Common;
import com.memreas.base.SessionManager;
import com.memreas.sax.handler.RegisterDeviceHandler;
import com.memreas.sax.handler.SaxParser;
import com.memreas.sax.handler.XMLGenerator;

public class GcmRegistrationRunner {

	protected static GcmRegistrationRunner instance;
	protected String gcmRegistrationId;
	protected Context context;
	protected GoogleCloudMessaging gcm;
	protected String userName;
	protected String device_id;

	protected GcmRegistrationRunner(Context context) {
		this.context = context;
		// getGcmRegistrationId();

	}

	public static GcmRegistrationRunner getInstance(Context context) {
		if (instance == null) {
			instance = new GcmRegistrationRunner(context);
		}
		return instance;
	}

	public void getGcmRegistrationId(String userName, String device_id) {
		this.userName = userName;
		this.device_id = device_id;
		getGcmRegistrationId();
	}

	public String getGcmRegistrationId() {

		if (gcmRegistrationId == null) {
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						// Check if Play store services are available.
						if (!checkPlayServices())
							throw new Exception(
									"Google Play Services not supported. Please install and configure Google Play Store.");

						if (gcm == null) {
							gcm = GoogleCloudMessaging.getInstance(context);
						}
						gcmRegistrationId = gcm.register(Common.GCMSenderId);

						if ((userName != null) && (device_id != null)) {
							RegisterDeviceHandler handler = new RegisterDeviceHandler();
							String registerDeviceXmlData = XMLGenerator
									.registerDeviceXML(userName, device_id,
											gcmRegistrationId);
							SaxParser.parse(Common.SERVER_URL
									+ Common.REGISTER_DEVICE_ACTION,
									registerDeviceXmlData, "xml");

							if (handler.getStatus().equalsIgnoreCase("success")) {
								SessionManager.getInstance().setDevice_token(
										String.copyValueOf(gcmRegistrationId
												.toCharArray()));
							} else {
								SessionManager.getInstance().setDevice_token(
										null);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});
			thread.start();
		}

		return gcmRegistrationId;
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(context);
		if (resultCode != ConnectionResult.SUCCESS) {
			return false;
		}
		return true;
	}

}
