
package com.memreas.stsfail;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.memreas.base.SessionManager;

import java.util.Iterator;
import java.util.List;

/**
 * This class is used to get clients to the various AWS services. Before
 * accessing a client the credentials should be checked to ensure validity.
 */
public class AmazonClientManagerSTS {
	private static AmazonClientManagerSTS instance;
	private AmazonS3Client s3 = null;
	private boolean isInitialized = false;
	private Context context;

	protected AmazonClientManagerSTS() {
		java.util.logging.Logger.getLogger("com.amazonaws").setLevel(
				java.util.logging.Level.OFF);
		new InitAmazonClientManagerSTSAsyncTask()
				.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public static AmazonClientManagerSTS getInstance(Context _context) {
		if (instance == null) {
			initInstance();
			instance.context = _context;
		}
		if (instance.isInitialized) {
			return instance;
		}
		return null;
	}

	public static void initInstance() {
		if (instance == null) {
			instance = new AmazonClientManagerSTS();
		}
	}

	private class InitAmazonClientManagerSTSAsyncTask extends
			AsyncTask<String, Void, String> {

		public InitAmazonClientManagerSTSAsyncTask() {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... arg0) {

			BasicSessionCredentials basicSessionCredentials = new BasicSessionCredentials(
					SessionManager.getInstance().getAccessKeyId(),
					SessionManager.getInstance().getSecretAccessKey(),
					SessionManager.getInstance().getSessionToken());

			// The following will be part of your less trusted code. You provide
			// temporary security
			// credentials so it can send authenticated requests to Amazon S3.
			// Create Amazon S3 client by passing in the basicSessionCredentials
			// object.
			try {
				s3 = new AmazonS3Client(basicSessionCredentials);
			} catch (Exception e) {
				e.printStackTrace();
				return "failure";
			}
			return "success";

		}

		@Override
		protected void onPostExecute(String result) {

			if (result.toString().equalsIgnoreCase("success")) {
				// set instance...
				List<Bucket> buckets=null;
				try {
					// Test. For example, get object keys in a bucket.
					buckets = s3.listBuckets();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Iterator<Bucket> iterator = buckets.iterator();
					while (iterator.hasNext()) {
						Bucket bucket = iterator.next();
						Log.d("bucket.getName()-->", bucket.getName());
						Log.d("bucket.getOwner()-->", bucket.getOwner()
								.toString());
						Log.d("bucket.getCreationDate()-->", bucket
								.getCreationDate().toString());

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

}
