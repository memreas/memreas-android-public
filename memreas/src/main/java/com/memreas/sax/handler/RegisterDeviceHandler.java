
package com.memreas.sax.handler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RegisterDeviceHandler extends DefaultHandler {

	Boolean curElement;// ,url_web=false;
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
}