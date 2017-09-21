
package com.memreas.memreas;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.memreas.R;
import com.memreas.gallery.GalleryAdapter;
import com.memreas.memreas.MemreasEventBean.MediaShortDetails;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasImageLoader;
import com.memreas.util.MemreasProgressDialog;
import com.memreas.util.MemreasVideoViewer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class MemreasEventsDetailsPagerAdapter extends FragmentStatePagerAdapter {

    protected LinkedList<VideoView> videoViewList;
    protected FragmentManager fragmentManager;
    protected Activity context;
    protected int resource;
    protected LayoutInflater mInflater;
    protected DisplayImageOptions optionsGallery;
    protected DisplayImageOptions optionsStorage;
    protected AnimateFirstDisplayListener animateGalleryListener;
    protected AnimateFirstDisplayListener animateProfileListener;
    protected ImageLoader memreasImageLoader;
    protected LinkedList<MediaShortDetails> mMediaShortDetailsList;
    protected GalleryAdapter galleryAdapter;

    protected MemreasEventsDetailsPagerAdapter(Activity context, int resource,
                                               FragmentManager fragmentManager) {
        super(fragmentManager);
        this.context = context;
        this.resource = resource;
        this.fragmentManager = fragmentManager;
        animateGalleryListener = new AnimateFirstDisplayListener();
        animateGalleryListener.setFailImage(R.drawable.gallery_img);
        animateProfileListener = new AnimateFirstDisplayListener();
        animateProfileListener.setFailImage(R.drawable.profile_img);
        memreasImageLoader = MemreasImageLoader.getInstance();
        optionsGallery = MemreasImageLoader.getDefaultDisplayImageOptions();
        optionsStorage = MemreasImageLoader
                .getDefaultDisplayImageOptionsStorage();

        galleryAdapter = GalleryAdapter.getInstance();
        galleryAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                MemreasEventsDetailsPagerAdapter.this.notifyDataSetChanged();
            }
        });

    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    protected void addVisibleVideoView(VideoView videoView) {
        if (videoViewList == null) {
            videoViewList = new LinkedList<VideoView>();
        }
        videoViewList.add(videoView);
    }

    public void pauseVideoViews() {
        if (videoViewList != null) {
            Iterator<VideoView> iterator = videoViewList.iterator();
            while (iterator.hasNext()) {
                VideoView videoView = iterator.next();
                if ((videoView != null) && (videoView.isPlaying())) {
                    videoView.pause();
                }
            }
        }
    }

    @Override
    public Fragment getItem(int position) {
        return new DetailsStripFragment(position);
    }

    @Override
    public int getCount() {
        return mMediaShortDetailsList.size();
    }

    public LinkedList<MediaShortDetails> getmMediaShortDetailsList() {
        return mMediaShortDetailsList;
    }

    public void setmMediaShortDetailsList(
            LinkedList<MediaShortDetails> mMediaShortDetailsList) {
        this.mMediaShortDetailsList = mMediaShortDetailsList;
        this.notifyDataSetChanged();
    }

    public class DetailsStripFragment extends Fragment {
        protected int position;
        protected MemreasProgressDialog mProgressDialog;
        protected ViewHolder holder;

        public DetailsStripFragment(int position) {

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("position", position);
            setArguments(args);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mProgressDialog = MemreasProgressDialog.getInstance(context);
            position = getArguments() != null ? getArguments().getInt(
                    "position") : 0;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            /**
             * Inflate View
             */
            View convertView = inflater.inflate(resource, container, false);

            /**
             * Initialize ViewHolder
             */
            holder = new ViewHolder();
            holder.memreasVideoViewer.memreas_event_details_pager_framelayout = (FrameLayout) convertView
                    .findViewById(R.id.memreas_event_details_pager_framelayout);
            holder.memreasVideoViewer.memreas_event_details_pager_image_view = (ImageView) convertView
                    .findViewById(R.id.memreas_event_details_pager_image_view);
            holder.memreasVideoViewer.memreas_event_details_pager_play_video_icon = (ImageView) convertView
                    .findViewById(R.id.memreas_event_details_pager_play_video_icon);
            holder.memreasVideoViewer.memreas_event_details_video_view = (VideoView) convertView
                    .findViewById(R.id.memreas_event_details_video_view);
            holder.memreas_event_details_inappropriate_text_view = (TextView) convertView
                    .findViewById(R.id.memreas_event_details_inappropriate_text_view);

            /**
             * Set specific items here
             */
            MediaShortDetails mediaShortDetails = mMediaShortDetailsList
                    .get(position);
            /**
             * Check if media was marked by user as inappropriate
             */
            if ((mediaShortDetails.event_media_inappropriate != null)
                    && (!mediaShortDetails
                    .isApproriate(ViewMemreasEventActivity.eventId))) {
                holder.memreasVideoViewer.memreas_event_details_pager_image_view
                        .setVisibility(View.GONE);
                holder.memreasVideoViewer.memreas_event_details_pager_play_video_icon
                        .setVisibility(View.GONE);
                holder.memreas_event_details_inappropriate_text_view
                        .setVisibility(View.VISIBLE);
            } else {

                if (mediaShortDetails.media_type.equalsIgnoreCase("video")) {
                    holder.isVideo = true;
                    holder.memreasVideoViewer.memreas_event_details_pager_image_view
                            .setVisibility(View.VISIBLE);
                    holder.memreasVideoViewer.memreas_event_details_pager_play_video_icon
                            .setVisibility(View.VISIBLE);
                    holder.memreasVideoViewer.setListeners();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        //holder.memreasVideoViewer.media_url = mediaShortDetails.media_url_hls;
                        holder.memreasVideoViewer.media_url = mediaShortDetails.media_url_webm;
                    } else {
                        holder.memreasVideoViewer.media_url = mediaShortDetails.media_url_web;
                    }

                    /**
                     * Add Video to pause list...
                     */
                    addVisibleVideoView(holder.memreasVideoViewer.memreas_event_details_video_view);
                } else {
                    holder.isVideo = false;
                    holder.memreasVideoViewer.memreas_event_details_pager_image_view
                            .setVisibility(View.VISIBLE);
                    holder.memreasVideoViewer.memreas_event_details_pager_play_video_icon
                            .setVisibility(View.GONE);
                }

                holder.position = position;
                holder.mediaId = mediaShortDetails.media_id;
                String image_url = "";
                try {
                    int random_index;
                    if ((mediaShortDetails.media_448x306 != null)
                            && (mediaShortDetails.media_448x306.length() > 0)) {
                        random_index = new Random()
                                .nextInt(mediaShortDetails.media_448x306
                                        .length());
                        image_url = mediaShortDetails.media_448x306
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

                /**
                 * Check if local or server
                 */
                galleryAdapter = GalleryAdapter.getInstance();
                if (galleryAdapter.getHashmapGalleryKeys().containsKey(
                        mediaShortDetails.media_name)) {
                    int pos = galleryAdapter.getHashmapGalleryKeys().get(
                            mediaShortDetails.media_name);
                    holder.memreasVideoViewer.memreas_event_details_pager_framelayout
                            .setBackgroundColor(galleryAdapter
                                    .getGalleryImageList().get(pos)
                                    .getMediaOuterColorCode());
                }

                memreasImageLoader
                        .displayImage(
                                image_url,
                                holder.memreasVideoViewer.memreas_event_details_pager_image_view,
                                optionsGallery, animateGalleryListener);
            } // end else isAppropriate...
            // holder...
            holder.position = position;
            convertView.setTag(holder);
            return convertView;
        }

        public ViewHolder getHolder() {
            return holder;
        }

        public class ViewHolder {
            public ViewHolder() {
                memreasVideoViewer = new MemreasVideoViewer(context);
            }

            public boolean isVideo = false;
            public TextView memreas_event_details_inappropriate_text_view;
            public MemreasVideoViewer memreasVideoViewer;
            public String mediaUrl;
            public String mediaId;
            public int position;

        } // end ViewHolder

    } // end DetailsStripFragment
} // end MemreasEventsDetailsPagerAdapter...
