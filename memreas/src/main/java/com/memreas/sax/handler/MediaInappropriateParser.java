
package com.memreas.sax.handler;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.memreas.base.Common;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Iterator;

public class MediaInappropriateParser extends AsyncTask<String, String, String> {

	protected ProgressBar progressBar;
	protected Activity activity;
	protected String event_id;
	protected String user_id;
	protected String media_id;
	protected ArrayList<String> reasonTypeList;
	protected MediaInappropriateHandler handler = new MediaInappropriateHandler();

	public MediaInappropriateParser(Activity activity, ProgressBar progressBar,
			String event_id, String user_id, String media_id,
			ArrayList<String> reasonTypeList) {
		this.activity = activity;
		this.progressBar = progressBar;
		this.event_id = event_id;
		this.user_id = user_id;
		this.media_id = media_id;
		this.reasonTypeList = reasonTypeList;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... arg0) {

		try {
			String xmlData = XMLGenerator.inappropriateXMLStart(event_id,
					user_id, media_id);

			Iterator<String> iterator = reasonTypeList.iterator();
			while (iterator.hasNext()) {
				xmlData += XMLGenerator
						.inappropriateXMLReason((String) iterator.next());
			}
			xmlData += XMLGenerator.inappropriateXMLEnd();
			SaxParser.parse(Common.SERVER_URL
							+ Common.MEDIA_INAPPROPRIATE_ACTION, xmlData, handler,
					"xml");
			String[] resultArr = new String[2];
			resultArr[0] = handler.getStatus();
			resultArr[1] = handler.getMessage();

			publishProgress(resultArr);
			return handler.getStatus();
		} catch (Exception e) {
			e.printStackTrace();
			return "Failure";
		}
	}

	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		if (values[0].equalsIgnoreCase("Success")) {
			Toast.makeText(activity, values[1], Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(activity, values[1], Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
	}

	private class MediaInappropriateHandler extends DefaultHandler {

		Boolean curElement;
		String curValue;
		int i = 0;
		private String message;
		private String status;

		public String getMessage() {
			return message;
		}

		public String getStatus() {
			return status;
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			i++;
			curElement = true;
			curValue = "";

			/*
			 * Nothing needed here
			 */
			// if (localName.equals("main_media_url")) {
			// more = true;
			// }

		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			Log.i(getClass().getName() + i, "endElement =>" + localName + " =>"
					+ curValue);
			if (localName.equals("status")) {
				this.status = curValue;
			} else if (localName.equals("message")) {
				this.message = curValue;
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

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return super.toString();
		}

		@Override
		public void error(SAXParseException e) throws SAXException {
			// TODO Auto-generated method stub
			e.printStackTrace();
			Log.d(getClass().getName() + " columnNumber error @: ",
					String.valueOf(e.getColumnNumber()));
			Log.d(getClass().getName() + " lineNumber error @: ",
					String.valueOf(e.getLineNumber()));
			Log.d(getClass().getName() + " getLocalizedMessage error @: ",
					e.getLocalizedMessage());
			Log.d(getClass().getName() + " getMessage error @: ",
					e.getMessage());
			super.error(e);
		}

		public void warning(SAXParseException e) throws SAXException {
			Log.d(getClass().getName() + " Warning error @: ", e.getMessage());
			System.out.println("Warning:" + e.getMessage());
		}

		public void fatalError(SAXParseException e) throws SAXException {
			Log.d(getClass().getName() + " Fatal error @: ", e.getMessage());
			System.out.println("Fatal error");
			throw new SAXException(e.getMessage());
		}
	} // end loginhandler

}
