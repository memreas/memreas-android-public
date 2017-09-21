package com.memreas.memreas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.memreas.R;
import com.memreas.base.BaseActivity;
import com.memreas.base.SessionManager;
import com.memreas.gallery.GalleryAdapter;
import com.memreas.gallery.GalleryBean;
import com.memreas.gallery.GalleryBean.GalleryType;
import com.memreas.gallery.MediaIdManager;
import com.memreas.location.MemreasGoogleMap;
import com.memreas.member.FriendBean;
import com.memreas.member.GroupBean;
import com.memreas.memreas.MemreasEventBean.CommentShortDetails;
import com.memreas.memreas.MemreasEventBean.EventType;
import com.memreas.memreas.MemreasEventBean.FriendShortDetails;
import com.memreas.memreas.MemreasEventBean.MediaShortDetails;
import com.memreas.memreas.MemreasEventFriendsBean.FriendEventDetails;
import com.memreas.queue.MemreasTransferModel;
import com.memreas.queue.MemreasTransferModel.MemreasQueueStatus;
import com.memreas.queue.QueueAdapter;
import com.memreas.queue.QueueService;
import com.memreas.sax.handler.AddCommentParser;
import com.memreas.sax.handler.AddFriendToShareParser;
import com.memreas.sax.handler.LikeParser;
import com.memreas.sax.handler.MediaInappropriateParser;
import com.memreas.share.AddAudioCommentActivity;
import com.memreas.share.AddShareFriendActivity;
import com.memreas.share.ShareFriendAdapter;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasImageLoader;
import com.memreas.util.MemreasProgressDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class ViewMemreasEventActivity extends BaseActivity {

    private enum LastTab {
        GALLERY, DETAILS, LOCATION
    }

    //progress dialog failing in asyntasks...
    public MemreasProgressDialog mDialog;

    private MemreasEventBean.EventType type;
    private MemreasEventMeBean memreasEventMeBean;
    private MemreasEventFriendsBean memreasEventFriendsBean;
    private FriendEventDetails friendEventDetails;
    private MemreasEventPublicBean memreasEventPublicBean;
    private MemreasEventsCommentsPagerAdapter commentsPagerAdapter;
    private MemreasEventsCommentsPagerAdapter commentsDetailsPagerAdapter;
    private MemreasEventsCommentsListAdapter commentsDetailsListAdapter;

    public static LastTab lastTab = LastTab.GALLERY;
    public static int current_position = 0;
    public static int current_sub_position = 0;
    public static String sType = "ME";

    public static boolean state_saved = false;

    private LinearLayout memreas_event_gallery_LinearLayout;
    private LinearLayout memreas_event_details_LinearLayout;
    private LinearLayout memreas_event_location_LinearLayout;
    private Button eventGalleryBtn;
    private Button eventDetailsBtn;
    private Button eventMediaBtn;
    private Button eventLocationBtn;
    private ProgressBar mProgressBar;
    private LayoutInflater mInflater;
    protected DisplayImageOptions optionsGallery;
    private DisplayImageOptions optionsStorage;
    private AnimateFirstDisplayListener animateProfileListener;
    private AnimateFirstDisplayListener animateGalleryListener;
    private ImageLoader memreasImageLoader;

    /**
     * Main Gallery Content Views
     */
    private ImageView memreas_gallery_event_profile;
    private TextView memreas_gallery_event_profile_name;
    private TextView txtGalleryEventName;
    private ImageView btn_like;
    private TextView txt_like;
    private ImageView btn_comment;
    private TextView txt_comment;
    private LinearLayout friendsLinearLayout;
    private ImageView img_friend;
    private GridView memreas_event_media_gridview;
    private LinearLayout memreas_event_gallery_comments_viewpager_linearlayout;
    private ViewPager memreas_event_gallery_comments_viewpager;

    /**
     * Main Details Content Views
     */
    private ImageView memreas_details_event_profile;
    private TextView memreas_details_event_profile_name;
    private TextView txtDetailsEventName;
    private ImageButton imgBtnDownload;
    private ImageButton imgBtnLike;
    private TextView mediaLikeTxt;
    private ImageButton imgBtnCommentView;
    private TextView txtCommentText;
    private ImageButton imgBtnSound;
    private TextView txtCommentSound;
    private ImageButton imgBtnMaximize;
    private ImageButton imgBtnReport;
    private ViewPager memreas_event_details_view_pager;
    private MemreasEventsDetailsPagerAdapter memreasEventsDetailsPagerAdapter;
    private ViewPager memreas_event_media_details_viewpager;
    private LinearLayout memreas_event_comment_linearlayout;
    private LinearLayout memreas_event_comment_viewpager_linearlayout;
    private ViewPager memreas_event_comment_details_viewpager;
    private ListView memreas_event_comment_listview;
    private LinkedList<CommentShortDetails> commentDetailsShortDetailsList = null;
    private LinkedList<MediaShortDetails> mediaShortDetailsList = null;
    public static LinkedList<MediaShortDetails> mediaShortDetailsListForFullScreenActivity = null;
    private LinearLayout mediaLinearLayout;
    private LinearLayout mediaMenuLinearLayout;
    private FrameLayout memreas_event_details_pager_framelayout;
    private String sProfileUrl = "", sProfileName = "", sUrl = "",
            sEventName = "", sLikeCount = "-", sCommentCount = "-";

    /**
     * Location related views
     */
    private ImageView memreas_location_event_profile;
    private TextView memreas_location_event_profile_name;
    private TextView txtLocationEventName;
    private ImageButton btnPagerPrev;
    private ImageButton btnPagerNext;
    private MemreasGoogleMap mGoogleMap;
    private MemreasEventDetailsLocationAdapter locationDetailsPagerAdapter;
    private ViewPager event_details_media_location_pager;

    /**
     * Generic Views
     */
    private View galleryProfile;
    private View detailsProfile;
    private View locationProfile;
    private Button btnAddComment;
    private Button btnAddMedia;
    private Button btnAddFriends;

    public static String eventId;
    private String eventName;
    private String mediaId = "";

    // Audio vars..
    private String sAddCommentEditText;
    private GalleryBean mAudioBean;
    private String mAudioCommentFilePath;
    private int mAudioCommentDurationInMillis = 0;

    // Friend Vars
    private LinkedList mFriendOrGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //
        // Recreate state if necessary
        //
        super.onCreate(savedInstanceState); // Always call the superclass first

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            current_position = savedInstanceState.getInt("position");
            current_sub_position = savedInstanceState.getInt("subPosition");
            sType = savedInstanceState.getString("type");
            state_saved = true;
        } else {
            // Probably initialize members with default values for a new instance
            current_position = 0;
            current_sub_position = 0;
            sType = "ME";
            state_saved = false;
        }

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.screen_view_memreas_event_ref);
        PREV_INDEX = MEMREAS_INDEX;

        // Get inflater for profile pics and image loader...
        mInflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        animateGalleryListener = new AnimateFirstDisplayListener();
        animateGalleryListener.setFailImage(R.drawable.gallery_img);
        animateProfileListener = new AnimateFirstDisplayListener();
        animateProfileListener.setFailImage(R.drawable.profile_img_small);
        memreasImageLoader = MemreasImageLoader.getInstance();
        optionsGallery = MemreasImageLoader.getDefaultDisplayImageOptions();
        optionsStorage = MemreasImageLoader
                .getDefaultDisplayImageOptionsStorage();

        // Set full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // disable the keyboard input
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Set Progress Bars...
        mProgressBar = (ProgressBar) findViewById(R.id.processBar);
        mProgressBar.setVisibility(View.VISIBLE);

        //fetch progress dialog to avoid leaks
        mDialog = MemreasProgressDialog.getInstance(this);

        // fetch layouts and buttons...
        memreas_event_gallery_LinearLayout = (LinearLayout) this
                .findViewById(R.id.memreas_event_gallery_LinearLayout);
        memreas_event_details_LinearLayout = (LinearLayout) this
                .findViewById(R.id.memreas_event_details_LinearLayout);
        memreas_event_location_LinearLayout = (LinearLayout) this
                .findViewById(R.id.memreas_event_location_LinearLayout);

        /**
         * Fetch Gallery related Views
         */
        eventGalleryBtn = (Button) this.findViewById(R.id.eventGalleryBtn);
        eventDetailsBtn = (Button) this.findViewById(R.id.eventDetailsBtn);
        eventDetailsBtn.setTag(Integer.valueOf(0));
        eventLocationBtn = (Button) this.findViewById(R.id.eventLocationBtn);
        btnAddComment = (Button) this.findViewById(R.id.btnAddComment);
        btnAddMedia = (Button) this.findViewById(R.id.btnAddMedia);
        btnAddFriends = (Button) this.findViewById(R.id.btnAddFriends);
        galleryProfile = (View) this.findViewById(R.id.galleryProfile);
        memreas_gallery_event_profile = (ImageView) galleryProfile
                .findViewById(R.id.memreas_event_profile);
        memreas_gallery_event_profile_name = (TextView) galleryProfile
                .findViewById(R.id.memreas_event_profile_name);
        txtGalleryEventName = (TextView) findViewById(R.id.txtGalleryEventName);
        btn_like = (ImageView) findViewById(R.id.btn_like);
        txt_like = (TextView) findViewById(R.id.txt_like);
        btn_comment = (ImageView) findViewById(R.id.btn_comment);
        txt_comment = (TextView) findViewById(R.id.txt_comment);
        friendsLinearLayout = (LinearLayout) findViewById(R.id.friendsLinearLayout);
        memreas_event_media_gridview = (GridView) findViewById(R.id.memreas_event_media_gridview);
        memreas_event_gallery_comments_viewpager = (ViewPager) findViewById(R.id.memreas_event_gallery_comments_viewpager);
        memreas_event_gallery_comments_viewpager_linearlayout = (LinearLayout) findViewById(R.id.memreas_event_gallery_comments_viewpager_linearlayout);

        /**
         * Fetch Details related Views
         */
        memreas_gallery_event_profile = (ImageView) findViewById(R.id.memreas_event_profile);
        memreas_gallery_event_profile_name = (TextView) findViewById(R.id.memreas_event_profile_name);
        txtGalleryEventName = (TextView) findViewById(R.id.txtGalleryEventName);
        detailsProfile = (View) this.findViewById(R.id.detailsProfile);
        memreas_details_event_profile = (ImageView) detailsProfile
                .findViewById(R.id.memreas_event_profile);
        memreas_details_event_profile_name = (TextView) detailsProfile
                .findViewById(R.id.memreas_event_profile_name);
        txtDetailsEventName = (TextView) findViewById(R.id.txtDetailsEventName);
        // @layout/memreas_event_details_image_pager_item
        //
        // Hide download for public events...
        //
        imgBtnDownload = (ImageButton) findViewById(R.id.imgBtnDownload);
        imgBtnLike = (ImageButton) findViewById(R.id.imgBtnLike);
        mediaLikeTxt = (TextView) findViewById(R.id.mediaLikeTxt);
        imgBtnCommentView = (ImageButton) findViewById(R.id.imgBtnCommentView);
        txtCommentText = (TextView) findViewById(R.id.txtCommentText);
        imgBtnSound = (ImageButton) findViewById(R.id.imgBtnSound);
        txtCommentSound = (TextView) findViewById(R.id.txtCommentSound);
        imgBtnMaximize = (ImageButton) findViewById(R.id.imgBtnMaximize);
        imgBtnReport = (ImageButton) findViewById(R.id.imgBtnReport);
        memreas_event_details_view_pager = (ViewPager) findViewById(R.id.memreas_event_details_view_pager);
        memreas_event_comment_details_viewpager = (ViewPager) findViewById(R.id.memreas_event_comment_details_viewpager);
        memreas_event_comment_viewpager_linearlayout = (LinearLayout) findViewById(R.id.memreas_event_comment_viewpager_linearlayout);
        memreas_event_comment_listview = (ListView) findViewById(R.id.memreas_event_comment_listview);
        mediaLinearLayout = (LinearLayout) findViewById(R.id.mediaLinearLayout);
        mediaMenuLinearLayout = (LinearLayout) findViewById(R.id.mediaMenuLinearLayout);
        memreas_event_details_pager_framelayout = (FrameLayout) findViewById(R.id.memreas_event_details_pager_framelayout);

        /**
         * Fetch Location related Views
         */
        locationProfile = (View) this.findViewById(R.id.locationProfile);
        memreas_location_event_profile = (ImageView) locationProfile
                .findViewById(R.id.memreas_event_profile);
        memreas_location_event_profile_name = (TextView) locationProfile
                .findViewById(R.id.memreas_event_profile_name);
        txtLocationEventName = (TextView) findViewById(R.id.txtLocationEventName);
        btnPagerPrev = (ImageButton) findViewById(R.id.btnPagerPrev);
        btnPagerNext = (ImageButton) findViewById(R.id.btnPagerNext);
        event_details_media_location_pager = (ViewPager) findViewById(R.id.event_details_media_location_pager);

        /**
         * Populate tab
         */
        //clickLastTab();

        // Show Ads...
        AdmobView();

        // menuBar
        menuConfiguration();
    }

    // Handle Completed Tab
    @Override
    protected void onResume() {
        super.onResume();
        // Start the service if it's not running...
        startQueueService();

        // Setup LocalBroadcastManager
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("queue-progress"));

        clickLastTab();
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
        super.onPause();
    }

    public void showProgress(String msg) {
        if (!this.isDestroyed()) {
            mDialog.setMessage(msg);
            mDialog.show();
        }
    }

    public void dismiss() {
        if (!this.isDestroyed()) {
            mDialog.dismiss();
        }
    }

    //
    // Store Activity State
    //
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        Bundle bundle = new Bundle();
        bundle.putInt("STATE_SAVED", 1);
        bundle.putInt("position", current_position);
        bundle.putInt("subPosition", current_sub_position);
        bundle.putString("type", sType);
        state_saved = true;

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        current_position = savedInstanceState.getInt("position");
        current_sub_position = savedInstanceState.getInt("subPosition");
        sType = savedInstanceState.getString("type");
        state_saved = true;
    }

    public void clickLastTab() {

        // Get Intent Data for Views
        Bundle data = getIntent().getExtras();
        if (!state_saved) {
            current_position = data.getInt("position", 0);
            current_sub_position = data.getInt("subPosition", 0);
            sType = data.getString("type", "ME");
        } else {
            // do nothing resume restored state
        }

        if (sType.equalsIgnoreCase(EventType.ME.toString())) {
            type = EventType.ME;
            memreasEventMeBean = (MemreasEventMeBean) MemreasEventsMeAdapter
                    .getInstance().getmMemreasList().get(current_position);
            // no need for report for me
            imgBtnReport.setVisibility(View.GONE);
        } else if (sType.equalsIgnoreCase(EventType.FRIENDS.toString())) {
            type = EventType.FRIENDS;
            memreasEventFriendsBean = (MemreasEventFriendsBean) MemreasEventsFriendsAdapter
                    .getInstance().getmMemreasList().get(current_position);
            friendEventDetails = memreasEventFriendsBean
                    .getFriendEventDetailsList().get(current_sub_position);
            // no need for report for friends...
            imgBtnReport.setVisibility(View.GONE);
        } else if (sType.equalsIgnoreCase(EventType.PUBLIC.toString())) {
            type = EventType.PUBLIC;
            memreasEventPublicBean = (MemreasEventPublicBean) MemreasEventsPublicAdapter
                    .getInstance().getmMemreasList().get(current_position);
            imgBtnReport.setVisibility(View.VISIBLE);
			imgBtnDownload.setVisibility(View.GONE);
		}

        /**
         * Setup listeners
         */
        eventGalleryBtn.setOnClickListener(eventGalleryTabListener);
        eventDetailsBtn.setOnClickListener(eventDetailsTabListener);
        eventLocationBtn.setOnClickListener(eventLocationTabListener);
        btnAddComment.setOnClickListener(addCommentButtonListener);
        btnAddMedia.setOnClickListener(addMediaButtonListener);
        btnAddFriends.setOnClickListener(addFriendsButtonListener);

        if (lastTab == LastTab.GALLERY) {
            eventGalleryBtn.performClick();
        } else if (lastTab == LastTab.DETAILS) {
            eventDetailsBtn.performClick();
        } else if (lastTab == LastTab.LOCATION) {
            eventLocationBtn.performClick();
        } else {
            eventGalleryBtn.performClick();
        }
        mProgressBar.setVisibility(View.GONE);
    }

    public void hideTabViews() {
        /*
         * TODO: TBD Views...
		 */
        eventGalleryBtn.setVisibility(View.GONE);
        eventDetailsBtn.setVisibility(View.GONE);
        eventLocationBtn.setVisibility(View.GONE);
        memreas_event_gallery_LinearLayout.setVisibility(View.GONE);
        memreas_event_details_LinearLayout.setVisibility(View.GONE);
        memreas_event_location_LinearLayout.setVisibility(View.GONE);
    }

    /*
     * Location section
     */
    OnClickListener eventLocationTabListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            try {
                // int start_position = (v.getTag() == null) ? ((Integer) v
                // .getTag()).intValue() : 0;
                int start_position = current_position;

                // Remove any prior views...
                hideTabViews();
                lastTab = LastTab.LOCATION;
                memreas_event_location_LinearLayout.setVisibility(View.VISIBLE);

                /**
                 * Setup View
                 */
                eventGalleryBtn.setVisibility(View.VISIBLE);
                eventDetailsBtn.setVisibility(View.VISIBLE);
                eventLocationBtn.setVisibility(View.VISIBLE);
                eventGalleryBtn.setBackgroundResource(R.drawable.btn_gray);
                eventDetailsBtn.setBackgroundResource(R.drawable.btn_gray);
                eventLocationBtn.setBackgroundResource(R.drawable.btn_black);

                /**
                 * Populate Header
                 */
                // MediaShortDetails mediaShortDetails = mediaShortDetailsList
                // .get(current_position);
                memreasImageLoader.displayImage(sProfileUrl,
                        memreas_location_event_profile, optionsGallery,
                        animateProfileListener);
                memreas_location_event_profile_name.setText("@" + sProfileName);
                txtLocationEventName.setText("!" + eventName);

                /**
                 * Setup map with current item
                 */
                // Check for Google Play Services...
                if (checkPlayServices()) {
                    /**
                     * Setup map...
                     */
                    mGoogleMap = new MemreasGoogleMap(R.id.event_location_map,
                            getSupportFragmentManager());

                    /**
                     * Setup media pager
                     */
                    locationDetailsPagerAdapter = new MemreasEventDetailsLocationAdapter(
                            ViewMemreasEventActivity.this, mGoogleMap,
                            getSupportFragmentManager(), mediaShortDetailsList);
                    event_details_media_location_pager
                            .setAdapter(locationDetailsPagerAdapter);
                    event_details_media_location_pager
                            .setCurrentItem(start_position);

                    btnPagerPrev.setOnClickListener(pagerPrevListener);
                    btnPagerNext.setOnClickListener(pagerNextListener);
                }
            } catch (Exception e) {
                Toast.makeText(ViewMemreasEventActivity.this,
                        "an error occurred please check data...", Toast.LENGTH_SHORT)
                        .show();

            }
        }
    };

    protected OnClickListener pagerPrevListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int position = event_details_media_location_pager.getCurrentItem();
            position = (position > 0) ? --position : 0;
            event_details_media_location_pager.setCurrentItem(position);
        }
    };

    protected OnClickListener pagerNextListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int position = event_details_media_location_pager.getCurrentItem();
            position = (position < locationDetailsPagerAdapter.getCount()) ? ++position
                    : locationDetailsPagerAdapter.getCount();
            event_details_media_location_pager.setCurrentItem(position);
        }
    };

    /**
     * Details Section
     */
    OnClickListener eventDetailsTabListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            int start_position = 0;
            if ((v != null) && (!state_saved)) {
                start_position = ((Integer) v.getTag()).intValue();
                current_sub_position = start_position;
            } else {
                start_position = current_sub_position;
            }

            // Remove any prior views...
            hideTabViews();
            lastTab = LastTab.DETAILS;
            memreas_event_details_LinearLayout.setVisibility(View.VISIBLE);

            /**
             * Setup View
             */
            eventGalleryBtn.setVisibility(View.VISIBLE);
            eventDetailsBtn.setVisibility(View.VISIBLE);
            eventLocationBtn.setVisibility(View.VISIBLE);
            eventGalleryBtn.setBackgroundResource(R.drawable.btn_gray);
            eventDetailsBtn.setBackgroundResource(R.drawable.btn_black);
            eventLocationBtn.setBackgroundResource(R.drawable.btn_gray);
            setListeners(true);

            /**
             * Setup this view
             */
            try {
                boolean isImageView = (v instanceof ImageView) ? true : false;
                MediaShortDetails mediaShortDetails = null;
                if (type == EventType.ME) {
                    sProfileUrl = SessionManager.getInstance()
                            .getUser_profile_picture();
                    sProfileName = SessionManager.getInstance().getUser_name();
                    mediaShortDetailsList = memreasEventMeBean.getMediaList();
                    eventId = memreasEventMeBean.getEventId();
                    eventName = memreasEventMeBean.getName();
                    //MemreasEventsMeAdapter.ViewHolder holder;
                    if ((mediaShortDetailsList != null) && (mediaShortDetailsList.size() > 0)) {
                        mediaShortDetails = mediaShortDetailsList.get(start_position);
                    }
                    if ((memreasEventMeBean != null) && (memreasEventMeBean
                            .getCommentList().size() > 0)) {
                        commentDetailsShortDetailsList = memreasEventMeBean
                                .getCommentList();
                    }
                } else if (type == EventType.FRIENDS) {
                    sProfileUrl = memreasEventFriendsBean.getProfile_pic();
                    sProfileName = memreasEventFriendsBean.getEvent_creator();
                    mediaShortDetailsList = friendEventDetails.mediaShortDetailsList;
                    mediaShortDetails = mediaShortDetailsList.get(start_position);
                    eventId = friendEventDetails.eventId;
                    eventName = friendEventDetails.event_name;
                    commentDetailsShortDetailsList = friendEventDetails.commentShortDetailsList;
                } else if (type == EventType.PUBLIC) {
                    sProfileUrl = memreasEventPublicBean.getProfile_pic();
                    sProfileName = memreasEventPublicBean.getEvent_creator();
                    eventId = memreasEventPublicBean.getEvent_id();
                    eventName = memreasEventPublicBean.getEvent_name();
                    mediaShortDetailsList = memreasEventPublicBean
                            .getPublicEventMediaList();
                    mediaShortDetails = mediaShortDetailsList.get(start_position);
                    if ((memreasEventPublicBean != null) && (memreasEventPublicBean
                            .getPublicEventCommentsList().size() > 0)) {
                        commentDetailsShortDetailsList = memreasEventPublicBean
                                .getPublicEventCommentsList();
                    }
                }
                if (mediaShortDetails.media_type.equalsIgnoreCase("video")) {
                    try {
                        if ((mediaShortDetails.media_448x306 != null)
                                && (mediaShortDetails.media_448x306.getString(0) != null)) {
                            sUrl = mediaShortDetails.media_448x306.getString(0);
                        } else if ((mediaShortDetails.media_98x78 != null)
                                && (mediaShortDetails.media_98x78.getString(0) != null)) {
                            sUrl = mediaShortDetails.media_98x78.getString(0);
                        }
                    } catch (JSONException e) {
                        sUrl = "";
                    }
                } else {
                    sUrl = mediaShortDetails.media_url;
                }

                /**
                 * Populate Header
                 */
                memreasImageLoader.displayImage(sProfileUrl,
                        memreas_details_event_profile, optionsGallery,
                        animateProfileListener);
                memreas_details_event_profile_name.setText("@" + sProfileName);
                txtDetailsEventName.setText("!" + eventName);

                /**
                 * Populate Media Pager
                 */
                populateDetailsMediaPager(mediaShortDetailsList, start_position);

                /**
                 * Populate Comments Pager for Media
                 */
                populateDetailsCommentsPager(start_position);
            } catch (Exception e) {
                Log.e(this.getClass().toString(), e.toString());
            }

        }
    };

    public void populateDetailsCommentsPager(int position) {
        int detailLikeCount = 0;
        int detailCommentCount = 0;
        int detailCommentAudioCount = 0;
        try {
            if ((commentDetailsShortDetailsList != null) && (commentDetailsShortDetailsList.size() > 0)) {
                /**
                 * Comment Pager Adapter
                 */
                commentsDetailsPagerAdapter = new MemreasEventsCommentsPagerAdapter(
                        ViewMemreasEventActivity.this, getSupportFragmentManager());
                /**
                 * Comment List Adapter
                 */
                commentsDetailsListAdapter = MemreasEventsCommentsListAdapter
                        .getInstance();
                /**
                 * Build array
                 */
                LinkedList<CommentShortDetails> commentShortDetailsListForMedia = new LinkedList<CommentShortDetails>();
                Iterator<CommentShortDetails> iterator = commentDetailsShortDetailsList
                        .iterator();
                String currentMediaId = mediaId = memreasEventsDetailsPagerAdapter
                        .getmMediaShortDetailsList().get(position).media_id;
                MediaShortDetails mediaShortDetails = mediaShortDetailsList
                        .get(position);
                // Build the array
                while (iterator.hasNext()) {
                    CommentShortDetails commentShortDetails = iterator.next();
                    if (commentShortDetails.media_id.equalsIgnoreCase(currentMediaId)) {
                        commentShortDetailsListForMedia.add(commentShortDetails);
                        if (commentShortDetails.type.equalsIgnoreCase("like")) {
                            detailLikeCount++;
                        }
                        if (commentShortDetails.type.equalsIgnoreCase("audio")) {
                            detailCommentAudioCount++;
                        }
                        if ((commentShortDetails.type.equalsIgnoreCase("text") || (commentShortDetails.text
                                .length() > 0))) {
                            detailCommentCount++;
                        }
                    }
                }

                mediaLikeTxt.setText(String.valueOf(detailLikeCount));
                txtCommentText.setText(String.valueOf(detailCommentCount));
                txtCommentSound.setText(String.valueOf(detailCommentAudioCount));

                /**
                 * Turn off listeners if media was marked inappropriate by user
                 */
                if (!mediaShortDetails.isApproriate(eventId)) {
                    setListeners(false);
                } else {
                    setListeners(true);
                }
                if ((commentShortDetailsListForMedia.size() == 0)
                        || (!mediaShortDetails.isApproriate(eventId))) {
                    memreas_event_comment_viewpager_linearlayout
                            .setVisibility(View.GONE);
                } else {
                    commentsDetailsPagerAdapter
                            .setmCommentsList(commentShortDetailsListForMedia);
                    memreas_event_comment_details_viewpager
                            .setAdapter(commentsDetailsPagerAdapter);
                    commentsDetailsListAdapter
                            .setmCommentList(commentShortDetailsListForMedia);
                    txtCommentText.setOnClickListener(listCommentsDialogListener);
                    memreas_event_comment_viewpager_linearlayout
                            .setVisibility(View.VISIBLE);
                    //
                    // set sub_position for dialogs
                    //
                    current_sub_position = position;

                }
            } // end if ((commentDetailsShortDetailsList != null) && (commentDetailsShortDetailsList.size() > 0))
            else {
                memreas_event_comment_viewpager_linearlayout
                        .setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Toast.makeText(ViewMemreasEventActivity.this,
                    "an error occurred please check data...", Toast.LENGTH_SHORT)
                    .show();

        }

    }

    public void populateDetailsMediaPager(
            LinkedList<MediaShortDetails> mediaShortDetailsList, int position) {

        try {
        /*
         * Media List View
		 */
            memreasEventsDetailsPagerAdapter = new MemreasEventsDetailsPagerAdapter(
                    ViewMemreasEventActivity.this,
                    R.layout.memreas_event_details_image_pager_item,
                    getSupportFragmentManager());
            memreasEventsDetailsPagerAdapter
                    .setmMediaShortDetailsList(mediaShortDetailsList);
            memreas_event_details_view_pager
                    .setAdapter(memreasEventsDetailsPagerAdapter);
            memreas_event_details_view_pager
                    .setOnPageChangeListener(viewPagerOnPageChangeListener);
            memreas_event_details_view_pager.setCurrentItem(position);
        } catch (Exception e) {
            Toast.makeText(ViewMemreasEventActivity.this,
                    "an error occurred please check data...", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    public void setListeners(boolean turnOn) {
        if (turnOn) {
			/*
			 * Setup listeners
			 */
            imgBtnDownload.setOnClickListener(downloadListener);
            imgBtnLike.setOnClickListener(addLikeButtonListener);
            imgBtnCommentView.setOnClickListener(listCommentsDialogListener);
            imgBtnSound.setOnClickListener(addCommentButtonListener);
            imgBtnMaximize.setOnClickListener(mediaMaximizeListener);
            imgBtnReport.setOnClickListener(mediaInappropriateListener);
        } else {
            imgBtnDownload.setOnClickListener(onMediaInappropriateListener);
            imgBtnLike.setOnClickListener(onMediaInappropriateListener);
            imgBtnCommentView.setOnClickListener(onMediaInappropriateListener);
            imgBtnSound.setOnClickListener(onMediaInappropriateListener);
            imgBtnMaximize.setOnClickListener(onMediaInappropriateListener);
            imgBtnReport.setOnClickListener(onMediaInappropriateListener);
        }
    }

    public OnClickListener onMediaInappropriateListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Toast.makeText(ViewMemreasEventActivity.this,
                    "disable due to hidden content...", Toast.LENGTH_SHORT)
                    .show();
        }
    };

    OnPageChangeListener viewPagerOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {
            // TODO Auto-generated method stub
            memreasEventsDetailsPagerAdapter.pauseVideoViews();
            populateDetailsCommentsPager(arg0);
            current_sub_position = arg0;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
        }
    };

    /**
     * Gallery Section
     */
    OnClickListener eventGalleryTabListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            // Remove any prior views...
            hideTabViews();
            lastTab = LastTab.GALLERY;
            memreas_event_gallery_LinearLayout.setVisibility(View.VISIBLE);

            //
            // Reset sub position to 0 since we're back to gallery view
            //
            state_saved = false;
            current_sub_position = 0;

            // Show linear layout ...
            memreas_event_gallery_LinearLayout.setVisibility(View.VISIBLE);
            // Set tab backgrounds
            eventGalleryBtn.setVisibility(View.VISIBLE);
            eventDetailsBtn.setVisibility(View.VISIBLE);
            eventGalleryBtn.setBackgroundResource(R.drawable.btn_black);
            eventDetailsBtn.setBackgroundResource(R.drawable.btn_gray);

            /**
             * Setup listeners
             */
            btn_like.setOnClickListener(addLikeButtonListener);
            mediaId = ""; // reset mediaId for like button...
            btn_comment.setOnClickListener(addCommentButtonListener);

            /**
             * Setup this view
             */
            LinkedList<MediaShortDetails> mediaShortDetailsList = null;
            LinkedList<FriendShortDetails> friendShortDetailsList = null;
            LinkedList<CommentShortDetails> commentShortDetailsList = null;
            if (type == EventType.ME) {
                eventId = memreasEventMeBean.getEventId();
                sEventName = eventName = memreasEventMeBean.getName();
                sProfileUrl = SessionManager.getInstance()
                        .getUser_profile_picture();
                sProfileName = SessionManager.getInstance().getUser_name();
                sLikeCount = String.valueOf(memreasEventMeBean.getLikeCount());
                sCommentCount = String.valueOf(memreasEventMeBean
                        .getCommentCount());
                mediaShortDetailsList = memreasEventMeBean.getMediaList();
                friendShortDetailsList = memreasEventMeBean.getFriendList();
                commentShortDetailsList = memreasEventMeBean.getCommentList();
                if ((commentShortDetailsList != null) && (commentShortDetailsList.size() > 0)) {
                    memreas_event_gallery_comments_viewpager_linearlayout.setVisibility(View.VISIBLE);
                } else {
                    memreas_event_gallery_comments_viewpager_linearlayout.setVisibility(View.GONE);
                }

            } else if (type == EventType.FRIENDS) {
                eventId = friendEventDetails.eventId;
                sEventName = eventName = friendEventDetails.event_name;
                sProfileUrl = memreasEventFriendsBean.getProfile_pic();
                sProfileName = memreasEventFriendsBean.getEvent_creator();
                mediaShortDetailsList = friendEventDetails.mediaShortDetailsList;
                friendShortDetailsList = friendEventDetails.eventFriendShortDetailsList;
                commentShortDetailsList = friendEventDetails.commentShortDetailsList;
                // Determine like count
                Iterator<CommentShortDetails> iterator = commentShortDetailsList
                        .iterator();
                int likeCount = 0;
                int commentCount = 0;
                while (iterator.hasNext()) {
                    CommentShortDetails commentShortDetails = iterator.next();
                    if (commentShortDetails.type.equalsIgnoreCase("like")) {
                        likeCount++;
                    }
                    if (commentShortDetails.type.equalsIgnoreCase("audio")) {
                        commentCount++;
                    }
                    if (commentShortDetails.type.equalsIgnoreCase("text")) {
                        commentCount++;
                    }
                }
                sLikeCount = String.valueOf(likeCount);
                sCommentCount = String.valueOf(commentCount);
            } else if (type == EventType.PUBLIC) {
                eventId = memreasEventPublicBean.getEvent_id();
                sEventName = eventName = memreasEventPublicBean.getEvent_name();
                sProfileUrl = memreasEventPublicBean.getProfile_pic();
                sProfileName = memreasEventPublicBean.getEvent_creator();
                sCommentCount = String.valueOf(memreasEventPublicBean
                        .getEvent_comment_total());
                sLikeCount = String.valueOf(memreasEventPublicBean
                        .getEvent_like_total());
                mediaShortDetailsList = memreasEventPublicBean
                        .getPublicEventMediaList();
                friendShortDetailsList = memreasEventPublicBean
                        .getPublicEventFriendsList();
                commentShortDetailsList = memreasEventPublicBean
                        .getPublicEventCommentsList();
            }

            /**
             * Populate Header
             */
            memreasImageLoader.displayImage(sProfileUrl,
                    memreas_gallery_event_profile, optionsGallery,
                    animateProfileListener);
            memreas_gallery_event_profile_name.setText("@" + sProfileName);
            txtGalleryEventName.setText("!" + eventName);
            txt_like.setText(sLikeCount);
            txt_comment.setText(sCommentCount);

            /**
             * Populate Friends
             */
            populateFriends(friendShortDetailsList);

            /**
             * Populate Media
             */
            populateMedia(mediaShortDetailsList);

            /**
             * Populate Comments
             */
            populateComments(commentShortDetailsList);
        }
    };

    public void populateFriends(
            LinkedList<FriendShortDetails> friendShortDetailsList) {
        /**
         * Populate friends...
         */
        Iterator<FriendShortDetails> friends_iterator = friendShortDetailsList
                .iterator();
        friendsLinearLayout.removeAllViewsInLayout();
        while (friends_iterator.hasNext()) {
            FriendShortDetails friend = friends_iterator.next();
            View friendView = mInflater.inflate(
                    R.layout.memreas_event_gallery_hscroll_friend_item,
                    friendsLinearLayout);
            ImageView friendProfile = (ImageView) friendView
                    .findViewById(R.id.img_friend);

            memreasImageLoader.displayImage(friend.friend_url_image,
                    friendProfile, optionsGallery, animateProfileListener);
        }
    }

    public void populateMedia(
            LinkedList<MediaShortDetails> mediaShortDetailsList) {
        /**
         * Media Grid View for Event...
         */
        MemreasEventsGalleryMediaAdapter.getInstance()
                .setMemreasEventsGalleryMediaAdapterView(
                        ViewMemreasEventActivity.this,
                        R.layout.memreas_event_gallery_item);
        MemreasEventsGalleryMediaAdapter.getInstance().setMediaLinkedList(
                mediaShortDetailsList);
        memreas_event_media_gridview
                .setAdapter(MemreasEventsGalleryMediaAdapter.getInstance());
        memreas_event_media_gridview.setVisibility(View.VISIBLE);
    }

    public void populateComments(
            LinkedList<CommentShortDetails> commentShortDetailsList) {

		/*
		 * Comment List View Build array
		 */
        LinkedList<CommentShortDetails> commentShortDetailsListForGallery = new LinkedList<CommentShortDetails>();
        Iterator<CommentShortDetails> iterator = commentShortDetailsList
                .iterator();
        // Build the array
        while (iterator.hasNext()) {
            CommentShortDetails commentShortDetails = iterator.next();
            // event only comments...
            if ((commentShortDetails.media_id == null)
                    || (commentShortDetails.media_id.equalsIgnoreCase(""))) {
                commentShortDetailsListForGallery.add(commentShortDetails);
            }
        }

		/*
		 * Set the adapter
		 */
        commentsPagerAdapter = new MemreasEventsCommentsPagerAdapter(
                ViewMemreasEventActivity.this, getSupportFragmentManager());
        commentsPagerAdapter.setmCommentsList(commentShortDetailsListForGallery);
        //memreas_event_comment_details_viewpager.setAdapter(commentsPagerAdapter);
        memreas_event_gallery_comments_viewpager.setAdapter(commentsPagerAdapter);

    }

    OnClickListener mediaInappropriateListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(ViewMemreasEventActivity.this,
                    ViewMemreasEventDetailsInappropriateDialogActivity.class);
            intent.putExtra("position", current_position);
            intent.putExtra("sub_position", current_sub_position);
            startActivityForResult(
                    intent,
                    ViewMemreasEventDetailsInappropriateDialogActivity.requestCode);
        }
    };

    OnClickListener mediaMaximizeListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(ViewMemreasEventActivity.this,
                    ViewMemreasEventDetailsFullScreenActivity.class);
            intent.putExtra("position", current_position);
            intent.putExtra("sub_position", current_sub_position);
            intent.putExtra("cancelButton", true);
            mediaShortDetailsListForFullScreenActivity = mediaShortDetailsList;
            startActivity(intent);
        }
    };

    OnClickListener addLikeButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            new LikeParser(ViewMemreasEventActivity.this, mProgressBar)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, eventId,
                            mediaId);
        }
    };

    /*
     * Add media button listener
     */
    OnClickListener addMediaButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ViewMemreasEventActivity.this,
                    AddMemreasMediaActivity.class);
            startActivityForResult(intent, AddMemreasMediaActivity.requestCode);
        }
    };

    /*
     * Add media to background service queue...
     */
    private void handleMedia() {

        mLoadingDialog = MemreasProgressDialog.getInstance(this);
        mLoadingDialog.showWithDelay("adding media...", 1000);

        LinkedList<GalleryBean> selectedMediaLinkedList = MemreasEventsMediaSelectAdapter
                .getInstance().getSelectedList();
        if ((selectedMediaLinkedList != null)
                && (selectedMediaLinkedList.size() > 0)) {
            // Start the service if it's not running...
            startQueueService();

            Iterator<GalleryBean> iterator = selectedMediaLinkedList.iterator();
            GalleryBean media;
            while (iterator.hasNext()) {
                media = iterator.next();
                if (!media.isAddedToQueue()) {
                    // Sync the media - add to the queue
                    if (addedToQueue(media, MemreasTransferModel.Type.UPLOAD)) {
                        media.setAddedToQueue(true);
                    } else {
                        media.setAddedToQueue(false);
                    }
                } // end if media.getType() == GalleryType.NOT_SYNC
            } // end while
        }
        mLoadingDialog.showWithDelay("adding media complete...", 1000);
    }

    /*
     * Download media from the event...
     */
    OnClickListener downloadListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            // Start the service if it's not running...
            startQueueService();

            try {
                MediaShortDetails mediaShortDetails = mediaShortDetailsList
                        .get(current_sub_position);
                /**
                 * Check to ensure it's not already downloaded...
                 */
                GalleryAdapter galleryAdapter = GalleryAdapter.getInstance();
                boolean isServer = false;
                if (galleryAdapter.getHashmapGalleryKeys().containsKey(
                        mediaShortDetails.media_name)) {
                    int pos = galleryAdapter.getHashmapGalleryKeys().get(
                            mediaShortDetails.media_name);
                    GalleryBean media = galleryAdapter.getItem(pos);
                    if (media.getType() == GalleryType.SERVER) {
                        isServer = true;
                    }
                } else {
                    isServer = true;
                }

                if (isServer) {
                    GalleryBean downloadEventMedia = new GalleryBean(
                            GalleryType.SERVER);
                    downloadEventMedia.setEventId(eventId);
                    downloadEventMedia.setMediaId(mediaShortDetails.media_id);
                    downloadEventMedia.setMediaName(mediaShortDetails.media_name);
                    downloadEventMedia.setMediaType(mediaShortDetails.media_type);
                    downloadEventMedia
                            .setMediaUrlS3Path(mediaShortDetails.s3file_media_url_path);
                    downloadEventMedia
                            .setMediaUrlWebS3Path(mediaShortDetails.s3file_media_url_web_path);

                    if (addedToQueue(downloadEventMedia,
                            MemreasTransferModel.Type.DOWNLOAD)) {
                        downloadEventMedia.setAddedToQueue(true);
                        Toast.makeText(ViewMemreasEventActivity.this,
                                "added to download queue...", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        downloadEventMedia.setAddedToQueue(false);
                        Toast.makeText(ViewMemreasEventActivity.this,
                                "failed to add to download queue...",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ViewMemreasEventActivity.this,
                            "media added to gallery...", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(ViewMemreasEventActivity.this,
                        "an error occurred please check media...", Toast.LENGTH_LONG).show();
            }
        }
    };

    /*
     * Add friends button listener
     */
    OnClickListener addFriendsButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ViewMemreasEventActivity.this,
                    AddShareFriendActivity.class);
            startActivityForResult(intent, AddShareFriendActivity.requestCode);
        }
    };

    /*
     * Sends out invites to added friends...
     */
    private void handleFriends() {
        try {
            fetchFriendSelectedList(false);
            AddFriendToShareParser addFriendToEventParser = new AddFriendToShareParser(
                    this, eventId, eventName, mFriendOrGroup);
            // adds memreas friends, sends emails, and sms messages
            addFriendToEventParser.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Generates friends list from adapter...
     */
    public void fetchFriendSelectedList(boolean clearList) {
        if (ShareFriendAdapter.getInstance() != null) {
            Iterator iterator = ShareFriendAdapter.getInstance()
                    .getmFriendOrGroup().iterator();
            mFriendOrGroup = new LinkedList();
            while (iterator.hasNext()) {
                Object obj = iterator.next();
                if (obj instanceof FriendBean) {
                    FriendBean friend = (FriendBean) obj;
                    if (friend.isSelected() && !clearList) {
                        mFriendOrGroup.add(obj);
                    } else if (friend.isSelected() && !clearList) {
                        friend.setSelected(false);
                    }
                } else if (obj instanceof GroupBean) {
                    // handle group
                }
            }
        }
    }

    /*
     * Add comments button listener
     */
    OnClickListener listCommentsDialogListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(ViewMemreasEventActivity.this,
                    ViewMemreasEventListCommentsDialogActivity.class);
            intent.putExtra("contentView",
                    R.layout.memreas_event_list_comments_dialog);
            intent.putExtra("position", current_position);
            intent.putExtra("sub_position", current_sub_position);
            startActivityForResult(intent,
                    ViewMemreasEventListCommentsDialogActivity.requestCode);
        }
    };

    /*
     * Add comments button listener
     */
    OnClickListener addCommentButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                if (memreas_event_details_LinearLayout.getVisibility() == View.VISIBLE) {
                    int currentItem = memreas_event_details_view_pager.getCurrentItem();
                    mediaId = memreasEventsDetailsPagerAdapter
                            .getmMediaShortDetailsList().get(currentItem).media_id;
                } else {
                    mediaId = "";
                }
                Intent intent = new Intent(ViewMemreasEventActivity.this,
                        AddAudioCommentActivity.class);
                if (mAudioCommentFilePath != null) {
                    intent.putExtra("mFilePath", mAudioCommentFilePath);
                    intent.putExtra("mProgress", mAudioCommentDurationInMillis);
                    intent.putExtra("position", current_position);
                    intent.putExtra("sub_position", current_sub_position);
                }
                intent.putExtra("contentView",
                        R.layout.memreas_event_add_comment_dialog);
                startActivityForResult(intent, AddAudioCommentActivity.requestCode);
            } catch (Exception e) {
                Toast.makeText(ViewMemreasEventActivity.this, "an error occurred check data...",
                        Toast.LENGTH_LONG).show();
            }
        }
    };

    /*
     * Add Text and Audio Comments
     */
    private void addTextAndAudioCommentOnDone() {

        // Start the service if it's not running...
        startQueueService();

		/*
		 * Upload Audio Comment
		 */
        try {
            if ((mAudioCommentFilePath != null) && (mAudioBean == null)) {

                File mAudioFile = new File(mAudioCommentFilePath);
                mAudioBean = new GalleryBean(GalleryType.NOT_SYNC);
                mAudioBean.setEventId(eventId);
                mAudioBean.setMediaName(mAudioFile.getName());
                mAudioBean.setLocalMediaPath(mAudioCommentFilePath);
                mAudioBean.setMediaType("audio");
                mAudioBean.setMimeType("audio/mpeg");
                /** Fetch a media Id from batch with MediaId Manager */
                mAudioBean.setMediaId(MediaIdManager.getInstance().fetchNextMediaId());
                if (addedToQueue(mAudioBean, MemreasTransferModel.Type.UPLOAD)) {
                    mAudioBean.setAddedToQueue(true);
                } else {
                    mAudioBean.setAddedToQueue(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		/*
		 * Add Text and/or audio Comment
		 */
        try {
            if ((mAudioBean != null) || (sAddCommentEditText != null)) {
                AddCommentParser addCommentParser = new AddCommentParser(this, mAudioBean);
                addCommentParser.execute(eventId, mediaId, sAddCommentEditText);
            }
            // reset mAudioBean so audio comment isn't re-used...
            mAudioBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Activity results for Add Comment, Media, and Friends buttons...
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // Add Audio Comment
            if (requestCode == AddAudioCommentActivity.requestCode) {
                sAddCommentEditText = data.getStringExtra("addCommentEditText");
                mAudioCommentFilePath = data.getStringExtra("mFilePath");
                mAudioCommentDurationInMillis = data
                        .getIntExtra("mProgress", 0);
                addTextAndAudioCommentOnDone();
                if (commentsPagerAdapter != null) {
                    commentsPagerAdapter.notifyDataSetChanged();
                }
                //current_position = data.getIntExtra("position", 0);
                //current_sub_position = data.getIntExtra("sub_position", 0);
                memreasReloadFlag = true;
            }
            if (requestCode == AddMemreasMediaActivity.requestCode) {
                handleMedia();
                memreasReloadFlag = true;
            }
            if (requestCode == AddShareFriendActivity.requestCode) {
                handleFriends();
                memreasReloadFlag = true;
            }
            if (requestCode == ViewMemreasEventListCommentsDialogActivity.requestCode) {
                // do nothing - use static saved vars for position
                Log.i("POSITION--->", String.valueOf(current_position));
                Log.i("SUB_POSITION--->", String.valueOf(current_sub_position));
                memreasReloadFlag = false;
            }
            if (requestCode == ViewMemreasEventDetailsInappropriateDialogActivity.requestCode) {
                mProgressBar.setVisibility(View.VISIBLE);
                ArrayList<String> reasonTypeList = data
                        .getStringArrayListExtra("reasonTypeList");
                new MediaInappropriateParser(ViewMemreasEventActivity.this,
                        mProgressBar, eventId, SessionManager.getInstance()
                        .getUser_id(), mediaId, reasonTypeList)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    /*
     * Background Queue Service related functions below...
     */
    private boolean addedToQueue(GalleryBean media,
                                 MemreasTransferModel.Type type) {
        try {
            if (!media.isAddedToQueue()) {
                MemreasTransferModel transferModel = new MemreasTransferModel(
                        media);
                transferModel.setEventId(eventId);
                transferModel.setType(type);
                QueueAdapter.getInstance().getSelectedTransferModelQueue()
                        .add(transferModel);
                QueueAdapter
                        .getInstance()
                        .getSelectedMediaHashMap()
                        .put(transferModel.getName(),
                                QueueAdapter.getInstance()
                                        .getSelectedTransferModelQueue()
                                        .indexOf(transferModel));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void startQueueService() {
        // Start the background service here and bind to it
        Intent intent = new Intent(this, QueueService.class);
        intent.putExtra("service", "start");
        startService(intent);
    }

    // handler for received Intents for the ... event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public synchronized void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent

            final String name = intent.getStringExtra("transferModelName");
            final MemreasQueueStatus status = MemreasQueueStatus.valueOf(intent
                    .getStringExtra("status"));

            if (name.equalsIgnoreCase("QueueService")
                    && (status.equals(MemreasQueueStatus.COMPLETED))) {
                // Uploads are finished so update the views...
                ViewMemreasEventActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ViewMemreasEventActivity.this,
                                "media uploaded...", Toast.LENGTH_LONG)
                                .show();
                    }
                });
                if ((MemreasEventsMediaSelectAdapter.getInstance() != null) && (MemreasEventsMediaSelectAdapter.getInstance().getCount() > 0)) {
                    //MemreasEventsMediaSelectAdapter.getInstance().clearSelectedList();
                    MemreasEventsMediaSelectAdapter.getInstance().notifyDataSetChanged();
                }
            }
        } // end onReceive
    }; // end broadcast receiver...

    public void onItemClickViewEvent(int position) {
        eventDetailsBtn.setTag(Integer.valueOf(position));
        eventDetailsBtn.performClick();
    }

    ;

    @Override
    public void onBackPressed() {
        // if there is a fragment and the back stack of this fragment is not
        // empty,
        // then emulate 'onBackPressed' behaviour, because in default, it is not
        // working
        FragmentManager fm = getSupportFragmentManager();
        try {
            for (Fragment frag : fm.getFragments()) {
                if (frag.isVisible()) {
                    FragmentManager childFm = frag.getChildFragmentManager();
                    if (childFm.getBackStackEntryCount() > 0) {
                        childFm.popBackStack();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            // do nothing...
        }
        super.onBackPressed();
    }

    private boolean checkPlayServices() {
        int status = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getApplicationContext());
        if (status != ConnectionResult.SUCCESS) {
            Toast.makeText(
                    ViewMemreasEventActivity.this,
                    "you must install or update Google Play Services for location support",
                    Toast.LENGTH_LONG).show();
            // Go back to the Gallery...
            eventGalleryBtn.performClick();
            return false;
        }
        return true;
    }

}
