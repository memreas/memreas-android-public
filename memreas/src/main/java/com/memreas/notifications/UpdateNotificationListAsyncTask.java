package com.memreas.notifications;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.memreas.base.BaseActivity;
import com.memreas.base.Common;
import com.memreas.sax.handler.NotificationUpdateHandler;
import com.memreas.sax.handler.SaxParser;
import com.memreas.sax.handler.XMLGenerator;

public class UpdateNotificationListAsyncTask extends
		AsyncTask<Void, Void, String> {

	private Context context;
	private List<Pair<NotificationItem, String>> mAcceptItems;
	private List<Pair<NotificationItem, String>> mDeclineItems;
	private List<Pair<NotificationItem, String>> mBlockItems;

	public UpdateNotificationListAsyncTask(Context context,
			List<Pair<NotificationItem, String>> mAcceptItems,
			List<Pair<NotificationItem, String>> mDeclineItems,
			List<Pair<NotificationItem, String>> mBlockItems) {
		this.context = context;
		this.mAcceptItems = mAcceptItems;
		this.mDeclineItems = mDeclineItems;
		this.mBlockItems = mBlockItems;

	}

	@Override
	protected String doInBackground(Void... args) {

		String xmlData = XMLGenerator.UpdateNotificationListXML(mAcceptItems,
				mDeclineItems, mBlockItems);
		Log.i("LoginParser XML DATA", xmlData);

		NotificationUpdateHandler handler = new NotificationUpdateHandler();

		SaxParser.parse(Common.SERVER_URL + Common.UPDATE_NOTIFICATION,
				xmlData, handler, "xml");

		return handler.getStatus();
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (result != null && result.equalsIgnoreCase("success")) {
			for (Pair<NotificationItem, String> item : mAcceptItems) {
				NotificationList.getInstance().getNotificationList()
						.remove(item.first);
			}
			for (Pair<NotificationItem, String> item : mDeclineItems) {
				NotificationList.getInstance().getNotificationList()
						.remove(item.first);
			}
			for (Pair<NotificationItem, String> item : mBlockItems) {
				NotificationList.getInstance().getNotificationList()
						.remove(item.first);
			}
			try {
				if (context instanceof BaseActivity) {
					((BaseActivity) context).updateNotificationNumber();
				}
			} catch (Exception e) {

			}
		}

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}
}