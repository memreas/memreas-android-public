package com.memreas.notifications;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.memreas.base.BaseActivity;
import com.memreas.base.Common;
import com.memreas.sax.handler.NotificationHandler;
import com.memreas.sax.handler.SaxParser;
import com.memreas.sax.handler.XMLGenerator;

public class UpdateNotificationAsyncTask extends
		AsyncTask<Void, String, String> {

	private Context context;
	private NotificationAdapter adapter;
	private NotificationItem item;
	private String status;
	private boolean isSwipe;

	public UpdateNotificationAsyncTask(Context context,
			NotificationAdapter adapter, NotificationItem item, boolean isSwipe) {
		this.context = context;
		this.item = item;
		this.adapter = adapter;
		this.isSwipe = isSwipe;
	}

	@Override
	protected String doInBackground(Void... args) {
		String xmlData = XMLGenerator.UpdateNotificationXML(item);
		Log.i("LoginParser XML DATA", xmlData);

		NotificationHandler handler = new NotificationHandler();
		SaxParser.parse(Common.SERVER_URL + Common.UPDATE_NOTIFICATION,
				xmlData, handler, "xml");

		publishProgress(handler.getStatus());

		return handler.getStatus();
	}

	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		//remove entry from list in activity regardless of result ...
		//if (values[0] != null && values[0].equalsIgnoreCase("success")) {
		//	handleNotifyDataSetChanged();
		//}
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (result != null && result.equalsIgnoreCase("success")) {
			try {
				if (context instanceof BaseActivity) {
					((BaseActivity) context).updateNotificationNumber();
				}
			} catch (Exception e) {
			}
		}
	}

	//private void handleNotifyDataSetChanged() {
	//	if (!this.isSwipe) {
	//		adapter.remove(item);
	//		NotificationList.getInstance().removeNotification(item);
	//		adapter.notifyDataSetChanged();
	//	}
	//}
}