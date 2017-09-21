
package com.memreas.sax.handler;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.memreas.base.Common;
import com.memreas.search.TagUser;
import com.memreas.util.MemreasProgressDialog;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AddFriendParser extends AsyncTask<String, String, String> {

	private Context context;
	private TagUser friend;
	private MemreasProgressDialog mDialog;
	private AddFriendHandler handler;

	public AddFriendParser(Context context, TagUser friend) {
		this.context = context;
		this.friend = friend;
		mDialog = MemreasProgressDialog.getInstance(context);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mDialog.showWithDelay("adding memreas friends to share...", 1000);
	}

	@Override
	protected String doInBackground(String... params) {

		/*
		 * generate xml
		 */
		String xmlData = XMLGenerator.addFriendXML(friend.getUser_id());

		String msg = "";
		try {
			handler = new AddFriendHandler();
			SaxParser.parse(Common.SERVER_URL + Common.ADD_FRIEND_ACTION,
					xmlData, handler, "xml");

			if (handler.getStatus().equalsIgnoreCase("success")) {
				msg = "memreas friend request sent...";
			} else {
				msg = "an error occurred : " + handler.getMessage();
			}
			publishProgress(msg);
		} catch (Exception e) {
			e.printStackTrace();
			return "failure";
		}
		return "success";
	}

	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.setMessage(values[0]);
		}
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

	/*
	 * Handler class for parsing return data
	 */
	private class AddFriendHandler extends DefaultHandler {
		Boolean curElement;
		String curValue;
		int i = 0;
		private String status;
		private String message;

		public AddFriendHandler() {
			status = null;
			message = null;
		}

		public String getStatus() {
			return status;
		}

		public String getMessage() {
			return message;
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

			Log.d(getClass().getName() + i, "endElement =>" + localName + " =>"
					+ curValue);
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
	} // end private class AddFriendHandler
} // end async class
