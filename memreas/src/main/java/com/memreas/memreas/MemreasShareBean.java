package com.memreas.memreas;

import java.util.HashMap;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.memreas.share.ShareFriendSelectedAdapter;
import com.memreas.share.ShareMediaSelectedAdapter;

public class MemreasShareBean {

	//Details
	private String eventId;
	private String name;
	private String date;
	private String location;
	private LatLng latLng;
	private boolean friendsCanPost = true;
	private boolean friendsCanAddFriends = true;
	private boolean publicShare;
	private boolean viewable;
	private String viewableFrom;
	private String viewableTo;
	private boolean ghost;
	private String ghostEndDate;

	//Media
	private ShareMediaSelectedAdapter mediaSelectedAdapter;
	private String textComment;
	private String audioCommentFilePath;
	
	//Friends
	private ShareFriendSelectedAdapter friendsSelectedAdapter;
	private boolean groupCheckBoxSelected=false;
	private String groupName;


	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	//Details Getters and Setters 
	public String getEventId() {
		return eventId;
	}

	public String getName() {
		return name;
	}

	public String getDate() {
		return date;
	}

	public String getLocation() {
		return location;
	}

	public LatLng getLatLng() {
		return latLng;
	}

	public boolean isFriendsCanPost() {
		return friendsCanPost;
	}

	public boolean isFriendsCanAddFriends() {
		return friendsCanAddFriends;
	}

	public boolean isPublicShare() {
		return publicShare;
	}

	public boolean isViewable() {
		return viewable;
	}

	public String getViewableFrom() {
		return viewableFrom;
	}

	public String getViewableTo() {
		return viewableTo;
	}

	public boolean isGhost() {
		return ghost;
	}

	public String getGhostEndDate() {
		return ghostEndDate;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	public void setFriendsCanPost(boolean friendsCanPost) {
		this.friendsCanPost = friendsCanPost;
	}

	public void setFriendsCanAddFriends(boolean friendsCanAddFriends) {
		this.friendsCanAddFriends = friendsCanAddFriends;
	}

	public void setPublicShare(boolean publicShare) {
		this.publicShare = publicShare;
	}

	public void setViewable(boolean viewable) {
		this.viewable = viewable;
	}

	public void setViewableFrom(String viewableFrom) {
		this.viewableFrom = viewableFrom;
	}

	public void setViewableTo(String viewableTo) {
		this.viewableTo = viewableTo;
	}

	public void setGhost(boolean ghost) {
		this.ghost = ghost;
	}

	public void setGhostEndDate(String ghostEndDate) {
		this.ghostEndDate = ghostEndDate;
	}


	//Media 
	public ShareMediaSelectedAdapter getMediaSelectedAdapter() {
		return mediaSelectedAdapter;
	}

	public void setMediaSelectedAdapter(
			ShareMediaSelectedAdapter mediaSelectedAdapter) {
		this.mediaSelectedAdapter = mediaSelectedAdapter;
	}

	public String getTextComment() {
		return textComment;
	}

	public void setTextComment(String textComment) {
		this.textComment = textComment;
	}

	public String getAudioCommentFilePath() {
		return audioCommentFilePath;
	}

	public void setAudioCommentFilePath(String audioCommentFilePath) {
		this.audioCommentFilePath = audioCommentFilePath;
	}
	
	//Friends 
	public ShareFriendSelectedAdapter getFriendsSelectedAdapter() {
		return friendsSelectedAdapter;
	}

	public void setFriendsSelectedAdapter(
			ShareFriendSelectedAdapter friendsSelectedAdapter) {
		this.friendsSelectedAdapter = friendsSelectedAdapter;
	}

	public boolean isGroupCheckBoxSelected() {
		return groupCheckBoxSelected;
	}

	public void setGroupCheckBoxSelected(boolean groupCheckBoxSelected) {
		this.groupCheckBoxSelected = groupCheckBoxSelected;
	}

	public void setDetails(HashMap<String, Object> detailsHashMap) {

		// Fetch from Hashmap all variables except lists...
		EditText editTxtName = (EditText) detailsHashMap.get("editTxtName");
		TextView txtInputDate = (TextView) detailsHashMap.get("txtInputDate");
		String mAddress = (String) detailsHashMap.get("mAddress");
		LatLng mLatLng = (LatLng) detailsHashMap.get("mLatLng");
		ImageButton cbFriendsCanPost = (ImageButton) detailsHashMap
				.get("cbFriendsCanPost");
		ImageButton cbFriendsCanAddFriends = (ImageButton) detailsHashMap
				.get("cbFriendsCanAddFriends");
		ImageButton cbPublic = (ImageButton) detailsHashMap.get("cbPublic");
		ImageButton cbViewable = (ImageButton) detailsHashMap.get("cbViewable");
		TextView editTextViewableFrom = (TextView) detailsHashMap
				.get("editTextViewableFrom");
		TextView editTextViewableTo = (TextView) detailsHashMap
				.get("editTextViewableTo");
		ImageButton cbGhost = (ImageButton) detailsHashMap.get("cbGhost");
		TextView editTextGhost = (TextView) detailsHashMap.get("editTextGhost");

		if ((editTxtName != null) && (editTxtName.getText() != null)) {
			this.setName(editTxtName.getText().toString());
		}
		if ((txtInputDate != null) && (txtInputDate.getText() != null)) {
			this.setDate(txtInputDate.getText().toString());
		}
		if (mAddress != null) {
			this.setLocation(mAddress);
		}
		if (mLatLng != null) {
			this.setLatLng(mLatLng);
		}
		this.setFriendsCanPost(cbSelected(cbFriendsCanPost));
		this.setFriendsCanAddFriends(cbSelected(cbFriendsCanAddFriends));
		this.setPublicShare(cbSelected(cbPublic));
		this.setViewable(cbSelected(cbViewable));
		if ((editTextViewableFrom != null) && (editTextViewableFrom.getText() != null)) {
			this.setViewableFrom(editTextViewableFrom.getText().toString());
		}
		if ((editTextViewableTo != null) && (editTextViewableTo.getText() != null)) {
			this.setViewableTo(editTextViewableTo.getText().toString());
		}
		this.setGhost(cbSelected(cbGhost));
		if ((editTextGhost != null) && (editTextGhost.getText() != null)) {
			this.setGhostEndDate(editTextGhost.getText().toString());
		}

	}
	
	private boolean cbSelected(ImageButton cb) {
		if ((cb!=null) && (cb.getTag().toString().equalsIgnoreCase("selected"))) {
			return true;
		} 
		return false;
	}

}
