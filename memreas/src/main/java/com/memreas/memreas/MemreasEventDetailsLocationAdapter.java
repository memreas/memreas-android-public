
package com.memreas.memreas;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.maps.model.LatLng;
import com.memreas.R;
import com.memreas.gallery.GalleryAdapter;
import com.memreas.location.MemreasGoogleMap;
import com.memreas.memreas.MemreasEventBean.MediaShortDetails;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedList;
import java.util.Random;

public class MemreasEventDetailsLocationAdapter extends
		FragmentStatePagerAdapter {

	protected LinkedList<VideoView> videoViewList;
	protected FragmentManager fragmentManager;
	protected Activity context;
	protected int resource;
	protected LayoutInflater mInflater;
	protected DisplayImageOptions optionsGallery;
	protected DisplayImageOptions optionsStorage;
	protected AnimateFirstDisplayListener animateGalleryListener;
	protected ImageLoader memreasImageLoader;
	protected LinkedList<MediaShortDetails> mMediaShortDetailsList;
	protected GalleryAdapter galleryAdapter;
	protected MemreasGoogleMap mGoogleMap;
	protected LatLng mLocation;

	protected MemreasEventDetailsLocationAdapter(Activity context,
			MemreasGoogleMap mGoogleMap, FragmentManager fragmentManager,
			LinkedList<MediaShortDetails> mMediaShortDetailsList) {
		super(fragmentManager);
		this.mGoogleMap = mGoogleMap;
		this.context = context;
		this.fragmentManager = fragmentManager;
		this.mMediaShortDetailsList = mMediaShortDetailsList;
		animateGalleryListener = new AnimateFirstDisplayListener();
		animateGalleryListener.setFailImage(R.drawable.gallery_img);
		memreasImageLoader = MemreasImageLoader.getInstance();
		optionsGallery = MemreasImageLoader.getDefaultDisplayImageOptions();
		optionsStorage = MemreasImageLoader
				.getDefaultDisplayImageOptionsStorage();

		galleryAdapter = GalleryAdapter.getInstance();
		galleryAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				MemreasEventDetailsLocationAdapter.this.notifyDataSetChanged();
			}
		});

	}

	public FragmentManager getFragmentManager() {
		return fragmentManager;
	}

	@Override
	public Fragment getItem(int position) {
		return new LocatonStripFragment(position);
	}

	@Override
	public int getCount() {
		if (mMediaShortDetailsList != null) {
			return mMediaShortDetailsList.size();
		}
		return 0;
	}

	private class LocatonStripFragment extends Fragment {
		protected int position;

		public LocatonStripFragment(int position) {

			// Supply num input as an argument.
			Bundle args = new Bundle();
			args.putInt("position", position);
			setArguments(args);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			position = getArguments() != null ? getArguments().getInt(
					"position") : 0;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			/*
			 * Inflate View
			 */
			View convertView = inflater.inflate(
					R.layout.memreas_event_details_location_pager_item,
					container, false);

			/*
			 * Initialize ViewHolder
			 */
			ViewHolder holder = new ViewHolder();
			holder.memreas_event_details_location_inappropriate_text_view = (TextView) convertView
					.findViewById(R.id.memreas_event_details_location_inappropriate_text_view);
			holder.memreas_event_details_location_pager_image_view = (ImageView) convertView
					.findViewById(R.id.memreas_event_details_location_pager_image_view);
			holder.memreas_event_details_location_pager_play_video_icon = (ImageView) convertView
					.findViewById(R.id.memreas_event_details_location_pager_play_video_icon);

			/*
			 * Set specific items here
			 */
			MediaShortDetails mediaShortDetails = mMediaShortDetailsList
					.get(position);
			/*
			 * Check if media was marke by user as inappropriate
			 */
			if ((mediaShortDetails.event_media_inappropriate != null)
					&& (!mediaShortDetails
							.isApproriate(ViewMemreasEventActivity.eventId))) {
				holder.memreas_event_details_location_pager_image_view
						.setVisibility(View.GONE);
				holder.memreas_event_details_location_pager_play_video_icon
						.setVisibility(View.GONE);
				holder.memreas_event_details_location_inappropriate_text_view
						.setVisibility(View.VISIBLE);
			} else {

				if (mediaShortDetails.media_type.equalsIgnoreCase("video")) {
					holder.isVideo = true;
					holder.memreas_event_details_location_pager_image_view
							.setVisibility(View.VISIBLE);
					holder.memreas_event_details_location_pager_play_video_icon
							.setVisibility(View.VISIBLE);
				} else {
					holder.isVideo = false;
					holder.memreas_event_details_location_pager_image_view
							.setVisibility(View.VISIBLE);
					holder.memreas_event_details_location_pager_play_video_icon
							.setVisibility(View.GONE);
				}

				String image_url = "";
				try {
					int random_index;
					if ((mediaShortDetails.media_79x80 != null)
							&& (mediaShortDetails.media_79x80.length() > 0)) {
						random_index = new Random()
								.nextInt(mediaShortDetails.media_79x80.length());
						image_url = mediaShortDetails.media_79x80
								.getString(random_index);
					} else {
						if (mediaShortDetails.media_type
								.equalsIgnoreCase("image")) {
							image_url = mediaShortDetails.media_url;
						}
					}
				} catch (Exception e) {
					// use fail image...
					e.printStackTrace();
				}

				/*
				 * Check if local or server
				 */
				galleryAdapter = GalleryAdapter.getInstance();
				if (galleryAdapter.getHashmapGalleryKeys().containsKey(
						mediaShortDetails.media_name)) {
					int pos = galleryAdapter.getHashmapGalleryKeys().get(
							mediaShortDetails.media_name);
					holder.memreas_event_details_location_pager_image_view
							.setBackgroundColor(galleryAdapter
									.getGalleryImageList().get(pos)
									.getMediaOuterColorCode());
				}

				memreasImageLoader.displayImage(image_url,
						holder.memreas_event_details_location_pager_image_view,
						optionsGallery, animateGalleryListener);

				/*
				 * Set location for map
				 */
				if (mediaShortDetails.location != null) {
					try {
						mLocation = new LatLng(
								mediaShortDetails.location.latititude,
								mediaShortDetails.location.longitude);
						mGoogleMap.setmLocation(mLocation);
						mGoogleMap.showMarker();
					} catch (Exception e) {
						mLocation = null;
					}
				}

			} // end else isAppropriate...
				// holder..
			holder.position = position;
			convertView.setTag(holder);
			return convertView;
		}

		private class ViewHolder {
			public boolean isVideo = false;
			public TextView memreas_event_details_location_inappropriate_text_view;
			public ImageView memreas_event_details_location_pager_image_view;
			public ImageView memreas_event_details_location_pager_play_video_icon;
			public int position;
		}
	}

}
