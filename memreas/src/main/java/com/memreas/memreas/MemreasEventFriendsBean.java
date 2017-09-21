
package com.memreas.memreas;

import java.util.LinkedList;

import org.json.JSONArray;

public class MemreasEventFriendsBean  extends MemreasEventBean {

	private String event_creator;
	private String event_creator_user_id;
	private String profile_pic;
	private JSONArray profile_pic_79x80;
	private JSONArray profile_pic_448x306;
	private JSONArray profile_pic_98x78;
	private LinkedList<FriendEventDetails> friendEventDetailsList;
	
	public String getEvent_creator() {
		return event_creator;
	}

	public void setEvent_creator(String event_creator) {
		this.event_creator = event_creator;
	}

	public String getEvent_creator_user_id() {
		return event_creator_user_id;
	}

	public void setEvent_creator_user_id(String event_creator_user_id) {
		this.event_creator_user_id = event_creator_user_id;
	}

	public String getProfile_pic() {
		return profile_pic;
	}

	public void setProfile_pic(String profile_pic) {
		this.profile_pic = profile_pic;
	}

	public JSONArray getProfile_pic_79x80() {
		return profile_pic_79x80;
	}

	public void setProfile_pic_79x80(JSONArray profile_pic_79x80) {
		this.profile_pic_79x80 = profile_pic_79x80;
	}

	public JSONArray getProfile_pic_448x306() {
		return profile_pic_448x306;
	}

	public void setProfile_pic_448x306(JSONArray profile_pic_448x306) {
		this.profile_pic_448x306 = profile_pic_448x306;
	}

	public JSONArray getProfile_pic_98x78() {
		return profile_pic_98x78;
	}

	public void setProfile_pic_98x78(JSONArray profile_pic_98x78) {
		this.profile_pic_98x78 = profile_pic_98x78;
	}

	public LinkedList<FriendEventDetails> getFriendEventDetailsList() {
		return friendEventDetailsList;
	}

	public void setFriendEventDetailsList(
			LinkedList<FriendEventDetails> friendEventDetailsList) {
		this.friendEventDetailsList = friendEventDetailsList;
	}

	public FriendEventDetails newFriendEventDetails() {
		return new FriendEventDetails();
	}

	public class FriendEventDetails {
		public String eventId;
		public String event_name;
		public String event_metadata;
		public boolean friendsCanPost;
		public boolean friendsCanAddFriends;
		public LinkedList<MediaShortDetails> mediaShortDetailsList;
		public LinkedList<FriendShortDetails> eventFriendShortDetailsList;
		public LinkedList<CommentShortDetails> commentShortDetailsList;
	}
}
