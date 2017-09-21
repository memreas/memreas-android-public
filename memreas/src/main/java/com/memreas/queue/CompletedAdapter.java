
package com.memreas.queue;

import java.util.LinkedList;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.memreas.R;
import com.memreas.gallery.GalleryBean;
import com.memreas.gallery.GalleryBean.GalleryType;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class CompletedAdapter extends BaseAdapter {

	private static CompletedAdapter instance;
	private Context context;
	private LayoutInflater mInflater;
	private LinkedList<MemreasTransferModel> completedTransferModelQueue;
	protected DisplayImageOptions optionsGallery;
	protected DisplayImageOptions optionsStorage;
	protected ImageLoadingListener animateFirstListener;
	protected ImageLoader memreasImageLoader;

	protected void setCompletedView(Context context) {
		this.context = context;
		mInflater = (LayoutInflater) context
				.getSystemService(context.LAYOUT_INFLATER_SERVICE);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.outWidth = 96;
		options.outHeight = 96;
		options.inJustDecodeBounds = true;
		optionsGallery = new DisplayImageOptions.Builder().cacheInMemory(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.showImageForEmptyUri(R.drawable.loading_image_fail)
				.showImageOnFail(R.drawable.loading_image_fail).build();
		optionsStorage = new DisplayImageOptions.Builder().cacheInMemory(true)
				.decodingOptions(options)
				.imageScaleType(ImageScaleType.EXACTLY)
				.showImageForEmptyUri(R.drawable.loading_image_fail)
				.showImageOnFail(R.drawable.loading_image_fail).build();

		animateFirstListener = new AnimateFirstDisplayListener();
		memreasImageLoader = MemreasImageLoader.getInstance();
	}

	public synchronized static CompletedAdapter getInstance() {
		if (instance == null) {
			instance = new CompletedAdapter();
			instance.completedTransferModelQueue = new LinkedList<MemreasTransferModel>();
		}
		return instance;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return completedTransferModelQueue.size();
	}

	@Override
	public MemreasTransferModel getItem(int position) {
		return completedTransferModelQueue.get(position);
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
			convertView = mInflater.inflate(R.layout.gallerysyncitem_main, parent,
					false);

			holder.backview = (ImageView) convertView
					.findViewById(R.id.syncthumbback);
			holder.imageview = (ImageView) convertView
					.findViewById(R.id.syncthumbImage);
			holder.imageviewGone = (ImageView) convertView
					.findViewById(R.id.syncthumbImageGone);
			holder.videoImg = (ImageView) convertView
					.findViewById(R.id.syncvideoImg);
			holder.imageview.setBackgroundColor(Color.BLACK);
			holder.imageview.setImageResource(R.drawable.gallery_img);

			// Set onClick to toast status
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showStatus(position);
				}
			});

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// row specific entries here...
		MemreasTransferModel transferModel = completedTransferModelQueue
				.get(position);
		GalleryBean media = transferModel.getMedia();
		if (media.getType() == GalleryType.SERVER
				|| (media.getMediaType().equalsIgnoreCase("video")
						&& media.getMediaThumbnailUrl().length() > 0)) {
			holder.imageviewGone.setVisibility(View.GONE);
			holder.imageview.setVisibility(View.VISIBLE);
			try {
				memreasImageLoader.cancelDisplayTask(holder.imageview);
				if (media.getMediaType().equalsIgnoreCase("video")) {

					int random_index = new Random().nextInt(media
							.getMediaThumbnailUrl98x78().length());
					String url = media.getMediaThumbnailUrl98x78().get(
							random_index).toString();
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

		if (media.getMediaType() != null
				&& media.getMediaType().equalsIgnoreCase("video"))
			holder.videoImg.setVisibility(View.VISIBLE);
		else
			holder.videoImg.setVisibility(View.GONE);

		// Set the name for the holder to match in the progress task
		holder.name = transferModel.getName();
		holder.transferModel = transferModel;

		/*
		 * TODO Set color code in QueueService...
		 */
		// Set outer color code for sync media...
		holder.backview.setBackgroundColor(media.getMediaOuterColorCode());

		convertView.setTag(holder);

		return convertView;
	}

	public void addTransferModel(MemreasTransferModel transferModel) {
		completedTransferModelQueue.add(transferModel);
		Log.d(getClass().getName() + "completedTransferModelQueue.mediaId--->", transferModel.getMedia().getMediaId());
	}

	public void removeAllQueueEntries() {
		completedTransferModelQueue = new LinkedList<MemreasTransferModel>();
		notifyDataSetChanged();
	}

	public void showStatus(int position) {
		MemreasTransferModel transferModel = completedTransferModelQueue
				.get(position);
		Toast.makeText(
				context,
				"media status: "
						+ transferModel.getMemreasQueueStatus().toString().toLowerCase(),
				Toast.LENGTH_SHORT).show();
	}

	private class ViewHolder {
		public ImageView backview;
		public ImageView imageview, imageviewGone;
		public ImageView videoImg;
		public MemreasTransferModel transferModel;
		public String name;
	}
}
