
package com.memreas.gallery;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.memreas.R;
import com.memreas.base.BaseActivity;

import java.util.HashMap;
import java.util.Map;

public class PlayVideoActivity extends BaseActivity {

    protected VideoView myVideoView;
    protected MediaController mediaController;
    protected View layoutView;
    protected MediaPlayer mediaPlayer;
    protected int bean_position;
    protected int video_position;
    protected ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);

            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            setContentView(R.layout.gallery_video_view);
            //super.hideStatusBar();

            Bundle data = getIntent().getExtras();
            bean_position = data.getInt("position");

            if (savedInstanceState != null) {
                bean_position = savedInstanceState.getInt("bean_position");
                video_position = savedInstanceState.getInt("video_position");
            }

            GalleryBean bean = GalleryAdapter.getInstance().getGalleryImageList()
                    .get(bean_position);
            if (!bean.isLocal()) {
                if (!bean.getMediaTranscodeStatus().trim().equalsIgnoreCase("success")) {
                    Toast.makeText(PlayVideoActivity.this, "transcoding video - can't play until complete...",
                            Toast.LENGTH_SHORT).show();
                    this.onBackPressed();
                }
            }

            myVideoView = (VideoView) findViewById(R.id.videoView);

            // create a progress bar while the video file is loading
            progressDialog = new ProgressDialog(PlayVideoActivity.this);


            //
            // Fetch the uri and set a title for the progress dialog
            //
            String url = null;
            Uri uriToPlay = null;
            Map<String, String> headers = new HashMap<String, String>();
            if (!bean.isLocal()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //
                    // Setup url after...
                    //
                    url = bean.getMediaUrlHls();
                    //url = bean.getMediaUrlWebm();
                    Uri uri = Uri.parse(url);
                    String filename = uri.getLastPathSegment();
                    progressDialog.setTitle("media file: " + filename);
                } else {
                    url = bean.getMediaUrlWeb();
                    progressDialog.setTitle("media file: " + bean.getMediaName());
                }
                uriToPlay = Uri.parse(url);
            } else {
                uriToPlay = Uri.parse(bean.getLocalMediaPath());
            }

            //
            // set a message for the progress bar
            //
            progressDialog.setMessage("loading...");
            // set the progress bar not cancelable on users' touch
            progressDialog.setCancelable(false);
            // show the progress bar
            progressDialog.show();
            myVideoView.setVisibility(View.VISIBLE);


            myVideoView.requestFocus();
            mediaController = new MediaController(this);
            myVideoView.setVideoURI(uriToPlay);
            mediaController.setAnchorView(myVideoView);
            mediaController.setMediaPlayer(myVideoView);
            myVideoView.setMediaController(mediaController);

            myVideoView.setOnTouchListener(new OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // mediaController.showContextMenu();
                    myVideoView.showContextMenu();
                    return false;
                }
            });

            myVideoView.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // reset position to 0
                    video_position = 0;
                }
            });

            myVideoView.setOnErrorListener(new OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    //mp.release();
                    //finish();

                    String strWhat;
                    String strExtra;
                    switch (what) {
                        case 1:  strWhat = "MEDIA_ERROR_UNKNOWN";
                            break;
                        case 100:  strWhat = "MEDIA_ERROR_SERVER_DIED";
                            break;
                        default: strWhat = String.valueOf(what);
                            break;
                    }

                    switch (what) {
                        case -1004:  strExtra = "MEDIA_ERROR_IO";
                            break;
                        case -1007:  strExtra = "MEDIA_ERROR_MALFORMED";
                            break;
                        case -1010:  strExtra = "MEDIA_ERROR_UNSUPPORTED";
                            break;
                        case -110:  strExtra = "MEDIA_ERROR_TIMED_OUT";
                            break;
                        default: strExtra = String.valueOf(extra);
                            break;
                    }

                    Toast.makeText(PlayVideoActivity.this, "could not play video what::" + String.valueOf(strWhat) + " extra::" + String.valueOf(strExtra),
                            Toast.LENGTH_SHORT).show();
                    onBackPressed();

                    return true;
                }
            });

            myVideoView.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (video_position == 0) {
                        progressDialog.dismiss();
                        myVideoView.start();
                    } else if (video_position != 0) {
                        myVideoView.seekTo(video_position);
                        progressDialog.dismiss();
                        myVideoView.start();
                    } else {
                        // if we come from a resumed activity, video playback
                        // will
                        // be paused
                        myVideoView.pause();
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(PlayVideoActivity.this, "could not play video exception::" + e.getLocalizedMessage(),
                    Toast.LENGTH_SHORT).show();
            this.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        try {
            myVideoView.pause();
            video_position = myVideoView.getCurrentPosition();
            super.onPause();
        } catch (Exception e) {
            Toast.makeText(PlayVideoActivity.this, "could not play video",
                    Toast.LENGTH_SHORT).show();
            this.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        try {
            if (myVideoView != null) {
                myVideoView.seekTo(video_position);
                myVideoView.start();
            } else {
                this.onCreate(null);
            }
            super.onResume();
        } catch (Exception e) {
            Toast.makeText(PlayVideoActivity.this, "could not play video",
                    Toast.LENGTH_SHORT).show();
            this.onBackPressed();
        }

    }

    @Override
    public void onBackPressed() {
        if (myVideoView != null) {
            myVideoView.suspend();
        }
        this.finish();
    }

}
