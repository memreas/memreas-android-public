
package com.memreas.sax.handler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.memreas.base.Common;

public class ChangePasswordParser extends AsyncTask<String, Void, String> {

	ProgressBar progressBar;
	Activity activity;

	public ChangePasswordParser(Activity activity, ProgressBar progressBar) {

		this.activity = activity;
		this.progressBar = progressBar;
	}

	@Override
	protected String doInBackground(String... arg0) {

		String password = arg0[0];
		String verify = arg0[1];
		String code = arg0[2];

		String xmlData = XMLGenerator.changePassXML(password, verify, code,
				true);

		Log.i("ChangePswParser XML DATA", xmlData);
		SaxParser.parse(Common.SERVER_URL + Common.CHANGE_PSS_ACTION, xmlData,
				new CommonHandler(), "xml");

		final CommonGetSet changeGet = CommonHandler.commonList;

		if ("success".equalsIgnoreCase(changeGet.getStatus())) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					new AlertDialog.Builder(activity)
							.setMessage(changeGet.getMessage().toString())
							.setPositiveButton(
									"Ok",
									new android.content.DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface arg0, int arg1) {
											return;
										}
									}).show();
				}
			});
		} else {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(activity, changeGet.getMessage().toString(),
							Toast.LENGTH_LONG).show();
				}
			});
		}

		return "";
	}

	@Override
	protected void onPostExecute(String result) {

		super.onPostExecute(result);
		progressBar.setVisibility(View.INVISIBLE);
	}
}