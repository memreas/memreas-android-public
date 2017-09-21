package com.memreas.base;

import java.net.HttpCookie;

/**
 * This class is used to manage session data.
 */
public class SessionManager {

	private static SessionManager instance;
	private String session_id;
	private String user_id;
	private String user_name;
	private String user_email;
	private String user_profile_picture;
	private String user_alternal_email;
	private String user_birthday;
	private String user_plan_type;
	private String user_plan_name;
	private String device_id;
	private String device_token;
	//STS token data
	private String accessKeyId;
	private String secretAccessKey;
	private String sessionToken;
	private String expiration;
	private int duration;
	private HttpCookie cookieCloudFrontPolicy;
	private HttpCookie cookieCloudFrontSignature;
	private HttpCookie cookieCloudFrontKeyPairId;


	private SessionManager() {
	}

	/**
	 * This method returns and manges the SessionManager Instance.
	 */
	public synchronized static SessionManager getInstance() {
		if (instance == null) {
			instance = new SessionManager();
		}
		return instance;
	}

	public static void resetSessionManager() {
		// use for Logout
		instance = null;

		//getInstance();
	}

	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_email() {
		return user_email;
	}

	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}

	public String getUser_profile_picture() {
		return user_profile_picture;
	}

	public void setUser_profile_picture(String user_profile_picture) {
		this.user_profile_picture = user_profile_picture;
	}

	public String getUser_alternal_email() {
		return user_alternal_email;
	}

	public void setUser_alternal_email(String user_alternal_email) {
		this.user_alternal_email = user_alternal_email;
	}

	public String getUser_birthday() {
		return user_birthday;
	}

	public void setUser_birthday(String user_birthday) {
		this.user_birthday = user_birthday;
	}

	public String getUser_plan_type() {
		return user_plan_type;
	}

	public void setUser_plan_type(String user_plan_type) {
		this.user_plan_type = user_plan_type;
	}

	public String getUser_plan_name() {
		return user_plan_name;
	}

	public void setUser_plan_name(String user_plan_name) {
		this.user_plan_name = user_plan_name;
	}

	public String getDevice_id() {
		return device_id;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public String getDevice_token() {
		return device_token;
	}

	public void setDevice_token(String device_token) {
		this.device_token = device_token;
	}

	public String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public String getSecretAccessKey() {
		return secretAccessKey;
	}

	public void setSecretAccessKey(String secretAccessKey) {
		this.secretAccessKey = secretAccessKey;
	}

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public String getExpiration() {
		return expiration;
	}

	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public HttpCookie getCookieCloudFrontPolicy() {
		return cookieCloudFrontPolicy;
	}

	public void setCookieCloudFrontPolicy(HttpCookie cookieCloudFrontPolicy) {
		this.cookieCloudFrontPolicy = cookieCloudFrontPolicy;
	}

	public HttpCookie getCookieCloudFrontSignature() {
		return cookieCloudFrontSignature;
	}

	public void setCookieCloudFrontSignature(HttpCookie cookieCloudFrontSignature) {
		this.cookieCloudFrontSignature = cookieCloudFrontSignature;
	}

	public HttpCookie getCookieCloudFrontKeyPairId() {
		return cookieCloudFrontKeyPairId;
	}

	public void setCookieCloudFrontKeyPairId(HttpCookie cookieCloudFrontKeyPairId) {
		this.cookieCloudFrontKeyPairId = cookieCloudFrontKeyPairId;
	}

	public long getLimitedUploadSize() {
		if (getUser_plan_type().equalsIgnoreCase("FREE")) {
			return Common.MAX_VIDEO_SIZE_FREE;
		} else {
			return Common.MAX_VIDEO_SIZE_PAID;
		}
	}

} // end SessionManager