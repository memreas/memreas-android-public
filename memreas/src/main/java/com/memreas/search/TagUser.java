
package com.memreas.search;

public class TagUser {
	private String username;
	private String user_id;
	private String profile_photo;
	private String friend_request_sent;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getProfile_photo() {
		return profile_photo;
	}
	public void setProfile_photo(String profile_photo) {
		this.profile_photo = profile_photo;
	}
	public String getFriend_request_sent() {
		return friend_request_sent;
	}
	public void setFriend_request_sent(String friend_request_sent) {
		this.friend_request_sent = friend_request_sent;
	}
}
