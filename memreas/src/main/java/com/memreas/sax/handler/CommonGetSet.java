
package com.memreas.sax.handler;

import java.util.ArrayList;
import java.util.List;

/**
 * Getter setter for server response
 * 
 * @author memreas llc
 * 
 */
public class CommonGetSet {

	private String memses_id;
	private String status;
	private String email;
	private String profile;
	private String message;
	private int noofimage;
	private String user_id;
	private List<String> name = new ArrayList<String>();
	private String event_id;
	private String event_name;
	private String isExist;
//	private String group_id;
	private int totle_like_on_media;
	private int totle_comment_on_media;
	private String last_comment;
	private String last_audio;
	private String audio_url;
//	private String last_audiotext_comment;
	
	private List<String> media_id = new ArrayList<String>();
	private List<Boolean> is_downloaded = new ArrayList<Boolean>();
	private List<String> main_media_url = new ArrayList<String>();
	private List<String> media_url_web = new ArrayList<String>();
	private List<String> media_url_webm = new ArrayList<String>();
	private List<String> media_url_hls = new ArrayList<String>();
	private List<String> event_media_video_thum = new ArrayList<String>();
	
	private List<String> type = new ArrayList<String>();
	private List<String> media_name = new ArrayList<String>();
	
	private List<String> friend_id = new ArrayList<String>();
	private List<String> network = new ArrayList<String>();
	private List<String> social_username = new ArrayList<String>();
	private List<String> media_url = new ArrayList<String>();
	private List<String> media_url_79x80 = new ArrayList<String>();
	private List<String> media_url_98x78 = new ArrayList<String>();
	private List<String> media_url_448x306 = new ArrayList<String>();
	private List<String> media_url_1280x720 = new ArrayList<String>();
	
	private List<String> list_group_id = new ArrayList<String>();
	private List<String> list_group_name = new ArrayList<String>();
	
	private List<Double> list_location_latitude = new ArrayList<Double>();
	private List<Double> list_location_longitude = new ArrayList<Double>();	
	
//	public String getLast_audiotext_comment() {
//		return last_audiotext_comment;
//	}
//
//	public void setLast_audiotext_comment(String last_audiotext_comment) {
//		this.last_audiotext_comment = last_audiotext_comment;
//	}

	public String getMemses_id() {
		return memses_id;
	}

	public void setMemses_id(String memses_id) {
		this.memses_id = memses_id;
	}
	public List<String> getList_group_id() {
		return list_group_id;
	}

	public void setList_group_id(String list_group_id) {
		this.list_group_id.add(list_group_id);
	}

	public List<String> getList_group_name() {
		return list_group_name;
	}

	public void setList_group_name(String list_group_name) {
		this.list_group_name.add(list_group_name);
	}

	public int getTotle_like_on_media() {
		return totle_like_on_media;
	}

	public List<String> getEvent_media_video_thum() {
		return event_media_video_thum;
	}

	public void setEvent_media_video_thum(String event_media_video_thum) {
		this.event_media_video_thum.add(event_media_video_thum);
	}

	/**
	 * @return the audio_url
	 */
	public String getAudio_url() {
		return audio_url;
	}

	/**
	 * @param audio_url the audio_url to set
	 */
	public void setAudio_url(String audio_url) {
		this.audio_url = audio_url;
	}

	/**
	 * @return the main_media_url
	 */
	public List<String> getMain_media_url() {
		return main_media_url;
	}
	
	public List<String> getMedia_url_web() {
		return media_url_web;
	}
	
	public void setMedia_url_web(String media_url_web) {
		this.media_url_web.add(media_url_web);
	}

	public List<String> getMedia_url_webm() {
		return media_url_webm;
	}

	public void setMedia_url_webm(String media_url_webm) {
		this.media_url_webm.add(media_url_webm);
	}

	public List<String> getMedia_url_hls() {
		return media_url_hls;
	}

	public void setMedia_url_hls(String media_url_hls) {
		this.media_url_web.add(media_url_hls);
	}
	
	
	/**
	 * @param main_media_url the main_media_url to set
	 */
	public void setMain_media_url(String main_media_url) {
		this.main_media_url.add(main_media_url);
	}

	/**
	 * @return the media_url_98x78
	 */
	public List<String> getMedia_url_98x78() {
		return media_url_98x78;
	}

	/**
	 * @param media_url_79x80 the media_url_79x80 to set
	 */
	public List<String> getMedia_url_79x80() {
		return media_url_79x80;
	}

	public void setMedia_url_79x80(String media_url_79x80) {
		this.media_url_79x80.add(media_url_79x80);
	}

	/**
	 * @param media_url_98x78 the media_url_98x78 to set
	 */
	public void setMedia_url_98x78(String media_url_98x78) {
		this.media_url_98x78.add(media_url_98x78);
	}

	/**
	 * @return the media_url_448x306
	 */
	public List<String> getMedia_url_448x306() {
		return media_url_448x306;
	}

	/**
	 * @param media_url_448x306 the media_url_448x306 to set
	 */
	public void setMedia_url_448x306(String media_url_448x306) {
		this.media_url_448x306.add(media_url_448x306);
	}

