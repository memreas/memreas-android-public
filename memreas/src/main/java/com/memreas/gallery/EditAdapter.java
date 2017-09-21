
package com.memreas.gallery;

import android.app.Activity;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EditAdapter extends BaseAdapter {

	protected static EditAdapter instance = null;
	protected List<GalleryBean> editGalleryImageList = new ArrayList<GalleryBean>();
	protected Activity context;
	protected int resource;
	protected LayoutInflater mInflater;
	protected DisplayImageOptions optionsGallery;
	protected DisplayImageOptions optionsStorage;
	protected ImageLoadingListener animateFirstListener;
	protected ImageLoader memreasImageLoader;

	protected EditAdapter(Activity context, int resource) {

		this.context = context;
		this.resource = resource;
		mInflater = (LayoutInflater) context
				.getSystemService(context.LAYOUT_INFLATER_SERVICE);

		BitmapFactory.Options options = MemreasImageLoader.getDefaultBitmapFactoryOptions();
		optionsGallery = MemreasImageLoader.getDefaultDisplayImageOptions();
		optionsStorage = MemreasImageLoader.getDefaultDisplayImageOptionsStorage();
		animateFirstListener = new AnimateFirstDisplayListener();
		memreasImageLoader = MemreasImageLoader.getInstance();

		Iterator<GalleryBean> iterator = GalleryAdapter.getInstance()
				.getGalleryImageList().iterator();
		while (iterator.hasNext()) {
			GalleryBean media = iterator.next();
			if (media.getMediaType().equalsIgnoreCase("image")) {
				this.editGalleryImageList.add(media);
			}
		}
	}

	public static EditAdapter getInstance(Activity context, int resource) {
		if (instance == null) {
			instance = new EditAdapter(context, resource);
		}
		
		GalleryAdapter.getInstance().registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				EditAdapter.getInstance().notifyDataSetChanged();
			}
		});

		
		return instance;
	}

	public static EditAdapter getInstance() {
		// Assumes context and resource are set.
		return instance;
	}

	@Override
	public int getCount() {
		return editGalleryImageList.size();
	}

	@Override
	public GalleryBean getItem(int position) {
		return editGalleryImageList.get(position);
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
					.findViewById(R.id.editthumbback);
			holder.imageview = (ImageView) convertView
					.findViewById(R.id.editthumbImage);
			holder.imageviewGone = (ImageView) convertView
					.findViewById(R.id.editthumbImageGone);

			holder.backview.setBackgroundColor(Color.GRAY);
			holder.imageview.setBackgroundColor(Color.BLACK);
			holder.imageview.setImageResource(R.drawable.gallery_img);
			holder.position = 0;

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.position = position;
		}

		GalleryBean media = editGalleryImageList.get(position);
		if (media.getType() == GalleryType.SERVER) {
			holder.imageviewGone.setVisibility(View.GONE);
			holder.imageview.setVisibility(View.VISIBLE);
			try {
				memreasImageLoader.cancelDisplayTask(holder.imageview);
				String url = media.getMediaThumbnailUrl98x78().get(0).toString();
				memreasImageLoader.displayImage(url, holder.imageview,
						optionsGallery, animateFirstListener);
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

		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (GalleryActivity.isAsyncLoadComplete) {
					//int pos = ((ViewHolder) v.getTag()).position;
					//((GalleryActivity) context).editImageWithAviary(pos);
				} else {
					Toast.makeText(context, "loading gallery...",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		// Set outer color code for sync media...
		holder.backview.setBackgroundColor(editGalleryImageList
				.get(position).getMediaOuterColorCode());
		
		convertView.setTag(holder);

		return convertView;
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
		public int position;
	}

}
