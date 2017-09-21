package com.memreas.memreas;

import java.util.LinkedList;

import com.google.android.gms.maps.model.LatLng;

public class MemreasEventMeBean extends MemreasEventBean {
	
	private String eventId;
	private String name;
	private String date;
	private String location;
	private LatLng latLng;
	private boolean friendsCanPost;
	private boolean friendsCanAddFriends;
	private boolean publicShare;
	private boolean viewable;
	private String viewableFrom;
	private String viewableTo;
	private boolean ghost;
	private String ghostEndDate;
	private String metadata;
	
	private int likeCount;
	private int commentCount;
	
	private LinkedList<FriendShortDetails> friendList;
	private LinkedList<MediaShortDetails> mediaList;
	private LinkedList<CommentShortDetails> commentList;
	

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public LatLng getLatLng() {
		return latLng;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	public boolean isFriendsCanPost() {
		return friendsCanPost;
	}

	public void setFriendsCanPost(boolean friendsCanPost) {
		this.friendsCanPost = friendsCanPost;
	}

	public boolean isFriendsCanAddFriends() {
		return friendsCanAddFriends;
	}

	public void setFriendsCanAddFriends(boolean friendsCanAddFriends) {
		this.friendsCanAddFriends = friendsCanAddFriends;
	}

	public boolean isPublicShare() {
		return publicShare;
	}

	public void setPublicShare(boolean publicShare) {
		this.publicShare = publicShare;
	}

	public boolean isViewable() {
		return viewable;
	}

	public void setViewable(boolean viewable) {
		this.viewable = viewable;
	}

	public String getViewableFrom() {
		return viewableFrom;
	}

	public void setViewableFrom(String viewableFrom) {
		this.viewableFrom = viewableFrom;
	}

	public String getViewableTo() {
		return viewableTo;
	}

	public void setViewableTo(String viewableTo) {
		this.viewableTo = viewableTo;
	}

	public boolean isGhost() {
		return ghost;
	}

	public void setGhost(boolean ghost) {
		this.ghost = ghost;
	}

	public String getGhostEndDate() {
		return ghostEndDate;
	}

	public void setGhostEndDate(String ghostEndDate) {
		this.ghostEndDate = ghostEndDate;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public LinkedList<FriendShortDetails> getFriendList() {
		return friendList;
	}

	public void setFriendList(LinkedList<FriendShortDetails> friendList) {
		this.friendList = friendList;
	}

	public LinkedList<MediaShortDetails> getMediaList() {
		return mediaList;
	}

	public void setMediaList(LinkedList<MediaShortDetails> mediaList) {
		this.mediaList = mediaList;
	}

	public LinkedList<CommentShortDetails> getCommentList() {
		return commentList;
	}

	public void setCommentList(LinkedList<CommentShortDetails> commentList) {
		this.commentList = commentList;
	}

}
