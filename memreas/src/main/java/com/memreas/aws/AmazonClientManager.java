package com.memreas.aws;

import android.app.Activity;
import android.content.Context;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManagerConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.memreas.base.BaseActivity;
import com.memreas.base.Common;

import java.util.logging.Level;

/**
 * This class is used to get clients to the various AWS services. Before
 * accessing a client the credentials should be checked to ensure validity.
 */
public class AmazonClientManager {
	private static AmazonClientManager instance;
	private CognitoCredentialsProvider sCredProvider;
	private TransferManager sTransferManager;
	private Context context;

	protected AmazonClientManager() {
		java.util.logging.Logger.getLogger("com.amazonaws").setLevel(
				Level.WARNING);
	}

	public static AmazonClientManager getInstance() {
		if (instance == null) {
			instance = new AmazonClientManager();
		}
		return instance;
	}

	public CognitoCredentialsProvider getCredProvider(Context context) {
		if (sCredProvider == null) {
			sCredProvider = new CognitoCredentialsProvider(
					Common.AWS_ACCOUNT_ID, Common.COGNITO_POOL_ID,
					Common.COGNITO_ROLE_UNAUTH, Common.COGNITO_ROLE_AUTH,
					Regions.US_EAST_1);
		}
		return sCredProvider;
	}

	public TransferManager getTransferManager() {
		if (sTransferManager == null) {
			initTransferManager(BaseActivity.mApplication.getCurActivity());
		}
		return sTransferManager;
	}

	/*
	public void testSTSCredentials() {
		BasicSessionCredentials basicSessionCredentials = new BasicSessionCredentials(
				SessionManager.getInstance().getAccessKeyId(), SessionManager
						.getInstance().getSecretAccessKey(), SessionManager
						.getInstance().getSessionToken());

		// The following will be part of your less trusted code. You provide
		// temporary security
		// credentials so it can send authenticated requests to Amazon S3.
		// Create Amazon S3 client by passing in the basicSessionCredentials
		// object.
		AmazonS3Client s3 = null;
		try {
			s3 = new AmazonS3Client(basicSessionCredentials);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// Test. For example, get object keys in a bucket.
			List<Bucket> buckets = s3.listBuckets();
			Iterator<Bucket> iterator = buckets.iterator();
			while (iterator.hasNext()) {
				Bucket bucket = iterator.next();
				Log.d("bucket.getName()-->", bucket.getName());
				Log.d("bucket.getOwner()-->", bucket.getOwner().toString());
				Log.d("bucket.getCreationDate()-->", bucket.getCreationDate()
						.toString());

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/

	public TransferManager initTransferManager(Activity activity) {

		this.context = (Context) activity;

		if (sCredProvider == null) {
			getCredProvider(this.context);
		}
		if (sTransferManager == null) {
			// Configuration
			ClientConfiguration s3Config = new ClientConfiguration();
			// Sets the maximum number of allowed open HTTP connections.
			s3Config.setMaxConnections(5);
			// Sets the amount of time to wait (in milliseconds) for data
			// to be transferred over a connection.
			s3Config.setSocketTimeout(30000);

			TransferManagerConfiguration tmConfig = new TransferManagerConfiguration();
			// Sets the minimum part size for upload parts.
			tmConfig.setMinimumUploadPartSize(Common.MULTIPART_UPLOAD_SIZE);
			// Sets the size threshold in bytes for when to use multipart
			// uploads.
			tmConfig.setMultipartUploadThreshold(Common.MULTIPART_UPLOAD_THRESHOLD);

			// Set the config in the transfer manager
			sTransferManager = new TransferManager(new AmazonS3Client(
					getCredProvider(context)));
			sTransferManager.setConfiguration(tmConfig);
		}
		return sTransferManager;
	}

}
