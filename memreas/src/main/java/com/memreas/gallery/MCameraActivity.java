
package com.memreas.gallery;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.CamcorderProfile;
import android.media.ExifInterface;
import android.media.ImageReader;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.memreas.R;
import com.memreas.base.BaseActivity;
import com.memreas.base.Common;
import com.memreas.base.Foreground;
import com.memreas.base.SessionManager;
import com.memreas.queue.MemreasTransferModel;
import com.memreas.queue.QueueAdapter;
import com.memreas.queue.QueueService;
import com.memreas.util.ImageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class MCameraActivity extends BaseActivity {

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }


    protected static final int MEDIA_TYPE_IMAGE = 1;
    protected static final int MEDIA_TYPE_VIDEO = 2;
    protected static final String TAG = MCameraActivity.class.getName();
    protected static int frontCamera = -1;
    protected static int backCamera = -1;
    protected boolean isBackCameraSelected = false;
    protected boolean isRecordingVideo = false;
    protected boolean hasBackCamera = false;
    protected boolean hasFrontCamera = false;
    protected boolean isImageButtonSelected = true;

    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    protected static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    protected Uri fileUri;

    protected Button btnImage;
    protected Button btnVideo;
    protected ImageView imgviewThumbnail;
    protected ImageView imgviewRecord;
    protected TextView textviewTimer;
    protected ImageView imgviewCameraSelect;

    protected MImageCapture mImageCapture;
    protected MVideoCapture mVideoCapture;

    protected CameraManager mCameraManager;
    protected Camera mCamera;
    protected CameraCharacteristics mCameraCharactersticsFront;
    protected CameraCharacteristics mCameraCharactersticsBack;
    protected LinearLayout previewView;
    protected TextView previewViewCopyright;
    //protected FrameLayout previewView;
    protected boolean isPortrait;
    protected int screenWidth;
    protected int screenHeight;

    protected CopyrightManager copyrightManager;
    protected String copyrightMD5;
    protected String copyrightSHA1;
    protected String copyrightSHA256;
    protected JSONObject copyrightJsonObject;

    protected File pictureFile;
    protected File videoFile;

    protected static ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        super.hideStatusBar();
        setContentView(R.layout.gallery_shoot_view);
        mProgressBar = (ProgressBar) findViewById(R.id.processBar);
        mProgressBar.setVisibility(View.VISIBLE);


        /** Handle button selection */
        btnImage = (Button) findViewById(R.id.btnImage);
        btnVideo = (Button) findViewById(R.id.btnVideo);
        btnImage.setOnClickListener(mHandleBtnImageVideo);
        btnVideo.setOnClickListener(mHandleBtnImageVideo);
        btnImage.setTextColor(Color.BLUE);

        /** Set view for thumbnail */
        imgviewThumbnail = (ImageView) findViewById(R.id.cameraThumbnail);

        /** Set button for record and add onClickListener */
        imgviewRecord = (ImageView) findViewById(R.id.recordBtn);
        imgviewRecord.setOnClickListener(mHandleRecordButton);

        /** Set textviewTimer */
        textviewTimer = (TextView) findViewById(R.id.recorderTimer);

        /** Set Back/Front Camera Selection */
        imgviewCameraSelect = (ImageView) findViewById(R.id.cameraSwitch);
        imgviewCameraSelect.setOnClickListener(mHandleCameraSwitchButton);


        /** find cameras by id */
        fetchCameras();

        /** fetch camera preview parent holder */
        previewView = (LinearLayout) findViewById(R.id.camera_preview);
        previewViewCopyright = (TextView) findViewById(R.id.copyright);

        //
        // fetch copyright
        //
        setNewCopyright();

        //
        // Set to image portrait as default
        //
        btnImage.performClick();

        /** stop progress tracker */
        mProgressBar.setVisibility(View.GONE);

        /** fetch screen width and height */
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

    }

    @Override
    protected void onPause() {
        super.onPause();
        onBackPressed();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //
        // Start over...
        //
        if (Foreground.getInstance().isBackground()) {
            Foreground.getInstance().hasReturnedFromBackground(true);
            //restart the activity
            onStart();
        } else {
            if (mCamera == null) {
                //
                // Setup camera and preview
                //
                fetchCameras();
                setupCameraAndPreview();
            } else {
                //if (isImageButtonSelected) {
                mCamera.stopPreview();
                mCamera.startPreview();
                //}
            }
            // Start the service if it's not running...
            startQueueService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        onBackPressed();
    }

    //
    // Override onBackPressed
    //
    @Override
    public void onBackPressed() {
        //release capture objects to release
        releaseImageObjects();
        releaseVideoCapturebjects(true);

        //Close the activity
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Handle button selection
     */
    private OnClickListener mHandleBtnImageVideo = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Button btn = (Button) v;
            if (btn == btnImage) {
                isImageButtonSelected = true;
                btn.setTextColor(Color.BLUE);
                btnVideo.setTextColor(Color.WHITE);

                /** Setup CameraPreview for Image Capture */
                isImageButtonSelected = true;
                previewViewCopyright.setVisibility(View.GONE);
                setupCameraAndPreview();
            } else if (btn == btnVideo) {
                btn.setTextColor(Color.BLUE);
                btnImage.setTextColor(Color.WHITE);
                isImageButtonSelected = false;
                previewViewCopyright.setVisibility(View.VISIBLE);
                setupCameraAndPreview();
            }
        }
    };

    /**
     * Handle button selection
     */
    private View.OnClickListener mHandleCameraSwitchButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String selectedCamera = (String) v.getTag();
            if (selectedCamera.equalsIgnoreCase("backCamera")) {
                isBackCameraSelected = false;
                imgviewCameraSelect.setTag("frontCamera");
                imgviewCameraSelect.setImageResource(R.drawable.blue_camera_front_icon);
            } else {
                isBackCameraSelected = true;
                imgviewCameraSelect.setTag("backCamera");
                imgviewCameraSelect.setImageResource(R.drawable.blue_camera_back_icon);
            }
            setupCameraAndPreview();
        }
    };

    /**
     * Handle button selection
     */
    private View.OnClickListener mHandleRecordButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isImageButtonSelected) {

                int rotation = MCameraActivity.this.getWindowManager().getDefaultDisplay()
                        .getRotation();
                int screenOrientation = getResources().getConfiguration().orientation;

                //
                // Handle Image Capture
                //
                mCamera.takePicture(null, null, mPicture);
            } else {
                //
                // Handle Video Capture
                //
                if (isRecordingVideo) {

                    //
                    // Stop recording
                    //
                    try {
                        mVideoCapture.mPreviewSession.stopRepeating();
                        mVideoCapture.mPreviewSession.abortCaptures();
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                    mVideoCapture.mMediaRecorder.stop();
                    long length = videoFile.length();
                    isRecordingVideo = false;

                    //
                    // Change image view to not recording here
                    //
                    imgviewRecord.setImageResource(R.drawable.media_recording_start);

                    //
                    // Set the thumbnail
                    //
                    final Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoFile.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                    imgviewThumbnail.setImageBitmap(thumbnail);


                    //
                    // Add video to Gallery
                    // Save what was recorded here...
                    //
                    new Thread(new Runnable() {
                        public void run() {

                            /** Poor quality on video so inscribe will happen on server
                             //
                             // Inscribe Video Task
                             //

                             //File mInscribedVideoFile = getOutputMediaFile(MEDIA_TYPE_VIDEO);
                             //MInscribeVideoTask mInscribeVideoTask = new MInscribeVideoTask(videoFile, copyrightJsonObject, mInscribedVideoFile, screenWidth, screenHeight);
                             //mInscribedVideoFile = mInscribeVideoTask.inscribeCopyright();
                             */


                            if (videoFile != null) {
                                //
                                // Add Image to Photos Gallery
                                //
                                //final Uri uri = addMediaToGallery(mInscribedVideoFile.getAbsolutePath(), MCameraActivity.this, "video");
                                //final Uri uri = addMediaToGallery(videoFile.getAbsolutePath(), MCameraActivity.this, "video");
                                //Log.e(TAG, "uri--->" + uri.getPath());

                                //
                                // Create a media bean and set the copyright for server flags
                                //
                                String media_id = "";
                                try {
                                    media_id = copyrightJsonObject.getString("media_id");
                                    copyrightJsonObject.put("applyCopyrightOnServer", 1);
                                    copyrightJsonObject.put("device_id", SessionManager.getInstance().getDevice_id());
                                    copyrightJsonObject.put("device_type", Common.DEVICE_TYPE);
                                } catch (JSONException e) {
                                    // this shouldn't occur
                                    Log.e(TAG, "JSONException trying to get media_id from copyright!");
                                }
                                String mediaName = ImageUtils.getInstance()
                                        .getImageNameFromPath(videoFile.getAbsolutePath());
                                GalleryBean media = new GalleryBean(videoFile.getAbsolutePath(),
                                        mediaName,
                                        "video",
                                        media_id,
                                        false,
                                        thumbnail,
                                        GalleryBean.GalleryType.NOT_SYNC);
                                media.setDeviceId(SessionManager.getInstance().getDevice_id());
                                media.setDeviceType(Common.DEVICE_TYPE);
                                String mediaNamePrefix = mediaName.substring(0,
                                        mediaName.lastIndexOf('.'));
                                media.setMediaNamePrefix(mediaNamePrefix);
                                media.setMediaThumbBitmap(thumbnail);
                                media.setLocalMediaPath(videoFile.getAbsolutePath());
                                Date date = new Date();
                                media.setMediaDate(date.getTime());
                                //String mimeType = getContentResolver().getType(uri);
                                media.setMimeType("video/mp4"); // must be set for Amazon.
                                media.setCopyright(copyrightJsonObject.toString());

                                MemreasTransferModel transferModel = new MemreasTransferModel(
                                        media);
                                transferModel.setType(MemreasTransferModel.Type.UPLOAD);
                                QueueAdapter.getInstance().getSelectedTransferModelQueue()
                                        .add(transferModel);

                                // Start the service if it's not running...
                                startQueueService();
                            } else {
                                //
                                // Show Toast error occurred here...
                                //
                                videoFile.delete();
                            }

                        }
                    }).start();

                    //
                    // fetch copyright - used one above
                    //
                    setNewCopyright();
                    //} catch (IOException ioe) {
                    //    /** alert that orientation is set for camcorder */
                    //    Toast.makeText(MCameraActivity.this,
                    //            "error saving video...",
                    //            Toast.LENGTH_SHORT).show();
                    //    Log.e(TAG, "error saving video:" + ioe.getMessage());
                    //}

                    //
                    // Reset video to re-rerun prepare here.
                    //
                    setupCameraAndPreview();


                } else {
                    try {
                        /**
                         * New Camera2 code start here...
                         */
                        mVideoCapture.mMediaRecorder.start();
                        isRecordingVideo = true;
                        //
                        // Change image view to recording here
                        //
                        imgviewRecord.setImageResource(R.drawable.media_recording);


                    } catch (Exception e) {
                        // Something has gone wrong! Release the camera
                        //mVideoCapture.releaseMediaRecorder();
                        Toast.makeText(MCameraActivity.this,
                                "error: could not start video...",
                                Toast.LENGTH_LONG).show();

                        /** reset capture back to image */
                        btnImage.performClick();
                    }
                }
            }
        }
    };

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            //
            // Add Image To Gallery
            //
            final byte[] mdata = data;
            new Thread(new Runnable() {
                public void run() {
                    //
                    // Create copy of photo
                    //
                    Bitmap photo = BitmapFactory.decodeByteArray(mdata, 0, mdata.length).copy(Bitmap.Config.RGB_565, true);

                    //
                    // Correct rotation issues with orientation (i.e. landscape right saved upside down)
                    //
                    float rotate = 0.0f;
                    Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                    int rotation = display.getRotation();
                    if (rotation == Surface.ROTATION_0) {
                        //Portrait
                        if (!isBackCameraSelected) {
                            rotate = 270;
                        } else {
                            rotate = 90;
                        }
                    } else if (rotation == Surface.ROTATION_90) {
                        //Landscape left - ok for both front and back
                        rotate = 0;
                    } else if (rotation == Surface.ROTATION_180) {
                        rotate = 270;
                    } else if (rotation == Surface.ROTATION_270) {
                        //Landscape right
                        rotate = 180;
                    }

                    Bitmap scaled;
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        // Notice that width and height are reversed
                        scaled = Bitmap.createScaledBitmap(photo, screenHeight, screenWidth, true);
                    } else {// LANDSCAPE MODE
                        //No need to reverse width and height
                        scaled = Bitmap.createScaledBitmap(photo, screenWidth, screenHeight, true);
                        //photo = scaled;
                    }
                    int w = scaled.getWidth();
                    int h = scaled.getHeight();
                    // Setting post rotate to 90
                    Matrix mtx = new Matrix();
                    mtx.postRotate(rotate);
                    // Rotating Bitmap
                    photo = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);

                    //
                    // Write the copyright
                    //
                    float scale = getResources().getDisplayMetrics().density;
                    Canvas canvasView = new Canvas(photo);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setColor(Common.COPYRIGHT_TEXT_COLOR);
                    paint.setTextSize((int) (Common.COPYRIGHT_TEXT_SIZE * scale));

                    paint.setShadowLayer(1f, 0f, 1f, Color.LTGRAY);
                    String mRight = "md5:" + copyrightMD5 + " sha1:" + copyrightSHA1;
                    //canvasView.drawText(mRight, 0, 25, paint);

                    canvasView.save();
                    canvasView.rotate(rotate, 0, Common.COPYRIGHT_Y_AXIS_OFFSET);
                    canvasView.drawText(mRight, 0, Common.COPYRIGHT_Y_AXIS_OFFSET, paint);
                    canvasView.restore();

                    //
                    // Fetch the path
                    //
                    pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                    if (pictureFile == null) {
                        Log.d(TAG, "Error creating media file, check storage permissions...");
                        return;
                    }

                    //
                    // Write the new byte data...
                    //
                    try {
                        FileOutputStream fos = new FileOutputStream(pictureFile);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        byte[] bitmapdata = bos.toByteArray();
                        fos.write(bitmapdata);
                        fos.close();
                    } catch (FileNotFoundException e) {
                        Log.d(TAG, "File not found: " + e.getMessage());
                    } catch (IOException e) {
                        Log.d(TAG, "Error accessing file: " + e.getMessage());
                    }

                    //
                    // Add EXIF data to JPEG
                    //
                    try {
                        ExifInterface exif = new ExifInterface(pictureFile.getAbsolutePath());
                        String userComment = exif.getAttribute("UserComment"); // here, x is always null...


                        //
                        // Generate copyright specific to media
                        //
                        String mediaCopyright = generateMediaCopyright(pictureFile, MEDIA_TYPE_IMAGE);

                        //
                        // update copyrightJSONObject with copyright
                        //
                        exif.setAttribute("UserComment", mediaCopyright);

                        //
                        // GPS Data
                        //
                        // Set GPS location.
                        //exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, String.valueOf(mCurrentLocation.getLatitude()));
                        //exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, String.valueOf(mCurrentLocation.getLongitude()));
                        //exif.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, mLastUpdateTime);

                        //
                        // Save attributes
                        //
                        exif.saveAttributes();

                        //exif = exif.getAttribute("UserComment");  // x = "testtest"
                    } catch (IOException e) {
                        Log.e(TAG, "EXIF save error:" + e.getMessage());
                    }

                    //
                    // Add Image to Photos Gallery
                    //
                    final Uri uri = addMediaToGallery(pictureFile.getAbsolutePath(), getApplicationContext(), "image");

                    //
                    // Get Thumbnail
                    //
                    Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(pictureFile.getAbsolutePath()), 96, 96);

                    //
                    // Set thumbnail
                    //
                    imgviewThumbnail.post(new Runnable() {
                        public void run() {
                            imgviewThumbnail.setImageURI(uri);
                        }
                    });

                    //
                    // Create a media bean
                    //
                    String media_id = "";
                    try {
                        media_id = copyrightJsonObject.getString("media_id");
                    } catch (JSONException e) {
                        // this shouldn't occur
                        Log.e(TAG, "JSONException trying to get media_id from copyright!");
                    }
                    String mediaName = ImageUtils.getInstance()
                            .getImageNameFromPath(pictureFile.getAbsolutePath());
                    GalleryBean media = new GalleryBean(pictureFile.getAbsolutePath(),
                            mediaName,
                            "image",
                            media_id,
                            false,
                            thumbnail,
                            GalleryBean.GalleryType.NOT_SYNC);
                    media.setDeviceId(SessionManager.getInstance().getDevice_id());
                    media.setDeviceType(Common.DEVICE_TYPE);
                    String mediaNamePrefix = mediaName.substring(0,
                            mediaName.lastIndexOf('.'));
                    media.setMediaNamePrefix(mediaNamePrefix);
                    media.setMediaThumbBitmap(thumbnail);
                    media.setLocalMediaPath(pictureFile.getAbsolutePath());
                    Date date = new Date();
                    media.setMediaDate(date.getTime());
                    String mimeType = getContentResolver().getType(uri);
                    media.setMimeType(mimeType);
                    media.setCopyright(copyrightJsonObject.toString());

                    MemreasTransferModel transferModel = new MemreasTransferModel(
                            media);
                    transferModel.setType(MemreasTransferModel.Type.UPLOAD);
                    QueueAdapter.getInstance().getSelectedTransferModelQueue()
                            .add(transferModel);

                    // Start the service if it's not running...
                    startQueueService();

                }
            }).start();

            //
            // fetch new copyright
            //
            setNewCopyright();

            //
            // Return to Preview
            //
            setupCameraAndPreview();
            //mCamera.stopPreview();
            //mCamera.startPreview();

        }
    };

    //
    // Fetch front and back cameras
    //
    private void fetchCameras() {

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String[] cameraInfo = null;
        try {
            cameraInfo = mCameraManager.getCameraIdList();
            if (cameraInfo != null) {
                if (cameraInfo[0] != null) {
                    backCamera = Integer.valueOf(cameraInfo[0]);
                    isBackCameraSelected = true;
                    imgviewCameraSelect.setTag("backCamera");
                    mCameraCharactersticsBack = fetchCameraCharacteristics(backCamera);
                    hasBackCamera = true;
                }
                if (cameraInfo[1] != null) {
                    frontCamera = Integer.valueOf(cameraInfo[1]);
                    mCameraCharactersticsFront = fetchCameraCharacteristics(frontCamera);
                    hasFrontCamera = true;
                }

            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "CameraAccessException" + e.getMessage());
        }
    }

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance(int cameraId) {
        Camera c = null;
        try {
            c = Camera.open(cameraId); // attempt to get a Camera instance

        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    /**
     * Fetch CameraCharacteristics
     */
    public CameraCharacteristics fetchCameraCharacteristics(int cameraId) {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            return manager.getCameraCharacteristics(String.valueOf(cameraId));
        } catch (CameraAccessException cae) {
            Log.e(TAG, cae.getMessage());
            return null;
        }
    }


    //
    // Setup camera and preview
    //
    protected void setupCameraAndPreview() {

        //
        // always release
        //
        releaseImageObjects();
        releaseVideoCapturebjects(true);

        // now setup...
        if (isImageButtonSelected) {

            //
            // Image Camera can be fetched here
            //
            if (!isBackCameraSelected) {
                mCamera = getCameraInstance(frontCamera);
            } else {
                mCamera = getCameraInstance(backCamera);
            }

            if (mImageCapture == null) {
                // Initiate MImageCapture
                mImageCapture = new MImageCapture(MCameraActivity.this);
            }

            //
            // Set Orientation
            //
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            // Create our Preview view and set it as the content of our activity.
            // Add the copyright as an overlay...
            if (mImageCapture.mCameraPreview == null) {
                previewView = (LinearLayout) findViewById(R.id.camera_preview);
                mImageCapture.setCameraPreview(this, mCamera);
                previewView.addView(mImageCapture.mCameraPreview);
            }
        } else {
            //
            // Set Orientation
            //
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            /** handle video here... */
            if (mVideoCapture == null) {
                // now start over
                mVideoCapture = new MVideoCapture(this);
            }
            // Create our Preview view and set it as the content of our activity.
            // Add the copyright as an overlay...
            if (mVideoCapture.mTextureView != null) {
                previewView = (LinearLayout) findViewById(R.id.camera_preview);
                previewView.addView(mVideoCapture.mTextureView);
            }
        }

    }


    /**
     * Release Image handles
     */
    protected void releaseImageObjects() {
        /** release image capture if !null */
        if (mImageCapture != null) {
            mImageCapture.releaseCameraAndPreview();
            mImageCapture.releaseImagePreview();
            mImageCapture.releaseCamera();
            mImageCapture = null;
        }
    }

    /**
     * Release Video Capture handles
     */
    protected void releaseVideoCapturebjects(boolean stopThread) {
        /** release video capture if !null */
        if (mVideoCapture != null) {

            /**
             * TODO:: need method to release camera2
             */
            try {
                mVideoCapture.mPreviewSession.stopRepeating();
                mVideoCapture.mPreviewSession.abortCaptures();
                mVideoCapture.mPreviewSession.close();
                mVideoCapture.mPreviewSession = null;

                mVideoCapture.mMediaRecorder.release();
                mVideoCapture.closeCamera();
                mVideoCapture.releaseVideoPreview();
                mVideoCapture = null;

            } catch (Exception e) {
                mVideoCapture.mPreviewSession = null;
                mVideoCapture = null;
            }
        }
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = null;
        File mediaFile;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "memreas");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("memreas", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String filename;
        if (type == MEDIA_TYPE_IMAGE) {
            filename = "IMG_" + timeStamp + ".jpg";
        } else if (type == MEDIA_TYPE_VIDEO) {
            filename = "VID_" + timeStamp + ".mp4";
        } else {
            return null;
        }

        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                filename);

        return mediaFile;
    }

    /**
     * Save Image to Gallery
     */
    public Uri addMediaToGallery(final String filePath, final Context context, final String mediaType) {

        ContentValues values = new ContentValues();

        if (mediaType.equalsIgnoreCase("image")) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.MediaColumns.DATA, filePath);

            return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        } else {
            //
            // Add to metadata here (video must be done here)
            //
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(filePath);
            String mediaSpecificCopyright = "";
            try {
                mediaSpecificCopyright =
                        generateMediaCopyright
                                (videoFile, MEDIA_TYPE_VIDEO);
            } catch (IOException e) {
                Log.e(TAG, "addMediaToGallery.mediaSpecificCopyright error:" + e.getMessage());
            }

            //sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageAdded)));
            values.put(MediaStore.Video.Media.TAGS, mediaSpecificCopyright);
            values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            values.put(MediaStore.MediaColumns.DATA, filePath);

            return context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

        }
    }

    /**
     * Generate media specific copyright
     */
    public String generateMediaCopyright(File file, int mediaType) throws IOException {

        if (mediaType == MEDIA_TYPE_IMAGE) {
            //
            // Generate MD5 and SHA1 Checksums
            //
            HashCode md5 = Files.hash(file, Hashing.md5());
            String md5Hex = md5.toString();

            HashCode sha1 = Files.hash(file, Hashing.sha1());
            String sha1Hex = sha1.toString();

            HashCode sha256 = Files.hash(file, Hashing.sha256());
            String sha256Hex = sha256.toString();

            //
            // Set checksum into copyrightJSONObject
            //
            try {
                copyrightJsonObject.put("fileCheckSumMD5", md5Hex);
                copyrightJsonObject.put("fileCheckSumSHA1", sha1Hex);
                copyrightJsonObject.put("fileCheckSumSHA256", sha256Hex);
                copyrightJsonObject.put("applyCopyrightOnServer", 0);
                copyrightJsonObject.put("device_id", SessionManager.getInstance().getDevice_id());
            } catch (JSONException e) {
                Log.e("", e.getMessage());
            }

        } else {

            //
            // Set checksum into copyrightJSONObject
            //
            try {
                copyrightJsonObject.put("applyCopyrightOnServer", 1);
                copyrightJsonObject.put("device_id", SessionManager.getInstance().getDevice_id());
            } catch (JSONException e) {
                Log.e("", e.getMessage());
            }
        }

        //
        // update copyrightJSONObject with copyright
        //
        return copyrightJsonObject.toString();

    }


    /**
     * fetch and set copyright
     */
    public void setNewCopyright() {

        /** fetch the copyright manager */
        copyrightManager = CopyrightManager.getInstance();

        //
        // Fetch and Set the copyright textview
        //
        copyrightJsonObject = copyrightManager.fetchNextCopyRight();
        try {
            copyrightMD5 = copyrightJsonObject.getString("copyright_id_md5");
            copyrightSHA1 = copyrightJsonObject.getString("copyright_id_sha1");
            copyrightSHA256 = copyrightJsonObject.getString("copyright_id_sha256");
        } catch (JSONException e) {
            Log.e(TAG, "copyright exception:" + e.getMessage());
        }
    }

    //
    // Queue Intent Service
    //
    private void startQueueService() {
        // Start the background service here and bind to it
        Intent intent = new Intent(this, QueueService.class);
        intent.putExtra("service", "start");
        startService(intent);
    }

    //
    // MVideoCapture - handles video capture, inscription, metadata, and upload
    //
    protected class MVideoCapture {
        //
        // Declare vars
        //
        protected Size mPreviewSize;
        protected Size mVideoSize;
        protected CameraCaptureSession mPreviewSession;
        protected CaptureRequest.Builder mPreviewBuilder;
        protected MediaRecorder mMediaRecorder;
        protected boolean isRecordingVideo;
        protected boolean isPreviewRunning;
        protected HandlerThread mBackgroundThread;
        protected Handler mBackgroundHandler;
        protected Semaphore mCameraOpenCloseLock = new Semaphore(1);
        protected CameraDevice mCameraDevice;
        protected ImageReader mImageReader;

        protected MTextureView mTextureView;
        protected MCameraActivity mCameraActivity;
        protected final int screenWidth;
        protected final int screenHeight;

        //
        // Constructor
        //
        public MVideoCapture(MCameraActivity activity) {
            mCameraActivity = activity;
            mTextureView = new MTextureView(mCameraActivity);
            screenWidth = getResources().getDisplayMetrics().widthPixels;
            screenHeight = getResources().getDisplayMetrics().heightPixels;
        }

        protected class MTextureView extends TextureView implements TextureView.SurfaceTextureListener {

            public MTextureView(Context context) {
                super(context);
                super.setSurfaceTextureListener(this);
            }

            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture,
                                                  int width, int height) {
                Log.e(TAG, "onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) called...");
                //mTextureView.setMinimumWidth(screenWidth);
                //mTextureView.setMinimumHeight(screenHeight);
                String mRight = "md5:" + copyrightMD5 + " sha256:" + copyrightSHA256;
                Typeface type = Typeface.createFromAsset(getAssets(), "font/segoescb.ttf");
                previewViewCopyright.setTypeface(type);
                previewViewCopyright.setText(mRight);
                previewViewCopyright.setSingleLine();
                previewViewCopyright.setTextSize(Common.COPYRIGHT_TEXT_SIZE);
                previewViewCopyright.setTextColor(Color.BLUE);

                Log.e(TAG, "startBackgroundThread() called...");
                startBackgroundThread();
                openCamera(screenWidth, screenHeight);
                isPreviewRunning = true;
                Size size = new Size(width, height);
                updatePreviewDisplayRotation(size, mTextureView);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture,
                                                    int width, int height) {
                Log.e(TAG, "onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) called...");
                Size size = new Size(width, height);
                updatePreviewDisplayRotation(size, mTextureView);
                //adjustAspectRatio(width, height);
                //requestLayout();
                //invalidate();
                //configureTransform(width, height);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                Log.e(TAG, "onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) called...");
                isPreviewRunning = false;
                closeCamera();
                stopBackgroundThread();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                //Log.e(TAG, "onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) called...");
            }
        }

        /**
         * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its status.
         */
        protected CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

            @Override
            public void onOpened(CameraDevice cameraDevice) {
                Log.e(TAG, "onOpened(CameraDevice cameraDevice) called...");
                mCameraDevice = cameraDevice;
                startPreview();
                mCameraOpenCloseLock.release();
                //if (null != mTextureView) {
                //    configureTransform(mTextureView.getWidth(), mTextureView.getHeight());
                //}
            }

            @Override
            public void onDisconnected(CameraDevice cameraDevice) {
                Log.e(TAG, "onDisconnected(CameraDevice cameraDevice) called...");
                mCameraOpenCloseLock.release();
                cameraDevice.close();
                mCameraDevice = null;
            }

            @Override
            public void onError(CameraDevice cameraDevice, int error) {
                Log.e(TAG, "onError(CameraDevice cameraDevice) called...");
                mCameraOpenCloseLock.release();
                cameraDevice.close();
                mCameraDevice = null;
                mCameraActivity.finish();
            }
        };


        //
        // Methods
        //

        /**
         * Starts a background thread and its {@link Handler}.
         */
        protected void startBackgroundThread() {
            Log.e(TAG, "startBackgroundThread() called...");
            try {
                mBackgroundThread = new HandlerThread("CameraBackground");
                mBackgroundThread.start();
                mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Stops the background thread and its {@link Handler}.
         */
        protected void stopBackgroundThread() {
            Log.e(TAG, "stopBackgroundThread() called...");
            try {
                if (mBackgroundThread != null) {
                    mBackgroundThread.quitSafely();
                    mBackgroundThread.join();
                    mBackgroundThread = null;
                    mBackgroundHandler = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        /**
         * Tries to open a {@link CameraDevice}. The result is listened by `mStateCallback`.
         */
        protected void openCamera(int width, int height) {
            //CameraManager manager = (CameraManager) mCameraActivity.getSystemService(Context.CAMERA_SERVICE);
            try {
                Log.d(TAG, "openCamera");
                if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                    throw new RuntimeException("Time out waiting to lock camera opening.");
                }
                String cameraId;
                if (isBackCameraSelected) {
                    cameraId = String.valueOf(backCamera);
                } else {
                    cameraId = String.valueOf(frontCamera);
                }

                mMediaRecorder = new MediaRecorder();
                mCameraManager.openCamera(cameraId, mStateCallback, null);
            } catch (CameraAccessException e) {
                Toast.makeText(mCameraActivity, "Cannot access the video camera.", Toast.LENGTH_SHORT).show();
                setupCameraAndPreview();
            } catch (NullPointerException e) {
                // Currently an NPE is thrown when the Camera2API is used but not supported on the
                // device this code runs.
                Toast.makeText(mCameraActivity, "Cannot access the video camera.", Toast.LENGTH_SHORT).show();
                setupCameraAndPreview();
            } catch (InterruptedException e) {
                Toast.makeText(mCameraActivity, "Cannot access the video camera.", Toast.LENGTH_SHORT).show();
                setupCameraAndPreview();
            } catch (SecurityException e) {
                Toast.makeText(mCameraActivity, "Cannot access the video camera.", Toast.LENGTH_SHORT).show();
                setupCameraAndPreview();
            }

        }


        /**
         * Update preview TextureView rotation to accommodate discrepancy between preview
         * buffer and the view window orientation.
         * <p/>
         * Assumptions:
         * - Aspect ratio for the sensor buffers is in landscape orientation,
         * - Dimensions of buffers received are rotated to the natural device orientation.
         * - The contents of each buffer are rotated by the inverse of the display rotation.
         * - Surface scales the buffer to fit the current view bounds.
         * TODO: Make this method works for all orientations
         */
        protected void updatePreviewDisplayRotation(Size previewSize, TextureView textureView) {
            int rotationDegrees = 0;
            MCameraActivity activity = mCameraActivity;
            int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            Configuration config = activity.getResources().getConfiguration();
            // Get UI display rotation
            switch (displayRotation) {
                case Surface.ROTATION_0:
                    rotationDegrees = 0;
                    break;
                case Surface.ROTATION_90:
                    rotationDegrees = 90;
                    break;
                case Surface.ROTATION_180:
                    rotationDegrees = 180;
                    break;
                case Surface.ROTATION_270:
                    rotationDegrees = 270;
                    break;
            }
            // Get device natural orientation
            int deviceOrientation = Configuration.ORIENTATION_PORTRAIT;
            if ((rotationDegrees % 180 == 0 &&
                    config.orientation == Configuration.ORIENTATION_LANDSCAPE) ||
                    ((rotationDegrees % 180 != 0 &&
                            config.orientation == Configuration.ORIENTATION_PORTRAIT))) {
                deviceOrientation = Configuration.ORIENTATION_LANDSCAPE;
            }
            // Rotate the buffer dimensions if device orientation is portrait.
            int effectiveWidth = previewSize.getWidth();
            int effectiveHeight = previewSize.getHeight();
            if (deviceOrientation == Configuration.ORIENTATION_PORTRAIT) {
                effectiveWidth = previewSize.getHeight();
                effectiveHeight = previewSize.getWidth();
            }
            // Find and center view rect and buffer rect
            Matrix transformMatrix = textureView.getTransform(null);
            int viewWidth = textureView.getWidth();
            int viewHeight = textureView.getHeight();
            RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
            RectF bufRect = new RectF(0, 0, effectiveWidth, effectiveHeight);
            float centerX = viewRect.centerX();
            float centerY = viewRect.centerY();
            bufRect.offset(centerX - bufRect.centerX(), centerY - bufRect.centerY());
            // Undo ScaleToFit.FILL done by the surface
            transformMatrix.setRectToRect(viewRect, bufRect, Matrix.ScaleToFit.FILL);
            // Rotate buffer contents to proper orientation
            transformMatrix.postRotate((360 - rotationDegrees) % 360, centerX, centerY);
            if ((rotationDegrees % 180) == 90) {
                int temp = effectiveWidth;
                effectiveWidth = effectiveHeight;
                effectiveHeight = temp;
            }
            // Scale to fit view, cropping the longest dimension
            float scale =
                    Math.max(viewWidth / (float) effectiveWidth, viewHeight / (float) effectiveHeight);
            transformMatrix.postScale(scale, scale, centerX, centerY);
            Handler handler = new Handler(Looper.getMainLooper());
            class TransformUpdater implements Runnable {
                TextureView mView;
                Matrix mTransformMatrix;

                TransformUpdater(TextureView view, Matrix matrix) {
                    mView = view;
                    mTransformMatrix = matrix;
                }

                @Override
                public void run() {
                    mView.setTransform(mTransformMatrix);
                }
            }
            handler.post(new TransformUpdater(textureView, transformMatrix));
        }


        /**
         * close camera and recorder
         */
        protected void closeCamera() {
            try {
                mCameraOpenCloseLock.acquire();
                if (null != mCameraDevice) {
                    mCameraDevice.close();
                    mCameraDevice = null;
                }
                if (null != mMediaRecorder) {
                    mMediaRecorder.release();
                    mMediaRecorder = null;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted while trying to lock camera closing.");
            } finally {
                mCameraOpenCloseLock.release();
            }
        }

        /**
         * Use to process images...
         */
        private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {

            @Override
            public void onImageAvailable(ImageReader reader) {
                //mTextureView.onPreviewFrame(reader.acquireNextImage().getPlanes([0].getBuffer().array());
                Log.e(TAG, "onImageAvailable(ImageReader reader)");
            }

        };

        /**
         * Start the camera preview.
         */
        protected void startPreview() {
            if (null == mCameraDevice || !mTextureView.isAvailable()) {
                return;
            }
            try {
                //
                // Media Recorder
                //
                setUpMediaRecorder();

                //
                // Preview Builder - Video Recording
                //
                mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);

                //
                // Surfaces: Preview and Record
                //
                List<Surface> surfaces = new ArrayList<>();

                //
                // Preview Surface
                //
                SurfaceTexture texture = mTextureView.getSurfaceTexture();
                texture.setDefaultBufferSize(screenWidth, screenHeight);
                Surface previewSurface = new Surface(texture);
                surfaces.add(previewSurface);
                mPreviewBuilder.addTarget(previewSurface);


                //
                // Record Surface
                //
                Surface recorderSurface = mMediaRecorder.getSurface();
                surfaces.add(recorderSurface);
                mPreviewBuilder.addTarget(recorderSurface);

                //
                // Setup Capture Session
                //
                mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                    @Override
                    public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                        Log.e(TAG, "onConfigured(CameraCaptureSession cameraCaptureSession) ...");
                        mPreviewSession = cameraCaptureSession;
                        updatePreview();
                    }

                    @Override
                    public void onSurfacePrepared(CameraCaptureSession session, Surface surface) {
                        Log.e(TAG, "onSurfacePrepared(CameraCaptureSession session, Surface surface) ...");
                        //previewView = (LinearLayout) findViewById(R.id.camera_preview);
                        //previewView.addView(mVideoCapture.mTextureView);
                    }

                    @Override
                    public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                        Log.e(TAG, "onConfigureFailed(CameraCaptureSession cameraCaptureSession) ...");
                        Toast.makeText(mCameraActivity, "failed to configure video camera", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Update the camera preview. {@link #startPreview()} needs to be called in advance.
         */
        protected void updatePreview() {
            if (null == mCameraDevice) {
                return;
            }
            try {
                setUpCaptureRequestBuilder(mPreviewBuilder);
                HandlerThread thread = new HandlerThread("CameraPreview");
                thread.start();
                mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        /*
        protected CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
                super.onCaptureStarted(session, request, timestamp, frameNumber);
                Log.e(TAG, "onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber)");
            }

            @Override
            public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
                super.onCaptureProgressed(session, request, partialResult);
                Log.e(TAG, "onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult)");
            }

            @Override
            public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                super.onCaptureCompleted(session, request, result);
                Log.e(TAG, "onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result)");
            }

            @Override
            public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
                super.onCaptureFailed(session, request, failure);
                Log.e(TAG, "onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure)");
            }

            @Override
            public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId, long frameNumber) {
                super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
                Log.e(TAG, "onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId, long frameNumber)");
            }

            @Override
            public void onCaptureSequenceAborted(CameraCaptureSession session, int sequenceId) {
                super.onCaptureSequenceAborted(session, sequenceId);
                Log.e(TAG, "onCaptureSequenceAborted(CameraCaptureSession session, int sequenceId)");
            }
        };
        */

        private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {

            builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        }

        protected void setUpMediaRecorder() throws IOException {
            final Activity activity = mCameraActivity;
            if (null == activity) {
                return;
            }
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            //mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            videoFile = getOutputMediaFile(MEDIA_TYPE_VIDEO);
            mMediaRecorder.setOutputFile(videoFile.getAbsolutePath());
            //mMediaRecorder.setVideoEncodingBitRate(10000000);
            //mMediaRecorder.setVideoFrameRate(30);
            //mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
            //mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            //mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            if (isBackCameraSelected) {
                mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
            } else {
                mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
            }
            //int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            //int orientation = ORIENTATIONS.get(rotation);
            //Log.e(TAG, "setUpMediaRecorder::orientation::" + String.valueOf(orientation));
            //mMediaRecorder.setOrientationHint(orientation);
            mMediaRecorder.setOnInfoListener(mMediaRecorderOnInfoListener);
            mMediaRecorder.setOnErrorListener(mMediaRecorderOnErrorListener);
            //mMediaRecorder.onImageAvailable(mImageReader);
            mMediaRecorder.prepare();
        }

        protected MediaRecorder.OnInfoListener mMediaRecorderOnInfoListener = new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                Log.e(TAG, "what::" + String.valueOf(what) + " extra::" + String.valueOf(extra));
            }
        };

        protected MediaRecorder.OnErrorListener mMediaRecorderOnErrorListener = new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                Log.e(TAG, "what::" + String.valueOf(what) + " extra::" + String.valueOf(extra));
            }
        };


        /**
         * only call if preview is properly set and you want to release
         */
        public void releaseVideoPreview() {
            previewView.removeView(mVideoCapture.mTextureView);
        }

    } // end MVideoCapture


    //
    // MImageCapture - handles image capture, inscription, metadata, and upload
    //
    protected class MImageCapture {

        protected CameraPreview mCameraPreview;
        protected SurfaceHolder mSurfaceHolder;
        protected MCameraActivity mCameraActivity;

        public MImageCapture(MCameraActivity mCameraActivity) {
            this.mCameraActivity = mCameraActivity;
        }

        //
        // Release Camera for onPause, onBackPressed
        //
        public void releaseCameraAndPreview() {
            if (mCameraActivity.mCamera != null) {
                mCameraActivity.mCamera.setPreviewCallback(null);
                mCameraActivity.mCamera.stopPreview();
                mImageCapture.mSurfaceHolder.removeCallback(mCameraPreview);
                mCameraPreview.surfaceDestroyed(mImageCapture.mSurfaceHolder);
                mSurfaceHolder.getSurface().release();
                mSurfaceHolder = null;
                releaseCamera();
            }
        }

        public CameraPreview setCameraPreview(Context context, Camera camera) {
            this.mCameraPreview = new CameraPreview(context, camera);
            return this.mCameraPreview;
        }

        /**
         * A basic Camera preview class
         */
        public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

            protected final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            protected final float scale = getResources().getDisplayMetrics().density;


            public CameraPreview(Context context, Camera camera) {
                super(context);
                mCameraActivity.mCamera = camera;

                // Install a SurfaceHolder.Callback so we get notified when the
                // underlying surface is created and destroyed.
                if (mSurfaceHolder == null) {
                    mSurfaceHolder = getHolder();
                    mSurfaceHolder.addCallback(this);
                } else {
                    /** surface is set - add to camera preview */
                    try {
                        mCamera.setPreviewDisplay(mSurfaceHolder);
                    } catch (IOException e) {
                        Log.e("IOException", e.getMessage());
                    }
                }


                //
                // Ensures inscription is shown
                //
                setWillNotDraw(false);

                // Set Camera orientation
                //if (mCameraActivity.isBackCameraSelected) {
                //    setCameraDisplayOrientation((Activity) context, mCameraActivity.backCamera, mCameraActivity.mCamera);
                //} else {
                //    setCameraDisplayOrientation((Activity) context, mCameraActivity.frontCamera, mCameraActivity.mCamera);
                //}
            }


            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);

                if (!isRecordingVideo) {
                    paint.setColor(Color.BLUE);
                    paint.setTextSize(10*scale);
                    Typeface type = Typeface.createFromAsset(getAssets(), "font/segoescb.ttf");
                    paint.setTypeface(type);
                    paint.setShadowLayer(1f, 0f, 1f, Color.LTGRAY);
                    // deprecated setting, but required on Android versions prior to 3.0
                    // mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                    //Canvas canvasView = new Canvas(photo);
                    //String mRight = "md5:" + copyrightMD5 + " sha1:" + copyrightSHA1;
                    String mRight = "md5:" + copyrightMD5 + " sha256:" + copyrightSHA256;

                    //canvasView.save();
                    float rotate = 0.0f;
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        rotate = 90.0f;
                    }
                    canvas.rotate(rotate, 0, 25);
                    canvas.drawText(mRight, 0, 25, paint);
                    canvas.save();
                    //mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }


            public void surfaceCreated(SurfaceHolder holder) {

                // The Surface has been created, now tell the camera where to draw the preview.
                try {
                    mCamera.setPreviewDisplay(mImageCapture.mSurfaceHolder);
                    setCameraDisplayOrientationByCameraSelected();
                } catch (IOException e) {
                    Log.d(TAG, "Error setting camera preview: " + e.getMessage());
                }
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                // empty. Take care of releasing the Camera preview in your activity.
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                // If your preview can change or rotate, take care of those events here.
                // Make sure to stop the preview before resizing or reformatting it.

                //mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                //    public void onPreviewFrame(byte[] _data, Camera _camera) {
                //
                //    }
                //});

                if (mImageCapture.mSurfaceHolder.getSurface() == null) {
                    // preview surface does not exist
                    return;
                }
                // stop preview before making changes
                try {
                    mCamera.stopPreview();
                } catch (Exception e) {
                    // ignore: tried to stop a non-existent preview
                    return;
                }

                // set preview size and make any resize, rotate or
                // reformatting changes here

                // start preview with new settings
                try {
                    setCameraDisplayOrientationByCameraSelected();
                    mCamera.startPreview();

                } catch (Exception e) {
                    Log.d(TAG, "Error starting camera preview: " + e.getMessage());
                }
            }
        }

        /**
         * set camera orientation based on front / back
         */
        public int setCameraDisplayOrientationByCameraSelected() {
            int result = 0;
            if (isBackCameraSelected) {
                result = setCameraImageDisplayOrientation(mCameraActivity, backCamera, mCamera);
            } else {
                if (hasFrontCamera) {
                    result = setCameraImageDisplayOrientation(mCameraActivity, frontCamera, mCamera);
                }
            }
            return result;
        }

        /**
         * set camera display according to orientation
         */
        public int setCameraImageDisplayOrientation(Activity activity,
                                                    int cameraId, android.hardware.Camera camera) {
            android.hardware.Camera.CameraInfo info =
                    new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(cameraId, info);
            int rotation = activity.getWindowManager().getDefaultDisplay()
                    .getRotation();
            int screenOrientation = getResources().getConfiguration().orientation;
            if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                isPortrait = false;
            } else if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
                isPortrait = true;
            } else {
                isPortrait = true;
            }

            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break;
                case Surface.ROTATION_90:
                    degrees = 90;
                    break;
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;
            }

            int result;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;  // compensate the mirror
            } else {  // back-facing
                result = (info.orientation - degrees + 360) % 360;
            }
            camera.setDisplayOrientation(result);

            return result;
        }

        /**
         * only call if preview is properly set and you want to release
         */
        public void releaseImagePreview() {
            previewView.removeView(mImageCapture.mCameraPreview);
            mImageCapture.mCameraPreview = null;
        }

        public void releaseCamera() {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        }

    } // end MImageCapture


} // end class MCameraActivity

