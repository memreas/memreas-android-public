
package com.memreas.sax.handler;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CommonHandler extends DefaultHandler {

	Boolean curElement, more = false;
	String curValue;
	int i = 0;
	public static CommonGetSet commonList = null;

	public static CommonGetSet getCommon() {
		return commonList;
	}

	public static void setCommon(CommonGetSet commonList) {
		CommonHandler.commonList = commonList;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		i++;
		curElement = true;
		curValue = "";
		if (qName.equals("forgotpasswordresponse")) {
			commonList = new CommonGetSet();
		} else if (qName.equals("listphotosresponse")) {
			commonList = new CommonGetSet();
		} else if (qName.equals("loginresponse")) {
			commonList = new CommonGetSet();
		} else if (qName.equals("registrationresponse")) {
			commonList = new CommonGetSet();
		} else if (qName.equals("addeventresponse")) {
			commonList = new CommonGetSet();
		} else if (qName.equals("listallmediaresponse")) {
			commonList = new CommonGetSet();
		} else if (qName.equals("checkusernameresponse")) {
			commonList = new CommonGetSet();
		} else if (qName.equals("addcommentresponse")) {
			commonList = new CommonGetSet();
		} else if (qName.equals("likemediaresponse")) {
			commonList = new CommonGetSet();
		} else if (qName.equals("mediainappropriateresponse")) {
			commonList = new CommonGetSet();
		} else if (qName.equals("creategroupresponse")) {
			commonList = new CommonGetSet();
		} else if (qName.equals("friends")) {
			commonList = new CommonGetSet();
		} else if (qName.equals("downloadresponse")) {
			commonList = new CommonGetSet();
		} else if (qName.equals("viewmediadetailresponse")) {
			commonList = new CommonGetSet();
		} else if (qName.equals("getuserdetails")) {
			commonList = new CommonGetSet();
		}
		/*
		 * else if(qName.equals("media_url_web")){ url_web=true; }
		 */

		if (qName.equals("main_media_url")) {
			more = true;
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		Log.i("CommonHandler " + i, "endElement =>" + qName + " =>" + curValue);
		try {
			if (qName.equals("status")) {
				commonList.setStatus(curValue);
			} else if (qName.equals("message")) {
				commonList.setMessage(curValue);
			} else if (qName.equals("name")) {
				commonList.setName(curValue);
			} else if (qName.equals("noofimage")) {
				commonList.setNoofimage(Integer.valueOf(curValue.trim()));
			} else if (qName.equals("email")) {
				commonList.setUserEmailId(curValue);
			} else if (qName.equals("profile")) {
				commonList.setUserProfile(curValue);
			} else if (qName.equals("userid")) {
				commonList.setUser_id(curValue);
			} else if (qName.equals("event_id")) {
				commonList.setEvent_id(curValue.trim());
			} else if (qName.equals("media_id")) {
				commonList.setMedia_id(curValue);
			} else if (qName.equals("is_downloaded")
					|| qName.equals("is_download")) {
				commonList.setIs_downloaded((curValue.equals("1") ? true
						: false));
			} else if (qName.equals("main_media_url")) {
				commonList.setMain_media_url(curValue);
			} else if (qName.equals("media_url_web")) {
				commonList.setMedia_url_web(curValue);
			} else if (qName.equals("media_url_webm")) {
				commonList.setMedia_url_webm(curValue);
			} else if (qName.equals("media_url_hls")) {
				commonList.setMedia_url_hls(curValue);
			} else if (qName.equals("event_media_video_thum")) {
				commonList.setEvent_media_video_thum(parseMemJSON(curValue));
			} else if (qName.equals("media_url_79x80")) {
				commonList.setMedia_url_79x80(parseMemJSON(curValue));
			} else if (qName.equals("media_url_98x78")) {
				commonList.setMedia_url_98x78(parseMemJSON(curValue));
			} else if (qName.equals("media_url_448x306")) {
				commonList.setMedia_url_448x306(parseMemJSON(curValue));
			} else if (qName.equals("media_url_1280x720")) {
				commonList.setMedia_url_1280x720(parseMemJSON(curValue));
			} else if (qName.equals("type")) {
				commonList.setType(curValue);
			} else if (qName.equals("media_name")) {
				commonList.setMedia_name(curValue);
			} else if (qName.equals("isexist")) {
				commonList.setIsExist(curValue);
			} else if (qName.equals("friend_id")) {
				commonList.setFriend_id(curValue);
			} else if (qName.equals("network")) {
				commonList.setNetwork(curValue);
			} else if (qName.equals("social_username")) {
				commonList.setSocial_username(curValue);
			} else if (qName.equals("url")) {
				commonList.setMedia_url(parseMemJSON(curValue));
			} else if (qName.equals(parseMemJSON(curValue))) {
				commonList.setMedia_url_79x80(curValue);
			} else if (qName.equals("totle_like_on_media")) {
				commonList.setTotle_like_on_media(Integer.valueOf(curValue));
			} else if (qName.equals("totle_comment_on_media")) {
				commonList.setTotle_comment_on_media(Integer.valueOf(curValue));
			} else if (qName.equals("last_comment")) {
				commonList.setLast_comment(curValue);
			} else if (qName.equals("last_audio")) {
				commonList.setLast_audio(curValue);
			} else if (qName.equals("last_audiotext_comment")) {
				// commonList.setLast_audiotext_comment(curValue);
			} else if (qName.equals("audio_url")) {
				commonList.setAudio_url(parseMemJSON(curValue));
			} else if (qName.equals("group_id")) {
				commonList.setList_group_id(curValue);
			} else if (qName.equals("group_name")) {
				commonList.setList_group_name(curValue);
			} else if (qName.equals("metadata")) {
				try {
					JSONObject objMetadata = (JSONObject) new JSONTokener(
							curValue).nextValue();
					if (objMetadata.has("S3_files")) {
						String S3query = objMetadata.getString("S3_files");
						JSONObject objS3 = (JSONObject) new JSONTokener(S3query)
								.nextValue();
						if (objS3.has("location")) {
							String Locationquery = objS3.getString("location");
							if (Locationquery != null
									&& !(Locationquery.equals("null") || Locationquery
											.equals(""))) {
								JSONObject objLocation = (JSONObject) new JSONTokener(
										Locationquery).nextValue();
								double longitude = 0;
								try {
									longitude = objLocation
											.getDouble("longitude");
								} catch (Exception e) {
								}
								double latitude = 0;
								try {
									latitude = objLocation
											.getDouble("latitude");
								} catch (Exception e) {

								}
								commonList.setLocation_Latitude(latitude);
								commonList.setLocation_Longitude(longitude);
							} else {
								commonList.setLocation_Latitude(-100.0);
								commonList.setLocation_Longitude(-100.0);
							}
						} else {
							commonList.setLocation_Latitude(-100.0);
							commonList.setLocation_Longitude(-100.0);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		curElement = false;
		curValue = "";
	}

	public static String parseMemJSON(String json) {
		if (json.startsWith("[")) {
			//do nothing...
		} else {
			json = "[" + json + "]";
		}
		try {

			JSONArray array = new JSONArray(json);
			if (array.length() > 0) {
				String str = array.getString(0);
				return array.getString(0);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static JSONArray parseMemJSONArray(String json) {
 		if (json.startsWith("[")) {
			//do nothing...
		} else {
			json = "[" + json + "]";
		}
		try {
			return new JSONArray(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (curElement) {
			curValue += new String(ch, start, length);
		}
	}

}
