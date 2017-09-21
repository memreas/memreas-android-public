
package com.memreas.gallery;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.memreas.memreas.MemreasMediaBean;

import org.json.JSONArray;

public class GalleryBean extends MemreasMediaBean {
	public enum GalleryType {
		SERVER, SYNC, NOT_SYNC
	};

	private String mediaId;
	private String deviceId;
	private String deviceType;
	private String mediaType;
	private String mediaName;
	private String mediaNamePrefix;
	private String mediaTranscodeStatus;
	private int mediaSize;
	private String mediaUrl;
	private String mediaUrlS3Path;
	private String mediaUrlWeb;
	private String mediaUrlWebm;
	private String mediaUrlHls;
	private String mediaUrlWebS3Path;
	private JSONArray mediaThumbnailUrl = new JSONArray();
	private JSONArray mediaThumbnailUrl79x80 = new JSONArray();
	private JSONArray mediaThumbnailUrl98x78= new JSONArray();;
	private JSONArray mediaThumbnailUrl448x306= new JSONArray();;
	private JSONArray mediaThumbnailUrl1280x720= new JSONArray();;
	private Bitmap mediaThumbBitmap;
	private double mLatitude;
	private double mLongitude;
	public String mCountry;
	public String mCity;
	private String localMediaPath;
	private String serverPath;
	private long videoDuration;
	private GalleryType type;
	private long mediaDate;
	private boolean isLocal=true;
	private boolean isSelected=false;
	private boolean isProfilePic=false;
	private boolean isRegistration=false;
	private boolean selectedForShare=false;
	private String mimeType;
	private String copyright;

	public GalleryBean(GalleryType type) {
		this.type = type;
	}

	public GalleryBean(String mediaUrl, String mediaName, String mediaType,
			String mediaId, boolean isServerMedia, Bitmap mediaThumb,
			GalleryType type) {
		this.type = type;
		this.mediaUrl = mediaUrl;
		this.mediaType = mediaType;
		this.mediaId = mediaId;
		this.mediaThumbBitmap = mediaThumb;
		this.mediaName = mediaName;
	}

	public GalleryBean(String mediaUrl, String mediaName, String mediaType,
			String mediaId, Bitmap mediaThumb) {
		this.mediaUrl = mediaUrl;
		this.mediaType = mediaType;
		this.mediaId = mediaId;
		this.mediaThumbBitmap = mediaThumb;
		this.mediaName = mediaName;
	}

	public String getMediaName() {
		return mediaName;
	}

	public void setMediaName(String mediaName) {
		this.mediaName = mediaName;
	}

	public String getMediaNamePrefix() {
		return mediaNamePrefix;
	}

	public void setMediaNamePrefix(String prefix) {
		mediaNamePrefix = prefix;
	}

	public String getMediaTranscodeStatus() {
		return mediaTranscodeStatus;
	}

	public void setMediaTranscodeStatus(String mediaTranscodeStatus) {
		this.mediaTranscodeStatus = mediaTranscodeStatus;
	}

	public int getMediaSize() {
		return mediaSize;
	}

	public void setMediaSize(int mediaSize) {
		this.mediaSize = mediaSize;
	}

	public Bitmap getMediaThumbBitmap() {
		return mediaThumbBitmap;
	}

	public void setMediaThumbBitmap(Bitmap videoThumbBitmap) {
		this.mediaThumbBitmap = videoThumbBitmap;
	}

	public String getMediaUrl() {
		return mediaUrl;
	}

	public void setMediaUrl(String mediaUrl) {
		this.mediaUrl = mediaUrl;
	}

	public int getMediaOuterColorCode() {
		int code;
		switch (type) {
		case SYNC:
			code = Color.GREEN;
			break;
		case SERVER:
			code = Color.YELLOW;
			break;
		case NOT_SYNC:
			code = Color.RED;
			break;

		default:
			code = Color.GRAY;
			break;
		}
		return code;
	}

	public boolean isMediaSynchronized() {
		return (type == GalleryType.SYNC);
	}

	public String getMediaUrlS3Path() {
		return mediaUrlS3Path;
	}

	public void setMediaUrlS3Path(String mediaUrlS3Path) {
		this.mediaUrlS3Path = mediaUrlS3Path;
	}

	public String getMediaUrlWebS3Path() {
		return mediaUrlWebS3Path;
	}

