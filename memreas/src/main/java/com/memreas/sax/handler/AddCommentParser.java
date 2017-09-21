
package com.memreas.sax.handler;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.memreas.base.Common;
import com.memreas.comment.AddTextCommentActivity;
import com.memreas.gallery.GalleryBean;
import com.memreas.gallery.MediaIdManager;

public class AddCommentParser extends AsyncTask<String, Void, String> {
	Activity activity;
	// iShadowX
	private boolean canFinish = false;
	private String eventId;
	private String mediaId;
	private String status;
	private GalleryBean audioBean;

	public AddCommentParser(Activity activity,
							GalleryBean audioBean) {
		this.activity = activity;
		this.audioBean = audioBean;
		this.status = "failure";
	}

	public AddCommentParser(Activity activity, boolean canFinish) {
		// TODO Auto-generated constructor stub
		this.activity = activity;
		this.canFinish = canFinish;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			eventId = params[0];
			mediaId = params[1];
			String comment = params[2];
			String audio_media_id = null;
			// Log.i("AddCommentParser  mediaId", mediaId);
			String xmlData;
			if (audioBean != null) {
				audio_media_id = MediaIdManager.getInstance().fetchNextMediaId();
				audioBean.setMediaId(audio_media_id);

			}

			/**
			 * Cases to handle
			 *  1 - gallery text only
			 *  2 - gallery audio only
			 *  3 - gallery text and audio
			 *  4 - details text only
			 *  5 - details audio only
			 *  6 - details text and audio
			 */
			if (mediaId == null) {
				mediaId = "";
			}
			if (audioBean == null) {
				audio_media_id = "";
			}
			if (comment == null) {
				comment = "";
			}

			/**
			 * This sends "" which the web service handles
			 */
			xmlData = XMLGenerator.addCommentXML(comment, eventId, mediaId, audio_media_id);

			Log.i("AddCommentParser  XML DATA", xmlData);
			SaxParser.parse(
					Common.SERVER_URL + Common.ADD_COMMENT_MEDIA_ACTION,
					xmlData, new CommonHandler(), "xml");

			final CommonGetSet addEventGetter = CommonHandler.commonList;

			this.status = addEventGetter.getStatus().toString();
			if (addEventGetter != null
					&& addEventGetter.getStatus() != null
					&& (addEventGetter.getStatus().toString())
					.equalsIgnoreCase("success")) {
				Log.i("AddCommentParser", "Media Comment add successfully\n");
				Toast.makeText(activity, "comments added...", Toast.LENGTH_LONG).show();

			}
		} catch (Exception e) {
			Log.e("AddCommentParser", "Exception occurred");
		}
		return this.status;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (result.equalsIgnoreCase("success")) {
			Toast.makeText(activity, "comment submitted...",
					Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(
					AddTextCommentActivity.ACTION_ADDED_COMMENT);
			intent.putExtra("event_id", eventId);
			intent.putExtra("media_id", mediaId);
			activity.sendBroadcast(intent);
		} else {
			Toast.makeText(activity, "failed to submit comment...",
					Toast.LENGTH_SHORT).show();
		}

		if (canFinish) {
			activity.finish();
		}
	}
}
