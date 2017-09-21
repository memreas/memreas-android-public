
package com.memreas.sax.handler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.memreas.aws.AmazonClientManager;
import com.memreas.base.Common;
import com.memreas.base.SessionManager;
import com.memreas.member.LoginActivity;
import com.memreas.search.SearchPopup;
import com.memreas.util.MemreasImageLoader;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class LogoutParser extends AsyncTask<Object, Void, String> {

	private Activity activity;
	private ProgressDialog mDialog;

	public LogoutParser(Activity activity) {
		this.activity = activity;
		mDialog = new ProgressDialog(activity);
		mDialog.setMessage("Loading...");
		mDialog.setCancelable(false);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mDialog.show();
	}

	@Override
	protected String doInBackground(Object... params) {

		String xmlData = XMLGenerator.logoutXML();

		final LogoutHandler handler = new LogoutHandler();
		SaxParser.parse(Common.SERVER_URL + Common.LOGOUT_ACTION, xmlData,
				handler, "xml");

		if (handler.getStatus() == null) {
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(
							activity,
							"Server is not reachable! Your session will eventually timeout.",
							Toast.LENGTH_LONG).show();
				}
			});
			return null;
		}

		if (handler.getStatus().equalsIgnoreCase("success")) {
			return "";
			// } else {
			// activity.runOnUiThread(new Runnable() {
			//
			// @Override
			// public void run() {
			// Toast.makeText(activity, handler.getMessage(),
			// Toast.LENGTH_LONG).show();
			// }
			// });
		}
		return "";
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
		if (result != null) {
			activity.setResult(Activity.RESULT_OK);
			SessionManager.resetSessionManager();
			SearchPopup.clearSession();
			// Shutdown any transfers...
			AmazonClientManager.getInstance().getTransferManager()
					.shutdownNow();

			//reset memreas image loader
			MemreasImageLoader.getInstance().clearMemoryCache();
			MemreasImageLoader.getInstance().clearDiskCache();

			Intent i = new Intent(activity.getApplicationContext(),
					LoginActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.startActivity(i);
			activity.finish();
		}
	}

	private class LogoutHandler extends DefaultHandler {
		Boolean curElement;
		String curValue;
		int i = 0;
		private String status;
		private String message;

		public String getStatus() {
			return status;
		}

		public String getMessage() {
			return message;
		}

		public LogoutHandler() {
			status = null;
			message = null;
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			i++;
			curElement = true;
			curValue = "";

		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			// Log.i("CommonHandler " + i, "endElement =>" + localName + " =>"
			// + curValue);
			if (localName.equals("status")) {
				status = curValue;
			} else if (localName.equals("message")) {
				message = curValue;
			}

			curElement = false;
			curValue = "";
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (curElement) {
				curValue += new String(ch, start, length);
			}
		}
	}
}
