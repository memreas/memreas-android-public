
package com.memreas.sax.handler;

import android.app.Activity;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.memreas.R;
import com.memreas.base.Common;
import com.memreas.base.SessionManager;
import com.memreas.member.FriendBean;
import com.memreas.member.FriendBean.ContactType;
import com.memreas.member.FriendBean.FriendType;
import com.memreas.member.GroupBean;
import com.memreas.memreas.MemreasShareBean;
import com.memreas.memreas.ViewMemreasEventActivity;
import com.memreas.share.AddShareActivity;
import com.memreas.share.ShareFriendSelectedAdapter;
import com.memreas.share.ShareMediaSelectedAdapter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class AddFriendToShareParser extends AsyncTask<Object, String, String> {

	public static boolean isFinished;
	private ViewMemreasEventActivity viewMemreasEventActivity;
	private AddShareActivity addShareActivity;
	private Activity activity;
	private AddFriendsToEventHandler handler;
	private MemreasShareBean mMemreasShareBean;
	private String eventId;
	private String eventName;
	private HashMap<String, String> smsHashMap;
	private boolean memreasAndEmailComplete = false;
	private boolean smsComplete = false;
	private LinkedList friendList;


	public AddFriendToShareParser(ViewMemreasEventActivity activity,
			String eventId, String eventName, LinkedList list) {
		this.viewMemreasEventActivity = activity;
		this.activity = activity;
		this.eventId = eventId;
		this.eventName = eventName;
		this.friendList = list;
	}

	public AddFriendToShareParser(AddShareActivity activity) {
		this.addShareActivity = activity;
		this.activity = activity;
		this.mMemreasShareBean = addShareActivity.getmMemreasAddShareBean();
		this.eventId = mMemreasShareBean.getEventId();
		this.eventName = mMemreasShareBean.getName();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (ShareFriendSelectedAdapter.getInstance() != null) {
			ShareFriendSelectedAdapter.getInstance().refreshFriendSelectedList();
			friendList = ShareFriendSelectedAdapter.getInstance().getmFriendOrGroup();
			isFinished = false;
		} else {
			Toast.makeText(activity, "empty friend list", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	protected String doInBackground(Object... params) {

		String xmlData = XMLGenerator.addFriendToEventStart(eventId);
		String xmlEmails = "";
		String xmlFriends = "";
		smsHashMap = new HashMap<String, String>();

		/**
		 * Loop and fetch email, friends, and sms lists
 		 */
		Iterator iterator = friendList.iterator();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (obj instanceof FriendBean) {
				FriendBean friend = (FriendBean) obj;
				/*
				 * Email and SMS section
				 */
				if (friend.getFriendType() == FriendType.CONTACT) {
					LinkedList<FriendBean.LocalContact> contactList = friend
							.getContactList();
					Iterator<FriendBean.LocalContact> iterator_contacts = contactList
							.iterator();
					while (iterator_contacts.hasNext()) {
						FriendBean.LocalContact contact = iterator_contacts
								.next();
						if ((contact.getType() == ContactType.EMAIL)
								&& (contact.isSelected())) {
							xmlEmails += XMLGenerator.addEmailXML(contact
									.getContact());
						} else if ((contact.getType() == ContactType.SMS)
								&& (contact.isSelected())) {
							smsHashMap.put(friend.getFriendName(),
									contact.getContact());
						}
					}
				} else if ((friend.getFriendType() == FriendType.MEMREAS)
						&& (friend.isSelected())) {
					/*
					 * memreas section
					 */
					xmlFriends += XMLGenerator.addFriendsXML(
							friend.getFriendId(), friend.getFriendName(),
							FriendType.MEMREAS.toString().toLowerCase(),
							friend.getProfileImgUrl());
				}
			} else if (obj instanceof GroupBean) {
				// handle group...
			}
		} // end while

		/**
		 * Add memreas friends via web service
		 */
		xmlData += xmlEmails;
		xmlData += "<friends>" + xmlFriends + "</friends>";
		xmlData += "</addfriendtoevent></xml>";
		Log.d("AddFriendToEvent xml -->", xmlData);

		String msg = "";
		try {
			handler = new AddFriendsToEventHandler();
			SaxParser.parse(Common.SERVER_URL
					+ Common.ADD_FRIENDS_TO_EVENT_ACTION, xmlData, handler,
					"xml");

			publishProgress(msg);
			memreasAndEmailComplete = true;
		} catch (Exception e) {
			e.printStackTrace();
			memreasAndEmailComplete = false;
		}

		/**
		 * SMS Section - fetch from hash map genereated in while loop above
		 */
		try {
			String message = activity.getString(
					R.string.add_memreas_sms_content,
					SessionManager.getInstance().getUser_name(),
					eventName, Common.FE_SMS_URL + "action=mobile&event_id=" + eventId);
			Iterator<String> iterator_sms = smsHashMap.keySet().iterator();
			SmsManager smsManager = SmsManager.getDefault();
			while (iterator_sms.hasNext()) {
				String contactName = iterator_sms.next();
				String phoneNumber = smsHashMap.get(contactName);
                Log.w("MESSAGE LENGTH--->", String.valueOf(message.length()));
				smsManager.sendTextMessage(phoneNumber, null, message, null,
						null);
				msg = "memreas sms invite sent to: " + contactName + " @"
						+ phoneNumber;
				publishProgress(msg);
			}
			smsComplete = true;
		} catch (Exception e) {
			e.printStackTrace();
			smsComplete = false;
		}

		/**
		 * Clear friend lists
		 */
        if (ShareFriendSelectedAdapter
                .getInstance() != null) {
            ShareFriendSelectedAdapter
                    .getInstance().clearInstanceFriendsList();
        }


		if (memreasAndEmailComplete && smsComplete) {
			return "success";
		}
		return "failure";
	}

	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		/*
		if (addShareActivity != null) {
			addShareActivity.showProgress(values[0]);
		} else if (viewMemreasEventActivity != null) {
			viewMemreasEventActivity.showProgress(values[0]);
		}
		*/
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		/*
		if (!addShareActivity.isDestroyed()) {
			if (addShareActivity != null) {
				addShareActivity.dismiss();
			} else if (viewMemreasEventActivity != null) {
				viewMemreasEventActivity.dismiss();
			}
		}
		*/
		if (viewMemreasEventActivity != null) {
			viewMemreasEventActivity.fetchFriendSelectedList(true);
		}

	}

	/*
	 * Handler class for parsing return data
	 */
	private class AddFriendsToEventHandler extends DefaultHandler {
		Boolean curElement;
		String curValue;
		int i = 0;
		private String status;
		private String message;
		private String eventId;

		public AddFriendsToEventHandler() {
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
	} // end private class AddFriendsToEventHandler

} // end async class
