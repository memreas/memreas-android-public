
package com.memreas.sax.handler;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.memreas.base.Common;
import com.memreas.gallery.GalleryActivity;
import com.memreas.notifications.NotificationItem;

import java.util.List;

//Notification Parsser
public class NotificationParser extends
		AsyncTask<String, Void, List<NotificationItem>> {

	private Activity activity;
	private ProgressBar mProgressBar;

	public NotificationParser(Activity activity, ProgressBar progressbar) {
		this.activity = activity;
		this.mProgressBar = progressbar;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		NotificationHandler.getterSetter = null;
	}

	@Override
	protected List<NotificationItem> doInBackground(String... arg0) {

		String xmlData = XMLGenerator.ListNotificationXML();

		Log.i("LoginParser XML DATA", xmlData);
		NotificationHandler handler = new NotificationHandler();
		SaxParser.parse(Common.SERVER_URL + Common.LIST_NOTIFICATION, xmlData,
				handler, "xml");

		return handler.getNotifications();
	}

	@Override
	protected void onPostExecute(List<NotificationItem> result) {
		super.onPostExecute(result);

		Intent intent = new Intent(activity, GalleryActivity.class);
		activity.startActivity(intent);
		activity.finish();

	}
}