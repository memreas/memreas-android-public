
package com.memreas.queue;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transfermanager.Download;
import com.amazonaws.mobileconnectors.s3.transfermanager.PauseResult;
import com.amazonaws.mobileconnectors.s3.transfermanager.PauseStatus;
import com.amazonaws.mobileconnectors.s3.transfermanager.PersistableDownload;
import com.amazonaws.mobileconnectors.s3.transfermanager.PersistableTransfer;
import com.amazonaws.mobileconnectors.s3.transfermanager.PersistableUpload;
import com.amazonaws.mobileconnectors.s3.transfermanager.Transfer.TransferState;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.memreas.aws.AmazonClientManager;
import com.memreas.base.Common;
import com.memreas.base.SessionManager;
import com.memreas.gallery.GalleryBean.GalleryType;
import com.memreas.gallery.MediaIdManager;
import com.memreas.queue.MemreasTransferModel.MemreasQueueStatus;
import com.memreas.queue.MemreasTransferModel.Type;
import com.memreas.sax.handler.AddMediaEventHandler;
import com.memreas.sax.handler.SaxParser;
import com.memreas.sax.handler.XMLGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueService extends IntentService {

	protected static final String TAG = QueueService.class.getName();
	private LinkedBlockingQueue<TransferRunnable> inTransitQueue;
	private final IBinder myBinder = new QueueBinder();
	private ExecutorService executor;
	private boolean transferring = false;
	private boolean queueProcessed = false;

	public QueueService() {
		super("com.memreas.queue.queueservice");
		inTransitQueue = new LinkedBlockingQueue<TransferRunnable>(
				Common.CONCURRENT_TRANSFERS);
		executor = Executors.newCachedThreadPool();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			TransferRunnable transferRunnable;
			MemreasTransferModel transferModel;

			// Initialize Amazon...
			AmazonClientManager.getInstance().getTransferManager();

			// Set status for transferring.
			transferring = true;

			// Fetch iterator to fill up queue to capacity...
			while ((QueueAdapter.getInstance().getSelectedTransferModelQueue()
					.size() > 0)
					&& (!queueProcessed)) {
				transferModel = QueueAdapter.getInstance().getNextTransfer();
				if (transferModel != null) {
					// If Paused sleep until resumed, cleared, or cancelled...
					while (transferModel.getMemreasQueueStatus() == MemreasQueueStatus.PAUSED) {
						SystemClock.sleep(1000);
					}
					transferRunnable = new TransferRunnable(transferModel);
					try {
						inTransitQueue.put(transferRunnable);
						transferModel
								.setMemreasQueueStatus(MemreasTransferModel.MemreasQueueStatus.IN_PROGRESS);
						//Log.d(getClass().getName(),
						//		" about to transferModel name, type, mime_type-->" +
						//				transferModel.getName() + " "
						//				+ transferModel.getMedia().getMediaType()
						//				+ " "
						//				+ transferModel.getMedia().getMimeType());

						// Start thread here ... once queue is full
						executor.submit(transferRunnable);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						transferModel
								.setMemreasQueueStatus(MemreasTransferModel.MemreasQueueStatus.ERROR);
						break;
					}
				} else {
					// else queue is done move to completed...
					queueProcessed = true;
					//Intent move_intent = new Intent("queue-progress");
					//move_intent.putExtra("transferModelName", "QueueService");
					//move_intent.putExtra("status", MemreasQueueStatus.MOVE_TO_COMPLETED);
					//LocalBroadcastManager.getInstance(getApplicationContext())
					//		.sendBroadcast(move_intent);
					//Log.d(TAG, "MOVE_TO_COMPLETED message sent ... tabs should be completed");

				}
			} // end while transfer model queue size > 0

		} catch (Exception e) {
			// Unknow error - error out media
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "an error occurred - try restarting memreas", Toast.LENGTH_LONG);
			try {
				QueueAdapter.getInstance().getSelectedTransferModelQueue().clear();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	} // end protected void onHandleIntent(Intent intent)

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		super.onBind(intent);
		return myBinder;
	}

	public LinkedBlockingQueue<TransferRunnable> getInTransitQueue() {
		return inTransitQueue;
	}

	public boolean isTransferring() {
		return transferring;
	}

	public class QueueBinder extends Binder {
		public QueueService getService() {
			return QueueService.this;
		}
	}

	public class TransferRunnable implements Runnable {
		private String name;
		private TransferManager transferManager;
		private MemreasTransferModel transferModel;
		private Upload upload;
		private Download download;
		private PersistableUpload persistableUpload;
		private PersistableDownload persistableDownload;
		private String s3Path;
		private File file;

		public TransferRunnable(MemreasTransferModel transferModel) {
			super();
			this.transferManager = AmazonClientManager.getInstance()
					.getTransferManager();
			this.transferModel = transferModel;
			this.name = transferModel.getMedia().getMediaName();
		}

		protected void sendIntentMessage(String name, MemreasQueueStatus status) {
			Intent intent = new Intent("queue-progress");
			intent.putExtra("transferModelName", name);
			if (status != null) {
				intent.putExtra("status", status.toString());
			}
			LocalBroadcastManager.getInstance(getApplicationContext())
					.sendBroadcast(intent);
		}

		@Override
		public void run() {
			// Moves the current Thread into the background
			android.os.Process
					.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

			/*
			 * TODO AWS Transfers here...
			 */
			if (transferModel.getType() == Type.UPLOAD) {
				uploadRunner();
			} else if (transferModel.getType() == Type.DOWNLOAD) {
				downloadRunner();
			}

			if (inTransitQueue.size() == 0) {
				transferring = false;
				// Send the message to update progress
				sendIntentMessage("QueueService", MemreasQueueStatus.MOVE_TO_COMPLETED);
			}
		}

		private void uploadRunner() {
			try {
				Log.d(getClass().getName() + " uploadRunner()",
						"starting runner code for media type ->"
								+ transferModel.getMedia().getType().toString());

				if (!transferModel.isServerMedia()) {

					// Construct S3 path from UID + file name
					if ((transferModel.getMedia().getMediaId() == null)
							|| ((transferModel.getMedia().getMediaId()
							.equals("")))) {
						transferModel.getMedia().setMediaId(MediaIdManager.getInstance().fetchNextMediaId());
					}
					s3Path = SessionManager.getInstance().getUser_id() + "/"
							+ transferModel.getMedia().getMediaId() + "/"
							+ transferModel.getName();

					// Construct upload file from path
					file = new File(transferModel.getMedia()
							.getLocalMediaPath());

					// Request server-side encryption.
					PutObjectRequest putRequest = new PutObjectRequest(
							Common.BUCKET_NAME, s3Path, file);
					ObjectMetadata objectMetadata = new ObjectMetadata();
					objectMetadata
							.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
					putRequest.setMetadata(objectMetadata);

					upload = transferManager.upload(putRequest);
					while (!upload.isDone()) {
						SystemClock.sleep(100);
						double transferredPercent = upload.getProgress()
								.getPercentTransferred();
						int progress = (int) Math.round(transferredPercent);
						transferModel.setProgress(progress);

						// Send the message to update progress
						sendIntentMessage(this.name,
								MemreasQueueStatus.IN_PROGRESS);
						//Log.d(getClass().getName()
						//				+ "  upload.getProgress(), upload.getState() --->",
						//		upload.getProgress() + " " + upload.getState());

						/*
						 * AWS Cancel here...
						 */
						if (transferModel.getMemreasQueueStatus() == MemreasQueueStatus.CANCELED) {
							synchronized (inTransitQueue) {
								upload.abort();
							}
							break;
						}

						/*
						 * AWS - Pause / Resume here...
						 */
						if (transferModel.getMemreasQueueStatus() == MemreasQueueStatus.PAUSED) {
							PauseResult<PersistableUpload> pauseResult = upload
									.tryPause(true);
							if (pauseResult.getPauseStatus() == PauseStatus.SUCCESS) {
								transferModel
										.setMemreasQueueStatus(MemreasQueueStatus.PAUSED_AWS_SUCCESS);
								transferModel
										.setPersistableTransfer((PersistableTransfer) pauseResult
												.getInfoToResume());

							}
							while (transferModel.getMemreasQueueStatus() == MemreasQueueStatus.PAUSED_AWS_SUCCESS) {
								SystemClock.sleep(1000);
								if (transferModel.getMemreasQueueStatus() == MemreasQueueStatus.RESUMED) {
									upload = transferManager
											.resumeUpload((PersistableUpload) transferModel
													.getPersistableTransfer());
									transferModel
											.setMemreasQueueStatus(MemreasQueueStatus.RESUMED_AWS_SUCCESS);
									break;
								}
							}
						}

						if (upload.isDone()) {
							transferModel
									.setMemreasQueueStatus(MemreasQueueStatus.TRANSFER_COMPLETED);
						}
					} // end while (!upload.isDone())
					/**
					 * For memreas tab set flag to not selected
					 */
					transferModel.getMedia().setSelectedForShare(false);
				} // end if (transferModel.getMedia().getType() ==
				// GalleryType.NOT_SYNC)
				else {
					// mediaId is set so the media is on the server - add to
					// memreas event...
					Log.d(getClass().getName() + " uploadRunner()",
							"inside else for server media...");
					transferModel
							.setMemreasQueueStatus(MemreasQueueStatus.TRANSFER_COMPLETED);
					s3Path = transferModel.getMedia().getMediaUrlS3Path();
				}

				/*
				 * Transfer Completed send message
				 */
				if (transferModel.getMemreasQueueStatus() == MemreasQueueStatus.TRANSFER_COMPLETED) {

					Log.d(getClass().getName() + " uploadRunner()",
							"about to call addmediaevent...");

					// Send a message to add the media via web service...
					JSONObject locationJSON = new JSONObject();
					String location = "";
					if ((transferModel.getMedia().getLatitude() != 0)
							&& (transferModel.getMedia().getLongitude() != 0)) {
						try {
							locationJSON.put("latitude", transferModel
									.getMedia().getLatitude());
							locationJSON.put("longitude", transferModel
									.getMedia().getLongitude());
							location = locationJSON.toString();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					String xmlData = XMLGenerator.addMediaToEvent(
							transferModel.getEventId(),
							transferModel.getMedia().getMediaId(),
							transferModel.isServerMedia(),
							s3Path,
							transferModel.getMedia().getMimeType(),
							transferModel.getMedia().getMediaName(),
							transferModel.getMedia().isProfilePic(),
							transferModel.getMedia().isRegistration(),
							location,
							transferModel.getMedia().getCopyright());
					AddMediaEventHandler handler = new AddMediaEventHandler();
					Log.i(TAG, "xmlData-->" + xmlData);
					SaxParser.parse(Common.SERVER_URL
									+ Common.ADD_MEDIA_EVENT_ACTION, xmlData, handler,
							"xml");

					// Got the response from the web service so move the
					// transfer
					// over to completed error or not...
					if (handler.getStatus().equalsIgnoreCase("success")) {
						transferModel.getMedia().setMediaId(
								handler.getMediaId());
						transferModel
								.setMemreasQueueStatus(MemreasQueueStatus.COMPLETED);
						if (!transferModel.isServerMedia()) {
							transferModel.setType(Type.SYNC);
							transferModel.getMedia().setType(GalleryType.SYNC);
						}

						//
						//If copyright delete local file
						//
						if ((transferModel.getMedia().getCopyright() != null) && (!transferModel.getMedia().getCopyright().equalsIgnoreCase(""))) {
							File videoFile = new File(transferModel.getMedia().getLocalMediaPath());
							videoFile.delete();
						}

						// Send the message to update progress
						sendIntentMessage(this.name,
								MemreasQueueStatus.COMPLETED);
						// move the model to completed...
						QueueAdapter.getInstance().removeQueueEntry(
								transferModel);
						inTransitQueue.remove(this);
						// Synchronize to avoid IllegalStateMonitorException...
						synchronized (inTransitQueue) {
							inTransitQueue.notify();
						}

					} else {
						throw new Exception(getClass().getName()
								+ " ADD_MEDIA_EVENT_ACTION returned failure");
					}
				} else if (transferModel.getMemreasQueueStatus() == MemreasQueueStatus.CANCELED) {
					// Clear button pressed ...
					// Send the message to update progress
					sendIntentMessage(this.name, MemreasQueueStatus.CANCELED);
					// move the model to completed with canceled status...
					QueueAdapter.getInstance().removeQueueEntry(transferModel);
					inTransitQueue.remove(this);
					// Synchronize to avoid IllegalStateMonitorException...
					synchronized (inTransitQueue) {
						inTransitQueue.notify();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				transferModel.setMemreasQueueStatus(MemreasQueueStatus.ERROR);
				// Send the message to update progress and move the transfer...
				sendIntentMessage(this.name, MemreasQueueStatus.ERROR);
				QueueAdapter.getInstance().removeQueueEntry(transferModel);
				// remove the exception
				inTransitQueue.remove(this);
				// Synchronize to avoid IllegalStateMonitorException...
				synchronized (inTransitQueue) {
					inTransitQueue.notify();
				}
			}
		}

		private void downloadRunner() {

			try {
				File storageDir = null, file = null;
				final String name;

				if (isExternalStorageReadable() && isExternalStorageWritable()) {
					if (transferModel.getMedia().getMediaType()
							.equalsIgnoreCase("image")) {
						storageDir = Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
						s3Path = transferModel.getMedia().getMediaUrlS3Path();
						name = transferModel.getMedia().getMediaName();
					} else if (transferModel.getMedia().getMediaType()
							.equalsIgnoreCase("video")) {
						storageDir = Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
						s3Path = transferModel.getMedia()
								.getMediaUrlWebS3Path();
						name = s3Path.substring(s3Path.lastIndexOf('/') + 1,
								s3Path.length());
					} else {
						throw new Exception(getClass().getName()
								+ "media type is incorrect");
					}
				} else {
					throw new Exception(getClass().getName()
							+ "external storage is not accessible");
				}

				// Create a permanent file.
				file = new File(storageDir.getAbsolutePath(), name);

				Log.i("Download s3Path", s3Path);
				Log.i("Download file.getPath", file.getPath());
				// Start the download
				download = transferManager.download(Common.BUCKET_NAME, s3Path,
						file);
				while (!download.isDone()) {
					SystemClock.sleep(100);
					double transferredPercent = download.getProgress()
							.getPercentTransferred();
					int progress = (int) Math.round(transferredPercent);
					transferModel.setProgress(progress);

					// Send the message to update progress
					sendIntentMessage(this.name, MemreasQueueStatus.IN_PROGRESS);

					/*
					 * AWS Cancel here...
					 */
					if (transferModel.getMemreasQueueStatus() == MemreasQueueStatus.CANCELED) {
						synchronized (inTransitQueue) {
							download.abort();
						}
						break;
					}

					/*
					 * AWS Pause / Resume here...
					 */
					if (transferModel.getMemreasQueueStatus() == MemreasQueueStatus.PAUSED) {
						transferModel
								.setPersistableTransfer((PersistableTransfer) download
										.pause());
						transferModel
								.setMemreasQueueStatus(MemreasQueueStatus.PAUSED_AWS_SUCCESS);
						while (transferModel.getMemreasQueueStatus() == MemreasQueueStatus.PAUSED_AWS_SUCCESS) {
							SystemClock.sleep(1000);
							if (transferModel.getMemreasQueueStatus() == MemreasQueueStatus.RESUMED) {
								download = transferManager
										.resumeDownload((PersistableDownload) transferModel
												.getPersistableTransfer());
								transferModel
										.setMemreasQueueStatus(MemreasQueueStatus.RESUMED_AWS_SUCCESS);
								continue;
							}
						}
					}
				} // end while

				if (download.getState().equals(TransferState.Completed)) {

					// Use scan intent to store file in the media database...
					Intent mediaScanIntent = new Intent(
							Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
					Uri contentUri = Uri.fromFile(file);
					mediaScanIntent.setDataAndType(contentUri, transferModel
							.getMedia().getMimeType());
					mediaScanIntent.putExtra(MediaStore.EXTRA_OUTPUT, name);
					getApplication().sendBroadcast(mediaScanIntent);

					// Check the file status
					if (!file.isFile()) {
						throw new Exception(getClass().getName()
								+ "file was not created");
					}
					transferModel
							.setMemreasQueueStatus(MemreasQueueStatus.COMPLETED);
					transferModel.setType(Type.SYNC);
					transferModel.getMedia().setType(GalleryType.SYNC);
					// Send the message to update progress
					sendIntentMessage(this.name, MemreasQueueStatus.COMPLETED);
					QueueAdapter.getInstance().removeQueueEntry(transferModel);
					inTransitQueue.remove(this);
					synchronized (inTransitQueue) {
						inTransitQueue.notify();
					}
					//Log.i(getClass().getName()
					//				+ "--->file.getAbsolutePath()--->",
					//		file.getAbsolutePath());
					//Log.i(getClass().getName() + "--->file.getName()--->",
					//		file.getName());
				} else if (transferModel.getMemreasQueueStatus() == MemreasQueueStatus.CANCELED) {
					// Clear button pressed ...
					// move the model to completed with canceled status...
					sendIntentMessage(this.name, MemreasQueueStatus.CANCELED);
					// QueueAdapter.getInstance().removeQueueEntry(
					// transferModel);
					inTransitQueue.remove(this);
					// Synchronize to avoid IllegalStateMonitorException...
					synchronized (inTransitQueue) {
						inTransitQueue.notify();
					}
				} else {
					throw new Exception(getClass().getName()
							+ "download failed to complete");
				}

			} catch (Exception e) {
				// remove the exception
				e.printStackTrace();
				transferModel.setMemreasQueueStatus(MemreasQueueStatus.ERROR);
				QueueAdapter.getInstance().removeQueueEntry(transferModel);
				inTransitQueue.remove(this);
				synchronized (inTransitQueue) {
					inTransitQueue.notify();
				}
				// Send the message to update progress
				sendIntentMessage(this.name, MemreasQueueStatus.ERROR);
			}

		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public MemreasTransferModel getTransferModel() {
			return transferModel;
		}

		/* Checks if external storage is available for read and write */
		public boolean isExternalStorageWritable() {
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				return true;
			}
			return false;
		}

		/* Checks if external storage is available to at least read */
		public boolean isExternalStorageReadable() {
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)
					|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				return true;
			}
			return false;
		}

	}
}
