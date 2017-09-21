
package com.memreas.search;

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;

public class TagEvent {
	String name;
	String event_id;
	String type;
	String location;
	String user_id;
	String event_creator_name;
	String event_creator_pic;
	JSONArray event_media_url;
	boolean isAdded = false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEvent_id() {
		return event_id;
	}

	public void setEvent_id(String event_id) {
		this.event_id = event_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getEvent_creator_name() {
		return event_creator_name;
	}

	public void setEvent_creator_name(String event_creator_name) {
		this.event_creator_name = event_creator_name;
	}

	public String getEvent_creator_pic() {
		return event_creator_pic;
	}

	public void setEvent_creator_pic(String event_creator_pic) {
		this.event_creator_pic = event_creator_pic;
	}

	public String getEvent_media_url() {
		int random_index = 0;
		String url = "";
		type = "image";
		if (event_media_url != null) {
			try {
				random_index = new Random().nextInt(event_media_url.length());
				url = event_media_url.getString(random_index);
				if (event_media_url.length() > 1) {
					type = "video";
				}
			} catch (JSONException e) {
				// do nothing
			}
		}
		return url;
	}

	public void setEvent_media_url(JSONArray event_media_url) {
		this.event_media_url = event_media_url;
	}

	public boolean isAdded() {
		return isAdded;
	}

	public void setAdded(boolean isAdded) {
		this.isAdded = isAdded;
	}

}
