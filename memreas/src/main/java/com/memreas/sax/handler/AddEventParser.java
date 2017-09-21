
package com.memreas.sax.handler;

import android.os.AsyncTask;
import android.util.Log;

import com.memreas.base.Common;
import com.memreas.memreas.MemreasShareBean;
import com.memreas.share.AddShareActivity;
import com.memreas.util.MemreasProgressDialog;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AddEventParser extends AsyncTask<Object, Void, String> {

    public static boolean isFinished = false;
    private AddShareActivity addShareActivity;
	private MemreasProgressDialog mDialog;
	private MemreasShareBean mMemreasShareBean;
	private AddShareHandler handler;

	public AddEventParser(AddShareActivity activity) {
		this.addShareActivity = activity;
		this.mMemreasShareBean = activity.getmMemreasAddShareBean();
		mDialog = activity.mDialog;
		mDialog.setCancelable(false);
	}

	@Override
	protected String doInBackground(Object... params) {

		publishProgress();
		String xmlData = XMLGenerator.addEventXML(mMemreasShareBean);
		handler = new AddShareHandler();
		SaxParser.parse(Common.SERVER_URL + Common.ADD_EVENT_ACTION, xmlData,
                handler, "xml");

		return handler.getStatus();
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
        if (!addShareActivity.isDestroyed()) {
            mDialog.setMessage("adding memreas share...");
            mDialog.show();
        }
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (handler.getStatus().equalsIgnoreCase("success")) {
			addShareActivity.setDetailsSet(true);
			mMemreasShareBean.setEventId(handler.getEventId());

			//
            // Handle media and friends here
			//
            mDialog.setMessage("adding memreas share media...");
            mDialog.show();
			addShareActivity.handleMedia();


			/** 20-JAN-2016 - removed to reduce complexity for launch
			//addShareActivity.addTextAndAudioCommentOnDone();
             */
            mDialog.setMessage("adding memreas share friends...");
            mDialog.show();
			addShareActivity.handleFriends();

            if (!addShareActivity.isDestroyed()) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }

		} else {
			addShareActivity.setDetailsSet(false);
			mMemreasShareBean.setEventId(null);
		}
	}

	public boolean getHandlerStatus() {
		if (handler.getStatus().equalsIgnoreCase("success")) {
			return true;
		}
		return false;
	}

	/*
	 * Handler class for parsing return data
	 */
	private class AddShareHandler extends DefaultHandler {
		Boolean curElement;
		String curValue;
		int i = 0;
		private String status;
		private String message;
		private String eventId;

		public AddShareHandler() {
			status = null;
			message = null;
			eventId = null;
		}

		public String getStatus() {
			return status;
		}

		public String getMessage() {
			return message;
		}

		public String getEventId() {
			return eventId;
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
			} else if (localName.equals("event_id")) {
				eventId = curValue;
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
