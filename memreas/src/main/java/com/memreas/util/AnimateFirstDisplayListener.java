
package com.memreas.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.memreas.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

	final List<String> displayedImages = Collections
			.synchronizedList(new LinkedList<String>());
	private int failImage = R.drawable.loading_image_fail; 

	@Override
	public void onLoadingStarted(String imageUri, View view) {
		((ImageView) view).setScaleType(ScaleType.FIT_XY);
		((ImageView) view).setImageResource(R.drawable.loading_image_gallery);
		AnimationDrawable animationDrawable = (AnimationDrawable) ((ImageView) view)
				.getDrawable();
		animationDrawable.start();
		super.onLoadingStarted(imageUri, view);
	}

	public void setFailImage(int failImage) {
		this.failImage = failImage;
	}

	@Override
	public void onLoadingCancelled(String imageUri, View view) {
		// TODO Auto-generated method stub
		super.onLoadingCancelled(imageUri, view);
	}

	@Override
	public void onLoadingFailed(String imageUri, View view,
			FailReason failReason) {
		onLoadingCancelled(imageUri, view);
		ImageView imageView = (ImageView) view; 
		imageView.setScaleType(ScaleType.CENTER_CROP);
		imageView.setImageResource(failImage);
	}

	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		((ImageView) view).setScaleType(ScaleType.CENTER_CROP);
		if (loadedImage != null) {

			ImageView imageView = (ImageView) view;
			boolean firstDisplay = !displayedImages.contains(imageUri);

			if (firstDisplay) {
				FadeInBitmapDisplayer.animate(imageView, 500);
				displayedImages.add(imageUri);
			}
		}
	}

}