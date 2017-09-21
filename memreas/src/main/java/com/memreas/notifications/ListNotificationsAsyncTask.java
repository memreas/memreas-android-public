package com.memreas.notifications;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.memreas.base.BaseActivity;
import com.memreas.base.Common;
import com.memreas.sax.handler.NotificationHandler;
import com.memreas.sax.handler.SaxParser;
import com.memreas.sax.handler.XMLGenerator;

import java.util.List;

public class ListNotificationsAsyncTask extends
		AsyncTask<String, Void, List<NotificationItem>> {

	private Activity mActivity;
	private ProgressBar mProgressBar;
	private NotificationAdapter mAdapter;

	public ListNotificationsAsyncTask(NotificationAdapter adapter, ProgressBar progressbar) {
		this.mAdapter = adapter;
		this.mProgressBar = progressbar;
	}

	public ListNotificationsAsyncTask(NotificationAdapter adapter,
			ProgressBar progressbar, boolean refresh) {
		this(adapter, progressbar);
		if (refresh) {
			mAdapter.clear();
		}
	}

	@Override
	protected void onPreExecute() {

		super.onPreExecute();

		NotificationHandler.getterSetter = null;
		if (mAdapter.getCount() == 0) {
			mProgressBar.setVisibility(View.VISIBLE);
		}
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

		NotificationList.getInstance().getNotificationList().clear();
		NotificationList.getInstance().getNotificationList().addAll(result);
		try {
			if (mActivity instanceof BaseActivity) {
				((BaseActivity) mActivity).updateNotificationNumber();
			}
		} catch (Exception e) {

		}
		if (mAdapter != null) {
			mProgressBar.setVisibility(View.GONE);
			mAdapter.notifyDataSetChanged();
		}
	}
}