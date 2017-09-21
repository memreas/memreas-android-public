

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

public class ForgotPasswordParser extends AsyncTask<String, Void, String> {
	
	ProgressBar progressBar;
	Activity activity;
	
	public ForgotPasswordParser(Activity activity,ProgressBar progressBar) {
		this.activity = activity;
		this.progressBar=progressBar;
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		
		String email = arg0[0];
		String xmlData = XMLGenerator.forgotUserPassXML(email);
		
		Log.i("ForgotPswParser XML DATA", xmlData);
		SaxParser.parse(Common.SERVER_URL + Common.FORGOT_PSS_ACTION, xmlData,
				new CommonHandler(), "xml");
		
		final CommonGetSet forgotGet = CommonHandler.commonList;
		
		if ((forgotGet.getStatus().toString()).equalsIgnoreCase("success")) {
			
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					new AlertDialog.Builder(activity)			
					.setMessage(forgotGet.getMessage().toString())
					.setPositiveButton("Ok",
							new android.content.DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0, int arg1) {
									return;
								}
					}).show();
				}
			});
		}
		else {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(activity, forgotGet.getMessage().toString(),
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