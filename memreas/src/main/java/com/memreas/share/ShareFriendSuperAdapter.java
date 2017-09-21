
package com.memreas.share;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.memreas.R;
import com.memreas.member.FriendBean;
import com.memreas.member.FriendBean.FriendType;
import com.memreas.member.GroupBean;
import com.memreas.util.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;

public class ShareFriendSuperAdapter extends BaseAdapter {

    protected Activity context;
    protected int resource;
    protected LayoutInflater mInflater;
    protected DisplayImageOptions optionsGallery;
    protected DisplayImageOptions optionsStorage;
    protected AnimateFirstDisplayListener animateFirstListener;
    protected ImageLoader memreasImageLoader;
    protected LinkedList mFriendOrGroup;
    protected FriendBean friend;
    protected GroupBean group;
    protected boolean asyncTaskContactsComplete = false;

    @Override
    public int getCount() {
        return mFriendOrGroup.size();
    }

    @Override
    public Object getItem(int position) {
        return mFriendOrGroup.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            /*
             * Initialize Header
			 */
            holder = new ViewHolder();
            convertView = mInflater.inflate(this.resource, parent, false);

            holder.imageview = (ImageView) convertView
                    .findViewById(R.id.profileImageSmall);
            holder.checkbox = (ImageView) convertView
                    .findViewById(R.id.shareitemCheckBox);
            holder.txtNetwork = (TextView) convertView
                    .findViewById(R.id.txtNetwork);
            holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            holder.networkHeaderLinearLayout = (LinearLayout) convertView
                    .findViewById(R.id.networkHeaderLinearLayout);
            holder.networkDetailsLinearLayout = (LinearLayout) convertView
                    .findViewById(R.id.networkDetailsLinearLayout);
            holder.networkDetailsTableLayout = (TableLayout) convertView
                    .findViewById(R.id.networkDetailsTableLayout);

            holder.holderDetailsList = new LinkedList<>();
            holder.position = position;

            holder.imageview.setTag(holder);
            holder.imageview.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (asyncTaskContactsComplete) {
                        setSelected(v);
                    } else {
                        Toast.makeText(context, "loading contacts...",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            convertView.setTag(holder);
        } else {
            /**
             * fetch the re-usable holder
             */
            holder = (ViewHolder) convertView.getTag();


            /**
             * clear holderDetails view here and set below...
             */
            holder.networkDetailsTableLayout.removeAllViews();
            holder.networkDetailsTableLayout.setVisibility(View.GONE);
            holder.networkDetailsLinearLayout.setVisibility(View.GONE);
            holder.position = position;
        }

        /**
         * Set row data...
         */
        Object obj = mFriendOrGroup.get(holder.position);
        if (obj instanceof FriendBean) {
            friend = (FriendBean) obj;

			/*
             * Image and Checkbox section...
			 */
            if ((friend.getFriendType() == FriendType.CONTACT)
                    && (friend.getProfileImgUrl() != null)) {
                holder.imageview.setImageURI(Uri.parse(friend
                        .getProfileImgUrl()));
                holder.imageview.setImageResource(R.drawable.profile_img_small);
            } else if (friend.getFriendType() == FriendType.MEMREAS) {
                memreasImageLoader.displayImage(friend.getProfileImgUrl(),
                        holder.imageview, optionsGallery, animateFirstListener);
            } else {
                holder.imageview.setImageResource(R.drawable.profile_img_small);
            }

            /**
             * Name and network section...
             */
            holder.txtName.setText(friend.getFriendName());
            holder.txtNetwork.setText(friend.getFriendType().toString()
                    .toLowerCase(Locale.US));
            Log.e("Select Friends List - Friend Display friend.getFriendName() Name-->", friend.getFriendName());
            Log.e("Select Friends List - Friend Display holder.txtName.getText().toString() Name-->", holder.txtName.getText().toString());

            /**
             * Set imageview onclick with access to holder
             */
            holder.imageview.setTag(holder);

            /**
             * If friend is selected use click to set the checkbox
             */
            holder.friend = friend;
            holder.checkSwitch();

            /**
             * Set Contacts data rows within Contacts section
             */
            if ((friend.getContactList() != null)
                    && (friend.getContactList().size() > 0)) {

                /**
                 * Populate table layout
                 */
                int i = 0;
                Iterator<FriendBean.LocalContact> iterator = friend
                        .getContactList().iterator();
                while (iterator.hasNext()) {
                    FriendBean.LocalContact contact = iterator.next();

                    /**
                     * Inflate row view
                     */
                    ViewHolderDetails holderDetails = new ViewHolderDetails();
                    View v = mInflater.inflate(
                            R.layout.share_page_add_friends_contact_row_item, parent, false);
                    holderDetails.networkDetailsRow = (TableRow) v
                            .findViewById(R.id.networkDetailsRow);
                    holderDetails.imgbtnCbEmailOrSMS = (ImageButton) v
                            .findViewById(R.id.imgbtnCbEmailOrSMS);
                    holderDetails.txtContactDetails = (TextView) v
                            .findViewById(R.id.txtContactDetails);
                    holderDetails.networkDetailsRow.setVisibility(View.VISIBLE);
                    holderDetails.imgbtnCbEmailOrSMS.setImageResource(R.drawable.unselected);

                    /**
                     * Set Details - check box, email or sms data
                     */
                    if (contact.getType() == FriendBean.ContactType.EMAIL) {
                        holderDetails.txtContactDetails.setText("email: "
                                + contact.getContact());
                    } else if (contact.getType() == FriendBean.ContactType.SMS) {
                        holderDetails.txtContactDetails.setText("sms: "
                                + contact.getContact());
                    }
                    holderDetails.contact = contact;

                    /**
                     * Set imgbtnCbEmailOrSMS onclick with access to holderDetails
                     */
                    holderDetails.imgbtnCbEmailOrSMS.setTag(holderDetails);
                    holderDetails.imgbtnCbEmailOrSMS
                            .setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (asyncTaskContactsComplete) {
                                        setContactSelected(v);
                                    } else {
                                        Toast.makeText(context,
                                                "loading contacts...",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    /**
                     * Now add the view since its setup
                     */
                    holder.networkDetailsTableLayout.addView(v);
                    holder.networkDetailsLinearLayout
                            .setVisibility(View.VISIBLE);
                    holderDetails.parentPosition = position;
                    holderDetails.parentHolder = holder;
                    holderDetails.parentFriend = holder.friend;
                    holderDetails.position = i++;
                    holder.holderDetailsList.add(holderDetails);

                    /**
                     * check for checkbox selection
                     */
                    holderDetails.contact = contact;
                    holderDetails.checkSwitch();

                    /**
                     * Contacts set so show table layout
                     */
                    holder.networkDetailsLinearLayout.setVisibility(View.VISIBLE);
                    holder.networkDetailsTableLayout.setVisibility(View.VISIBLE);

                } // end while
                memreasImageLoader.cancelDisplayTask(holder.imageview);
            } else {
                holder.networkDetailsTableLayout.removeAllViewsInLayout();
                holder.networkDetailsTableLayout.setVisibility(View.GONE);
                holder.networkDetailsLinearLayout.setVisibility(View.GONE);
            }

        } else if (obj instanceof GroupBean) {
            /**
             * Group section...
             * - backlogged
             */
            group = (GroupBean) obj;
            // record position
            holder.txtName.setText(group.getGroupName());
        }

        holder.position = position;
        convertView.setTag(holder);

        return convertView;
    }

    @SuppressWarnings("unchecked")
    public void add(Object obj) {
        mFriendOrGroup.add(obj);
        this.notifyDataSetChanged();
    }

    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public LinkedList getmFriendOrGroup() {
        return mFriendOrGroup;
    }

    public boolean isAsyncTaskContactsComplete() {
        return asyncTaskContactsComplete;
    }

    public void setAsyncTaskContactsComplete(boolean asyncTaskContactsComplete) {
        this.asyncTaskContactsComplete = asyncTaskContactsComplete;
    }

    public void setSelected(View v) {
        /**
         * Handles Parent checkbox for Contact or memreas friend
         */
        ViewHolder holder = (ViewHolder) v.getTag();
        // flip switch
        holder.friend.setSelected(!holder.friend.isSelected());
        holder.checkSwitch();

        /**
         * Handles setting to on/off all sub checkboxes
         */
        Iterator<ViewHolderDetails> iterator = holder.holderDetailsList.iterator();
        while (iterator.hasNext()) {
            ViewHolderDetails holderDetails = iterator.next();
            holderDetails.contact.setSelected(holder.friend.isSelected());
            holderDetails.checkSwitch();
        }
    }

    public void setContactSelected(View v) {
        /**
         * Handles email and sms checkboxes for Contacts - single row
         */
        ViewHolderDetails holderDetails = (ViewHolderDetails) v.getTag();

        /**
         * set checkbox based on contact selected
         */
        // flip switch
        holderDetails.contact.setSelected(!holderDetails.contact.isSelected());
        holderDetails.checkSwitch();

        /**
         * This section handles scenario where if all checkboxes are off then parent should be off
         */
        /*
        Iterator<FriendBean.LocalContact> iterator = friend.getContactList().iterator();
        */
        boolean hasSelection = false;
        Iterator<FriendBean.LocalContact> iterator = holderDetails.parentFriend.getContactList().iterator();
        while (iterator.hasNext()) {
            FriendBean.LocalContact contact = iterator.next();
            if (contact.isSelected()) {
                hasSelection = true;
            }
        }

        holderDetails.parentFriend.setSelected(hasSelection);
        holderDetails.parentHolder.checkSwitch();
        notifyDataSetChanged();

    }

    protected class ViewHolder {
        public FriendBean friend;
        public ImageView imageview;
        public ImageView checkbox;
        public TextView txtName;
        public TextView txtNetwork;
        public LinearLayout networkHeaderLinearLayout;
        public LinearLayout networkDetailsLinearLayout;
        public TableLayout networkDetailsTableLayout;
        public LinkedList<ViewHolderDetails> holderDetailsList;
        public int position;

        //function to turn on / off checkbox
        public void checkSwitch() {
            if (friend.isSelected()) {
                checkbox.setVisibility(View.VISIBLE);
            } else {
                checkbox.setVisibility(View.GONE);
            }
        }
    }

    protected class ViewHolderDetails {
        public TableRow networkDetailsRow;
        public ImageButton imgbtnCbEmailOrSMS;
        public TextView txtContactDetails;
        public ViewHolder parentHolder;
        public FriendBean parentFriend;
        public FriendBean.LocalContact contact;
        public int parentPosition;
        public int position;

        //function to turn on / off checkbox
        public void checkSwitch() {
            if (contact.isSelected()) {
                imgbtnCbEmailOrSMS.setImageResource(R.drawable.selected);
            } else {
                imgbtnCbEmailOrSMS.setImageResource(R.drawable.unselected);
            }
        }

    }

}
