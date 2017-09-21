
package com.memreas.memreas.load;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.BaseAdapter;

import com.memreas.base.Common;
import com.memreas.base.SessionManager;
import com.memreas.memreas.MemreasEventBean.EventType;
import com.memreas.memreas.MemreasEventBean.FriendShortDetails;
import com.memreas.memreas.MemreasEventFinder;
import com.memreas.memreas.MemreasEventFriendsBean;
import com.memreas.memreas.MemreasEventFriendsBean.FriendEventDetails;
import com.memreas.memreas.MemreasEventMeBean;
import com.memreas.memreas.MemreasEventPublicBean;
import com.memreas.memreas.MemreasEventsFriendsAdapter;
import com.memreas.memreas.MemreasEventsMeAdapter;
import com.memreas.memreas.MemreasEventsPublicAdapter;
import com.memreas.memreas.ViewMemreasActivity;
import com.memreas.sax.handler.CommonHandler;
import com.memreas.sax.handler.SaxParser;
import com.memreas.sax.handler.XMLGenerator;
import com.memreas.util.MemreasProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Iterator;
import java.util.LinkedList;

public class LoadMemreasEventsAsyncTask extends
        AsyncTask<String, Object, String> {

    private MemreasProgressDialog mProgressDialog;
    private ViewMemreasActivity activity;
    private MemreasEventsMeAdapter meMemreasAdapter;
    private MemreasEventsFriendsAdapter friendsMemreasAdapter;
    private MemreasEventsPublicAdapter publicMemreasAdapter;
    private MemreasEventFinder memreasEventFinder;
    private int myEvents = 0;
    private int friendsEvents = 0;
    private int publicEvents = 0;
    private String sType = "";

    public LoadMemreasEventsAsyncTask(ViewMemreasActivity activity,
                                      BaseAdapter adapter, EventType type) {
        this.activity = activity;
        this.mProgressDialog = MemreasProgressDialog.getInstance(activity);
        switch (type) {
            case ME:
                myEvents = 1;
                meMemreasAdapter = (MemreasEventsMeAdapter) adapter;
                break;
            case FRIENDS:
                friendsEvents = 1;
                friendsMemreasAdapter = (MemreasEventsFriendsAdapter) adapter;
                break;
            case PUBLIC:
                publicEvents = 1;
                publicMemreasAdapter = (MemreasEventsPublicAdapter) adapter;
                break;
            default:
                myEvents = 1;
                meMemreasAdapter = (MemreasEventsMeAdapter) adapter;
                break;
        }
        sType = type.toString().toLowerCase();
        memreasEventFinder = MemreasEventFinder.getInstance();
        mProgressDialog = MemreasProgressDialog.getInstance(activity);
        mProgressDialog.setMessage("loading memreas...");
    }

    @Override
    protected String doInBackground(String... params) {

        String xmlData = XMLGenerator.viewEventXML(myEvents, friendsEvents,
                publicEvents);
        if (myEvents == 1) {
            try {
                SaxParser.parse(Common.SERVER_URL + Common.VIEW_EVENT_ACTION,
                        xmlData, new ViewEventsMeHandler(), "xml");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (friendsEvents == 1) {
            try {
                SaxParser.parse(Common.SERVER_URL + Common.VIEW_EVENT_ACTION,
                        xmlData, new ViewEventsFriendsHandler(), "xml");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (publicEvents == 1) {
            try {
                SaxParser.parse(Common.SERVER_URL + Common.VIEW_EVENT_ACTION,
                        xmlData, new ViewEventsPublicHandler(), "xml");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "success";
    }

    @Override
    protected void onProgressUpdate(Object... values) {

        mProgressDialog.showWithDelay("loading memreas " + sType.toString().toLowerCase() + "...", 2000);
        //mProgressDialog.show();
        if (values[0] instanceof MemreasEventMeBean) {
            MemreasEventMeBean memreasEventMeBean = (MemreasEventMeBean) values[0];
            meMemreasAdapter.add(memreasEventMeBean);
            /*
             * Add to MemreasEventFinder here...
			 */
            memreasEventFinder.add(memreasEventMeBean.getEventId(),
                    SessionManager.getInstance().getUser_id(),
                    EventType.ME, (meMemreasAdapter.getCount() - 1), -1);
        } else if (values[0] instanceof MemreasEventFriendsBean) {
            MemreasEventFriendsBean memreasEventFriendsBean = (MemreasEventFriendsBean) values[0];
            friendsMemreasAdapter.add(memreasEventFriendsBean);

			/*
             * Add to MemreasEventFinder here...
			 */
            LinkedList<FriendEventDetails> friendEventDetailsList = memreasEventFriendsBean
                    .getFriendEventDetailsList();
            int i = 0;
            Iterator<FriendEventDetails> iterator = friendEventDetailsList
                    .iterator();
            while (iterator.hasNext()) {
                FriendEventDetails friendEventDetails = iterator.next();
                memreasEventFinder.add(friendEventDetails.eventId,
                        memreasEventFriendsBean.getEvent_creator_user_id(),
                        EventType.FRIENDS,
                        (friendsMemreasAdapter.getCount() - 1), i++);
            }
        } else if (values[0] instanceof MemreasEventPublicBean) {
            MemreasEventPublicBean memreasEventPublicBean = (MemreasEventPublicBean) values[0];
            publicMemreasAdapter.add(memreasEventPublicBean);
			/*
			 * Add to MemreasEventFinder here...
			 */
            memreasEventFinder
                    .add(memreasEventPublicBean.getEvent_id(),
                            memreasEventPublicBean.getEvent_creator_user_id(),
                            EventType.PUBLIC,
                            (publicMemreasAdapter.getCount() - 1), -1);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (myEvents == 1) {
            MemreasEventsMeAdapter.asyncTaskComplete = true;
        } else if (friendsEvents == 1) {
            MemreasEventsFriendsAdapter.asyncTaskComplete = true;
        } else if (publicEvents == 1) {
            MemreasEventsPublicAdapter.asyncTaskComplete = true;
        }
        try {
            mProgressDialog.dismiss();
        } catch (Exception e) {
            // do nothing...
        }
        if ((MemreasEventsMeAdapter.asyncTaskComplete)
                && (MemreasEventsFriendsAdapter.asyncTaskComplete)
                && (MemreasEventsPublicAdapter.asyncTaskComplete)) {
            activity.onDataLoadFinishCallBack();
        }
    }

    /*
     * Handler class for parsing return data for me events
     */
    private class ViewEventsPublicHandler extends DefaultHandler {
        Boolean curElement;
        String curValue;
        int i = 0;
        private String status;
        private String message;
        private MemreasEventPublicBean memreasEventPublicBean;
        private LinkedList<FriendShortDetails> publicEventFriendsList;
        private MemreasEventPublicBean.FriendShortDetails publicEventFriend;
        private LinkedList<MemreasEventPublicBean.MediaShortDetails> publicEventMediaList;
        private MemreasEventPublicBean.MediaShortDetails publicEventMedia;
        private LinkedList<MemreasEventPublicBean.CommentShortDetails> publicEventCommentList;
        private MemreasEventPublicBean.CommentShortDetails publicEventComment;
        private boolean inComment = false;

        public ViewEventsPublicHandler() {
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {
            i++;
            curElement = true;
            curValue = "";

            if (localName.equals("event")) {
                memreasEventPublicBean = new MemreasEventPublicBean();
                publicEventFriendsList = new LinkedList<FriendShortDetails>();
                memreasEventPublicBean
                        .setPublicEventFriendsList(publicEventFriendsList);
                publicEventMediaList = new LinkedList<MemreasEventPublicBean.MediaShortDetails>();
                memreasEventPublicBean
                        .setPublicEventMediaList(publicEventMediaList);
                publicEventCommentList = new LinkedList<MemreasEventPublicBean.CommentShortDetails>();
                memreasEventPublicBean
                        .setPublicEventCommentsList(publicEventCommentList);
            } else if (localName.equals("event_friend")) {
                publicEventFriend = memreasEventPublicBean
                        .newFriendShortDetails();
                publicEventFriendsList.add(publicEventFriend);
            } else if (localName.equals("comment")) {
                inComment = true;
                publicEventComment = memreasEventPublicBean
                        .newCommentShortDetails();
                publicEventCommentList.add(publicEventComment);
            } else if (localName.equals("event_media")) {
                publicEventMedia = memreasEventPublicBean
                        .newMediaShortDetails();
                publicEventMediaList.add(publicEventMedia);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {

            Log.d(getClass().getName() + i, "endElement =>" + localName + " =>"
                    + curValue);
            if (localName.equals("status")) {
                status = curValue;
            } else if (localName.equals("event_id")) {
                memreasEventPublicBean.setEvent_id(curValue);
            } else if (localName.equals("event_name")) {
                memreasEventPublicBean.setEvent_name(curValue);
            } else if (localName.equals("event_creator")) {
                memreasEventPublicBean.setEvent_creator(curValue);
            } else if (localName.equals("event_creator_user_id")) {
                memreasEventPublicBean.setEvent_creator_user_id(curValue);
            } else if (localName.equals("event_location")) {
                memreasEventPublicBean.setEvent_location(curValue);
            } else if (localName.equals("event_date")) {
                memreasEventPublicBean.setEvent_date(curValue);
            } else if (localName.equals("event_metadata")) {
                memreasEventPublicBean.setEvent_metadata(curValue);
            } else if (localName.equals("event_viewable_from")) {
                memreasEventPublicBean.setEvent_viewable_from(curValue);
            } else if (localName.equals("event_viewable_to")) {
                memreasEventPublicBean.setEvent_viewable_to(curValue);
            } else if (localName.equals("event_like_total")) {
                memreasEventPublicBean.setEvent_like_total(Integer.valueOf(
                        curValue).intValue());
            } else if (localName.equals("event_comment_total")) {
                memreasEventPublicBean.setEvent_comment_total(Integer.valueOf(
                        curValue).intValue());
            } else if (localName.equals("profile_pic")) {
                if (inComment) {
                    publicEventComment.profilePicUrl = CommonHandler
                            .parseMemJSON(curValue);
                } else {
                    memreasEventPublicBean.setProfile_pic(CommonHandler
                            .parseMemJSON(curValue));
                }
                // memreasEventPublicBean.setProfile_pic(CommonHandler
                // .parseMemJSON(curValue));
            } else if (localName.equals("profile_pic_79x80")) {
                memreasEventPublicBean.setProfile_pic_79x80(CommonHandler
                        .parseMemJSONArray(curValue));
            } else if (localName.equals("profile_pic_448x306")) {
                memreasEventPublicBean.setProfile_pic_448x306(CommonHandler
                        .parseMemJSONArray(curValue));
            } else if (localName.equals("profile_pic_98x78")) {
                memreasEventPublicBean.setProfile_pic_98x78(CommonHandler
                        .parseMemJSONArray(curValue));
            }
			/*
			 * Friend section...
			 */
            else if (localName.equals("event_friend_id")) {
                publicEventFriend.friend_id = curValue;
            } else if (localName.equals("event_friend_social_username")) {
                publicEventFriend.friend_social_username = curValue;
            } else if (localName.equals("event_friend_url_image")) {
                publicEventFriend.friend_url_image = CommonHandler
                        .parseMemJSON(curValue);
            }
			/*
			 * Comments section...
			 */
            else if (localName.equals("comment_media_id")) {
                publicEventComment.media_id = curValue;
            } else if (localName.equals("comment_event_id")) {
                publicEventComment.event_id = curValue;
            } else if (localName.equals("username")) {
                publicEventComment.username = curValue;
            } else if (localName.equals("comment_text")) {
                publicEventComment.text = curValue;
            } else if (localName.equals("type")) {
                publicEventComment.type = curValue;
            } else if (localName.equals("audio_media_url")) {
                publicEventComment.audio_media_url = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("commented_about")) {
                publicEventComment.commented_about = curValue;
            } else if (localName.equals("comment")) {
                inComment = false;
            }
			/*
			 * Media section...
			 */
            else if (localName.equals("event_media_type")) {
                publicEventMedia.media_type = curValue;
            } else if (localName.equals("event_media_s3file_location")) {
                JSONArray locationArr = CommonHandler
                        .parseMemJSONArray(curValue);
                if (locationArr != null) {
                    try {
                        JSONObject locatonObj = (JSONObject) locationArr.get(0);
                        publicEventMedia.location.latititude = locatonObj
                                .getDouble("latitude");
                        publicEventMedia.location.longitude = locatonObj
                                .getDouble("longitude");
                    } catch (JSONException e) {
                        publicEventMedia.location.latititude = 0;
                        publicEventMedia.location.longitude = 0;
                    }
                    try {
                        JSONObject locatonObj = (JSONObject) locationArr.get(0);
                        publicEventMedia.location.address = locatonObj
                                .getString("address");
                    } catch (JSONException e) {
                        publicEventMedia.location.address = "";
                    }
                }
            } else if (localName.equals("event_media_name")) {
                publicEventMedia.media_name = curValue;
            } else if (localName.equals("event_media_url")) {
                publicEventMedia.media_url = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("event_media_url_web")) {
                publicEventMedia.media_url_web = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("event_media_url_webm")) {
                publicEventMedia.media_url_webm = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("event_media_url_hls")) {
                publicEventMedia.media_url_hls = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("event_media_id")) {
                publicEventMedia.media_id = curValue;
            } else if (localName.equals("event_media_video_thum")) {
                publicEventMedia.media_video_thum = CommonHandler
                        .parseMemJSONArray(curValue);
            } else if (localName.equals("event_media_79x80")) {
                publicEventMedia.media_79x80 = CommonHandler
                        .parseMemJSONArray(curValue);
            } else if (localName.equals("event_media_98x78")) {
                publicEventMedia.media_98x78 = CommonHandler
                        .parseMemJSONArray(curValue);
            } else if (localName.equals("event_media_448x306")) {
                publicEventMedia.media_448x306 = CommonHandler
                        .parseMemJSONArray(curValue);
            } else if (localName.equals("event_media_s3_url_path")) {
                publicEventMedia.s3file_media_url_path = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("event_media_s3_url_web_path")) {
                publicEventMedia.s3file_media_url_web_path = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("event_media_s3file_download_path")) {
                publicEventMedia.s3file_media_url_download_path = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("event_media_inappropriate")) {
                publicEventMedia.event_media_inappropriate = CommonHandler
                        .parseMemJSONArray(curValue);
            } else if (localName.equals("event")) {
				/*
				 * end of event publish progress
				 */
                publishProgress(memreasEventPublicBean);
            }
			/*
			 * Reset element
			 */
            curElement = false;
            curValue = "";
        }

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            if (curElement) {
                curValue += new String(ch, start, length);
            }
        }

    }

    /*
     * Handler class for parsing return data for me events
     */
    private class ViewEventsFriendsHandler extends DefaultHandler {
        Boolean curElement;
        String curValue;
        int i = 0;
        private String status;
        private String message;
        private MemreasEventFriendsBean memreasEventFriendsBean;
        private LinkedList<FriendEventDetails> friendEventDetailsList;
        private MemreasEventFriendsBean.FriendEventDetails friendEventDetails;
        private LinkedList<FriendShortDetails> eventFriendShortDetailsList;
        private FriendShortDetails eventFriendShortDetails;
        private LinkedList<MemreasEventFriendsBean.MediaShortDetails> mediaShortDetailsList;
        private MemreasEventFriendsBean.MediaShortDetails mediaShortDetails;
        private LinkedList<MemreasEventFriendsBean.CommentShortDetails> commentShortDetailsList;
        private MemreasEventFriendsBean.CommentShortDetails commentShortDetails;
        private boolean inComment = false;

        public ViewEventsFriendsHandler() {
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {
            i++;
            curElement = true;
            curValue = "";

            if (localName.equals("friend")) {
                memreasEventFriendsBean = new MemreasEventFriendsBean();
                friendEventDetailsList = new LinkedList<FriendEventDetails>();
            } else if (localName.equals("events")) {
                memreasEventFriendsBean
                        .setFriendEventDetailsList(friendEventDetailsList);
            } else if (localName.equals("event")) {
                friendEventDetails = memreasEventFriendsBean
                        .newFriendEventDetails();
                friendEventDetailsList.add(friendEventDetails);
                friendEventDetails.mediaShortDetailsList = new LinkedList<MemreasEventFriendsBean.MediaShortDetails>();
                friendEventDetails.eventFriendShortDetailsList = new LinkedList<FriendShortDetails>();
                friendEventDetails.commentShortDetailsList = new LinkedList<MemreasEventFriendsBean.CommentShortDetails>();
            } else if (localName.equals("comment")) {
                inComment = true;
                commentShortDetails = memreasEventFriendsBean
                        .newCommentShortDetails();
                friendEventDetails.commentShortDetailsList
                        .add(commentShortDetails);
            } else if (localName.equals("event_media")) {
                mediaShortDetails = memreasEventFriendsBean
                        .newMediaShortDetails();
                friendEventDetails.mediaShortDetailsList.add(mediaShortDetails);
            } else if (localName.equals("event_friend")) {
                eventFriendShortDetails = memreasEventFriendsBean
                        .newFriendShortDetails();
                friendEventDetails.eventFriendShortDetailsList
                        .add(eventFriendShortDetails);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {

            // Log.d(getClass().getName() + i, "endElement =>" + localName +
            // " =>"
            // + curValue);
            if (localName.equals("status")) {
                status = curValue;
            } else if (localName.equals("message")) {
                message = curValue;
            } else if (localName.equals("event_creator")) {
                memreasEventFriendsBean.setEvent_creator(curValue);
            } else if (localName.equals("profile_pic")) {
                if (inComment) {
                    commentShortDetails.profilePicUrl = CommonHandler
                            .parseMemJSON(curValue);
                } else {
                    memreasEventFriendsBean.setProfile_pic(CommonHandler
                            .parseMemJSON(curValue));
                }
            } else if (localName.equals("profile_pic_79x80")) {
                memreasEventFriendsBean.setProfile_pic_79x80(CommonHandler
                        .parseMemJSONArray(curValue));
            } else if (localName.equals("profile_pic_448x306")) {
                memreasEventFriendsBean.setProfile_pic_448x306(CommonHandler
                        .parseMemJSONArray(curValue));
            } else if (localName.equals("profile_pic_98x78")) {
                memreasEventFriendsBean.setProfile_pic_98x78(CommonHandler
                        .parseMemJSONArray(curValue));
            } else if (localName.equals("event_id")) {
                friendEventDetails.eventId = curValue;
            } else if (localName.equals("event_name")) {
                friendEventDetails.event_name = curValue;
            } else if (localName.equals("event_metadata")) {
                friendEventDetails.event_metadata = curValue;
            } else if (localName.equals("friend_can_post")) {
                friendEventDetails.friendsCanPost = (curValue
                        .equalsIgnoreCase("1")) ? true : false;
            } else if (localName.equals("friend_can_share")) {
                friendEventDetails.friendsCanAddFriends = (curValue
                        .equalsIgnoreCase("1")) ? true : false;
            }
			/*
			 * Friend section...
			 */
            else if (localName.equals("event_friend_id")) {
                eventFriendShortDetails.friend_id = curValue;
            } else if (localName.equals("event_friend_social_username")) {
                eventFriendShortDetails.friend_social_username = curValue;
            } else if (localName.equals("event_friend_url_image")) {
                eventFriendShortDetails.friend_url_image = CommonHandler
                        .parseMemJSON(curValue);
            }
			/*
			 * Comment section...
			 */
            else if (localName.equals("comment_media_id")) {
                commentShortDetails.media_id = curValue;
            } else if (localName.equals("comment_event_id")) {
                commentShortDetails.event_id = curValue;
            } else if (localName.equals("username")) {
                commentShortDetails.username = curValue;
            } else if (localName.equals("comment_text")) {
                commentShortDetails.text = curValue;
            } else if (localName.equals("type")) {
                commentShortDetails.type = curValue;
            } else if (localName.equals("audio_media_url")) {
                commentShortDetails.audio_media_url = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("commented_about")) {
                commentShortDetails.commented_about = curValue;
            } else if (localName.equals("comment")) {
                inComment = false;
            } else if (localName.equals("event_media_type")) {
                mediaShortDetails.media_type = curValue;
            } else if (localName.equals("event_media_s3file_location")) {
                JSONArray locationArr = CommonHandler
                        .parseMemJSONArray(curValue);
                if (locationArr != null) {
                    try {
                        JSONObject locatonObj = (JSONObject) locationArr.get(0);
                        mediaShortDetails.location.latititude = locatonObj
                                .getDouble("latitude");
                        mediaShortDetails.location.longitude = locatonObj
                                .getDouble("longitude");
                    } catch (JSONException e) {
                        mediaShortDetails.location.latititude = 0;
                        mediaShortDetails.location.longitude = 0;
                    }
                    try {
                        JSONObject locatonObj = (JSONObject) locationArr.get(0);
                        mediaShortDetails.location.address = locatonObj
                                .getString("address");
                    } catch (JSONException e) {
                        mediaShortDetails.location.address = "";
                    }
                }
            } else if (localName.equals("event_media_name")) {
                mediaShortDetails.media_name = curValue;
            } else if (localName.equals("event_media_url")) {
                mediaShortDetails.media_url = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("event_media_url_web")) {
                mediaShortDetails.media_url_web = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("event_media_url_webm")) {
                mediaShortDetails.media_url_webm = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("event_media_url_hls")) {
                mediaShortDetails.media_url_hls = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("event_media_id")) {
                mediaShortDetails.media_id = curValue;
            } else if (localName.equals("event_media_video_thum")) {
                mediaShortDetails.media_video_thum = CommonHandler
                        .parseMemJSONArray(curValue);
            } else if (localName.equals("event_media_79x80")) {
                mediaShortDetails.media_79x80 = CommonHandler
                        .parseMemJSONArray(curValue);
            } else if (localName.equals("event_media_98x78")) {
                mediaShortDetails.media_98x78 = CommonHandler
                        .parseMemJSONArray(curValue);
            } else if (localName.equals("event_media_448x306")) {
                mediaShortDetails.media_448x306 = CommonHandler
                        .parseMemJSONArray(curValue);
            } else if (localName.equals("event_media_s3_url_path")) {
                mediaShortDetails.s3file_media_url_path = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("event_media_s3_url_web_path")) {
                mediaShortDetails.s3file_media_url_web_path = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("event_media_s3file_download_path")) {
                mediaShortDetails.s3file_media_url_download_path = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("friend")) {
				/*
				 * end of event publish progress
				 */
                publishProgress(memreasEventFriendsBean);
            }
			/*
			 * Reset element
			 */
            curElement = false;
            curValue = "";
        }

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            if (curElement) {
                curValue += new String(ch, start, length);
            }
        }

    }

    private class ViewEventsMeHandler extends DefaultHandler {
        Boolean curElement;
        String curValue;
        int i = 0;
        private String status;
        private String message;
        private MemreasEventMeBean memreasEventMeBean;
        private LinkedList<FriendShortDetails> friendShortDetailsList;
        private MemreasEventMeBean.FriendShortDetails friendShortDetails;
        private LinkedList<MemreasEventMeBean.MediaShortDetails> mediaShortDetailsList;
        private MemreasEventMeBean.MediaShortDetails mediaShortDetails;
        private LinkedList<MemreasEventMeBean.CommentShortDetails> commentShortDetailsList;
        private MemreasEventMeBean.CommentShortDetails commentShortDetails;
        private boolean inComment = false;

        public ViewEventsMeHandler() {
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {
            i++;
            curElement = true;
            curValue = "";

            if (localName.equals("event")) {
                memreasEventMeBean = new MemreasEventMeBean();
                mediaShortDetailsList = new LinkedList<MemreasEventMeBean.MediaShortDetails>();
                memreasEventMeBean.setMediaList(mediaShortDetailsList);
                friendShortDetailsList = new LinkedList<FriendShortDetails>();
                memreasEventMeBean.setFriendList(friendShortDetailsList);
                commentShortDetailsList = new LinkedList<MemreasEventMeBean.CommentShortDetails>();
                memreasEventMeBean.setCommentList(commentShortDetailsList);
            } else if (localName.equals("comment")) {
                inComment = true;
                commentShortDetails = memreasEventMeBean
                        .newCommentShortDetails();
                commentShortDetailsList.add(commentShortDetails);
            } else if (localName.equals("event_media")) {
                mediaShortDetails = memreasEventMeBean.newMediaShortDetails();
                mediaShortDetailsList.add(mediaShortDetails);
            } else if (localName.equals("event_friend")) {
                friendShortDetails = memreasEventMeBean.newFriendShortDetails();
                friendShortDetailsList.add(friendShortDetails);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {

            //Log.d(getClass().getName() + i, "endElement =>" + localName +
            //        " =>"
            //        + curValue);
            if (localName.equals("status")) {
                status = curValue;
            } else if (localName.equals("message")) {
                message = curValue;
            } else if (localName.equals("profile_pic")) {
                commentShortDetails.profilePicUrl = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("event_id")) {
                memreasEventMeBean.setEventId(curValue);
            } else if (localName.equals("event_name")) {
                memreasEventMeBean.setName(curValue);
            } else if (localName.equals("event_metadata")) {
                memreasEventMeBean.setMetadata(curValue);
            } else if (localName.equals("friend_can_post")) {
                memreasEventMeBean.setFriendsCanPost(curValue
                        .equalsIgnoreCase("1") ? true : false);
            } else if (localName.equals("friend_can_share")) {
                memreasEventMeBean.setFriendsCanAddFriends(curValue
                        .equalsIgnoreCase("1") ? true : false);
            } else if (localName.equals("like_count")) {
                try {
                    memreasEventMeBean.setLikeCount(Integer.valueOf(curValue));
                } catch (Exception e) {
                    memreasEventMeBean.setLikeCount(0);
                }
            } else if (localName.equals("comment_count")) {
                try {
                    memreasEventMeBean.setCommentCount(Integer
                            .valueOf(curValue));
                } catch (Exception e) {
                    memreasEventMeBean.setCommentCount(0);
                }
            }
			/*
			 * Friend section...
			 */
            else if (localName.equals("event_friend_id")) {
                friendShortDetails.friend_id = curValue;
            } else if (localName.equals("event_friend_social_username")) {
                friendShortDetails.friend_social_username = curValue;
            } else if (localName.equals("event_friend_url_image")) {
                friendShortDetails.friend_url_image = CommonHandler
                        .parseMemJSON(curValue);
            }
			/*
			 * Comment section...
			 */
            else if (localName.equals("comment_media_id")) {
                commentShortDetails.media_id = curValue;
            } else if (localName.equals("comment_event_id")) {
                commentShortDetails.event_id = curValue;
            } else if (localName.equals("username")) {
                commentShortDetails.username = curValue;
            } else if (localName.equals("comment_text")) {
                commentShortDetails.text = curValue;
            } else if (localName.equals("type")) {
                commentShortDetails.type = curValue;
            } else if (localName.equals("audio_media_url")) {
                commentShortDetails.audio_media_url = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("commented_about")) {
                commentShortDetails.commented_about = curValue;
            } else if (localName.equals("comment")) {
                inComment = false;
            } else if (localName.equals("event_media_type")) {
                mediaShortDetails.media_type = curValue;
            } else if (localName.equals("event_media_s3file_location")) {
                JSONArray locationArr = CommonHandler
                        .parseMemJSONArray(curValue);
                if (locationArr != null) {
                    try {
                        JSONObject locatonObj = (JSONObject) locationArr.get(0);
                        mediaShortDetails.location.latititude = locatonObj
                                .getDouble("latitude");
                        mediaShortDetails.location.longitude = locatonObj
                                .getDouble("longitude");
                    } catch (JSONException e) {
                        mediaShortDetails.location.latititude = 0;
                        mediaShortDetails.location.longitude = 0;
                    }
                    try {
                        JSONObject locatonObj = (JSONObject) locationArr.get(0);
                        mediaShortDetails.location.address = locatonObj
                                .getString("address");
                    } catch (JSONException e) {
                        mediaShortDetails.location.address = "";
                    }
                }
            } else if (localName.equals("event_media_name")) {
                mediaShortDetails.media_name = curValue;
            } else if (localName.equals("event_media_url")) {
                mediaShortDetails.media_url = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("event_media_url_web")) {
                mediaShortDetails.media_url_web = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("event_media_url_webm")) {
                mediaShortDetails.media_url_webm = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("event_media_url_hls")) {
                mediaShortDetails.media_url_hls = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("event_media_id")) {
                mediaShortDetails.media_id = curValue;
            } else if (localName.equals("event_media_video_thum")) {
                mediaShortDetails.media_video_thum = CommonHandler
                        .parseMemJSONArray(curValue);
            } else if (localName.equals("event_media_79x80")) {
                mediaShortDetails.media_79x80 = CommonHandler
                        .parseMemJSONArray(curValue);
            } else if (localName.equals("event_media_98x78")) {
                mediaShortDetails.media_98x78 = CommonHandler
                        .parseMemJSONArray(curValue);
            } else if (localName.equals("event_media_448x306")) {
                mediaShortDetails.media_448x306 = CommonHandler
                        .parseMemJSONArray(curValue);
            } else if (localName.equals("event_media_s3_url_path")) {
                mediaShortDetails.s3file_media_url_path = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("event_media_s3_url_web_path")) {
                mediaShortDetails.s3file_media_url_web_path = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("event_media_s3file_download_path")) {
                mediaShortDetails.s3file_media_url_download_path = CommonHandler
                        .parseMemJSON(curValue);
            } else if (localName.equals("event")) {
				/*
				 * end of event publish progress
				 */
                publishProgress(memreasEventMeBean);
            }
			/*
			 * Reset element
			 */
            curElement = false;
            curValue = "";
        }

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            if (curElement) {
                curValue += new String(ch, start, length);
            }
        }

    }

}
