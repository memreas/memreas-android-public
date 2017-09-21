
package com.memreas.sax.handler;

import android.os.AsyncTask;
import android.util.Log;

import com.memreas.base.Common;
import com.memreas.gallery.CopyrightManager;

import org.json.JSONArray;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class CopyrightManagerParser extends AsyncTask<Void, Void, String> {

    protected static final String TAG = CopyrightManagerParser.class.getName();

    public static String resultCopyrightManagerParser = "failure";

    private CopyrightManagerHandler handler;

    private CopyrightManager copyrightManager;

    public CopyrightManagerParser() {
        this.handler = new CopyrightManagerHandler();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... arg0) {
        String xmlData = XMLGenerator.generateMediaIdXML();

        SaxParser.parse(Common.SERVER_URL + Common.FETCH_COPYRIGHT_BATCH_ACTION,
                xmlData, handler, "xml");

        Log.e(TAG, "xmldata--->" + xmlData);

        //
        // Set CopyrightManager class vars here...
        //
        copyrightManager = CopyrightManager.getInstance();
        copyrightManager.setCopyrightBatchJsonArray(handler.getCopyRightBatchJsonArray());
        copyrightManager.setFetching(false);

        return handler.status;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //do nothing - media_id is set
    }

    private class CopyrightManagerHandler extends DefaultHandler {
        Boolean curElement;
        String curValue;
        int i = 0;
        private String status;
        private String message;
        private int remaining;
        private JSONArray copyrightBatchJsonArray;


        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public int getRemaining() {
            return remaining;
        }

        public JSONArray getCopyRightBatchJsonArray() {
            return copyrightBatchJsonArray;
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

            if (localName.equals("status")) {
                status = curValue;
            } else if (localName.equals("message")) {
                message = curValue;
            } else if (localName.equals("remaining")) {
                remaining = Integer.parseInt(curValue);
            } else if (localName.equals("copyright_batch")) {
                copyrightBatchJsonArray = CommonHandler.parseMemJSONArray(curValue);
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

    }

}
