
package com.memreas.sax.handler;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class NotificationUpdateHandler extends DefaultHandler {

	Boolean curElement, more = false;// ,url_web=false;
	String curValue;
	int i = 0;
	private String status;

	public String getStatus() {
		return status;
	}

	public NotificationUpdateHandler() {
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

		if (localName.equals("status")) {
			status = curValue;
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
