package com.memreas.memreas;

import com.memreas.base.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MemreasEventBean {

	public enum EventType {
		ME, FRIENDS, PUBLIC
	};

	public MediaShortDetails newMediaShortDetails() {
		return new MediaShortDetails();
	}

	public class MediaShortDetails {
		public MediaShortDetails() {
			location = new Location();
		}
		public String media_type;
		public String media_url;
		public String media_url_web;
		public String media_url_webm;
		public String media_url_hls;
		public String media_name;
		public String media_id;
		public JSONArray media_video_thum;
		public JSONArray media_79x80;
		public JSONArray media_98x78;
		public JSONArray media_448x306;
		public JSONArray event_media_inappropriate;
		public String s3file_media_url_path;
		public String s3file_media_url_web_path;
		public String s3file_media_url_download_path;
		public Location location;
		
		public class Location {
			public String address;
			public double latititude;
			public double longitude;
		}
		
		public boolean isApproriate(String eventId) {
			String userId = SessionManager.getInstance().getUser_id();
			if (event_media_inappropriate != null) {
				try {
					//JSONArray event = event_media_inappropriate.getJSONArray(0);
					JSONObject jsonEvent = (JSONObject) event_media_inappropriate.get(0);
					JSONObject jsonEventId = (JSONObject) jsonEvent.get("event:"+eventId);
					JSONObject jsonUserId = (JSONObject) jsonEventId.get("user:"+userId);
					return false;
				} catch (JSONException e) {
					e.printStackTrace();
					return true;
				}
			}
			return true;
		}
	}

	public FriendShortDetails newFriendShortDetails() {
		return new FriendShortDetails();
	}

	public class FriendShortDetails {
		public String friend_id;
		public String friend_social_username;
		public String friend_url_image;
	}

	public CommentShortDetails newCommentShortDetails() {
		return new CommentShortDetails();
	}

	public class CommentShortDetails {
		public String event_id;
		public String media_id;
		public String text;
		public String type;
		public String audio_media_url;
		public String username;
		public String profilePicUrl;
		public String commented_about;
	}
}
