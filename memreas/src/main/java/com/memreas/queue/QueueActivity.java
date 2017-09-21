
package com.memreas.queue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.memreas.R;
import com.memreas.base.BaseActivity;
import com.memreas.base.Foreground;
import com.memreas.gallery.GalleryAdapter;
import com.memreas.queue.MemreasTransferModel.MemreasQueueStatus;

import java.util.Iterator;
import java.util.LinkedList;

public class QueueActivity extends BaseActivity {

    private enum LastTab {
        QUEUE, COMPLETED
    }

    ;

    private String TAG = getClass().getName();

    // Tabs
    public static LastTab lastTab;
    private Button btnQueueView;
    private Button btnCompleteView;
    private Button clearBtn;
    private Button pauseBtn;

    // Layouts
    private ListView queueView;
    private GridView completedView;
    private FrameLayout fullScreenLayout;
    private ImageButton closeView;
    private SyncAdapter syncAdapter;
    private LinkedList<MemreasTransferModel> transferModelQueue;

    // misc
    private ProgressBar loadMediaProgressBar;

    // Service Binding
    private QueueService queueService;
    private boolean isQueueBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start the service if it's not running...
        startQueueService();

        // Fetch the view
        setContentView(R.layout.screen_queue);
        PREV_INDEX = QUEUE_INDEX;

        // Setup Tabs (Buttons)...
        // View Gallery
        btnQueueView = (Button) findViewById(R.id.btnQueueView);
        btnQueueView.setOnClickListener(mHandleBtnQueueView);

        // View Completed
        btnCompleteView = (Button) findViewById(R.id.btnCompletedView);
        btnCompleteView.setOnClickListener(mHandleBtnCompletedView);

        queueView = (ListView) findViewById(R.id.queueView);
        completedView = (GridView) findViewById(R.id.completedView);
        fullScreenLayout = (FrameLayout) findViewById(R.id.fullScreenLayout);
        fullScreenLayout.setVisibility(View.GONE);
        closeView = (ImageButton) findViewById(R.id.closeView);
        loadMediaProgressBar = (ProgressBar) findViewById(R.id.processBar);
        pauseBtn = (Button) findViewById(R.id.pauseBtn);
        clearBtn = (Button) findViewById(R.id.clearBtn);

        // Configure menu
        menuConfiguration();

        // Show Ads...
        AdmobView();

        // Set the Adapters to avoid null pointer exceptions...
        queueView.setAdapter(QueueAdapter.getInstance());
        QueueAdapter.getInstance().setQueueView(QueueActivity.this);
        CompletedAdapter.getInstance().setCompletedView(QueueActivity.this);
        completedView.setAdapter(CompletedAdapter.getInstance());

