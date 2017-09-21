
package com.memreas.sax.handler;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.memreas.base.Common;

public class UpdateMediaParser extends AsyncTask<String, Void, String> {

	ProgressBar progressBar;
	Activity activity;
	UpdateMediaHandler handler = new UpdateMediaHandler();

	public UpdateMediaParser(Activity activity, ProgressBar progressBar) {
		this.activity = activity;
		this.progressBar = progressBar;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... arg0) {

		String mediaId = arg0[0];
		String latitude = arg0[1];
		String longitude = arg0[2];
		//String deviceId = SessionManager.getInstance().getDeviceId();
		String address = arg0[3];
		String xmlData = XMLGenerator.updateMediaLocationXML(mediaId, latitude, longitude, address);

		SaxParser.parse(Common.SERVER_URL + Common.UPDATE_MEDIA_LOCATION, xmlData,
				handler, "xml");

		return handler.isSuccess() ? "success":"failure";
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);

		if (handler.isSuccess()) {
			Toast.makeText(activity, "success: location update", Toast.LENGTH_SHORT)
					.show();
			progressBar.setVisibility(View.GONE);
		} else {
			//((LoginActivity) activity).enableLoginButton();
			Toast.makeText(activity, "failed to update location",
					Toast.LENGTH_LONG).show();
			progressBar.setVisibility(View.GONE);
		}

	}


}