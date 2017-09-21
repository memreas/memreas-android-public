
package com.memreas.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.memreas.R;
import com.memreas.base.BaseActivity;
import com.memreas.util.MemreasImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class GalleryPagerActivity extends BaseActivity {

    private ViewPager pager;
    private static ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_image_pager_item);
        mProgressBar = (ProgressBar) findViewById(R.id.galleryPagerProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        Bundle data = getIntent().getExtras();
        int position = data.getInt("position");

        pager = (ViewPager) findViewById(R.id.gallery_pager_view_pager);
        pager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        pager.setCurrentItem(position);
        mProgressBar.setVisibility(View.GONE);
    }

    private static class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            if ((GalleryAdapter.getInstance() != null) && (GalleryAdapter.getInstance().getGalleryImageList() != null)) {
                return GalleryAdapter.getInstance().getGalleryImageList().size();
            } else {
                //reinitialize
                GalleryAdapter.reset();
                return GalleryAdapter.getInstance().getGalleryImageList().size();
            }
        }

        @Override
        public Fragment getItem(int position) {
            return ImageFragment.init(position);
        }
    }

    public static class ImageFragment extends Fragment {
        protected int position;
        protected GalleryBean bean;
        protected View layoutView;
        protected ImageView imageView;
        protected ImageView playButton;
        private DisplayImageOptions optionsGallery;
        private AnimateFirstDisplayListener animateFirstListener;
        private ImageLoader memreasImageLoader;
        private DisplayImageOptions optionsStorage;

        static ImageFragment init(int position) {
            ImageFragment imageFrag = new ImageFragment();
            // Supply val input as an argument.
            Bundle args = new Bundle();
            args.putInt("position", position);
            imageFrag.setArguments(args);
            return imageFrag;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            position = getArguments() != null ? getArguments().getInt(
                    "position") : 1;
            optionsGallery = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                    .showImageForEmptyUri(R.drawable.loading_image_fail)
                    .showImageOnFail(R.drawable.loading_image_fail).build();
            optionsStorage = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                            // .decodingOptions(options)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .showImageForEmptyUri(R.drawable.loading_image_fail)
                    .showImageOnFail(R.drawable.loading_image_fail).build();

            animateFirstListener = new AnimateFirstDisplayListener();
            memreasImageLoader = MemreasImageLoader.getInstance();

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            mProgressBar.setVisibility(View.VISIBLE);

            layoutView = inflater.inflate(R.layout.gallery_image_pager_item,
                    container, false);
            imageView = (ImageView) layoutView
                    .findViewById(R.id.gallery_pager_image_view);
            playButton = (ImageView) layoutView
                    .findViewById(R.id.gallery_pager_video_view);
            bean = GalleryAdapter.getInstance().getGalleryImageList()
                    .get(position);
            if (bean.isLocal() && bean.getMediaType().equalsIgnoreCase("video")) {
                /*
                 * TODO - Change to Universal Image Loader here...
				 */
                Bitmap bm = ThumbnailUtils.createVideoThumbnail(
                        bean.getLocalMediaPath(),
                        MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                imageView.setImageBitmap(bm);
                playButton.setVisibility(View.VISIBLE);
                playButton.setOnClickListener(getPlayButtonListener());
            } else if (bean.isLocal()
                    && bean.getMediaType().equalsIgnoreCase("image")) {
                // local media...
                memreasImageLoader.displayImage(
                        "file://" + bean.getLocalMediaPath(), imageView,
                        optionsStorage, animateFirstListener);
            } else if (!bean.isLocal()) {
                if (bean.getMediaType().equalsIgnoreCase("video")) {
                    int random_index = new Random().nextInt(bean
                            .getMediaThumbnailUrl98x78().length());
                    String url = "";
                    try {
                        url = bean.getMediaThumbnailUrl448x306().get(
                                random_index).toString();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    memreasImageLoader.displayImage(url, imageView,
                            optionsGallery, animateFirstListener);
                    if (URLUtil.isValidUrl(bean.getMediaUrlWeb())) {
                        playButton.setVisibility(View.VISIBLE);
                        playButton.setOnClickListener(getPlayButtonListener());
                    }
                } else {
                    String url = bean.getMediaUrl();
                    memreasImageLoader.displayImage(url, imageView,
                            optionsGallery, animateFirstListener);
                }
            } else {
                imageView.setImageResource(R.drawable.gallery_img);
            }
            mProgressBar.setVisibility(View.GONE);

            return layoutView;
        }

        private OnClickListener getPlayButtonListener() {
            return new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    try {
                        // Start activity here...
                        intent.setClass(getActivity(), PlayVideoActivity.class);
                        intent.putExtra("position", position);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "could not play video",
                                Toast.LENGTH_SHORT).show();
                        //do nothing - let pager resume.
                    }

                }
            };
        }

        private class AnimateFirstDisplayListener extends
                SimpleImageLoadingListener {

            final List<String> displayedImages = Collections
                    .synchronizedList(new LinkedList<String>());

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                ((ImageView) view).setScaleType(ScaleType.FIT_XY);
                ((ImageView) view)
                        .setImageResource(R.drawable.loading_image_gallery);
                AnimationDrawable animationDrawable = (AnimationDrawable) ((ImageView) view)
                        .getDrawable();
                animationDrawable.start();
                super.onLoadingStarted(imageUri, view);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
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
    }

}