        // onCreate so show QueueView
        lastTab = LastTab.QUEUE;
        clickLastTab();

    }

    public void clickLastTab() {
        if (lastTab == LastTab.QUEUE) {
            btnQueueView.performClick();
        } else if (lastTab == LastTab.COMPLETED) {
            btnCompleteView.performClick();
        }
    }

    public void hideTabViews() {
        queueView.setVisibility(View.GONE);
        completedView.setVisibility(View.GONE);
    }

    // Handle Queue Tab
    private OnClickListener mHandleBtnQueueView = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // Remove any prior views...
            hideTabViews();

            // Add to queueadapter and show queue entries here
            QueueAdapter.getInstance().setQueueView(QueueActivity.this);
            queueView.setAdapter(QueueAdapter.getInstance());
            queueView.setVisibility(View.VISIBLE);

            // Setup Clear Button here
            pauseBtn = (Button) findViewById(R.id.pauseBtn);
            pauseBtn.setVisibility(View.VISIBLE);
            pauseBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * Pause / Resume Section
                     */
                    // Setup pause / resume here
                    if (!isPauseUploadDownload) {
                        /**
                         * Pause AWS via status of transferModels here...
                         */
                        transferModelQueue = QueueAdapter.getInstance()
                                .getSelectedTransferModelQueue();
                        Iterator<MemreasTransferModel> iterator = transferModelQueue
                                .iterator();
                        MemreasTransferModel transferModel;
                        while (iterator.hasNext()) {
                            transferModel = iterator.next();
                            transferModel.setMemreasQueueStatus(MemreasQueueStatus.PAUSED);
                        }
                        ((Button) findViewById(R.id.pauseBtn))
                                .setText(R.string.upload_btn_resume);
                        isPauseUploadDownload = true;
                    } else {
                        /**
                         * Resume AWS via status of transferModels here...
                         */
                        transferModelQueue = QueueAdapter.getInstance()
                                .getSelectedTransferModelQueue();
                        Iterator<MemreasTransferModel> iterator = transferModelQueue
                                .iterator();
                        MemreasTransferModel transferModel;
                        while (iterator.hasNext()) {
                            transferModel = iterator.next();
                            transferModel.setMemreasQueueStatus(MemreasQueueStatus.RESUMED);
                        }
                        ((Button) findViewById(R.id.pauseBtn))
                                .setText(R.string.upload_btn_pause);
                        isPauseUploadDownload = false;
                    }
                }
            });

            // Setup Clear Button here
            clearBtn = (Button) findViewById(R.id.clearBtn);
            clearBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // clear queue entries
                    QueueAdapter.getInstance().removeAllQueueEntries(queueView);
                }
            });

            // Set LastView
            lastTab = LastTab.QUEUE;
        }
    };

    // Handle Queue Tab
    private OnClickListener mHandleBtnCompletedView = new OnClickListener() {
        @Override
        public void onClick(View v) {

            // Remove any prior views...
            hideTabViews();

            // Initialize CompletedAdapter and show any entries here
            CompletedAdapter.getInstance().setCompletedView(QueueActivity.this);
            completedView.setAdapter(CompletedAdapter.getInstance());
            completedView.setVisibility(View.VISIBLE);

            // Hide the pause/resume button
            pauseBtn.setVisibility(View.INVISIBLE);

            // re-purpuse Clear Button here
            clearBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // clear queue entries
                    CompletedAdapter.getInstance().removeAllQueueEntries();
                }
            });

            // Set LastView
            lastTab = LastTab.COMPLETED;
        }
    };

    // Handle Completed Tab
    @Override
    protected void onResume() {
        super.onResume();
        // Start the service if it's not running...
        startQueueService();

        // Setup LocalBroadcastManager
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("queue-progress"));

        if (lastTab == LastTab.QUEUE) {
            QueueAdapter.getInstance().fetchSelectedMedia();
            QueueAdapter.getInstance().notifyDataSetChanged();
        }
        clickLastTab();
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
        //if ((isQueueBound) && (mConnection != null)) {
        //	unbindService(mConnection);
        //}
        super.onPause();
    }

    private void startQueueService() {
        if (!isQueueBound) {
            // Start the background service here and bind to it
            Intent intent = new Intent(this, QueueService.class);
            intent.putExtra("service", "start");
            //bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            startService(intent);
        }
    }

	/*
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			QueueBinder binder = (QueueBinder) service;
			queueService = binder.getService();
			isQueueBound = true;
		}

		public void onServiceDisconnected(ComponentName arg0) {
			queueService = null;
			isQueueBound = false;
		}
	};
	*/

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        private QueueAdapter.ViewHolder holder;
        private ProgressBar progressBar;

        @Override
        public synchronized void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent

            final String name = intent.getStringExtra("transferModelName");
            final MemreasQueueStatus status = MemreasQueueStatus.valueOf(intent
                    .getStringExtra("status"));

            if (name.equalsIgnoreCase("QueueService")
                    && (status.equals(MemreasQueueStatus.MOVE_TO_COMPLETED))) {
                // Uploads are finished so update the views...
                QueueActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        // UI code goes here
                        if (QueueAdapter.getInstance() != null) {
                            QueueAdapter.getInstance().notifyDataSetChanged();
                        }
                        if (GalleryAdapter.getInstance() != null) {
                            GalleryAdapter.getInstance().notifyDataSetChanged();
                        }
                        if (SyncAdapter.getInstance() != null) {
                            SyncAdapter.getInstance().notifyDataSetChanged();
                        }
                        if (CompletedAdapter.getInstance() != null) {
                            CompletedAdapter.getInstance().notifyDataSetChanged();
                        }
                        if (Foreground.getInstance().isForeground()) {
                            btnCompleteView.performClick();
                        }
                    }
                });
            } else if (status.equals(MemreasQueueStatus.COMPLETED)
                    || status.equals(MemreasQueueStatus.CANCELED)
                    || status.equals(MemreasQueueStatus.ERROR)) {
                // an entry finished so update the view
                QueueActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        // UI code goes here
                        try {
                            QueueAdapter.getInstance().notifyDataSetChanged();
                            SyncAdapter.getInstance().notifyDataSetChanged();
                            CompletedAdapter.getInstance()
                                    .notifyDataSetChanged();
                        } catch (Exception e) {
                            // do nothing...
                        }
                    }
                });
            } else if (status.equals(MemreasQueueStatus.IN_PROGRESS)) {
                // Queue Processing is still taking place so update progress...
                holder = findViewbyMediaName(name);
                if ((holder != null) && (status.equals(MemreasQueueStatus.IN_PROGRESS))) {
                    if (holder.uploadProgressBar.getVisibility() == View.VISIBLE) {
                        progressBar = holder.uploadProgressBar;
                    } else if (holder.downloadProgressBar.getVisibility() == View.VISIBLE) {
                        progressBar = holder.downloadProgressBar;
                    }

                    // View is showing...
                    QueueActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            // UI code goes here
                            if (progressBar != null) {
                                progressBar.setProgress(holder.transferModel
                                        .getProgress());
                                holder.processText.setText(String
                                        .valueOf(holder.transferModel
                                                .getProgress())
                                        + "%");
                            }
                        }
                    });
                }
            }
        } // end onReceive
    }; // end broadcast receiver...

    public QueueAdapter.ViewHolder findViewbyMediaName(String name) {
        QueueAdapter.ViewHolder holder;
        try {
            for (int i = 0; i < queueView.getChildCount(); i++) {
                View view = queueView.getChildAt(i);
                holder = (QueueAdapter.ViewHolder) view.getTag();
                if (name.equalsIgnoreCase(holder.name)) {
                    return holder;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

} // end class ....

