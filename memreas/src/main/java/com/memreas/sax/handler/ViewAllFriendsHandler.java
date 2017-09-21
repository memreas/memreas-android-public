
package com.memreas.sax.handler;

import com.memreas.member.FriendBean;
import com.memreas.member.FriendBean.FriendType;
import com.memreas.member.GroupBean;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.LinkedList;

public class ViewAllFriendsHandler extends DefaultHandler {

	Boolean curElement;
	String curValue;
	int i = 0;
	private String status;
	private String message;
	private LinkedList mFriendsOrGroups;
	private FriendBean mFriend;
	private GroupBean mGroup;
	private boolean isPartOfGroup=false;

	@SuppressWarnings("rawtypes")
	public ViewAllFriendsHandler() {
		mFriendsOrGroups = new LinkedList();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		i++;
		curElement = true;
		curValue = "";
		
		if (localName.equals("friend")) {
			mFriend = new FriendBean();
		}
		if (localName.equals("group")) {
			mGroup = new GroupBean();
			isPartOfGroup = true;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		if (localName.equals("status")) {
			this.status = curValue;
		} else if (localName.equals("message")) {
			this.message = curValue;
		} else if (localName.equals("group")) {
			mFriendsOrGroups.add(mGroup);
		} else if ((localName.equals("friend")) && !isPartOfGroup) {
			mFriendsOrGroups.add(mFriend);
		} else if ((localName.equals("friend")) && isPartOfGroup) {
			mGroup.add(mFriend);
		} else if (localName.equals("group")) {
			isPartOfGroup = false;
		} else if (localName.equals("friend_id")) {
			mFriend.setFriendId(curValue);
		} else if (localName.equals("social_username")) {
			mFriend.setFriendName(curValue);
		} else if (localName.equals("network")) {
			if (curValue.equalsIgnoreCase("memreas")) {
				mFriend.setFriendType(FriendType.MEMREAS);
			}
		} else if (localName.equals("url")) {
			mFriend.setProfileImgUrl(CommonHandler.parseMemJSON(curValue));
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

	public String getStatus() {
		return status;
	}

	@SuppressWarnings("rawtypes")
	public LinkedList getmFriendsOrGroups() {
		return mFriendsOrGroups;
	}

}
