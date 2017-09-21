
package com.memreas.stsfail;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MemreasTVMHandler extends DefaultHandler {

	Boolean curElement;
	String curValue;
	int i = 0;
	private String status;
	//STS token data
	private JSONObject memreasTVM;
	private String accessKeyId;
	private String secretAccessKey;
	private String sessionToken;
	private String expiration;
	private int duration; 

	public JSONObject getMemreasTVM() {
		return memreasTVM;
	}

	public void setMemreasTVM(JSONObject memreasTVM) {
		this.memreasTVM = memreasTVM;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public String getSecretAccessKey() {
		return secretAccessKey;
	}

	public void setSecretAccessKey(String secretAccessKey) {
		this.secretAccessKey = secretAccessKey;
	}

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public String getExpiration() {
		return expiration;
	}

	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
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

		// Log.i("CommonHandler " + i, "endElement =>" + localName + " =>"
		// + curValue);
		if (localName.equals("memreas_tvm")) {
			try {
				this.memreasTVM = new JSONObject(curValue);
				this.accessKeyId = this.memreasTVM.getString("AccessKeyId");
				this.secretAccessKey = this.memreasTVM.getString("SecretAccessKey");
				this.sessionToken = this.memreasTVM.getString("SessionToken");
				this.expiration = this.memreasTVM.getString("Expiration");
				this.duration = Integer.valueOf(this.memreasTVM.getString("Duration"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new SAXException(e.getMessage());
			}
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