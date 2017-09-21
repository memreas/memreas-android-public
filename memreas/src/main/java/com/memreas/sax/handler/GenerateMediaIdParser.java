
package com.memreas.sax.handler;

import android.os.AsyncTask;
import android.util.Log;

import com.memreas.base.Common;
import com.memreas.gallery.GalleryBean;
import com.memreas.gallery.MediaIdManager;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class GenerateMediaIdParser extends AsyncTask<Void, Void, String> {

	GalleryBean media;
	GenerateMediaIdHandler handler;
	private String mediaId;
	private MediaIdManager mediaIdManager;

	public GenerateMediaIdParser(MediaIdManager mediaIdManager_) {
		this.handler = new GenerateMediaIdHandler();
		this.mediaIdManager = mediaIdManager_;
	}

	//public GenerateMediaIdParser(GalleryBean media) {
	//	this.handler = new GenerateMediaIdHandler();
	//	this.media = media;
	//}

	public GenerateMediaIdParser() {
		this.handler = new GenerateMediaIdHandler();
		this.media = null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(Void... arg0) {

		String xmlData = XMLGenerator.generateMediaIdXML();

		SaxParser.parse(Common.SERVER_URL + Common.GENERATE_MEDIA_ID_ACTION,
				xmlData, handler, "xml");

		if (this.media != null) {
			this.media.setMediaId(handler.media_id);
		}

		return handler.status;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		//do nothing - media_id is set
	}

	public String getMediaId() {
		return mediaId;
	}

	private class GenerateMediaIdHandler extends DefaultHandler {

		Boolean curElement;
		String curValue;
		int i = 0;
		private String status;
		private String media_id;

		public String getStatus() {
			return status;
		}

		public String getMediaId() {
			return media_id;
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

			Log.i("CommonHandler " + i, "endElement =>" + localName + " =>"
					+ curValue);
			if (localName.equals("status")) {
				this.status = curValue;
			} else if (localName.equals("media_id")) {
				mediaId = this.media_id = curValue;
			} else if (localName.equals("media_id_batch")) {
				mediaIdManager.setMediaIdBatchJsonArray(CommonHandler.parseMemJSONArray(curValue));
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
	} // end generateMediaIdHandler

}