	/**
	 * @return the media_url_1280x720
	 */
	public List<String> getMedia_url_1280x720() {
		return media_url_1280x720;
	}

	/**
	 * @param media_url_1280x720 the media_url_1280x720 to set
	 */
	public void setMedia_url_1280x720(String media_url_1280x720) {
		this.media_url_1280x720.add(media_url_1280x720);
	}

	public void setTotle_like_on_media(int totle_like_on_media) {
		this.totle_like_on_media = totle_like_on_media;
	}

	public int getTotle_comment_on_media() {
		return totle_comment_on_media;
	}

	public void setTotle_comment_on_media(int totle_comment_on_media) {
		this.totle_comment_on_media = totle_comment_on_media;
	}

	public String getLast_comment() {
		return last_comment;
	}

	public void setLast_comment(String last_comment) {
		this.last_comment = last_comment;
	}

	public String getLast_audio() {
		return last_audio;
	}

	public void setLast_audio(String last_audio) {
		this.last_audio = last_audio;
	}
	
	
	/**
	 * @return the group_id
	 */
/*	public String getGroup_id() {
		return group_id;
	}
*/	
	/**
	 * @return the friend_id
	 */
	public List<String> getFriend_id() {
		return friend_id;
	}

	/**
	 * @param friend_id the friend_id to set
	 */
	public void setFriend_id(String friend_id) {
		this.friend_id.add(friend_id);
	}

	/**
	 * @return the network
	 */
	public List<String> getNetwork() {
		return network;
	}

	/**
	 * @param network the network to set
	 */
	public void setNetwork(String network) {
		this.network.add(network);
	}

	/**
	 * @return the social_username
	 */
	public List<String> getSocial_username() {
		return social_username;
	}

	/**
	 * @param social_username the social_username to set
	 */
	public void setSocial_username(String social_username) {
		this.social_username.add(social_username);
	}

	/**
	 * @return the media_url
	 */
	public List<String> getMedia_url() {
		return media_url;
	}

	public void setMedia_url(String media_url) {
		this.media_url.add(media_url);
	}


	/**
	 * @param group_id the group_id to set
	 */
/*	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
*/	

	/**
	 * @return the media_id
	 */
	public List<String> getMedia_id() {
		return media_id;
	}
	
//	public List<String> getMedia_type() {
//		return media_type;
//	}
//	
	/**
	 * @return the isExist
	 */
	public String getIsExist() {
		return isExist;
	}

	/**
	 * @param isExist the isExist to set
	 */
	public void setIsExist(String isExist) {
		this.isExist = isExist;
	}

	/**
	 * @param media_id the media_id to set
	 */
	public void setMedia_id(String media_id) {
		this.media_id.add(media_id);
	}
	
//	public void setMedia_type(String media_type) {
//		this.media_type.add(media_type);
//	}

	/**
	 * @return the is_downloaded
	 */
	public List<Boolean> getIs_downloaded() {
		return is_downloaded;
	}

	/**
	 * @param is_downloaded the is_downloaded to set
	 */
	public void setIs_downloaded(Boolean is_downloaded) {
		this.is_downloaded.add(is_downloaded);
	}
	/**
	 * @return the type
	 */
	public List<String> getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type.add(type);
	}

	/**
	 * @return the media_name
	 */
	public List<String> getMedia_name() {
		return media_name;
	}

	/**
	 * @param media_name the media_name to set
	 */
	public void setMedia_name(String media_name) {
		this.media_name.add(media_name);
	}


	/**
	 * @return the event_name
	 */
	public String getEvent_name() {
		return event_name;
	}

	/**
	 * @param event_name the event_name to set
	 */
	public void setEvent_name(String event_name) {
		this.event_name = event_name;
	}

	/**
	 * @return the event_id
	 */
	public String getEvent_id() {
		return event_id;
	}

	/**
	 * @param event_id the event_id to set
	 */
	public void setEvent_id(String event_id) {
		this.event_id = event_id;
	}
	/**
	 * @return the noofimage
	 */
	public int getNoofimage() {
		return noofimage;
	}

	/**
	 * @param noofimage
	 *            the noofimage to set
	 */
	public void setNoofimage(int noofimage) {
		this.noofimage = noofimage;
	}

	/**
	 * @return the name
	 */
	public List<String> getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String namelist) {
		this.name.add(namelist);
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the user_id
	 */
	public String getUser_id() {
		return user_id;
	}

	/**
	 * @param user_id
	 *            the user_id to set
	 */
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	
	public List<Double> getLocation_Latitude() {
		return list_location_latitude;
	}

	public void setLocation_Latitude(double latitude) {
		this.list_location_latitude .add(latitude);
	}
	
	public List<Double> getLocation_Longitude() {
		return list_location_longitude;
	}

	public void setLocation_Longitude(double Longitude) {
		this.list_location_longitude .add(Longitude);
	}

	public void setUserEmailId(String curValue) {
		// TODO Auto-generated method stub
		this.email = curValue;
	}

	public void setUserProfile(String curValue) {
		// TODO Auto-generated method stub
		this.profile = curValue;
	}

	public String getUserEmail() {
		// TODO Auto-generated method stub
		return email;
	}

	public String getUserProfile() {
		// TODO Auto-generated method stub
		return profile;
	}
}
