
package com.memreas.queue;

import android.app.Activity;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.memreas.R;
import com.memreas.gallery.GalleryActivity;
import com.memreas.gallery.GalleryAdapter;
import com.memreas.gallery.GalleryBean;
import com.memreas.gallery.GalleryBean.GalleryType;
import com.memreas.gallery.MediaIdManager;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.Random;

public class SyncAdapter extends BaseAdapter {

	private static SyncAdapter instance;
	private static GalleryAdapter galleryAdapter;
	private Activity context;
	private int resource;
	private LayoutInflater mInflater;
	protected DisplayImageOptions optionsGallery;
	private DisplayImageOptions optionsStorage;
	private ImageLoadingListener animateFirstListener;
	private ImageLoader memreasImageLoader;
	private int syncCountSelected;

	protected SyncAdapter(Activity context, int resource) {
		this.setContext(context);
		this.resource = resource;
		mInflater = (LayoutInflater) context
				.getSystemService(context.LAYOUT_INFLATER_SERVICE);

		animateFirstListener = new AnimateFirstDisplayListener();
		memreasImageLoader = MemreasImageLoader.getInstance();
		optionsGallery = MemreasImageLoader.getDefaultDisplayImageOptions();
		optionsStorage = MemreasImageLoader
				.getDefaultDisplayImageOptionsStorage();
	}

	public static SyncAdapter getInstance(Activity context, int resource) {
		if (instance == null) {
			instance = new SyncAdapter(context, resource);
			galleryAdapter = GalleryAdapter.getInstance();
			// Add a listener for the data set
			galleryAdapter.registerDataSetObserver(new DataSetObserver() {
				@Override
				public void onChanged() {
					instance.clearSelectedMedia();
					instance.notifyDataSetChanged();
				}
			});
		}
		return instance;
	}

	public static SyncAdapter getInstance() {
		// Assumes adapter is set
		return instance;
	}

	@Override
	public int getCount() {
		return galleryAdapter.getGalleryImageList().size();
	}

	@Override
	public GalleryBean getItem(int position) {
		return galleryAdapter.getGalleryImageList().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private void setSelected(View v) {
		if (GalleryActivity.isAsyncLoadComplete) {
			ViewHolder holder = (ViewHolder) v.getTag();
			GalleryBean bean = galleryAdapter.getGalleryImageList().get(
					holder.position);
			if (bean.getType() != GalleryBean.GalleryType.SYNC) {
				if (!bean.isSelected()) {
					bean.setSelected(true);
					syncCountSelected++;
					holder.checkbox.setVisibility(View.VISIBLE);
				} else if (syncCountSelected > 0) {
					bean.setSelected(false);
					syncCountSelected--;
					holder.checkbox.setVisibility(View.GONE);
				}
			} else {
				Toast.makeText(context, "media is synced", Toast.LENGTH_LONG)
						.show();
			}
		} else {
			Toast.makeText(context, "loading gallery...", Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {

			holder = new ViewHolder();
			convertView = mInflater.inflate(this.resource, parent, false);

			holder.backview = (ImageView) convertView
					.findViewById(R.id.syncthumbback);
			holder.imageview = (ImageView) convertView
					.findViewById(R.id.syncthumbImage);
			holder.imageviewGone = (ImageView) convertView
					.findViewById(R.id.syncthumbImageGone);
			holder.videoImg = (ImageView) convertView
					.findViewById(R.id.syncvideoImg);
			holder.checkbox = (ImageView) convertView
					.findViewById(R.id.syncitemCheckBox);
			holder.imgSelect = (ImageView) convertView
					.findViewById(R.id.syncimgSelect);
			holder.imageview.setBackgroundColor(Color.BLACK);
			holder.imageview.setImageResource(R.drawable.gallery_img);

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setSelected(v);
				}
			});
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Adapter specific entries here...
		GalleryBean media = galleryAdapter.getGalleryImageList().get(position);
		if (media.getType() == GalleryType.SERVER
				|| (media.getMediaType().equalsIgnoreCase("video") && media
						.getMediaThumbnailUrl().length() > 0)) {
			holder.imageviewGone.setVisibility(View.GONE);
			holder.imageview.setVisibility(View.VISIBLE);
			try {
				memreasImageLoader.cancelDisplayTask(holder.imageview);
				if (media.getMediaType().equalsIgnoreCase("video")) {

					int random_index = new Random().nextInt(media
							.getMediaThumbnailUrl98x78().length());
					String url = media.getMediaThumbnailUrl98x78()
							.get(random_index).toString();
					memreasImageLoader.displayImage(url, holder.imageview,
							optionsGallery, animateFirstListener);
				} else {
					String url = media.getMediaUrl();
					memreasImageLoader.displayImage(url, holder.imageview,
							optionsGallery, animateFirstListener);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				Bitmap btmDisplay = media.getMediaThumbBitmap();
				if (btmDisplay != null && !btmDisplay.isRecycled()) {
					holder.imageviewGone.setImageBitmap(btmDisplay);
					holder.imageviewGone.setVisibility(View.VISIBLE);
					holder.imageview.setVisibility(View.GONE);
				} else {
					holder.imageviewGone.setVisibility(View.GONE);
					holder.imageview.setVisibility(View.VISIBLE);
					memreasImageLoader.cancelDisplayTask(holder.imageview);
					memreasImageLoader.displayImage(
							"file://" + media.getLocalMediaPath(),
							holder.imageview, optionsStorage,
							animateFirstListener);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		// Set the video play button for videos...
		if (media.getMediaType() != null
				&& media.getMediaType().equalsIgnoreCase("video"))
			holder.videoImg.setVisibility(View.VISIBLE);
		else
			holder.videoImg.setVisibility(View.GONE);

		// Set outer color code for sync media...
		holder.backview.setBackgroundColor(galleryAdapter.getGalleryImageList()
				.get(position).getMediaOuterColorCode());

		// Set the checkboxes for selected media...
		if (media.isSelected()) {
			holder.checkbox.setVisibility(View.VISIBLE);
		} else {
			holder.checkbox.setVisibility(View.GONE);
		}

		// record position
		holder.position = position;

		return convertView;
	}

	public void clearSelectedMedia() {
		for (int i = 0; i < galleryAdapter.getGalleryImageList().size(); i++) {
			galleryAdapter.getGalleryImageList().get(i).setSelected(false);
		}
	}

	public void fetchMediaIds() {
		for (int i = 0; i < galleryAdapter.getGalleryImageList().size(); i++) {
			GalleryBean media = galleryAdapter.getGalleryImageList().get(i);
			if (media.isSelected()) {
				//new GenerateMediaIdParser(media)
				//		.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				media.setMediaId(MediaIdManager.getInstance().fetchNextMediaId());
			}
		}
		this.syncCountSelected = 0;
		this.notifyDataSetChanged();
	}

	public Activity getContext() {
		return context;
	}

	public void setContext(Activity context) {
		this.context = context;
	}

	public int getSyncCountSelected() {
		return syncCountSelected;
	}

	private class ViewHolder {
		public ImageView backview;
		public ImageView imageview, imageviewGone;
		public ImageView checkbox;
		public ImageView videoImg;
		public ImageView imgSelect;
		public int position;
	}
}
