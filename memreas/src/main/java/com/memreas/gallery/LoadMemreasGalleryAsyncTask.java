
package com.memreas.gallery;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.memreas.base.Common;
import com.memreas.gallery.GalleryBean.GalleryType;
import com.memreas.queue.SyncAdapter;
import com.memreas.sax.handler.ListAllMediaDetailHandler;
import com.memreas.sax.handler.SaxParser;
import com.memreas.sax.handler.XMLGenerator;
import com.memreas.util.MemreasProgressDialog;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class LoadMemreasGalleryAsyncTask extends
		AsyncTask<Object, GalleryBean, String> {

	private MemreasProgressDialog progressDialog;
	private Activity activity;
	private GalleryAdapter galleryAdapter;
	private Bitmap mThumbnail;

	public LoadMemreasGalleryAsyncTask(Activity activity,
			GalleryAdapter galleryAdapter) {
		this.activity = activity;
		this.galleryAdapter = galleryAdapter;
		this.progressDialog = MemreasProgressDialog.getInstance(activity);
		this.progressDialog.setMessage("loading memreas gallery media...");
	}

	@Override
	protected String doInBackground(Object... params) {
		
		String xmlData = XMLGenerator.loadAllMediaXML();
		ListAllMediaDetailHandler handler = new ListAllMediaDetailHandler();
		SaxParser.parse(Common.SERVER_URL + Common.LIST_ALL_MEDIA, xmlData,
				handler, "xml");

		try {
			if (handler.getStatus() != null
					&& handler.getStatus().equalsIgnoreCase("success")) {
				HashMap<String, Integer> hm = this.galleryAdapter
						.getHashmapGalleryKeys();

				for (GalleryBean item : handler.getGalleryItems()) {
					/*
					 * Caching - this code check the list to see if the server
					 * only media is there already
					 */
					// #1 - if media name is in hashmap let's check
					if (hm.get(item.getMediaNamePrefix()) != null) {
						// #2 we have a match fetch the gallery bean and
						// check if SERVER
						GalleryBean galleryBean = this.galleryAdapter
								.getGalleryImageList().get(
										hm.get(item.getMediaNamePrefix()));
						if ((galleryBean != null)
								&& (galleryBean.getType() == GalleryType.NOT_SYNC)) {
							// #3 if NOT_SYNC and matched to SERVER then
							// we have SYNC, store and continue
							galleryBean.setType(GalleryType.SYNC);
							galleryBean.setMediaId(item.getMediaId());
							galleryBean.setMimeType(item.getMimeType());
							galleryBean.setMediaUrlS3Path(item.getMediaUrlWebS3Path());
							galleryBean.setMediaThumbnailUrl(item
									.getMediaThumbnailUrl());
							galleryBean.setMediaThumbnailUrl1280x720(item
									.getMediaThumbnailUrl1280x720());
							galleryBean.setMediaThumbnailUrl448x306(item
									.getMediaThumbnailUrl448x306());
							galleryBean.setMediaThumbnailUrl79x80(item
									.getMediaThumbnailUrl79x80());
							galleryBean.setMediaThumbnailUrl98x78(item
									.getMediaThumbnailUrl98x78());
							continue; // this item is already stored
						}
					} else {
						// only publish if the item isn't in the hashmap
						item.setType(GalleryType.SERVER);
						publishProgress(item);
					}
				}
			} // end for...
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "Done";
	}

	@Override
	protected void onProgressUpdate(GalleryBean... values) {
		GalleryAdapter.getInstance().addGalleryBean(values[0]);
	}

	@Override
	protected void onPostExecute(String result) {

		super.onPostExecute(result);
		this.progressDialog.showWithDelay("sorting media...",1000);
		// Sort here and update...
		List<GalleryBean> gallery_beans = this.galleryAdapter
				.getGalleryImageList();
		Collections.sort(gallery_beans, new Comparator<GalleryBean>() {
			public int compare(GalleryBean bean1, GalleryBean bean2) {
				long value = bean2.getMediaDate() - bean1.getMediaDate();
				return value == 0 ? 0 : value > 0 ? 1 : -1;
			}
		});

		// refresh hashmap of keys here...
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		int i = 0;
		for (Iterator<GalleryBean> it = gallery_beans.iterator(); it.hasNext(); i++) {
			GalleryBean gallery_bean = it.next();
			hm.put(gallery_bean.getMediaNamePrefix(), i);
		}
		this.galleryAdapter.setHashmapGalleryKeys(hm);

		if (SyncAdapter.getInstance() != null) {
			galleryAdapter.notifyDataSetChanged();
			//SyncAdapter.getInstance().notifyDataSetChanged();
		}
		GalleryActivity.isAsyncLoadComplete = true;
	}

}
