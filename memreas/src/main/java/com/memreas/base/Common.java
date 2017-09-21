package com.memreas.base;

import android.graphics.Color;

import com.memreas.BuildConfig;

import java.util.Locale;

public class Common {
	public static final String PACKAGE_NAME = "com.memreas";
	public static final String SIGNATURE = "YOUR_SIGNATURE";

	// ============ For Amazon Services - gradle covers dev and prod ===========

	public static final String ENV = BuildConfig.ENV;
    public static final String SERVER_URL = BuildConfig.SERVER_URL;
    public static final String FE_SMS_URL = BuildConfig.FE_SMS_URL;
    public static final String BUCKET_NAME = BuildConfig.BUCKET_NAME;

    public static final String BUCKET_NAME_CRASH_LOGS = BuildConfig.BUCKET_NAME_CRASH_LOGS;
    public static final String AWS_ACCOUNT_ID = BuildConfig.AWS_ACCOUNT_ID;
    public static final String COGNITO_POOL_ID = BuildConfig.COGNITO_POOL_ID;
    public static final String COGNITO_ROLE_UNAUTH = BuildConfig.COGNITO_ROLE_UNAUTH;
    public static final String COGNITO_ROLE_AUTH = BuildConfig.COGNITO_ROLE_AUTH;

	// ============================================

	public static final long MULTIPART_UPLOAD_SIZE = 5 * 1024 * 1024; // 5MB
	public static final long MULTIPART_UPLOAD_THRESHOLD = 5 * 1024 * 1024; // 5MB

	public static String getPictureBucket() {
		return (BUCKET_NAME).toLowerCase(Locale.US);
	}

	public static final int CONCURRENT_UPLOADS = 3;
	public static final int CONCURRENT_DOWNLOADS = 3;
	public static final int CONCURRENT_TRANSFERS = 3;

	// ====== same for all...

	public static final String LOG_TAG = "memreas";
	public static final String DEVICE_TYPE = "ANDROID";
	public static int NOTIFICATION_TYPE = 0;
	public static int THUMBNAIL_ROTATE_TIME = 5000; // 5 seconds...
	public static int NUM_LOCATION_ITEMS = 4;
	public static String NOTIFICATION_EVENTID = "";
	public static String NOTIFICATION_MEDIAID = "";
	public static final String MEMREASTVM_ACTION = "memreas_tvm";
	public static final String FORGOT_PSS_ACTION = "forgotpassword";
	public static final String CHANGE_PSS_ACTION = "changepassword";
	public static final String LOGIN_ACTION = "login";
	public static final String UPDATE_EMAIL = "saveuserdetails";
	public static final String GET_USERDETAILS = "getuserdetails";
	public static final String LOGOUT_ACTION = "logout";
	public static final String COMPLETED_MEDAI_ACTION = "listallmedia";
	public static final String REGISTER_ACTION = "registration";
	public static final String ADD_EVENT_ACTION = "addevent";
	public static final String VIEW_EVENT_ACTION = "viewevents";
	public static final String ADD_MEDIA_EVENT_ACTION = "addmediaevent";
	public static final String CHECK_USER_NAME_ACTION = "checkusername";
	public static final String ADD_COMMENT_MEDIA_ACTION = "addcomments";
	public static final String GET_COMMENT_EVENT_ACTION = "listcomments";
	public static final String CREATE_GROUP_ACTION = "creategroup";
	public static final String LIKE_MEDIA_ACTION = "likemedia";
	public static final String MEDIA_INAPPROPRIATE_ACTION = "mediainappropriate";
	public static final String LIST_ALL_MEDIA = "listallmedia";
	public static final String UPDATE_MEDIA_LOCATION = "updatemedia";
	public static final String VIEW_ALL_FRIEND = "viewallfriends";
	public static final String LIST_ALL_FRIEND = "listmemreasfriends";
	public static final String REMOVE_FRIENDS = "removefriends";
	public static final String DOWNLOAD_MEDIA_ACTION = "download";
	public static final String VIEW_MEDIA_DETAIL_ACTION = "viewmediadetails";
	public static final String GET_EVENT_DETAIL_ACTION = "geteventdetails";
	public static final String LIST_PHOTOS_ACTION = "listphotos";
	public static final String ADD_FRIEND_ACTION = "addfriend";
	public static final String ADD_FRIENDS_TO_EVENT_ACTION = "addfriendtoevent";
	public static final String ADD_FRIENDS_TO_GROUP_ACTION = "addfriendtogroup";
	public static final String LIST_NOTIFICATION = "listnotification";
	public static final String CLEAR_NOTIFICATION = "clearallnotification";
	public static final String UPDATE_NOTIFICATION = "updatenotification";
	public static final String CHANGE_PASSWORD = "updatepassword";
	public static final String GET_EVENT_FRIENDS = "geteventpeople";
	public static final String GET_EVENT_COUNT = "geteventcount";
	public static final String GET_MEDIALIKE_COUNT = "getmedialike";
	public static final String FINDTAG = "findtag";
	public static final String REGISTER_DEVICE_ACTION = "registerdevice";
	public static final String GENERATE_MEDIA_ID_ACTION = "generatemediaid";
	public static final String FETCH_COPYRIGHT_BATCH_ACTION = "fetchcopyrightbatch";


