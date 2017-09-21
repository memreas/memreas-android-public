
package com.memreas.sax.handler;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.memreas.memreas.MemreasShareBean;
import com.memreas.share.AddShareActivity;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RemoveEventMediaParser extends AsyncTask<Object, String, String> {

	ProgressDialog mDialog;
	AddShareActivity addShareActivity;
	MemreasShareBean mMemreasShareBean;
	RemoveEventMediaHandler handler = new RemoveEventMediaHandler();

	public RemoveEventMediaParser(AddShareActivity activity) {
		this.addShareActivity = activity;
		
		mDialog = new ProgressDialog(activity);
		mDialog.setMessage("adding memreas friends to share...");
		mDialog.setCancelable(false);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mDialog.show();
	}

	@Override
	protected String doInBackground(Object... params) {

		String xmlData = XMLGenerator.removeEventMediaStart((String) params[0]);

		// String xmlData = XMLGenerator.RemoveEventMediaXML(params[0]);
		// SaxParser.parse(Common.SERVER_URL + Common.RemoveEventMedia_ACTION,
		// xmlData,
		// handler, "xml");

		// if ((handler.getStatus().toString()).equalsIgnoreCase("success")) {
		// }
		// return handler.getStatus();

		return "";
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
	private class RemoveEventMediaHandler extends DefaultHandler {
		Boolean curElement;
		String curValue;
		int i = 0;
		private String status;
		private String message;

		public RemoveEventMediaHandler() {
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

			// Log.d(getClass().getName() + i, "endElement =>" + localName +
			// " =>"
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
	} // end private class RemoveEventMediaHandler
} // end RemoveEventMediaParser