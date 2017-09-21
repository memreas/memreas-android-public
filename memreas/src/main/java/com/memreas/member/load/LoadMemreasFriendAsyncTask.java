
package com.memreas.member.load;

import java.util.Iterator;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.memreas.base.Common;
import com.memreas.sax.handler.SaxParser;
import com.memreas.sax.handler.ViewAllFriendsHandler;
import com.memreas.sax.handler.XMLGenerator;
import com.memreas.share.ShareFriendAdapter;

public class LoadMemreasFriendAsyncTask extends
		AsyncTask<Void, Object, Void> {

	ProgressBar progressBar;
	ShareFriendAdapter adapter;
	Activity activity;

	public LoadMemreasFriendAsyncTask(Activity activity, ShareFriendAdapter adapter,
			ProgressBar progressBar) {
		this.adapter = adapter;
		this.activity = activity;
		this.progressBar = progressBar;
		this.progressBar.setVisibility(View.VISIBLE);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Void doInBackground(Void... arg0) {
		String xmlData = XMLGenerator.viewAllFriendsXML();
		Log.i("LoadMemreasFriend doInBackground ",
				"XML DATA ListAllFriends XML : " + xmlData);
		ViewAllFriendsHandler handler = new ViewAllFriendsHandler();
		//handler will populate list...
		SaxParser.parse(Common.SERVER_URL + Common.VIEW_ALL_FRIEND, xmlData,
				handler, "xml");
		Iterator<Object> iterator = handler.getmFriendsOrGroups().iterator();
		while (iterator.hasNext()) {
			publishProgress(iterator.next());
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Object... values) {
		adapter.add(values[0]);
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		adapter.notifyDataSetChanged();		
		new LoadContactsAsyncTask(activity,
				ShareFriendAdapter.getInstance(), progressBar)
				.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

}