	public static final String PREF_USERa = "pref_user";
	public static final String PREF_USERID = "userid";

	public static final int FETCHCOPYRIGHTBATCH_RUNNING_LOW  = 10;
	public static final float COPYRIGHT_TEXT_SIZE = 6.0f; //used * scale (MCameraActivity)
	public static final float COPYRIGHT_IMAGE_TEXT_SIZE = 5.0f; //used for video (MCameraActivity)
	public static final float COPYRIGHT_Y_AXIS_OFFSET= 30; //used to push copyright onto screen (MCameraActivity)
	public static final int COPYRIGHT_TEXT_COLOR = Color.BLUE; //used to push copyright onto screen (MCameraActivity)

	public static String EVENTID = "";
	public static String DEVICEID = "";
	public static String MEDIA_ID = "";
	public static String AUDIO_MEDIA_ID = "";
	public static String AUDIO_EXT_MP3 = ".mp3";
	public static String AUDIO_EXT_AAC = ".aac";
	public static int AUDIO_COMMENT_MAX_TIME = 60000;// 1 minute
	public static int CurrentMedia;
	public static final int GALLARY_IMG_WIDTH = 98;
	public static final int GALLARY_IMG_HEIGHT = 78;

	public static final int BLUE = Color.BLUE;
	public static final int GREEN = Color.GREEN;
	public static final int YELLOW = Color.YELLOW;

	public static final int IMAGE = 1;
	public static final int VIDEO = 2;

	public static final String IMAGE_MIMETYPE = "image";
	public static final String VIDEO_MIMETYPE = "video";

	public static final int RED = Color.RED;

	public static final int ON_SERVER_AND_GALLERY = 1;
	public static final int ON_SERVER = 2;
	public static final int ON_GALLERY = 3;

	public static final String DEFAULT_IMG_URL = "http://www.gravatar.com/avatar/69234902a8aa03ee6c98b07facfb3be2?s=128&d=identicon&r=PG";
	public static final String FB_PROFILE_PIC_URL = "http://graph.facebook.com/1583885283/picture";
	public static final String TW_PROFILE_PIC_URL = "https://api.twitter.com/1/users/profile_image?user_id=92726549&size=normal";

	// ============= Google Related ================
	public static String GCMSenderId = "YOUR_GCMSenderId";
	public static final String ADMOB_ID = "YOUR_ADMOB_ID";
	public static final String ADMOB_BANNER_UNIT_ID = "YOUR_ADMOB_BANNER_UNIT_ID";
	public static final String PLACES_API_KEY = "YOUR_PLACES_API_KEY";
	public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	public static final String PLACES_API_DETAILS = "https://maps.googleapis.com/maps/api/place/details/json";
	public static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	public static final String OUT_JSON = "/json";

	// ============================================

	public static final int USER_LANDING_PAGE = BaseActivity.GALLERY_INDEX;

	// ============================================
	public static final long MAX_VIDEO_SIZE_FREE = 100 * 1024 * 1024;// 100 MB
	public static final long MAX_VIDEO_SIZE_PAID = 5 * 1024 * 1024 * 1024;// 500
																			// GB
	public static final int MAX_VIDEO_DURATION = 10 * 60 * 1000;// 10 minutes
	public static final int MIN_VIDEO_DURATION = 0 * 60 * 1000;// 0 seconds
}
