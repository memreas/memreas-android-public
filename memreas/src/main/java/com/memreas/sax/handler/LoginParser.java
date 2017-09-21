
package com.memreas.sax.handler;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.memreas.aws.AmazonClientManager;
import com.memreas.base.Common;
import com.memreas.base.SessionManager;
import com.memreas.gallery.MediaIdManager;
import com.memreas.notifications.GcmRegistrationRunner;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.net.HttpCookie;

public class LoginParser extends AsyncTask<String, Void, String> {

    ProgressBar progressBar;
    Activity activity;
    public static String resultLoginParser = "failure";
    LoginHandler handler = new LoginHandler();

    public LoginParser(Activity activity, ProgressBar progressBar) {
        this.activity = activity;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... arg0) {

        String username = arg0[0];
        String password = arg0[1];
        String device_id = arg0[2];
        String loginXmlData = XMLGenerator.loginXML(username, password,
                device_id);

        if (device_id.length() == 0) {
            return "missing_device_id";
        }

        SaxParser.parse(Common.SERVER_URL + Common.LOGIN_ACTION, loginXmlData,
                handler, "xml");

        if (handler.getUserId() == null && handler.getMessage() == null) {
            return "network_error";
        }

        if ((handler.getStatus().toString()).equalsIgnoreCase("success")) {

			/*
             * Login Success
			 */
            SessionManager.getInstance().setSession_id(handler.getSID());
            SessionManager.getInstance().setDevice_id(device_id);
            SessionManager.getInstance().setUser_id(handler.getUserId());
            SessionManager.getInstance().setUser_name(username);
            SessionManager.getInstance().setCookieCloudFrontPolicy(handler.getCloudFrontPolicy());
            SessionManager.getInstance().setCookieCloudFrontSignature(handler.getCloudFrontSignature());
            SessionManager.getInstance().setCookieCloudFrontKeyPairId(handler.getCloudFrontKeyPairId());

			/*
             * If device_token is empty then register device...
			 */
            GcmRegistrationRunner gcmRunner = null;
            if (handler.getDevice_token().length() == 0) {
                gcmRunner = GcmRegistrationRunner.getInstance(activity);
                gcmRunner.getGcmRegistrationId(handler.getUserId(), device_id);
            } else {
                SessionManager.getInstance().setDevice_token(
                        handler.getDevice_token());
            }

			/*
             * Login Success - fetch user details
			 */
            String userDetailsXmlData = XMLGenerator
                    .getUserDetails(SessionManager.getInstance().getUser_id());

            Log.i("LoginParser user details XML DATA", userDetailsXmlData);
            UserDetailHandler userDetailHandler = new UserDetailHandler();
            SaxParser.parse(Common.SERVER_URL + Common.GET_USERDETAILS,
                    userDetailsXmlData, userDetailHandler, "xml");

            SessionManager.getInstance().setUser_email(
                    userDetailHandler.getEmail());
            SessionManager.getInstance().setUser_profile_picture(
                    userDetailHandler.getProfilePicture());
            SessionManager.getInstance().setUser_plan_type(
                    userDetailHandler.getPlanType());
            SessionManager.getInstance().setUser_plan_name(
                    userDetailHandler.getPlanName());

            /*
             * Fetch a batch of media Ids
             */
            MediaIdManager.getInstance();

        }
        return handler.getStatus();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result.toString().equalsIgnoreCase("success")) {
            // Fetch Cognito
            AmazonClientManager.getInstance().getTransferManager();
            new NotificationParser(activity, progressBar).execute();
            progressBar.setVisibility(View.GONE);

        } else if (handler.getStatus() != null) {
            // ((LoginActivity) activity).enableLoginButton();
            Toast.makeText(activity, handler.getMessage(), Toast.LENGTH_LONG)
                    .show();
            progressBar.setVisibility(View.GONE);
        } else if (result.toString().equalsIgnoreCase("network_error")) {
            // ((LoginActivity) activity).enableLoginButton();
            Toast.makeText(activity, "network error - check connection",
                    Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        } else if (result.toString().equalsIgnoreCase("missing_device_token")) {
            // ((LoginActivity) activity).enableLoginButton();
            Toast.makeText(activity, "could not obtain device token...",
                    Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        } else {
            // ((LoginActivity) activity).enableLoginButton();
            Toast.makeText(activity, "username or password invalid",
                    Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private class LoginHandler extends DefaultHandler {

        Boolean curElement;
        String curValue;
        int i = 0;
        private String SID;
        private String device_token = "";
        private String user_id;
        private String message;
        private String status;
        private HttpCookie cookieCloudFrontPolicy;
        private HttpCookie cookieCloudFrontSignature;
        private HttpCookie cookieCloudFrontKeyPairId;


        public String getMessage() {
            return message;
        }

        public String getSID() {
            return SID;
        }

        public String getDevice_token() {
            return device_token;
        }

        public String getUserId() {
            return user_id;
        }

        public String getStatus() {
            return status;
        }

        public HttpCookie getCloudFrontPolicy() {
            return cookieCloudFrontPolicy;
        }

        public HttpCookie getCloudFrontSignature() {
            return cookieCloudFrontSignature;
        }

        public HttpCookie getCloudFrontKeyPairId() {
            return cookieCloudFrontKeyPairId;
        }

        public LoginHandler() {
            SID = null;
            user_id = null;
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
            } else if (localName.equals("user_id")) {
                this.user_id = curValue;
            } else if (localName.equals("sid")) {
                this.SID = curValue;
            } else if (localName.equals("device_token")) {
                this.device_token = curValue;
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

        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return super.toString();
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            // TODO Auto-generated method stub
            e.printStackTrace();
            Log.d(getClass().getName() + " columnNumber error @: ",
                    String.valueOf(e.getColumnNumber()));
            Log.d(getClass().getName() + " lineNumber error @: ",
                    String.valueOf(e.getLineNumber()));
            Log.d(getClass().getName() + " getLocalizedMessage error @: ",
                    e.getLocalizedMessage());
            Log.d(getClass().getName() + " getMessage error @: ",
                    e.getMessage());
            super.error(e);
        }

        public void warning(SAXParseException e) throws SAXException {
            Log.d(getClass().getName() + " Warning error @: ", e.getMessage());
            System.out.println("Warning:" + e.getMessage());
        }

        public void fatalError(SAXParseException e) throws SAXException {
            Log.d(getClass().getName() + " Fatal error @: ", e.getMessage());
            System.out.println("Fatal error");
            throw new SAXException(e.getMessage());
        }
    } // end loginhandler

}
