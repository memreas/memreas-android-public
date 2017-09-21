
package com.memreas.member;

import java.util.LinkedList;

public class FriendBean {
	public enum FriendType {
		MEMREAS, FACEBOOK, TWITTER, CONTACT
	};

	public enum ContactType {
		EMAIL, SMS
	};

	String friendId;
	String friendName;
	FriendType friendType;
	String profileImgUrl;
	boolean selected = false;
	LinkedList<LocalContact> contactList;

	public String getFriendId() {
		return friendId;
	}

	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}

	public String getFriendName() {
		return friendName;
	}

	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}

	public FriendType getFriendType() {
		return friendType;
	}

	public void setFriendType(FriendType friendType) {
		this.friendType = friendType;
	}

	public String getProfileImgUrl() {
		return profileImgUrl;
	}

	public void setProfileImgUrl(String profileImgUrl) {
		this.profileImgUrl = profileImgUrl;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void addContact(ContactType type, String contact) {
		if (contactList == null) {
			contactList = new LinkedList<LocalContact>();
		}
		contactList.add(new LocalContact(type, contact));
	}

	public LinkedList<LocalContact> getContactList() {
		return contactList;
	}

	public class LocalContact {
		private ContactType type;
		private String contact;
		private boolean selected = false;

		public LocalContact(ContactType type, String contact) {
			super();
			this.type = type;
			this.contact = contact;
		}

		public ContactType getType() {
			return type;
		}

		public void setType(ContactType type) {
			this.type = type;
		}

		public String getContact() {
			return contact;
		}

		public void setContact(String contact) {
			this.contact = contact;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}
	}
}
