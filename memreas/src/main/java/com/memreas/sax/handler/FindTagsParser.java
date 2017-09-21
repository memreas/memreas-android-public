
package com.memreas.sax.handler;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.memreas.base.Common;
import com.memreas.base.SessionManager;
import com.memreas.search.TagComment;
import com.memreas.search.TagEvent;
import com.memreas.search.TagUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FindTagsParser extends AsyncTask<String, String, String> {
	private List<Object> results;
	private FindTagCallBack mCallBack;
	private Context context;

	public FindTagsParser(Context _context, FindTagCallBack callback) {
		this.mCallBack = callback;
		this.context = _context;
	}

	@Override
	protected String doInBackground(String... args) {

		String xmlData = XMLGenerator.getTags(args[0]);
		Log.i("SearchParser XML DATA", xmlData);

		String response = SaxParser.parse(Common.SERVER_URL + Common.FINDTAG,
				xmlData, "xml");
		if (response.length() != 0) {
			try {
				JSONObject jSONObject = new JSONObject(response);
				JSONArray searchResults = jSONObject.getJSONArray("search");
				results = new ArrayList<Object>();
				if (args[0].startsWith("@")) {
					for (int i = 0; i < searchResults.length(); i++) {
						JSONObject resultJSONObject = searchResults
								.getJSONObject(i);
						TagUser item = new TagUser();
						if (resultJSONObject.has("username")) {
							item.setUsername(resultJSONObject
									.getString("username"));
						}
						if (resultJSONObject.has("user_id")) {
							item.setUser_id(resultJSONObject
									.getString("user_id"));
						}
						if (resultJSONObject.has("profile_photo")) {
							try {
								item.setProfile_photo(CommonHandler
										.parseMemJSON(resultJSONObject
												.getString("profile_photo")));
							} catch (JSONException e) {
								item.setProfile_photo(resultJSONObject
										.getString("profile_photo"));
							}
						}
						if (resultJSONObject.has("friend_request_sent")) {
							item.setFriend_request_sent(resultJSONObject
									.getString("friend_request_sent"));
						}

						results.add(item);
					}
					return "@";
				} else if (args[0].startsWith("!")) {
					for (int i = 0; i < searchResults.length(); i++) {
						JSONObject resultJSONObject = searchResults
								.getJSONObject(i);
						TagEvent item = new TagEvent();
						if (resultJSONObject.has("name")) {
							item.setName(resultJSONObject.getString("name"));
						}
						if (resultJSONObject.has("event_id")) {
							item.setEvent_id(resultJSONObject
									.getString("event_id"));

						}
						if (resultJSONObject.has("location")) {
							item.setLocation(resultJSONObject
									.getString("location"));
						}
						if (resultJSONObject.has("user_id")) {
							item.setUser_id(resultJSONObject
									.getString("user_id"));
							if (item.getUser_id().equals(
									SessionManager.getInstance()
											.getUser_id())) {
								item.setAdded(true);
							}
						}
						if (resultJSONObject.has("event_media_url")) {
							JSONArray jsonArray = CommonHandler
									.parseMemJSONArray(resultJSONObject
											.getString("event_media_url"));
							item.setEvent_media_url(jsonArray);
						}
						if (resultJSONObject.has("event_creator_pic")) {
							item.setEvent_creator_pic(CommonHandler
									.parseMemJSON(resultJSONObject
											.getString("event_creator_pic")));
						}

						if (resultJSONObject.has("event_creator_name")) {
							item.setEvent_creator_name(resultJSONObject
									.getString("event_creator_name"));
						}

						if (resultJSONObject.has("event_request_sent")) {
							item.setAdded(true);
						}
						results.add(item);
					}
					return "!";
				} else if (args[0].startsWith("#")) {
					for (int i = 0; i < searchResults.length(); i++) {
						JSONObject resultJSONObject = searchResults
								.getJSONObject(i);
						TagComment item = new TagComment();
						if (resultJSONObject.has("tag_name")) {
							item.setTag_name(resultJSONObject
									.getString("tag_name"));
						}
						if (resultJSONObject.has("event_name")) {
							item.setEvent_name(resultJSONObject
									.getString("event_name"));
						}
						if (resultJSONObject.has("event_id")) {
							item.setEvent_id(resultJSONObject
									.getString("event_id"));
						}
						if (resultJSONObject.has("event_media_url")) {
							JSONArray jsonArray = resultJSONObject
									.getJSONArray("event_media_url");
							item.setEvent_media_url(jsonArray);
						}
						if (resultJSONObject.has("commenter_photo")) {
							item.setCommenter_photo(CommonHandler
									.parseMemJSON(resultJSONObject
											.getString("commenter_photo")));
						}
						if (resultJSONObject.has("commenter_name")) {
							item.setCommenter_name(resultJSONObject
									.getString("commenter_name"));
						}
						if (resultJSONObject.has("comment")) {
							item.setComment(resultJSONObject
									.getString("comment"));
						}
						results.add(item);
					}
					return "#";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (mCallBack != null) {
			mCallBack.callBack(result, results);
		}
	}

	public interface FindTagCallBack {
		void callBack(String result, List<Object> results);
	}
}
