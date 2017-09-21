
package com.memreas.gallery;

import android.util.Log;

import com.memreas.sax.handler.GenerateMediaIdParser;

import org.json.JSONArray;
import org.json.JSONException;

public class MediaIdManager {

    protected static final String TAG = MediaIdManager.class.getName();

    protected static MediaIdManager instance = null;

    private JSONArray mediaIdBatchJsonArray = null;

    private GenerateMediaIdParser generateMediaIdParser = null;

    protected MediaIdManager() {
    }

    /**
     * instantiate Singleton
     */
    public static MediaIdManager getInstance() {
        if (instance == null) {
            instance = new MediaIdManager();
            instance.generateMediaIdParser = new GenerateMediaIdParser(instance);
            instance.generateMediaIdParser.execute();
        }
        return instance;
    }

    /**
     * reset Singleton
     */
    public void reset() {
        instance = null;
    }

    public void setMediaIdBatchJsonArray(JSONArray mediaIdBatchJsonArray) {
        this.mediaIdBatchJsonArray = mediaIdBatchJsonArray;
    }


    //
    // Main Methods go here...
    //

    public String fetchNextMediaId() {

        String mediaId = "";

        try {
            if (mediaIdBatchJsonArray.length() > 5) {
                try {
                    mediaId = mediaIdBatchJsonArray.getString(0);
                    mediaIdBatchJsonArray.remove(0);
                } catch (JSONException jse) {
                    jse.printStackTrace();
                }
            } else {
                if (mediaIdBatchJsonArray.length() > 0) {
                    try {
                        mediaId = mediaIdBatchJsonArray.getString(0);
                        mediaIdBatchJsonArray.remove(0);
                    } catch (JSONException jse) {
                        jse.printStackTrace();
                    }
                }
                /** refresh the mediaId list */
                this.reset();
                MediaIdManager.getInstance();
            }
            //
            // if found return
            //
            return mediaId;
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "fetchNextMediaId exception: " + e.getMessage());
        }
        return null;

        //
        // Sample json
        //
        /*
        [
            "1bd64d16-6a74-48a4-9371-a5fa0a8a0a71",
            ...
        }
        */

    }

}
