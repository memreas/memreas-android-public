
package com.memreas.search;

import java.util.HashMap;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;

public class TagComment {
	String tag_name;
	String event_name;
	String event_id;
	String comment;
	String update_time;
	String commenter_photo;
	String commenter_name;
	JSONArray event_media_url;
	String updated_on;
	public String getTag_name() {
		return tag_name;
	}
	public void setTag_name(String tag_name) {
		this.tag_name = tag_name;
	}
	public String getEvent_name() {
		return '!'+event_name;
	}
	public void setEvent_name(String event_name) {
		this.event_name = event_name;
	}
	public String getEvent_id() {
		return event_id;
	}
	public void setEvent_id(String event_id) {
		this.event_id = event_id;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public String getCommenter_photo() {
		return commenter_photo;
	}
	public void setCommenter_photo(String commenter_photo) {
		this.commenter_photo = commenter_photo;
	}
	public String getCommenter_name() {
		return '@'+commenter_name;
	}
	public void setCommenter_name(String commenter_name) {
		this.commenter_name = commenter_name;
	}
	public HashMap<String, String> getEvent_media_url() {
		int random_index_media = 0;
		String type, url;
		HashMap<String, String> map = new HashMap<String, String>();
		JSONArray jsonArr;
		if (event_media_url != null) {
			try {
				for (int i=0; i<event_media_url.length(); i++) {
					type = "image";
					random_index_media = 0;
					jsonArr = event_media_url.optJSONArray(i);
					
					if ((jsonArr != null) && (jsonArr.length() > 1)) {
						type = "video";
						random_index_media = new Random().nextInt(jsonArr.length());
						url = jsonArr.getString(random_index_media);
					} else {
						url = event_media_url.getString(random_index_media);
					}
					map.put(url, type);
				}
			} catch (JSONException e) {
				// do nothing
			}
		}
		return map;
	}
	public void setEvent_media_url(JSONArray event_media_url) {
		this.event_media_url = event_media_url;
	}
	public String getUpdated_on() {
		return updated_on;
	}
	public void setUpdated_on(String updated_on) {
		this.updated_on = updated_on;
	}
	
} //end class
