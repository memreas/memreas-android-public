
package com.memreas.memreas;

import java.util.LinkedList;

import org.json.JSONArray;

public class MemreasEventPublicBean extends MemreasEventBean {

	private String event_id;
	private String event_name;
	private String event_creator;
	private String event_creator_user_id;
	private String event_location;
	private String event_date;
	private String event_metadata;
	private String event_viewable_from;
	private String event_viewable_to;
	private int event_like_total;
	private int event_comment_total;
	private String profile_pic;
	private JSONArray profile_pic_79x80;
	private JSONArray profile_pic_448x306;
	private JSONArray profile_pic_98x78;
	private LinkedList<FriendShortDetails> publicEventFriendsList;
	public LinkedList<CommentShortDetails> publicEventCommentsList;
	private LinkedList<MediaShortDetails> publicEventMediaList;
	
	public String getEvent_id() {
		return event_id;
	}

	public void setEvent_id(String event_id) {
		this.event_id = event_id;
	}
	
	public String getEvent_name() {
		return event_name;
	}

	public void setEvent_name(String event_name) {
		this.event_name = event_name;
	}
	
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

	public String getEvent_location() {
		return event_location;
	}

	public void setEvent_location(String event_location) {
		this.event_location = event_location;
	}

	public String getEvent_date() {
		return event_date;
	}

	public void setEvent_date(String event_date) {
		this.event_date = event_date;
	}

	public String getEvent_metadata() {
		return event_metadata;
	}

	public void setEvent_metadata(String event_metadata) {
		this.event_metadata = event_metadata;
	}

	public String getEvent_viewable_from() {
		return event_viewable_from;
	}

	public void setEvent_viewable_from(String event_viewable_from) {
		this.event_viewable_from = event_viewable_from;
	}

	public String getEvent_viewable_to() {
		return event_viewable_to;
	}

	public void setEvent_viewable_to(String event_viewable_to) {
		this.event_viewable_to = event_viewable_to;
	}

	public int getEvent_like_total() {
		return event_like_total;
	}

	public void setEvent_like_total(int event_like_total) {
		this.event_like_total = event_like_total;
	}

	public int getEvent_comment_total() {
		return event_comment_total;
	}

	public void setEvent_comment_total(int event_comment_total) {
		this.event_comment_total = event_comment_total;
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

	public LinkedList<FriendShortDetails> getPublicEventFriendsList() {
		return publicEventFriendsList;
	}

	public void setPublicEventFriendsList(
			LinkedList<FriendShortDetails> publicEventFriendsList) {
		this.publicEventFriendsList = publicEventFriendsList;
	}

	public LinkedList<CommentShortDetails> getPublicEventCommentsList() {
		return publicEventCommentsList;
	}

	public void setPublicEventCommentsList(
			LinkedList<CommentShortDetails> publicEventCommentsList) {
		this.publicEventCommentsList = publicEventCommentsList;
	}

	public LinkedList<MediaShortDetails> getPublicEventMediaList() {
		return publicEventMediaList;
	}

	public void setPublicEventMediaList(
			LinkedList<MediaShortDetails> publicEventMediaList) {
		this.publicEventMediaList = publicEventMediaList;
	}

}
