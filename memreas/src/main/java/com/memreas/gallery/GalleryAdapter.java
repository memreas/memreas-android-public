
package com.memreas.gallery;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.memreas.R;
import com.memreas.gallery.GalleryBean.GalleryType;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GalleryAdapter extends BaseAdapter {
	public enum Type {
		NORMAL, EDIT, SYNC
	}

	protected static GalleryAdapter instance = null;
	public int bLocalMediaSize = 0;
	public int bSyncMediaSize = 0;
	public int bServerMediaSize = 0;
	protected List<GalleryBean> galleryImageList = new ArrayList<GalleryBean>();
	protected HashMap<String, Integer> hashmapGalleryKeys;
	protected Activity context;
	protected int resource;
	protected LayoutInflater mInflater;
	protected DisplayImageOptions optionsGallery;
	protected DisplayImageOptions optionsStorage;
	protected AnimateFirstDisplayListener animateFirstListener;
	protected ImageLoader memreasImageLoader;

	protected GalleryAdapter(Activity context, int resource) {
		this.context = context;
		this.resource = resource;
		mInflater = (LayoutInflater) context
				.getSystemService(context.LAYOUT_INFLATER_SERVICE);

		animateFirstListener = new AnimateFirstDisplayListener();
		memreasImageLoader = MemreasImageLoader.getInstance();
        MemreasImageLoader.setFailImage(R.drawable.transcodingdisc);
        MemreasImageLoader.setOnLoadingImage(R.drawable.transcodingdisc_two_x);
		optionsGallery = MemreasImageLoader.getDefaultDisplayImageOptions();
		optionsStorage = MemreasImageLoader
				.getDefaultDisplayImageOptionsStorage();
        animateFirstListener.setFailImage(R.drawable.transcodingdisc_two_x);
	}

	public static GalleryAdapter getInstance(Activity context, int resource) {
		if (instance == null) {
			instance = new GalleryAdapter(context, resource);
		}
		return instance;
	}

	public static GalleryAdapter getInstance() {
		// Assumes context and resource are set.
		return instance;
	}

	public static void reset() {
		instance = null;
	}

	@Override
	public int getCount() {
		return galleryImageList.size();
	}

	@Override
	public GalleryBean getItem(int position) {
		return galleryImageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {

			holder = new ViewHolder();
			convertView = mInflater.inflate(this.resource, parent, false);

			holder.backview = (ImageView) convertView
					.findViewById(R.id.thumbback);
			holder.imageview = (ImageView) convertView
					.findViewById(R.id.thumbImage);
			holder.imageviewGone = (ImageView) convertView
					.findViewById(R.id.thumbImageGone);
			holder.videoImg = (ImageView) convertView
					.findViewById(R.id.videoImg);
			holder.checkbox = (ImageView) convertView
					.findViewById(R.id.itemCheckBox);
			holder.imgSelect = (ImageView) convertView
					.findViewById(R.id.imgSelect);

			holder.backview.setBackgroundColor(Color.GRAY);
			holder.imageview.setBackgroundColor(Color.BLACK);
			holder.imageview.setImageResource(R.drawable.gallery_img);
			holder.position = 0;

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		GalleryBean media = galleryImageList.get(position);
		if (!media.isLocal()
				|| ((media.getType() == GalleryType.SERVER) && (media
						.getMediaType().equalsIgnoreCase("video") && media
						.getMediaThumbnailUrl().length() > 0))) {
			holder.imageviewGone.setVisibility(View.GONE);
			holder.imageview.setVisibility(View.VISIBLE);
			try {
                Log.e("TRANSCODE_STATUS",media.getMediaTranscodeStatus().trim());
				if (media.getMediaTranscodeStatus().trim().equalsIgnoreCase("success")) {

					memreasImageLoader.cancelDisplayTask(holder.imageview);
					String url = "";
					if (media.getMediaType().equalsIgnoreCase("video")) {

						int random_index = new Random().nextInt(media
								.getMediaThumbnailUrl98x78().length());
						url = media.getMediaThumbnailUrl98x78()
								.get(random_index).toString();
					} else {
						try {
							url = media.getMediaThumbnailUrl98x78().get(0)
									.toString();
						} catch (JSONException jsone) {
							url = media.getMediaUrl();
						}
					}
					memreasImageLoader.displayImage(url, holder.imageview,
							optionsGallery, animateFirstListener);
				} else {
                    memreasImageLoader.displayImage(null, holder.imageview,
                            optionsGallery, animateFirstListener);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			// VideoViewError: 100,0
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

		if (media.getMediaType() != null
				&& media.getMediaType().equalsIgnoreCase("video"))
			holder.videoImg.setVisibility(View.VISIBLE);
		else
			holder.videoImg.setVisibility(View.GONE);

		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (GalleryActivity.isAsyncLoadComplete) {
                    int pos = ((ViewHolder) v.getTag()).position;
					((GalleryActivity) context).onItemClick(pos);
				} else {
					Toast.makeText(context, "loading gallery...",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		holder.position = position;
		convertView.setTag(holder);
		return convertView;
	}

	public List<GalleryBean> getGalleryImageList() {
		return galleryImageList;
	}

	public void setGalleryImageList(List<GalleryBean> galleryImageList) {
		this.galleryImageList = galleryImageList;
	}

	public void addGalleryBean(GalleryBean galleryBean) {

		galleryImageList.add(galleryBean);
		this.notifyDataSetChanged();
	}

	public HashMap<String, Integer> getHashmapGalleryKeys() {
		if (hashmapGalleryKeys == null) {
			hashmapGalleryKeys = new HashMap<String, Integer>();
		}

		return hashmapGalleryKeys;
	}

	public void setHashmapGalleryKeys(
			HashMap<String, Integer> hashmapGalleryKeys) {
		this.hashmapGalleryKeys = hashmapGalleryKeys;
	}

	public Activity getContext() {
		return context;
	}

	public void setContext(Activity context) {
		this.context = context;
	}

	private class ViewHolder {

		public ImageView backview;
		public ImageView imageview, imageviewGone;
		public ImageView checkbox;
		public ImageView videoImg;
		public ImageView imgSelect;
		public int position;
		private String currentThumbnail;
		private int currentThumbnailListLength;
		private int currentThumbnailListIndex;
	}

}
