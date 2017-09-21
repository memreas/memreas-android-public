
package com.memreas.gallery;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.memreas.R;
import com.memreas.base.BaseActivity;
import com.memreas.gallery.GalleryBean.GalleryType;
import com.memreas.location.MediaMapLocationAdapter;
import com.memreas.queue.QueueActivity;
import com.memreas.queue.SyncAdapter;
import com.memreas.sax.handler.CommonHandler;
import com.memreas.sax.handler.UpdateMediaParser;
import com.memreas.util.MemreasProgressDialog;

import java.io.IOException;
import java.util.List;

public class GalleryActivity extends BaseActivity {

    private enum LastTab {
        VIEW, EDIT, SYNC, LOCATION
    }

    ;

    private RelativeLayout rootLayout;

    // Tabs
    private Button btnViewGallery;
    private Button btnShootView;
    private Button btnSyncBtn;
    private Button btnLocationBtn;
    public static LastTab lastTab;

    // View related
    private FrameLayout galleryLayout;
    private FrameLayout fullScreenLayout;
    private GridView galleryGridView;
    private ViewPager viewPager;
    public static boolean isAsyncLoadComplete = false;
    private SwipeRefreshLayout swipeContainer;


    // Sync related
    private FrameLayout syncLayout;
    private GridView syncGridView;
    private LinearLayout syncOptionLayout;
    private SyncAdapter syncAdapter;
    private Button btnSyncClear;
    private Button btnSyncDone;
    private MediaMapLocationAdapter mMapLocationAdapter;
    private ImageView btnGreenColor;
    private ImageView btnYellowColor;
    private ImageView btnRedColor;

    // Shoot related
    //private LinearLayout shootLayout;
    private FrameLayout shootLayout;

    // Location related
    private LinearLayout locationLayout;
    private LatLng currentLatLng;
    private ViewPager locationPager;
    private int mMapCurrentPage;
	private Context context;

    private ProgressBar mainProgressBar;

    public static boolean menuGallery;
    public static boolean menuSyncGallery;
    public static boolean menuLocationGallery;
    public static boolean adp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainProgressBar = (ProgressBar) findViewById(R.id.processBar);
        mLoadingDialog = MemreasProgressDialog.getInstance(this);
        if (!isAsyncLoadComplete) {
            mLoadingDialog.setMessage("intializing media...");
            mLoadingDialog.show();
            //mLoadingDialog.setCancelable(false);
        }

        // Fetch the view
        setContentView(R.layout.screen_gallery_page_ref);
        PREV_INDEX = GALLERY_INDEX;

        // Set full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // disable the keyboard input
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        menuGallery = true;
        // get sub-layouts
        rootLayout = (RelativeLayout) findViewById(R.id.RootLyt);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        galleryLayout = (FrameLayout) findViewById(R.id.galleryLyt);
        shootLayout = (FrameLayout) findViewById(R.id.shootLyt);
        syncLayout = (FrameLayout) findViewById(R.id.syncLyt);
        locationLayout = (LinearLayout) findViewById(R.id.locationLyt);

        // Setup Tabs (Buttons)...
        // View Gallery
        btnViewGallery = (Button) findViewById(R.id.btnViewGallery);
        btnViewGallery.setOnClickListener(mHandleBtnViewGallery);
        swipeContainer.setOnRefreshListener(mHandleSwipeGalleryRefresh);

        // Edit Gallery
        btnShootView = (Button) findViewById(R.id.btnShootView);
        btnShootView.setOnClickListener(mHandleBtnShootView);

        // Sync Gallery
        btnSyncBtn = (Button) findViewById(R.id.btnSyncBtn);
        btnSyncBtn.setOnClickListener(mHandleBtnSyncBtn);

        // Location Gallery
        btnLocationBtn = (Button) findViewById(R.id.btnLocationBtn);
        btnLocationBtn.setOnClickListener(mHandleBtnLocationBtn);

        // Creating View tab so set it
        btnViewGallery.performClick();

