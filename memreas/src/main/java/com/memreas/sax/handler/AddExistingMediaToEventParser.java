
package com.memreas.sax.handler;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.memreas.gallery.GalleryBean;
import com.memreas.share.AddShareActivity;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AddExistingMediaToEventParser extends AsyncTask<Object, Void, String> {

	private AddShareActivity addShareActivity;
	private ProgressDialog mDialog;
	private GalleryBean media;
	private AddExistingMediaToEventHandler handler;
	
	public AddExistingMediaToEventParser(AddShareActivity activity) {
		this.addShareActivity = activity;
		mDialog = new ProgressDialog(activity);
		mDialog.setMessage("adding memreas media ..." + media.getMediaName() );
		mDialog.setCancelable(false);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mDialog.show();
	}

	@Override
	protected String doInBackground(Object... params) {

		// String xmlData = XMLGenerator.addEventXML(mMemreasShareBean);
		// handler = new AddShareHandler();
		// SaxParser.parse(Common.SERVER_URL + Common.ADD_EVENT_ACTION + "&sid="
		// + SessionManager.getInstance().getSessionId(), xmlData,
		// handler, "xml");
		//
		// return handler.getStatus();
		return "";
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
		if (result != null) {
		}
		if (handler.getStatus().equalsIgnoreCase("success")) {
			/*
			 * TODO - how to handle success and failed attempts
			 */
		}
	}

	/*
	 * Handler class for parsing return data
	 */
	private class AddExistingMediaToEventHandler extends DefaultHandler {
		Boolean curElement;
		String curValue;
		int i = 0;
		private String status;
		private String message;
		private String eventId;

		public AddExistingMediaToEventHandler() {
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
