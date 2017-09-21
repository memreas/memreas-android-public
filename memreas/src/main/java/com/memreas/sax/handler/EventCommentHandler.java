
package com.memreas.sax.handler;

import android.util.Log;

import com.memreas.share.EventComment;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class EventCommentHandler extends DefaultHandler {

	Boolean curElement, more = false;// ,url_web=false;
	String curValue;
	int i = 0;
	private List<EventComment> comments;
	private EventComment comment;

	public List<EventComment> getComments() {
		return comments;
	}

	public EventCommentHandler() {
		comments = new ArrayList<EventComment>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		i++;
		curElement = true;
		curValue = "";
		// Log.i("CommonHandler " + i, "startElement =>" + localName + " =>"
		// + curValue);
		if (localName.equals("comment")) {
			comment = new EventComment();
		}
		/*
		 * else if(localName.equals("media_url_web")){ url_web=true; }
		 */

		if (localName.equals("main_media_url")) {
			more = true;
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		Log.i("CommonHandler " + i, "endElement =>" + localName + " =>"
				+ curValue);
		if (localName.equals("comment")) {
			comments.add(comment);
		} else if (localName.equals("event_id")) {
			comment.setEventId(curValue);
		} else if (localName.equals("comment_text")) {
			comment.setCommentText(curValue);
		} else if (localName.equals("type")) {
			comment.setType(curValue);
		} else if (localName.equals("audio_media_url")) {
			comment.setAudioUrl(CommonHandler.parseMemJSON(curValue));
		} else if (localName.equals("username")) {
			comment.setUserName(curValue);
		} else if (localName.equals("profile_pic")) {
			comment.setProfilePicture(CommonHandler.parseMemJSON(curValue));
		} else if (localName.equals("commented_about")) {
			comment.setTimeString(curValue);
		}


		curElement = false;
		curValue = "";
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// Log.i("CommonHandler " + i, "characters =>"
		// + new String(ch, start, length));
		if (curElement) {
			curValue += new String(ch, start, length);
			// if(more){
			// curValue+=new String(ch, start, length);
			// }
			// else{
			// curValue = new String(ch, start, length);
			// curElement = false;
			// }
		}
	}
}
