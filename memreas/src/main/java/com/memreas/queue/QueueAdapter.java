
package com.memreas.queue;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.memreas.R;
import com.memreas.gallery.GalleryAdapter;
import com.memreas.gallery.GalleryBean;
import com.memreas.gallery.GalleryBean.GalleryType;
import com.memreas.queue.MemreasTransferModel.MemreasQueueStatus;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class QueueAdapter extends BaseAdapter {

    private static final String TAG = "QueueAdapter";
    private static QueueAdapter instance;
    private GalleryAdapter galleryAdapter;
    private Context context;
    private MemreasTransferModel transferModel;
    private GalleryBean media;
    private static LayoutInflater mInflater;
    private LinkedList<MemreasTransferModel> selectedTransferModelQueue;
    private HashMap<String, Integer> selectedMediaHashMap;
    protected DisplayImageOptions optionsGallery;
    protected DisplayImageOptions optionsStorage;
    protected ImageLoadingListener animateFirstListener;
    protected ImageLoader memreasImageLoader;
    protected static boolean layoutInflated;

    protected void setQueueView(Context context) {
        this.context = context;
        galleryAdapter = GalleryAdapter.getInstance();
        fetchSelectedMedia();
        mInflater = (LayoutInflater) context
                .getSystemService(context.LAYOUT_INFLATER_SERVICE);

        animateFirstListener = new AnimateFirstDisplayListener();
        memreasImageLoader = MemreasImageLoader.getInstance();
        optionsGallery = MemreasImageLoader.getDefaultDisplayImageOptions();
        optionsStorage = MemreasImageLoader
                .getDefaultDisplayImageOptionsStorage();
    }

    protected QueueAdapter() {
        if (selectedTransferModelQueue == null) {
            selectedTransferModelQueue = new LinkedList<MemreasTransferModel>();
        }
        if (selectedMediaHashMap == null) {
            selectedMediaHashMap = new HashMap<String, Integer>();
        }
    }

    public synchronized static QueueAdapter getInstance() {
        // assumes instance isn't null  or is used for QueueService
        if (instance == null) {
            instance = new QueueAdapter();
        }
        return instance;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return selectedTransferModelQueue.size();
    }

    @Override
    public MemreasTransferModel getItem(int position) {
        return selectedTransferModelQueue.get(position);
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
            convertView = mInflater.inflate(R.layout.screen_queue_listitem, parent,
                    false);

            holder.qbackview = (ImageView) convertView
                    .findViewById(R.id.qbackview);
            holder.qimageview = (ImageView) convertView
                    .findViewById(R.id.qimageview);
            holder.qimageviewgone = (ImageView) convertView
                    .findViewById(R.id.qimageviewgone);
            holder.qvideoImg = (ImageView) convertView
                    .findViewById(R.id.qvideoImg);
            holder.spaceView = (ImageView) convertView
                    .findViewById(R.id.spaceView);
            holder.cancelBtn = (ImageButton) convertView
                    .findViewById(R.id.cancel);
            holder.uploadProgressBar = (ProgressBar) convertView
                    .findViewById(R.id.uploadProgress);
            holder.downloadProgressBar = (ProgressBar) convertView
                    .findViewById(R.id.downloadProgress);
            holder.processText = (TextView) convertView
                    .findViewById(R.id.process);
            holder.cancelBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeQueueEntry(position,
                            MemreasQueueStatus.CANCELED);
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // row specific entries here...
        transferModel = selectedTransferModelQueue.get(position);
        media = transferModel.getMedia();
        GalleryBean media = transferModel.getMedia();
        if (media.getType() == GalleryType.SERVER
                || (media.getMediaType().equalsIgnoreCase("video")
                && media.getMediaThumbnailUrl().length() > 0)) {
            holder.qimageviewgone.setVisibility(View.GONE);
            holder.qimageview.setVisibility(View.VISIBLE);
            try {
                memreasImageLoader.cancelDisplayTask(holder.qimageview);
                if (media.getMediaType().equalsIgnoreCase("video")) {

                    int random_index = new Random().nextInt(media
                            .getMediaThumbnailUrl98x78().length());
                    String url = media.getMediaThumbnailUrl98x78().get(
                            random_index).toString();
                    memreasImageLoader.displayImage(url, holder.qimageview,
                            optionsGallery, animateFirstListener);
                } else {
                    String url = media.getMediaUrl();
                    memreasImageLoader.displayImage(url, holder.qimageview,
                            optionsGallery, animateFirstListener);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Bitmap btmDisplay = media.getMediaThumbBitmap();
                if (btmDisplay != null && !btmDisplay.isRecycled()) {
                    holder.qimageviewgone.setImageBitmap(btmDisplay);
                    holder.qimageviewgone.setVisibility(View.VISIBLE);
                    holder.qimageview.setVisibility(View.GONE);
                } else {
                    holder.qimageviewgone.setVisibility(View.GONE);
                    holder.qimageview.setVisibility(View.VISIBLE);
                    memreasImageLoader.cancelDisplayTask(holder.qimageview);
                    memreasImageLoader.displayImage(
                            "file://" + media.getLocalMediaPath(),
                            holder.qimageview, optionsStorage,
                            animateFirstListener);
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        // Display the play image overlay...
        if (media.getMediaType().equalsIgnoreCase("video"))
            holder.qvideoImg.setVisibility(View.VISIBLE);
        else
            holder.qvideoImg.setVisibility(View.GONE);

        // Rotate the download progress bar...
        if (media.getType() == GalleryType.SERVER) {
            holder.uploadProgressBar.setVisibility(View.GONE);
            holder.downloadProgressBar.setRotationY(180);
            holder.downloadProgressBar.setProgressDrawable(context
                    .getResources()
                    .getDrawable(R.drawable.download_progressbar));
            holder.downloadProgressBar.setVisibility(View.VISIBLE);
            holder.uploadProgressBar.setVisibility(View.GONE);
        } else {
            holder.downloadProgressBar.setVisibility(View.GONE);
            holder.uploadProgressBar.setRotationY(0);
            holder.uploadProgressBar.setProgressDrawable(context.getResources()
                    .getDrawable(R.drawable.upload_progressbar));
            holder.uploadProgressBar.setVisibility(View.VISIBLE);
        }

        // Set the name for the holder to match in the progress task
        holder.name = transferModel.getName();
        holder.transferModel = transferModel;

        // Set the progress bar...
        holder.progress = transferModel.getProgress();
        if (holder.uploadProgressBar.getVisibility() == View.VISIBLE) {
            holder.uploadProgressBar.setProgress(holder.progress);
            holder.processText.setText(String.valueOf(holder.progress) + "%");
        } else if (holder.downloadProgressBar.getVisibility() == View.VISIBLE) {
            holder.downloadProgressBar.setProgress(holder.progress);
            holder.processText.setText(String.valueOf(holder.progress) + "%");
        }

        // Set position for notifiyDataSetChanged() calls
        holder.position = position;
        convertView.setTag(holder);

        return convertView;
    }

    public MemreasTransferModel getItemByName(String name) {
        if (selectedMediaHashMap.containsKey(name)) {
            try {
                int position = selectedMediaHashMap.get(name);
                return selectedTransferModelQueue.get(position);
            } catch (Exception e) {
                Log.i(getClass().getName(), e.getCause().getMessage());
            }
        }
        return null;
    }

    public MemreasTransferModel getNextTransfer() {
        Iterator<MemreasTransferModel> iterator = selectedTransferModelQueue
                .iterator();
        while (iterator.hasNext()) {
            MemreasTransferModel nextTransferModel = iterator.next();
            if (nextTransferModel.getMemreasQueueStatus() == null) {
                return nextTransferModel;
            }
        }
        return null;
    }

    public void removeQueueEntry(int position,
                                 MemreasQueueStatus status) {
        try {

            MemreasTransferModel transferModel = selectedTransferModelQueue
                    .get(position);
            if (transferModel != null) {
                // Set to cancel to stop the transfer...
                transferModel.setMemreasQueueStatus(status);

                // remove and move the model...
                // QueueService calls remove entry...
                // removeQueueEntry(transferModel);
            }
        } catch (Exception e) {
            Log.e(TAG, "Queue exception: " + e.getMessage());
        }
    }

    public void removeQueueEntry(MemreasTransferModel transferModel) {

        if (transferModel.getMemreasQueueStatus() != MemreasQueueStatus.IN_PROGRESS) {

            // Add to the Completed Tab
            CompletedAdapter.getInstance().addTransferModel(transferModel);

            // Continue with removing the item...
            GalleryBean media = transferModel.getMedia();
            media.setSelected(false);
            selectedMediaHashMap.remove(media.getMediaName());
            selectedTransferModelQueue.remove(transferModel);

            Log.d(TAG, "selectedTransferModelQueue.count: " + String.valueOf(selectedTransferModelQueue.size()));

            // Update the ui - since QueueService is separate thread use handler
            if (transferModel.getMedia().getCopyright().equalsIgnoreCase("")) {
                Handler mainHandler = new Handler(context.getMainLooper());
                mainHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        QueueAdapter.getInstance().notifyDataSetChanged();
                    }
                });
            }
        }
    }

    public void removeAllQueueEntries(ListView queueView) {
        View v;
        ViewHolder holder;
        while (queueView.getCount() > 0) {
            v = queueView.getChildAt(0);
            holder = (ViewHolder) v.getTag();
            holder.cancelBtn.performClick();
        }
    }

    public void fetchSelectedMedia() {
        GalleryBean selectedMedia, selectedMediaInQueue;
        HashMap<String, Integer> galleryKeysHashMap = galleryAdapter
                .getHashmapGalleryKeys();
        for (int i = 0; i < GalleryAdapter.getInstance().getCount(); i++) {
            boolean addToQueue = false;
            selectedMedia = GalleryAdapter.getInstance().getItem(i);
            if (selectedMedia.isSelected()
                    && (selectedTransferModelQueue.size() == 0)) {
                // always add to empty queue...
                addToQueue = true;
            } else if (selectedMedia.isSelected()
                    && (!selectedMediaHashMap.containsKey(selectedMedia
                    .getMediaName()))) {
                // add to queue if selected and not in queue
                addToQueue = true;
            }
            if (addToQueue) {
                // add to queue and hashmap for checking later...
                MemreasTransferModel transferModel = new MemreasTransferModel(
                        selectedMedia);
                selectedTransferModelQueue.add(transferModel);
                selectedMediaHashMap.put(transferModel.getName(),
                        selectedTransferModelQueue.indexOf(transferModel));
            }
        }
    }

    public HashMap<String, Integer> getSelectedMediaHashMap() {
        return selectedMediaHashMap;
    }

    public LinkedList<MemreasTransferModel> getSelectedTransferModelQueue() {
        return selectedTransferModelQueue;
    }

    public class ViewHolder {
        public ImageView qbackview;
        public ImageView qimageview, qimageviewgone;
        public ImageView qvideoImg;
        public ProgressBar uploadProgressBar;
        public ProgressBar downloadProgressBar;
        public ImageButton cancelBtn;
        public TextView processText;
        public ImageView selectedImg;
        public ImageView spaceView;
        public MemreasTransferModel transferModel;
        public int progress;
        public String name;
        public int position;

    }
}
