
package com.memreas.member.load;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.ContactsContract.DisplayNameSources;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.memreas.member.FriendBean;
import com.memreas.member.FriendBean.FriendType;
import com.memreas.share.ShareFriendAdapter;

public class LoadContactsAsyncTask extends AsyncTask<Void, Object, Void> {

    ProgressBar progressBar;
    ShareFriendAdapter adapter;
    Activity activity;

    public LoadContactsAsyncTask(Activity activity, ShareFriendAdapter adapter,
                                 ProgressBar progressBar) {
        this.adapter = adapter;
        this.activity = activity;
        this.progressBar = progressBar;
        this.progressBar.setVisibility(View.VISIBLE);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Void doInBackground(Void... arg0) {

        FriendBean friend;
        String phoneNumber = null;
        String email = null;

        // Has phone Number
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String PHOTO_THUMBNAIL_URI = ContactsContract.Contacts.PHOTO_THUMBNAIL_URI;
        String DISPLAY_NAME_PRIMARY = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;
        String DISPLAY_NAME_SOURCE = ContactsContract.Contacts.DISPLAY_NAME_SOURCE;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        // Phone Number
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        String TYPE = ContactsContract.CommonDataKinds.Phone.TYPE;
        int TYPE_MOBILE = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
        int TYPE_WORK_MOBILE = ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE;

        // Has phone Number
        Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
                + " ASC";
        ContentResolver contentResolver = activity.getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null,
                sortOrder);

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                friend = new FriendBean();
                friend.setFriendType(FriendType.CONTACT);

                String displayNamePrimary = cursor.getString(cursor
                        .getColumnIndex(DISPLAY_NAME_PRIMARY));
                int intDisplayNameSource = cursor.getInt(cursor
                        .getColumnIndex(DISPLAY_NAME_SOURCE));
                boolean isEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(
                        displayNamePrimary).matches();
                if ((!isEmail)
                        && (intDisplayNameSource == DisplayNameSources.STRUCTURED_NAME)) {
                    friend.setFriendName(displayNamePrimary);
                } else {
                    continue;
                }
                friend.setFriendId(cursor.getString(cursor.getColumnIndex(_ID)));
                String contentPath = cursor.getString(cursor
                        .getColumnIndex(PHOTO_THUMBNAIL_URI));
                Uri contentUri;
                if (contentPath != null) {
                    contentUri = Uri.parse(contentPath);
                    friend.setProfileImgUrl(contentPath);
                } else {
                    friend.setProfileImgUrl(null);
                }

                /**
                 * Contact section for SMS
                 * - check if device can send SMS...
                 */
                int countEmailAndPhone = 0;
                if (activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor
                            .getColumnIndex(HAS_PHONE_NUMBER)));
                    if (hasPhoneNumber > 0) {
                        // Query and loop for every phone number of the contact
                        Cursor phoneCursor = contentResolver.query(
                                PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?",
                                new String[]{friend.getFriendId()}, null);

                        while (phoneCursor.moveToNext()) {
                            int phoneType = phoneCursor.getInt(phoneCursor
                                    .getColumnIndex(TYPE));
                            String unformattedPhoneNumber = phoneCursor
                                    .getString(phoneCursor.getColumnIndex(NUMBER));
                            // String formattedPhoneNumber =
                            // PhoneNumberUtils.formatNumber(
                            // unformattedPhoneNumber,
                            // Locale.getDefault().getCountry());
                            String formattedPhoneNumber = PhoneNumberUtils
                                    .formatNumber(unformattedPhoneNumber);
                            if ((phoneType == TYPE_MOBILE)
                                    || (phoneType == TYPE_WORK_MOBILE)) {

                                if (PhoneNumberUtils
                                        .isWellFormedSmsAddress(formattedPhoneNumber) && !formattedPhoneNumber.contains("x")) {
                                    friend.addContact(FriendBean.ContactType.SMS,
                                            formattedPhoneNumber);
                                    countEmailAndPhone++;
                                }
                            }
                        }
                        phoneCursor.close();
                    }
                } // end if (manager.getPhoneType() = TelephonyManager.PHONE_TYPE_NONE)

                /**
                 * Contact section for email
                 */
                Cursor emailCursor = contentResolver.query(
                        EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?",
                        new String[]{friend.getFriendId()}, null);

                while (emailCursor.moveToNext()) {
                    String emailFromContact = emailCursor
                            .getString(emailCursor.getColumnIndex(DATA));
                    isEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(
                            emailFromContact).matches();
                    if (isEmail) {
                        friend.addContact(FriendBean.ContactType.EMAIL,
                                emailFromContact);
                        countEmailAndPhone++;
                    }
                }
                emailCursor.close();
                if (countEmailAndPhone > 0) {
                    // only publish if contact has phone or email...
                    publishProgress((Object) friend);
                }
            } // end while
        } // end if

        return null;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        adapter.add(values[0]);
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        adapter.setAsyncTaskContactsComplete(true);
    }

}