	public void setMediaUrlWebS3Path(String mediaUrlWebS3Path) {
		this.mediaUrlWebS3Path = mediaUrlWebS3Path;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public JSONArray getMediaThumbnailUrl() {
		return mediaThumbnailUrl;
	}

	public void setMediaThumbnailUrl(JSONArray mediaThumbnailUrl) {
		this.mediaThumbnailUrl = mediaThumbnailUrl;
	}

	public JSONArray getMediaThumbnailUrl79x80() {
		return mediaThumbnailUrl79x80;
	}

	public void setMediaThumbnailUrl79x80(JSONArray mediaThumbnailUrl79x80) {
		this.mediaThumbnailUrl79x80 = mediaThumbnailUrl79x80;
	}

	public JSONArray getMediaThumbnailUrl98x78() {
		return mediaThumbnailUrl98x78;
	}

	public void setMediaThumbnailUrl98x78(JSONArray mediaThumbnailUrl98x78) {
		this.mediaThumbnailUrl98x78 = mediaThumbnailUrl98x78;
	}

	public JSONArray getMediaThumbnailUrl448x306() {
		return mediaThumbnailUrl448x306;
	}

	public void setMediaThumbnailUrl448x306(JSONArray mediaThumbnailUrl448x306) {
		this.mediaThumbnailUrl448x306 = mediaThumbnailUrl448x306;
	}

	public void setLocation(double latitude, double longitude) {
		this.mLatitude = latitude;
		this.mLongitude = longitude;
	}

	public double getLatitude() {
		return this.mLatitude;
	}

	public double getLongitude() {
		return this.mLongitude;
	}

	public String getLocalMediaPath() {
		return localMediaPath;
	}

	public void setLocalMediaPath(String localMediaPath) {
		this.localMediaPath = localMediaPath;
	}

	public String getServerPath() {
		return serverPath;
	}

	public void setServerPath(String serverPath) {
		this.serverPath = serverPath;
	}

	public GalleryType getType() {
		return type;
	}

	public void setType(GalleryType type) {
		this.type = type;
	}

	public long getVideoDuration() {
		return videoDuration;
	}

	public void setVideoDuration(long videoDuration) {
		this.videoDuration = videoDuration;
	}

	public String getmCountry() {
		return mCountry;
	}

	public void setmCountry(String mCountry) {
		this.mCountry = mCountry;
	}

	public String getmCity() {
		return mCity;
	}

	public void setmCity(String mCity) {
		this.mCity = mCity;
	}

	public long getMediaDate() {
		return mediaDate;
	}

	public void setMediaDate(long mediaDate) {
		this.mediaDate = mediaDate;
	}

	public boolean isProfilePic() {
		return isProfilePic;
	}

	public void setProfilePic(boolean isProfilePic) {
		this.isProfilePic = isProfilePic;
	}

	public boolean isRegistration() {
		return isRegistration;
	}

	public void setRegistration(boolean isProfilePic) {
		this.isRegistration = isRegistration;
	}

	public boolean isSelectedForShare() {
		return selectedForShare;
	}

	public void setSelectedForShare(boolean selectedForShare) {
		this.selectedForShare = selectedForShare;
	}
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isLocal() {
		if ((type == GalleryType.NOT_SYNC) || (type == GalleryType.SYNC))
			return true;
		else
			return false;
	}



	public String getMimeType() {
			return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getMediaUrlWeb() {
		return mediaUrlWeb;
	}

	public void setMediaUrlWeb(String mediaUrlWeb) {
		this.mediaUrlWeb = mediaUrlWeb;
	}

	public String getMediaUrlWebm() {
		return mediaUrlWebm;
	}

	public void setMediaUrlWebm(String mediaUrlWebm) {
		this.mediaUrlWebm = mediaUrlWebm;
	}

	public String getMediaUrlHls() {
		return mediaUrlHls;
	}

	public void setMediaUrlHls(String mediaUrlHls) {
		this.mediaUrlHls = mediaUrlHls;
	}

	public JSONArray getMediaThumbnailUrl1280x720() {
		return mediaThumbnailUrl1280x720;
	}

	public void setMediaThumbnailUrl1280x720(JSONArray mediaThumbnailUrl1280x720) {
		this.mediaThumbnailUrl1280x720 = mediaThumbnailUrl1280x720;
	}

	public String getCopyright() {
		if (copyright == null) {
			copyright = "";
		}
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}


}
