
package com.memreas.memreas;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.memreas.R;
import com.memreas.gallery.GalleryAdapter;
import com.memreas.memreas.MemreasEventBean.MediaShortDetails;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedList;
import java.util.Random;

public class MemreasEventsGalleryMediaAdapter extends BaseAdapter {

	private static MemreasEventsGalleryMediaAdapter instance;
	private LinkedList<MemreasEventMeBean.MediaShortDetails> mediaLinkedList;
	private Activity context;
	private int resource;
	private LayoutInflater mInflater;
	protected DisplayImageOptions optionsGallery;
	private DisplayImageOptions optionsStorage;
	private AnimateFirstDisplayListener animateGalleryListener;
	private AnimateFirstDisplayListener animateProfileListener;
	private ImageLoader memreasImageLoader;
	private GalleryAdapter galleryAdapter;

	protected void setMemreasEventsGalleryMediaAdapterView(Activity context,
			int resource) {
		this.setContext(context);
		this.resource = resource;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		animateGalleryListener = new AnimateFirstDisplayListener();
		animateGalleryListener.setFailImage(R.drawable.gallery_img);
		animateProfileListener = new AnimateFirstDisplayListener();
		animateProfileListener.setFailImage(R.drawable.profile_img_small);

		memreasImageLoader = MemreasImageLoader.getInstance();
		optionsGallery = MemreasImageLoader.getDefaultDisplayImageOptions();
		optionsStorage = MemreasImageLoader
				.getDefaultDisplayImageOptionsStorage();
	}

	protected MemreasEventsGalleryMediaAdapter() {
	}

	public static MemreasEventsGalleryMediaAdapter getInstance() {
		if (instance == null) {
			instance = new MemreasEventsGalleryMediaAdapter();
		}
		return instance;
	}

	@Override
	public int getCount() {
		return mediaLinkedList.size();
	}

	@Override
	public MediaShortDetails getItem(int position) {
		return mediaLinkedList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {

			holder = new ViewHolder();
			convertView = mInflater.inflate(this.resource, parent, false);

			holder.eventInappropriateText = (TextView) convertView
					.findViewById(R.id.eventInappropriateText);
			holder.eventThumbImage = (ImageView) convertView
					.findViewById(R.id.eventThumbImage);
			holder.videoImg = (ImageView) convertView
					.findViewById(R.id.eventVideoImg);
			holder.eventThumbImage.setBackgroundColor(Color.DKGRAY);
			holder.eventThumbImage.setImageResource(R.drawable.gallery_img);

			convertView.setOnClickListener(memreasEventDetailListener);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		/*
		 * Set Media entries here...
		 */
		// Adapter specific entries here...
		MediaShortDetails mediaShortDetails = mediaLinkedList.get(position);
		holder.eventThumbImage.setVisibility(View.VISIBLE);
		String url = "";
		
		if (mediaShortDetails.isApproriate(ViewMemreasEventActivity.eventId)) {
			try {
				memreasImageLoader.cancelDisplayTask(holder.eventThumbImage);
				int random_index = new Random()
						.nextInt(mediaShortDetails.media_98x78.length());
				url = mediaShortDetails.media_98x78.get(random_index)
						.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			memreasImageLoader.displayImage(url, holder.eventThumbImage,
					optionsGallery, animateGalleryListener);

			// Set the video play button for videos...
			if (mediaShortDetails.media_type != null
					&& mediaShortDetails.media_type.equalsIgnoreCase("video"))
				holder.videoImg.setVisibility(View.VISIBLE);
			else
				holder.videoImg.setVisibility(View.GONE);
		} else {
			holder.eventThumbImage.setVisibility(View.GONE);
			holder.videoImg.setVisibility(View.GONE);
			holder.eventInappropriateText.setVisibility(View.VISIBLE);
		}

		/*
		 * Check if local or server
		 */
		galleryAdapter = GalleryAdapter.getInstance();
		if (galleryAdapter.getHashmapGalleryKeys().containsKey(
				mediaShortDetails.media_name)) {
			int pos = galleryAdapter.getHashmapGalleryKeys().get(
					mediaShortDetails.media_name);
			holder.eventThumbImage.setBackgroundColor(galleryAdapter
					.getGalleryImageList().get(pos).getMediaOuterColorCode());
		}
		
		// record position
		holder.position = position;
		convertView.setTag(holder);

		return convertView;
	}

	public Activity getContext() {
		return context;
	}

	public void setContext(Activity context) {
		this.context = context;
	}

	public LinkedList<MemreasEventMeBean.MediaShortDetails> getMediaLinkedList() {
		return mediaLinkedList;
	}

	public void setMediaLinkedList(
			LinkedList<MemreasEventMeBean.MediaShortDetails> mediaLinkedList) {
		this.mediaLinkedList = mediaLinkedList;
		this.notifyDataSetChanged();
	}

	private class ViewHolder {
		public TextView eventInappropriateText;
		public ImageView eventThumbImage;
		public ImageView videoImg;
		public int position;
	}

	protected OnClickListener memreasEventDetailListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ViewHolder holder = (ViewHolder) v.getTag();
			int position = holder.position;
			((ViewMemreasEventActivity) context).onItemClickViewEvent(position);
		}
	};

}