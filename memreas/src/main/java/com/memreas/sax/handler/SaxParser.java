
package com.memreas.sax.handler;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SaxParser {
	private static final int TIME_OUT = 30000;
	private static final int SOCKET_TIME_OUT = 30000;
	private static MyErrorHandler myErrorHandler;

	public SaxParser() {
	}

	public static Reader fetchByteArrayFromInputStream(InputStream is)
			throws IOException {
		// printout the content
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String read = br.readLine();
		StringBuilder sb = new StringBuilder();

		while (read != null) {
			sb.append(read);
			read = br.readLine();
		}

		String xmlResponse = sb.toString().trim();
		Log.e("SaxParser.fetchByteArrayFromInputStream(is)-->", xmlResponse);

		//return new StringReader(xmlResponse.replace(" <?xml", "<?xml"));
		return new StringReader(xmlResponse);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void parse(String urlWithAction, String xmlRequest,
			ContentHandler handler, String paramName) {
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, TIME_OUT);
		HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIME_OUT);
		HttpPost httppost = new HttpPost(urlWithAction);
		HttpEntity entity = null;
		HttpResponse response = null;
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		try {
			URL sourceUrl = new URL(urlWithAction);
			if (!xmlRequest.equals(null)) {
				ArrayList nameValuePairs = new ArrayList();
				nameValuePairs
						.add(new BasicNameValuePair(paramName, xmlRequest));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						"UTF-8"));
				response = httpclient.execute(httppost);
				entity = response.getEntity();
			}
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			xr.setErrorHandler(myErrorHandler);
			xr.setContentHandler(handler);
			InputStream is = null;
			if (!xmlRequest.equals(null)) {
				is = entity.getContent();
				InputSource source = new InputSource(
						fetchByteArrayFromInputStream(is));
				source.setEncoding("UTF-8");
				Log.e("SAXParser - source ->","****" + source.toString() + "****");
				xr.parse(source);
			} else {
				is = sourceUrl.openStream();
				// InputStreamReader inputReader = new InputStreamReader(is,
				// "UTF-8");
				InputSource source = new InputSource(fetchByteArrayFromInputStream(is));
				source.setEncoding("UTF-8");
				xr.parse(source);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Basic Htttp call for an provided url and input parameters
	 * 
	 * @param urlWithAction
	 * @param xmlRequest
	 * @param paramName
	 * @return
	 */
	public static String parse(String urlWithAction, String xmlRequest,
			String paramName) {
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, TIME_OUT);
		HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIME_OUT);
		HttpPost httppost = new HttpPost(urlWithAction);
		HttpEntity entity = null;
		HttpResponse response = null;
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		try {
			if (!xmlRequest.equals(null)) {
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs
						.add(new BasicNameValuePair(paramName, xmlRequest));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						"UTF-8"));
				response = httpclient.execute(httppost);
				entity = response.getEntity();

			}
			InputStream is = entity.getContent();
			String xmlResponse = getStringFromInputStream(is);
			Log.e("MEAH - SAX PARSER xmlRequest", xmlRequest);
			Log.e("MEAH - SAX PARSER xmlResponse", xmlResponse);
			return xmlResponse;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Read data from input stream and return as string
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String getStringFromInputStream(InputStream is)
			throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String read = br.readLine();
		StringBuilder sb = new StringBuilder();
		while (read != null) {
			sb.append(read);
			read = br.readLine();
		}
		return sb.toString();
	}
	
	public static class MyErrorHandler implements ErrorHandler {
	    public void warning(SAXParseException exception) throws SAXException {
	        // Bring things to a crashing halt
	       Log.d(getClass().getName() + "**Parsing Warning**",
				   "  Line:    " +
						   exception.getLineNumber() + " \n" +
						   "  URI:     " +
						   exception.getSystemId() + " \n" +
						   "  Message: " +
						   exception.getMessage());
	        throw new SAXException("Warning encountered");
	    }
	    public void error(SAXParseException exception) throws SAXException {
	        // Bring things to a crashing halt
		       Log.d(getClass().getName() + "**Parsing Error**",
					   "  Line:    " +
							   exception.getLineNumber() + " \n" +
							   "  URI:     " +
							   exception.getSystemId() + " \n" +
							   "  Message: " +
							   exception.getMessage());
	        throw new SAXException("Error encountered");
	    }
	    public void fatalError(SAXParseException exception) throws SAXException {
	        // Bring things to a crashing halt
		       Log.d(getClass().getName() + "**Parsing Fatal Error**",
					   "  Line:    " +
							   exception.getLineNumber() + " \n" +
							   "  URI:     " +
							   exception.getSystemId() + " \n" +
							   "  Message: " +
							   exception.getMessage());
	        throw new SAXException("Fatal Error encountered");
	    }
	}
}
