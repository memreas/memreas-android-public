
package com.memreas.sax.handler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class UserDetailHandler extends DefaultHandler {

	Boolean curElement;// ,url_web=false;
	String curValue;
	int i = 0;
	private String status;
	private String userName;
	private String gender;
	private String email;
	private String alternalEmail;
	private String dateOfBirth;
	private String profilePicture;
	private String message;
	private String planType;
	private String planName;

	public String getMessage() {
		return message;
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public String getStatus() {
		return status;
	}

	public String getUserName() {
		return userName;
	}

	public String getGender() {
		return gender;
	}

	public String getEmail() {
		return email;
	}

	public String getAlternalEmail() {
		return alternalEmail;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public UserDetailHandler() {
		status = null;
	}

	public String getPlanType() {
		return planType;
	}

	public String getPlanName() {
		return planName;
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
		if (localName.equals("status")) {
			this.status = curValue;
		} else if (localName.equals("email")) {
			this.email = curValue;
		} else if (localName.equals("profile")) {
			this.profilePicture = CommonHandler.parseMemJSON(curValue);
		} else if (localName.equals("username")) {
			this.userName = curValue;
		} else if (localName.equals("alternate_email")) {
			this.alternalEmail = curValue;
		} else if (localName.equals("gender")) {
			this.gender = curValue;
		} else if (localName.equals("dob")) {
			this.dateOfBirth = curValue;
		} else if (localName.equals("message")) {
			this.message = curValue;
		} else if (localName.equals("plan")) {
			this.planType = curValue;
		} else if (localName.equals("account_type")) {
			this.planName = curValue;
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