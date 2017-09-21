
package com.memreas.gallery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.memreas.gallery.GalleryBean.GalleryType;
import com.memreas.util.ImageUtils;
import com.memreas.util.MemreasProgressDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.HashMap;

public class LoadLocalGalleryAsyncTask extends
		AsyncTask<Object, GalleryBean, String> {

	private static final String TAG = LoadLocalGalleryAsyncTask.class.getName();
	private ProgressDialog progressDialog;
	private Activity activity;
	private GalleryAdapter galleryAdapter;
	private boolean isOnline = false;

	public LoadLocalGalleryAsyncTask(Activity activity,
			GalleryAdapter galleryAdapter, boolean isOnline) {
		this.activity = activity;
		this.progressDialog = MemreasProgressDialog.getInstance(activity);
		this.progressDialog.setMessage("loading local media...");
		this.galleryAdapter = galleryAdapter;
		this.isOnline = isOnline;
	}

	@Override
	protected void onPreExecute() {
		this.progressDialog.setMessage("loading local media for gallery display...");
		this.progressDialog.show();
	}

	@Override
	protected String doInBackground(Object... params) {

		try {
			// Clear cache before fetching...
			ImageLoader.getInstance().clearMemoryCache();
			// Fetch external images
			String[] image_external_projection = { MediaStore.Images.Media._ID,
					MediaStore.Images.Media.DATA,
					MediaStore.MediaColumns.MIME_TYPE,
					MediaStore.Images.Media.DATE_MODIFIED };

			Cursor image_external_cursor = activity.getContentResolver().query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					image_external_projection, null, null,
					MediaStore.Images.Media.DATE_MODIFIED + " DESC");

			// Fetch internal images
			String[] image_internal_projection = { MediaStore.Images.Media._ID,
					MediaStore.Images.Media.DATA,
					MediaStore.MediaColumns.MIME_TYPE,
					MediaStore.Images.Media.DATE_MODIFIED };

			Cursor image_internal_cursor = activity.getContentResolver().query(
					MediaStore.Images.Media.INTERNAL_CONTENT_URI,
					image_internal_projection, null, null,
					MediaStore.Images.Media.DATE_MODIFIED + " DESC");

			// Fetch external videos
			String[] video_external_projection = { MediaStore.Video.Media._ID,
					MediaStore.Video.Media.DATA,
					MediaStore.MediaColumns.MIME_TYPE,
					MediaStore.Video.VideoColumns.DURATION,
					MediaStore.Video.Media.DATE_MODIFIED };

			Cursor video_external_cursor = activity.getContentResolver().query(
					MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
					video_external_projection, null, null,
					MediaStore.Video.Media.DATE_MODIFIED + " DESC");

			// Fetch internal videos
			String[] video_internal_projection = { MediaStore.Video.Media._ID,
					MediaStore.Video.Media.DATA,
					MediaStore.MediaColumns.MIME_TYPE,
					MediaStore.Video.VideoColumns.DURATION,
					MediaStore.Video.Media.DATE_MODIFIED };

			Cursor video_internal_cursor = activity.getContentResolver().query(
					MediaStore.Video.Media.INTERNAL_CONTENT_URI,
					video_internal_projection, null, null,
					MediaStore.Video.Media.DATE_MODIFIED + " DESC");

			Cursor mCursor[] = new Cursor[4];
			mCursor[0] = image_external_cursor;
			mCursor[1] = image_internal_cursor;
			mCursor[2] = video_external_cursor;
			mCursor[3] = video_internal_cursor;
			Cursor mergedCursor = new MergeCursor(mCursor);

			int size = mergedCursor.getCount();
			HashMap<String, Integer> hm = this.galleryAdapter
					.getHashmapGalleryKeys();

			while (mergedCursor.moveToNext()) {
				try {
					GalleryBean galleryBean = new GalleryBean(
							GalleryType.NOT_SYNC);

					String mediaPath;
					String mime_type;
					String mini_thumb_magic = null;
					Bitmap bitmap;
					String mediaDate;

					mime_type = mergedCursor.getString(mergedCursor
							.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
					if (mime_type.contains("video")) {
						/*
						 * Video section ...
						 */
						mediaPath = mergedCursor.getString(mergedCursor
								.getColumnIndex(MediaStore.Video.Media.DATA));

						String mediaName = ImageUtils.getInstance()
								.getImageNameFromPath(mediaPath);

						/*
						 * Caching - this code check the list to see if the
						 * media is there already if so skip...
						 */
						if ((hm != null) && (hm.get(mediaName) != null)) {
							break; // this item is already stored
						}

						// Fetch existing thumbnail
						bitmap = MediaStore.Video.Thumbnails
								.getThumbnail(
										activity.getContentResolver(),
										mergedCursor.getLong(mergedCursor
												.getColumnIndexOrThrow(MediaStore.Video.Thumbnails._ID)),
										MediaStore.Images.Thumbnails.MICRO_KIND,
										ImageUtils.getInstance()
												.getBitmapOption());

						if ((bitmap == null) && (mediaPath.contains("memreas"))) {
							//
							// Empty media file for app - delete?
							//
							Log.e(TAG, "video bitmap is null.......");
							//File emptyFile = new File(mediaPath);
							//emptyFile.delete();
							continue;
						}

						long duration = mergedCursor.getLong(2);
						galleryBean.setVideoDuration(duration);

						mediaDate = mergedCursor
								.getString(mergedCursor
										.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));
						galleryBean
								.setMediaDate(Long.valueOf(mediaDate) * 1000); // ms
						galleryBean.setMediaUrl(mediaPath);
						galleryBean.setLocalMediaPath(mediaPath);
						galleryBean.setMediaName(mediaName);
						// Prefix key for sync matching...
						String mediaNamePrefix = mediaName.substring(0,
								mediaName.lastIndexOf('.'));
						galleryBean.setMediaNamePrefix(mediaNamePrefix);
						galleryBean.setMediaThumbBitmap(bitmap);
						galleryBean.setMediaType("video");
						galleryBean.setMediaId("");
						galleryBean.setMimeType(mime_type);
						publishProgress(galleryBean);
					} else {
						/*
						 * Image section ...
						 */
						mediaPath = mergedCursor.getString(mergedCursor
								.getColumnIndex(MediaStore.Images.Media.DATA));
						if (mediaPath != null && new File(mediaPath).exists()) {
							String mediaName = ImageUtils.getInstance()
									.getImageNameFromPath(mediaPath);

							/*
							 * Caching - this code check the list to see if the
							 * media is there already if so skip...
							 */
							String mediaNamePrefix = mediaName.substring(0,
									mediaName.lastIndexOf('.'));
							if ((hm != null)
									&& (hm.get(mediaNamePrefix) != null)) {
								continue; // this item is already stored
							}

							bitmap = MediaStore.Images.Thumbnails
									.getThumbnail(
											activity.getContentResolver(),
											mergedCursor.getInt(mergedCursor
													.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID)),
											MediaStore.Images.Thumbnails.MICRO_KIND,
											ImageUtils.getInstance()
													.getBitmapOption());


							if ((bitmap == null) && (mediaPath.contains("memreas"))) {
								//
								// Empty media file for app - delete?
								//
								Log.e(TAG, "video bitmap is null.......");
								//File emptyFile = new File(mediaPath);
								//emptyFile.delete();
								continue;
							}


							galleryBean.setMediaThumbBitmap(bitmap);

							mediaDate = mergedCursor
									.getString(mergedCursor
											.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));

							galleryBean
									.setMediaDate(Long.valueOf(mediaDate) * 1000);//ms
							galleryBean.setMediaUrl(mediaPath);
							galleryBean.setLocalMediaPath(mediaPath);
							galleryBean.setMediaName(mediaName);
							// Prefix key for sync matching...
							mediaNamePrefix = mediaName.substring(0,
									mediaName.lastIndexOf('.'));
							galleryBean.setMediaNamePrefix(mediaNamePrefix);
							galleryBean.setMediaType("image");
							galleryBean.setMediaId("");
							galleryBean.setMimeType(mime_type);

							galleryBean.setLocalMediaPath(mediaPath);
							ExifInterface exif = new ExifInterface(mediaPath);
							String latitude = exif
									.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
							String latitudeRef = exif
									.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
							String longitude = exif
									.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
							String longitudeRef = exif
									.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
							if (latitude != null && longitude != null
									&& latitudeRef != null
									&& longitudeRef != null) {
								try {
									// Get Lat Lng from exif with Android
									// API
									float[] arrLatLng = new float[2];
									exif.getLatLong(arrLatLng);
									galleryBean.setLocation(
											Double.valueOf(arrLatLng[0]),
											Double.valueOf(arrLatLng[1]));
								} catch (Exception e) {
									galleryBean.setLocation(0, 0);
								}
							}
							// Moving non UI related tasks to here

							publishProgress(galleryBean);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.i("LoadLocalGalleryAsyncTask doInBackground exception ",
							e.toString());
				}
			} // end while

			image_external_cursor.close();
			image_internal_cursor.close();
			video_external_cursor.close();
			video_internal_cursor.close();
			mergedCursor.close();

			return null;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "Done";
	}

	@Override
	protected void onProgressUpdate(GalleryBean... values) {

		super.onProgressUpdate(values);

		GalleryBean galleryBean = values[0];
		/*
		this.progressDialog.setMessage("loading local media - "
				+ galleryBean.getMediaName());
		this.progressDialog.show();
		*/

		if (galleryAdapter instanceof GalleryAdapter) {
			galleryAdapter.addGalleryBean(galleryBean);

			// Set the hashmap for lookup later
			HashMap<String, Integer> hm = this.galleryAdapter
					.getHashmapGalleryKeys();
			int index = galleryAdapter.getGalleryImageList().indexOf(
					galleryBean);
			// Store filename without the extension if not in hm already...
			if (hm.get(galleryBean.getMediaNamePrefix()) == null) {
				hm.put(galleryBean.getMediaNamePrefix(), Integer.valueOf(index));
			}
			this.galleryAdapter.setHashmapGalleryKeys(hm);
		}
	}

	@Override
	protected void onPostExecute(String result) {

		super.onPostExecute(result);
		// run loadMemreasGalleryAsynTask after
		if (this.isOnline) {
			new LoadMemreasGalleryAsyncTask(this.activity, this.galleryAdapter)
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		}

	}

}
