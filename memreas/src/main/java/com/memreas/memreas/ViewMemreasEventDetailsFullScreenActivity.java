
package com.memreas.memreas;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.memreas.R;
import com.memreas.base.BaseActivity;
import com.memreas.memreas.MemreasEventBean.MediaShortDetails;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasImageLoader;
import com.memreas.util.MemreasProgressDialog;
import com.memreas.util.MemreasVideoViewer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedList;

public class ViewMemreasEventDetailsFullScreenActivity extends BaseActivity {

	protected MemreasVideoViewer memreasVideoViewer;
	protected View layoutView;
	protected MemreasProgressDialog mProgressDialog;
	protected int position;
	protected String type;
	protected String media_url;
	protected int video_position;
	protected DisplayImageOptions optionsGallery;
	protected DisplayImageOptions optionsStorage;
	protected AnimateFirstDisplayListener animateGalleryListener;
	protected ImageLoader memreasImageLoader;
	protected LinkedList<MediaShortDetails> mMediaShortDetailsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 getWindow().setFlags(
		 WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
		 WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		setContentView(R.layout.memreas_event_details_media_fullscreen_pager_item);
		animateGalleryListener = new AnimateFirstDisplayListener();
		animateGalleryListener.setFailImage(R.drawable.gallery_img);
		memreasImageLoader = MemreasImageLoader.getInstance();
		optionsGallery = MemreasImageLoader.getDefaultDisplayImageOptions();
		optionsStorage = MemreasImageLoader
				.getDefaultDisplayImageOptionsStorage();

		ViewPager memreas_event_details_view_pager = (ViewPager) findViewById(R.id.memreas_event_details_view_pager);
		Bundle data = getIntent().getExtras();
		int position = data.getInt("position");
		LinkedList<MediaShortDetails> mediaShortDetailsArrayList = ViewMemreasEventActivity.mediaShortDetailsListForFullScreenActivity;
		MemreasEventsDetailsPagerAdapter memreasEventsDetailsPagerAdapter = new MemreasEventsDetailsPagerAdapter(
				ViewMemreasEventDetailsFullScreenActivity.this,
				R.layout.memreas_event_details_media_fullscreen_pager_item,
				getSupportFragmentManager());
		memreasEventsDetailsPagerAdapter
				.setmMediaShortDetailsList((LinkedList<MediaShortDetails>) mediaShortDetailsArrayList);
		memreas_event_details_view_pager
				.setAdapter(memreasEventsDetailsPagerAdapter);
		memreas_event_details_view_pager.setCurrentItem(position);
	}

	protected OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (memreasVideoViewer.memreas_event_details_video_view != null) {
				memreasVideoViewer.memreas_event_details_video_view
						.stopPlayback();
				finish();
			}
		}
	};

	protected OnCompletionListener onCompletionListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			// reset audio
			onPause();
			finish();
		}
	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		try {
			if (type.equalsIgnoreCase("video")) {
				memreasVideoViewer.memreas_event_details_video_view.pause();
				this.video_position = memreasVideoViewer.memreas_event_details_video_view
						.getCurrentPosition();
			}
		} catch (Exception e) {
			// do nothing ....
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		try {
			if (type.equalsIgnoreCase("video")) {
				memreasVideoViewer.memreas_event_details_video_view
						.seekTo(video_position);
				memreasVideoViewer.memreas_event_details_video_view.start();
			} else {
				memreasVideoViewer.memreas_event_details_pager_image_view
						.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
			// do nothing ....
		}

		super.onResume();
	}

}
