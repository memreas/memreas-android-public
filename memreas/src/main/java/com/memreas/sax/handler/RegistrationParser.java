
package com.memreas.sax.handler;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.memreas.base.Common;
import com.memreas.base.SessionManager;
import com.memreas.member.RegistrationActivity;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RegistrationParser extends AsyncTask<String, String, String> {

	private RegistrationActivity activity;
	private ProgressDialog mProgressDialog;
	private int lastPercent = 0;
	private RegistrationHandler handler = new RegistrationHandler();
	private boolean isCanceledUpload = false;
	private boolean isUpload = false;
	private String email;
	private String username;
	private String password;
	private String device_id;
	private String invited_by;
	private String event_id;
	private String profile_photo;
	private String secret;

	public RegistrationParser(RegistrationActivity activity) {
		this.activity = activity;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		activity.btnImgSubmit.setEnabled(false);
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(activity);
		}
		mProgressDialog.setCancelable(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setTitle("Registration");
		mProgressDialog.setMessage("Setting profile...");
		mProgressDialog.show();
	}

	@Override
	protected String doInBackground(String... arg0) {

		this.email = arg0[0];
		this.username = arg0[1];
		this.password = arg0[2];
		this.device_id = arg0[3];
		this.invited_by = arg0[4];
		this.event_id = arg0[5];
		this.profile_photo = arg0[6];
		this.secret = arg0[7];
		String registrationXmlData = XMLGenerator.registrationXML(email,
				username, password, device_id, invited_by, event_id, profile_photo, secret);

		if (device_id.length() == 0) {
			return "missing_device_id";
		}

		SaxParser.parse(Common.SERVER_URL + Common.REGISTER_ACTION,
				registrationXmlData, handler, "xml");

		if ((handler.getStatus().toString()).equalsIgnoreCase("success")) {

			// Set session data - cleared in reg activity class...
			SessionManager.getInstance().setDevice_id(device_id);
			SessionManager.getInstance().setUser_name(username);
			SessionManager.getInstance().setUser_id(handler.getUserId());

			publishProgress(handler.getUserId());
		}
		return handler.getStatus();
	}

	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(String response) {
		super.onPostExecute(response);
		// If no profile pic close this task with a callback...
		mProgressDialog.dismiss();
		activity.callBackUploader(handler.getStatus(), handler.getMessage());
	}

	private class RegistrationHandler extends DefaultHandler {

		Boolean curElement, more = false;// ,url_web=false;
		String curValue;
		int i = 0;
		private String status;
		private String message;
		private String userId;
		private String email_verification_url;

		public RegistrationHandler() {
		}

		public String getStatus() {
			return status;
		}

		public String getMessage() {
			return message;
		}

		public String getUserId() {
			return userId;
		}

		public String getEmail_verification_url() {
			return email_verification_url;
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			i++;
			curElement = true;
			curValue = "";
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			Log.i("CommonHandler " + i, "endElement =>" + localName + " =>"
					+ curValue);
			if (localName.equals("status")) {
				this.status = curValue;
			} else if (localName.equals("message")) {
				this.message = curValue;
			} else if (localName.equals("userid")) {
				this.userId = curValue;
			} else if (localName.equals("email_verification_url")) {
				this.email_verification_url = curValue;
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

}
