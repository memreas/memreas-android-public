
package com.memreas.sax.handler;

import android.util.Log;

import com.memreas.gallery.GalleryBean;
import com.memreas.gallery.GalleryBean.GalleryType;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

//import com.amazonaws.util.json.JSONArray;
//import com.amazonaws.util.json.JSONException;
//import com.amazonaws.util.json.JSONObject;
//import com.amazonaws.util.json.JSONTokener;

public class ListAllMediaDetailHandler extends DefaultHandler {

	Boolean curElement;
	String curValue;
	int i = 0;
	private List<GalleryBean> mGalleryItems;

	private GalleryBean mGallery;

	private String status;
	private String message;

	public List<GalleryBean> getGalleryItems() {
		return mGalleryItems;
	}

	public String getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public ListAllMediaDetailHandler() {
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		i++;
		curElement = true;
		curValue = "";

		if (localName.equals("medias")) {
			mGalleryItems = new ArrayList<GalleryBean>();
		} else if (localName.equals("media")) {
			mGallery = new GalleryBean(GalleryType.SERVER);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		//Log.i(getClass().getName()+".endElement()."+i, localName + " =>" + curValue);
		if (localName.equals("status")) {
			this.status = curValue;
		} else if (localName.equals("message")) {
			this.message = curValue;
		} else if (localName.equals("media_id")) {
			mGallery.setMediaId(curValue);
		} else if (localName.equals("device_id")) {
			mGallery.setDeviceId(curValue);
		} else if (localName.equals("device_type")) {
			mGallery.setDeviceType(curValue);
		} else if (localName.equals("media_date")) {
			mGallery.setMediaDate(Long.valueOf(curValue)*1000L);
		} else if (localName.equals("main_media_url")) {
				String value = CommonHandler.parseMemJSON(curValue);
				mGallery.setMediaUrl(value);
		} else if (localName.equals("main_media_path")) {
			//String value = CommonHandler.parseMemJSON(curValue);
			mGallery.setMediaUrlS3Path(curValue);
		} else if (localName.equals("media_transcode_status")) {
			mGallery.setMediaTranscodeStatus(curValue);
		} else if (localName.equals("media_url_web")) {
			if (curValue != null && curValue.length() > 0) {
				String value = CommonHandler.parseMemJSON(curValue);
				mGallery.setMediaUrlWeb(value);
			}
		} else if (localName.equals("media_url_webm")) {
			if (curValue != null && curValue.length() > 0) {
				String value = CommonHandler.parseMemJSON(curValue);
				mGallery.setMediaUrlWebm(value);
			}
		} else if (localName.equals("media_url_hls")) {
			if (curValue != null && curValue.length() > 0) {
				String value = CommonHandler.parseMemJSON(curValue);
				mGallery.setMediaUrlHls(value);
			}
		} else if (localName.equals("metadata")) {
			try {
				JSONObject objMetadata = (JSONObject) new JSONTokener(curValue)
						.nextValue();
				if (objMetadata.has("S3_files")) {
					String S3query = objMetadata.getString("S3_files");
					JSONObject objS3 = (JSONObject) new JSONTokener(S3query)
							.nextValue();
					if (objS3.has("download")) {
						mGallery.setMediaUrlS3Path(objS3.getString("download"));
					}
					if (objS3.has("web")) {
						mGallery.setMediaUrlWebS3Path(objS3.getString("web"));
					}
					if (objS3.has("size")) {
						mGallery.setMediaSize(objS3.getInt("size"));
					}
					if (objS3.has("location")) {
						String Locationquery = objS3.getString("location");
						if (Locationquery != null
								&& !(Locationquery.equals("null") || Locationquery
										.equals(""))) {
							JSONObject objLocation = (JSONObject) new JSONTokener(
									Locationquery).nextValue();
							double longitude = 0;
							try {
								longitude = objLocation.getDouble("longitude");
							} catch (Exception e) {
							}
							double latitude = 0;
							try {
								latitude = objLocation.getDouble("latitude");
							} catch (Exception e) {

							}
							mGallery.setLocation(latitude, longitude);
						} else {
							mGallery.setLocation(0, 0);
						}
					} else {
						mGallery.setLocation(0, 0);
					}
				}
			} catch (JSONException e) {
				//Invalid Json - ignore...
				//e.printStackTrace();
			} catch (Exception e) {
				// Log.d(getClass().getName()+"curValue--->", "*"+curValue+"*");
				// e.printStackTrace();
				//Invalid Json - ignore...
				//e.printStackTrace();
			}
		} else if (localName.equals("event_media_video_thum")) {
			if (curValue != null && curValue.length() > 0) {
				String value = CommonHandler.parseMemJSON(curValue);
				// mGallery.setMediaThumbnailUrl(value);
			}
		} else if (localName.equals("media_url_79x80")) {
				mGallery.setMediaThumbnailUrl(CommonHandler
						.parseMemJSONArray(curValue));
				mGallery.setMediaThumbnailUrl79x80(CommonHandler
						.parseMemJSONArray(curValue));
				Log.i("media_url_79x80", curValue);
		} else if (localName.equals("media_url_98x78")) {

				mGallery.setMediaThumbnailUrl(CommonHandler
						.parseMemJSONArray(curValue));
				mGallery.setMediaThumbnailUrl98x78(CommonHandler
						.parseMemJSONArray(curValue));
				Log.i("media_url_98x78", curValue);
		} else if (localName.equals("media_url_448x306")) {
				mGallery.setMediaThumbnailUrl448x306(CommonHandler
						.parseMemJSONArray(curValue));
				Log.i("media_url_448x306", curValue);
		} else if (localName.equals("media_url_1280x720")) {
				mGallery.setMediaThumbnailUrl1280x720(CommonHandler
						.parseMemJSONArray(curValue));
				Log.i("media_url_1280x720", curValue);
		} else if (localName.equals("type")) {
			mGallery.setMediaType(curValue);
		} else if (localName.equals("media_name")) {
			mGallery.setMediaName(curValue);
		} else if (localName.equals("media_name_prefix")) {
			mGallery.setMediaNamePrefix(curValue);
		} else if (localName.equals("media")) {
			mGalleryItems.add(mGallery);
			mGallery = null;
		}
		curElement = false;
		curValue = "";
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (curElement) {
			curValue += new String(ch, start, length);
		}
	}
}
