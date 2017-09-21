
package com.memreas.location;

import java.util.Random;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.memreas.R;
import com.memreas.gallery.GalleryActivity;
import com.memreas.gallery.GalleryAdapter;
import com.memreas.gallery.GalleryBean;
import com.memreas.gallery.GalleryBean.GalleryType;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class MediaMapLocationAdapter extends FragmentStatePagerAdapter {

	protected static MediaMapLocationAdapter instance;
	protected static FragmentManager fragmentManager;
	protected static GalleryAdapter galleryAdapter = null;
	protected static Context context;
	protected static int mNumPage = 0;
	protected static int cellWidth;
	protected static int cellHeight;
	protected static int selectedItemPosition = -1;
	protected static int numCols;
	protected static int mSelectedPosition;
	protected static DisplayImageOptions optionsGallery;
	protected static DisplayImageOptions optionsStorage;

	protected MediaMapLocationAdapter() {
		super(fragmentManager);
		galleryAdapter = GalleryAdapter.getInstance();
		galleryAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				MediaMapLocationAdapter.getInstance(
						MediaMapLocationAdapter.getContext(),
						MediaMapLocationAdapter.getFragmentManager())
						.notifyDataSetChanged();
			}
		});
		optionsGallery = MemreasImageLoader.getDefaultDisplayImageOptions();
		optionsStorage = MemreasImageLoader.getDefaultDisplayImageOptionsStorage();
	}

	public static MediaMapLocationAdapter getInstance(Context context,
			FragmentManager fragmentManager) {
		if (instance == null) {
			MediaMapLocationAdapter.context = context;
			MediaMapLocationAdapter.fragmentManager = fragmentManager;
			instance = new MediaMapLocationAdapter();
		}
		return instance;
	}

	public static Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		MediaMapLocationAdapter.context = context;
	}

	public static FragmentManager getFragmentManager() {
		return fragmentManager;
	}

	public static void setFragmentManager(FragmentManager fragmentManager) {
		MediaMapLocationAdapter.fragmentManager = fragmentManager;
	}

	@Override
	public Fragment getItem(int position) {
		return LocatonStripFragment.newInstance(position);
	}

	@Override
	public int getCount() {
		return galleryAdapter.getGalleryImageList().size();
	}

	public static class LocatonStripFragment extends Fragment {
		protected int position;
		protected ImageLoadingListener animateFirstListener;
		protected ImageLoader memreasImageLoader;

		public static LocatonStripFragment newInstance(int position) {
			LocatonStripFragment f = new LocatonStripFragment();

			// Supply num input as an argument.
			Bundle args = new Bundle();
			args.putInt("position", position);
			f.setArguments(args);

			return f;
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

			View convertView = inflater.inflate(
					R.layout.gallerylocationitem_main, container, false);
			ViewHolder holder = new ViewHolder();
			animateFirstListener = new AnimateFirstDisplayListener();
			memreasImageLoader = MemreasImageLoader.getInstance();
			holder.backview = (ImageView) convertView
					.findViewById(R.id.locationthumbback);
			holder.imageview = (ImageView) convertView
					.findViewById(R.id.locationthumbImage);
			holder.imageviewGone = (ImageView) convertView
					.findViewById(R.id.locationthumbImageGone);
			holder.videoImg = (ImageView) convertView
					.findViewById(R.id.locationvideoImg);
			holder.checkbox = (ImageView) convertView
					.findViewById(R.id.locationitemCheckBox);
			holder.imgSelect = (ImageView) convertView
					.findViewById(R.id.locationimgSelect);

			if (GalleryActivity.isAsyncLoadComplete) {
				GalleryBean media = galleryAdapter.getItem(position);
				if (media.getType() == GalleryType.SERVER) {
					holder.imageviewGone.setVisibility(View.GONE);
					holder.imageview.setVisibility(View.VISIBLE);
					try {
						if (media.getMediaType().equalsIgnoreCase("video")) {
							int random_index = new Random().nextInt(media
									.getMediaThumbnailUrl98x78().length());
							String url = media.getMediaThumbnailUrl98x78().get(
									random_index).toString();
							memreasImageLoader.displayImage(url,
									holder.imageview, optionsGallery,
									animateFirstListener);
						} else {
							String url = media.getMediaUrl();
							memreasImageLoader.displayImage(url,
									holder.imageview, optionsGallery,
									animateFirstListener);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
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
				}
				if (media.getMediaType().equalsIgnoreCase("video")) {
					holder.videoImg.setVisibility(View.VISIBLE);
				} else {
					holder.videoImg.setVisibility(View.GONE);
				}
				// Set outer color code for sync media...
				holder.backview.setBackgroundColor(media
						.getMediaOuterColorCode());
			} else {
				holder.imageview.setImageResource(R.drawable.gallery_img);
			}
			convertView.setTag(holder);
			return convertView;
		}

		private class ViewHolder {

			public ImageView backview;
			public ImageView imageview, imageviewGone;
			public ImageView checkbox;
			public ImageView videoImg;
			public ImageView imgSelect;
			public int id;
		}
	}

}
