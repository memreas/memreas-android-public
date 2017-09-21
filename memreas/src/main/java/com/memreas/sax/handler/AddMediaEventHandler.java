package com.memreas.sax.handler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * sample...
 */
/*
<xml>
		<sid>
			xml.append(SessionManager.getInstance().getSession_id());
		</sid>
		<addmediaevent>
		<user_id>
			xml.append(SessionManager.getInstance().getUser_id());
		</user_id>
		<device_id>
			xml.append(SessionManager.getInstance().getDevice_id());
		</device_id>
		<device_type>
			xml.append(Common.DEVICE_TYPE);
		</device_type>
		<event_id>
			xml.append(eventId);
		</event_id>
		<media_id>
			xml.append(mediaId);
		</media_id>
		<is_server_image>
			xml.append((isServerImage ? "1" : "0"));
		</is_server_image>
		<content_type>
			xml.append(contentType);
		</content_type>
		<s3url>
			xml.append(s3url);
		</s3url>
		<s3file_name>
			my.mp4
		</s3file_name>
		<is_profile_pic>
			0
		</is_profile_pic>
		<location>
			{"latitude": "my_latitude","longitude": "my_longitude"}
		</location>
		<copyright>
			xml.append(copyright);
		</copyright>
		<applyCopyrightOnServer>
			xml.append((applyCopyrightOnServer ? "1" : "0"));
		</applyCopyrightOnServer>
	</addmediaevent>
</xml>
*/


public class AddMediaEventHandler extends DefaultHandler {

    Boolean curElement;
    String curValue = "";
    int i = 0;
    private String status;
    private String mediaId;

    public String getStatus() {
        return status;
    }

    public String getMediaId() {
        return mediaId;
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
            this.status = curValue;
        } else if (localName.equals("media_id")) {
            this.mediaId = curValue;
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