        // Show Ads...
        AdmobView();

        //
        // Fetch the copyright manager for later use...
        // instantiates the singleton
        //
        CopyrightManager.getInstance();

    }

    @Override
    public void onBackPressed() {
        // do nothing. We want to force user to stay in this activity and not drop out.
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (lastTab == LastTab.VIEW) {
            btnViewGallery.performClick();
        } else if (lastTab == LastTab.EDIT) {
            btnShootView.performClick();
        } else if (lastTab == LastTab.SYNC) {
            btnSyncBtn.performClick();
        } else if (lastTab == LastTab.LOCATION) {
            btnLocationBtn.performClick();
        }
    }

    @Override
    protected void onStop() {
        mLoadingDialog.dismiss();
        super.onStop();
    }

    private SwipeRefreshLayout.OnRefreshListener mHandleSwipeGalleryRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            galleryReloadFlag = true;
            btnViewGallery.performClick();
            swipeContainer.setRefreshing(false);
        }
    };


    public void onItemClick(int position) {
        // Start Image Pager...
        Intent intent = new Intent(this, GalleryPagerActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    ;

    public void hideTabViews() {
        galleryLayout.setVisibility(View.GONE);
        syncLayout.setVisibility(View.GONE);
        shootLayout.setVisibility(View.GONE);
        locationLayout.setVisibility(View.GONE);
    }

    private OnClickListener mHandleBtnViewGallery = new OnClickListener() {
        @Override
        public void onClick(View v) {

            // Set tab backgrounds
            mainProgressBar = (ProgressBar) findViewById(R.id.processBar);
            btnViewGallery.setBackgroundResource(R.drawable.btn_black);
            btnShootView.setBackgroundResource(R.drawable.btn_gray);
            btnSyncBtn.setBackgroundResource(R.drawable.btn_gray);
            btnLocationBtn.setBackgroundResource(R.drawable.btn_gray);
            lastTab = LastTab.VIEW;

            // Remove any prior views...
            hideTabViews();

            // Setup view layout
            galleryLayout = (FrameLayout) findViewById(R.id.galleryLyt);
            galleryLayout.setVisibility(View.VISIBLE);
            galleryGridView = (GridView) findViewById(R.id.gridview);
            fullScreenLayout = (FrameLayout) findViewById(R.id.fullScreenLayout);
            viewPager = (ViewPager) findViewById(R.id.imagepager);

            if ((null == GalleryAdapter.getInstance())
                    || (galleryReloadFlag == true)) {
                GalleryAdapter.reset();
                GalleryAdapter.getInstance(GalleryActivity.this,
                        R.layout.galleryitem_main);
                galleryGridView.setAdapter(GalleryAdapter.getInstance());

                if (mApplication.isOnline()) {
                    // run loadMemreasGalleryAsyncTask after
                    new LoadLocalGalleryAsyncTask(GalleryActivity.this,
                            GalleryAdapter.getInstance(), true)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    galleryReloadFlag = false;
                } else {
                    // don't run loadMemreasGalleryAsyncTask after
                    new LoadLocalGalleryAsyncTask(GalleryActivity.this,
                            GalleryAdapter.getInstance(), false)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    galleryReloadFlag = false;
                }
            } else {
                // Load already happened...
                galleryGridView.setAdapter(GalleryAdapter.getInstance());
            }
        }
    };


    static final int MCAMERA_REQUEST = 1;  // The request code
    private OnClickListener mHandleBtnShootView = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // Set tabs to reflect selected
            btnViewGallery.setBackgroundResource(R.drawable.btn_gray);
            btnShootView.setBackgroundResource(R.drawable.btn_black);
            btnSyncBtn.setBackgroundResource(R.drawable.btn_gray);
            btnLocationBtn.setBackgroundResource(R.drawable.btn_gray);
            lastTab = LastTab.EDIT;

            // Remove any prior views...
            hideTabViews();

            Intent intent = new Intent();
            intent.setClass(GalleryActivity.this, MCameraActivity.class);
            startActivityForResult(intent, MCAMERA_REQUEST);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == MCAMERA_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                btnViewGallery.performClick();
            }
        }
    }

    private OnClickListener mHandleBtnSyncBtn = new OnClickListener() {
        @Override
        public void onClick(View v) {

            // setup the main buttons for highlights
            btnViewGallery.setBackgroundResource(R.drawable.btn_gray);
            btnShootView.setBackgroundResource(R.drawable.btn_gray);
            btnSyncBtn.setBackgroundResource(R.drawable.btn_black);
            btnLocationBtn.setBackgroundResource(R.drawable.btn_gray);
            lastTab = LastTab.SYNC;

            // Remove any prior views...
            hideTabViews();

            // Setup view layout
            syncLayout = (FrameLayout) findViewById(R.id.syncLyt);
            syncLayout.setVisibility(View.VISIBLE);
            if (null == syncAdapter) {

                // Setup view
                syncGridView = (GridView) findViewById(R.id.syncgridview);
                btnSyncClear = (Button) findViewById(R.id.btnSyncClear);
                btnSyncDone = (Button) findViewById(R.id.btnSyncDone);
                btnGreenColor = (ImageView) findViewById(R.id.GreenColor);
                btnYellowColor = (ImageView) findViewById(R.id.YellowColor);
                btnRedColor = (ImageView) findViewById(R.id.RedColor);

                // Populate the grid
                syncAdapter = SyncAdapter.getInstance(GalleryActivity.this,
                        R.layout.gallerysyncitem_main);
                syncGridView.setAdapter(syncAdapter);

                // Setup onClick functions...
                btnGreenColor.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Toast.makeText(BaseActivity.mApplication.getApplicationContext(), "green - sync'd media", Toast.LENGTH_LONG).show();
                    }
                });

                btnYellowColor.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Toast.makeText(BaseActivity.mApplication.getApplicationContext(), "yellow - cloud media", Toast.LENGTH_LONG).show();
                    }
                });

                btnRedColor.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Toast.makeText(BaseActivity.mApplication.getApplicationContext(), "red - device media", Toast.LENGTH_LONG).show();
                    }
                });


                btnSyncClear.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        syncAdapter.clearSelectedMedia();
                    }
                });

                btnSyncDone.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (SyncAdapter.getInstance().getSyncCountSelected() != 0) {
                            SyncAdapter.getInstance().fetchMediaIds();
                            Intent intent = new Intent(GalleryActivity.this,
                                    QueueActivity.class);
                            startActivity(intent);
                        } else {
                            // Check the counts
                            int gallerySelected = 0;
                            GalleryBean media;
                            for (int i = 0; i > GalleryAdapter.getInstance()
                                    .getCount(); i++) {
                                media = GalleryAdapter.getInstance()
                                        .getGalleryImageList().get(i);
                                if (media.isSelected()) {
                                    gallerySelected++;
                                }
                            }
                            if (gallerySelected == SyncAdapter.getInstance()
                                    .getSyncCountSelected()) {
                                SyncAdapter.getInstance().fetchMediaIds();
                                // Try again to launch
                                Intent intent = new Intent(
                                        GalleryActivity.this,
                                        QueueActivity.class);
                                startActivity(intent);
                            } else {
                                // Engage user to reselect...
                                Toast.makeText(GalleryActivity.this,
                                        "to sync touch done",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }

        }
    };

    private OnClickListener mHandleBtnLocationBtn = new OnClickListener() {
        @Override
        public void onClick(View v) {

            // Check for Google Play Services...
            if (checkPlayServices()) {

                // Set tabs to reflect selected
                btnViewGallery.setBackgroundResource(R.drawable.btn_gray);
                btnShootView.setBackgroundResource(R.drawable.btn_gray);
                btnSyncBtn.setBackgroundResource(R.drawable.btn_gray);
                btnLocationBtn.setBackgroundResource(R.drawable.btn_black);
                lastTab = LastTab.LOCATION;

                // Remove any prior views...
                hideTabViews();

                locationLayout = (LinearLayout) findViewById(R.id.locationLyt);
                locationLayout.setVisibility(View.VISIBLE);

                // Set onClick listener for location search...
                Button btnSetLocation = (Button) findViewById(R.id.btnSetLocation);
                btnSetLocation.setOnClickListener(setLocationListener);

                // Set onClick listener for location search...
                Button btnCancelLocation = (Button) findViewById(R.id.btnCancelLocation);
                btnCancelLocation.setOnClickListener(cancelLocationListener);

                // Set onClick listener for location search...
                ImageView btnSearchLocation = (ImageView) findViewById(R.id.btnSearchLocation);
                btnSearchLocation.setOnClickListener(searchLocationListener);

                // Populate the pager
                locationPager = (ViewPager) findViewById(R.id.locationimagepager);
                mMapLocationAdapter = MediaMapLocationAdapter.getInstance(
                        (Context) GalleryActivity.this,
                        getSupportFragmentManager());
                locationPager.setAdapter(mMapLocationAdapter);
                locationPager.setCurrentItem(mMapCurrentPage, true);
                updateMapLocation(null);
                locationPager.setOnPageChangeListener(locationPagerListener);

                // Add listener for location image pager previous image/button
                View btnPagerPrev = (View) findViewById(R.id.btnPagerPrev);
                btnPagerPrev.setOnClickListener(pagerPrevListener);

                // Add listener for location image pager previous image/button
                View btnPagerNext = (View) findViewById(R.id.btnPagerNext);
                btnPagerNext.setOnClickListener(pagerNextListener);
            }// end check for Google Play Services...
        } // end new OnClickListener...

    }; // End mHandleBtnLocationBtn

    protected OnClickListener setLocationListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // Fetch the current lat long...
            GalleryBean bean = GalleryAdapter.getInstance()
                    .getItem(mMapCurrentPage);
            if ((null != currentLatLng)
                    && ((bean.getType() == GalleryType.SERVER))
                    || (bean.getType() == GalleryType.SYNC)) {

                // Get the address from the search bar
                EditText txtSearchLocaton = (EditText) findViewById(R.id.searchLocationView);
                String address = "";
                if (txtSearchLocaton.getText().toString().trim()
                        .length() > 0) {
                    address = txtSearchLocaton.getText().toString();
                } else {
                    Geocoder geo = new Geocoder(getBaseContext());
                    List<Address> address_list;
                    try {
                        /*
						 * Sample address Address Line 1 Address
						 * Line 2... City, State Postal_Code Country
						 */
                        address_list = geo.getFromLocation(
                                currentLatLng.latitude,
                                currentLatLng.longitude, 1);
                        for (int i = 0; i < address_list.get(0)
                                .getMaxAddressLineIndex(); i++) {
                            address = null != address_list.get(0)
                                    .getAddressLine(i) ? address_list
                                    .get(0).getAddressLine(i)
                                    + ",\n" : "";
                        }
                        address += null != address_list.get(0)
                                .getLocality() ? address_list
                                .get(0).getLocality() + ", " : "";
                        address += null != address_list.get(0)
                                .getAdminArea() ? address_list.get(
                                0).getAdminArea()
                                + " " : "";
                        address += null != address_list.get(0)
                                .getPostalCode() ? address_list
                                .get(0).getPostalCode() + "\n" : "";
                        address += null != address_list.get(0)
                                .getCountryName() ? address_list
                                .get(0).getCountryName() : "";
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        address = "";
                    }

                }

                CommonHandler.commonList = null;
                mainProgressBar.setVisibility(View.VISIBLE);
                new UpdateMediaParser(GalleryActivity.this,
                        mainProgressBar).executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR,
                        bean.getMediaId(),
                        String.valueOf(currentLatLng.latitude),
                        String.valueOf(currentLatLng.longitude),
                        address);
            } else {
                Toast.makeText(
                        GalleryActivity.this,
                        "location update available for server and sync media only",
                        Toast.LENGTH_SHORT).show();

            }
        }
    };

    protected OnClickListener cancelLocationListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // reset location to old view
            EditText txtSearchLocaton = (EditText) findViewById(R.id.searchLocationView);
            txtSearchLocaton.setText("");
            updateMapLocation(null);
        }
    };

    protected OnClickListener searchLocationListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            EditText txtSearchLocaton = (EditText) findViewById(R.id.searchLocationView);
            if (txtSearchLocaton.getText().toString().trim()
                    .length() > 0) {
                String locationName = txtSearchLocaton.getText()
                        .toString().trim();
                Geocoder geo = new Geocoder(getBaseContext());
                List<Address> address;
                LatLng latLng = new LatLng(0, 0);
                try {
                    address = geo.getFromLocationName(locationName,
                            1);
                    if (address.size() > 0) {
                        latLng = new LatLng(address.get(0)
                                .getLatitude(), address.get(0)
                                .getLongitude());
						/*
						// hide the keyboard
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(
								txtSearchLocaton.getWindowToken(),
								0);
						*/
                        updateMapLocation(latLng);
                    } else {
                        // hide the keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(
                                txtSearchLocaton.getWindowToken(),
                                0);
                        Toast.makeText(
                                GalleryActivity.this,
                                "address could not be found, try again",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    protected OnPageChangeListener locationPagerListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            // Update the map view onFocusChange of the
            // image...
            mMapCurrentPage = position;
            updateMapLocation(null);
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

    protected OnClickListener pagerPrevListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int position = mMapCurrentPage;
            position = (position > 0) ? --position : 0;
            locationPager.setCurrentItem(position);
        }
    };

    protected OnClickListener pagerNextListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int position = mMapCurrentPage;
            position = (position < mMapLocationAdapter.getCount()) ? ++position
                    : mMapLocationAdapter.getCount();
            locationPager.setCurrentItem(position);
        }
    };

    protected void updateMapLocation(LatLng latlng) {
        GoogleMap map;
        GalleryBean media = GalleryAdapter.getInstance().getItem(
                mMapCurrentPage);
        LatLng markerLatLong;
        Marker marker;
        double latitude;
        double longitude;

        // Init the map...
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        map.clear();

        if (null != latlng) {
            // Case for search for location...
            currentLatLng = latlng;
            markerLatLong = latlng;
            marker = map.addMarker(new MarkerOptions().position(markerLatLong));
            marker.setIcon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLong, 15));

            // Zoom in, animating the camera.
            map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        } else if ((null == latlng)
                && (media.getLatitude() != 0 && media.getLongitude() != 0)) {
            // use the bean...
            latitude = media.getLatitude();
            longitude = media.getLongitude();
            markerLatLong = new LatLng(latitude, longitude);
            currentLatLng = markerLatLong;
            marker = map.addMarker(new MarkerOptions().position(markerLatLong));
            marker.setTitle(media.getMediaName());
            marker.setIcon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLong, 15));

            // Zoom in, animating the camera.
            map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        } else {
            map.moveCamera(CameraUpdateFactory.zoomTo(2));
            // Zoom in, animating the camera.
            map.animateCamera(CameraUpdateFactory.zoomTo(2), 2000, null);
            LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
            markerLatLong = bounds.getCenter();
            marker = map.addMarker(new MarkerOptions().position(markerLatLong));
            marker.setIcon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            Toast.makeText(
                    this,
                    "media has no location set - use search to select and update",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPlayServices() {
        int status = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getApplicationContext());
        if (status != ConnectionResult.SUCCESS) {
            Toast.makeText(
                    GalleryActivity.this,
                    "you must install or update Google Play Services for location support",
                    Toast.LENGTH_LONG).show();
            // Go back to the Gallery...
            btnViewGallery.performClick();
            return false;
        }
        return true;
    }

}
