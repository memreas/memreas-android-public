
package com.memreas.gallery;

import android.util.Log;

import com.memreas.base.Common;
import com.memreas.sax.handler.CopyrightManagerParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CopyrightManager {

    protected static final String TAG = CopyrightManager.class.getName();

    protected static CopyrightManager instance = null;

    private int remaining = 0;

    private int lastCopyRightIndex = 0;

    private JSONObject copyrightJsonObject = null;

    private JSONArray copyrightBatchJsonArray = null;

    private CopyrightManagerParser copyrightManagerParser = null;

    private boolean fetching = false;

    protected CopyrightManager() {
    }

    /**
     * instantiate Singleton
     */
    public static CopyrightManager getInstance() {
        if (instance == null) {
            instance = new CopyrightManager();
            instance.copyrightManagerParser = new CopyrightManagerParser();
            instance.copyrightManagerParser.execute();
        }
        return instance;
    }

    /**
     * reset Singleton
     */
    public static void reset() {
        instance = null;
    }

    //
    // Main Methods go here...
    //

    public JSONObject fetchNextCopyRight() {

        if (copyrightBatchJsonArray == null) {
            reset();
            getInstance();
        }

        for (int i = 0; i < copyrightBatchJsonArray.length(); i++) {
            try {
                // check each object to find one not used
                copyrightJsonObject = copyrightBatchJsonArray.getJSONObject(i);
                if (copyrightJsonObject.getInt("used") == 0) {
                    copyrightJsonObject.put("used", Integer.valueOf(1));
                    remaining -= 1;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (!isFetching()) {
            if (getRemaining() <= Common.FETCHCOPYRIGHTBATCH_RUNNING_LOW) {
                copyrightManagerParser = new CopyrightManagerParser();
                copyrightManagerParser.execute();
                setFetching(true);
            }
            //SystemClock.sleep(1000);
        }

        //
        // if found return
        //
        return copyrightJsonObject;

        //
        // Sample json pre-usage
        //
        /*
        {
            "copyright_batch_id": "1bd64d16-6a74-48a4-9371-a5fa0a8a0a71",
            "copyright_id": "763c80f1-4847-4d6f-b7fa-ac28fadc5576",
            "media_id": "d5f24950-c9e9-42dc-8fca-84d638d5d5c1",
            "copyright_id_md5": "667834b471049590a554ea61f3e6c44a",
            "copyright_id_sha1": "3206f44b1e26b577b856b5b44b50e68332e04f39",
            "used": 0
        }
        */

    }

    public boolean isFetching() {
        return fetching;
    }

    public void setFetching(boolean fetching) {
        this.fetching = fetching;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public JSONArray getCopyrightBatchJsonArray() {
        return copyrightBatchJsonArray;
    }

    public void setCopyrightBatchJsonArray(JSONArray copyrightBatch) {
        try {
            this.copyrightBatchJsonArray = copyrightBatch;
            remaining = copyrightBatch.length();
        } catch (NullPointerException npe) {
            Log.e(TAG, "setCopyrightBatchJsonArray() null error" + npe.getMessage());
        }
    }
}
