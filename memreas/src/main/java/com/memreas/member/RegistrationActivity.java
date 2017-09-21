package com.memreas.member;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.memreas.R;
import com.memreas.base.BaseActivity;
import com.memreas.base.Common;
import com.memreas.gallery.GalleryBean;
import com.memreas.gallery.GalleryBean.GalleryType;
import com.memreas.gallery.MediaIdManager;
import com.memreas.legal.TermsOfServiceActivity;
import com.memreas.queue.MemreasTransferModel;
import com.memreas.queue.MemreasTransferModel.MemreasQueueStatus;
import com.memreas.queue.MemreasTransferModel.Type;
import com.memreas.queue.QueueAdapter;
import com.memreas.queue.QueueService;
import com.memreas.sax.handler.CommonGetSet;
import com.memreas.sax.handler.CommonHandler;
import com.memreas.sax.handler.RegistrationParser;
import com.memreas.sax.handler.SaxParser;
import com.memreas.sax.handler.XMLGenerator;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.ImageUtils;
import com.memreas.util.MemreasImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends BaseActivity implements
		OnClickListener {

	private ProgressDialog mProgressDialog;
	private LinearLayout loadingLayout;
	private ImageView profileImg;
	private ImageButton btnAddProfilePic;
	public ImageButton btnImgSubmit;
	private ImageView btnImgChecked;

	private EditText txtEmail;
	private EditText txtUserName;
	private EditText txtPassword;
	private EditText txtVerifiedPassword;
	private EditText txtInvited;

	private TextView tvVerEmail;
	private TextView tvVerUserName;
	private TextView tvVerPassword;
	private TextView tvVerVerfyPass;
	private TextView tvVerInvite;
	public TextView tvPercentage;
	private TextView tvTermsOfService;

	private String mImagePath;
	private String mMimeType;
	private String xmlData;
	private String device_id;
	private String profile_photo="0";

	private boolean emailFlag = false;
	private boolean usernameFlag = false;
	private boolean passwordFlag = false;
	private boolean inviteFlag = true;
	private boolean secretFlag = false;
	private boolean registrationFlag = false;

	private int USERNAME_MAX = 32;// Max 32 characters
	private int USERNAME_MIN = 4;// Min 4
	public static final int PASSWORD_MIN = 8;// Min 8
	private int PASSWORD_MAX = 32;// Max 32
	private CheckUserNameFromServer mTaskCheckUserName;
	private MediaIdManager mMediaIdManager;

	public MemreasTransferModel profilePicTransferModel;
	protected DisplayImageOptions optionsGallery;
	protected DisplayImageOptions optionsStorage;
	protected ImageLoadingListener animateFirstListener;
	protected ImageLoader memreasImageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_signup);

		// start Queue Service so Amazon client is ready...
		startQueueService();

		animateFirstListener = new AnimateFirstDisplayListener();
		memreasImageLoader = MemreasImageLoader.getInstance();
		optionsGallery = MemreasImageLoader.getDefaultDisplayImageOptions();
		optionsStorage = MemreasImageLoader
				.getDefaultDisplayImageOptionsStorage();

		btnAddProfilePic = (ImageButton) findViewById(R.id.btnAddProfilePic);
		btnImgSubmit = (ImageButton) findViewById(R.id.btnSubmit);
		btnImgChecked = (ImageView) findViewById(R.id.imgChecked);

		txtEmail = (EditText) findViewById(R.id.txtEmail);
		txtUserName = (EditText) findViewById(R.id.txtUsername);
		txtPassword = (EditText) findViewById(R.id.txtPassword);
		txtVerifiedPassword = (EditText) findViewById(R.id.txtVerifyPassword);
		txtInvited = (EditText) findViewById(R.id.txtInvite);

		tvVerEmail = (TextView) findViewById(R.id.tvVerEmail);
		tvVerPassword = (TextView) findViewById(R.id.tvPassword);
		tvVerUserName = (TextView) findViewById(R.id.tvVerUsername);
		tvVerVerfyPass = (TextView) findViewById(R.id.tvVerPassword);
		tvVerInvite = (TextView) findViewById(R.id.tvVerInvite);
		tvPercentage = (TextView) findViewById(R.id.percentageTxt);
		tvTermsOfService = (TextView) findViewById(R.id.lblLegalDisclaimer);

		loadingLayout = (LinearLayout) findViewById(R.id.loadingLayout);

		profileImg = (ImageView) findViewById(R.id.imgProfilePic);

		btnAddProfilePic.setOnClickListener(this);
		btnImgSubmit.setOnClickListener(this);
		btnImgChecked.setOnClickListener(this);

		tvTermsOfService.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(RegistrationActivity.this,
                        TermsOfServiceActivity.class);
                startActivity(intent);
            }
        });

		txtEmail.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !isEmailValid(txtEmail.getText())) {
                    emailFlag = false;
                    tvVerEmail.setText(R.string.register_invalid_email);
                } else {
                    tvVerEmail.setText("");
                    emailFlag = true;
                }
            }
        });

		txtUserName.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    checkUserName(false);
                }
            }
        });
		txtUserName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                usernameFlag = false;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

		txtPassword.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT
                        || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    checkPassword();
                }
                return false;
            }
        });

		txtPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                verifyPassword(txtPassword, tvVerPassword);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

		txtVerifiedPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                verifyPassword(txtVerifiedPassword, tvVerVerfyPass);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

		txtVerifiedPassword
				.setOnEditorActionListener(new OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView v, int actionId,
                                                  KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT
                                || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                            checkVerifiedPassword();
                        }
                        return false;
                    }
                });

        txtInvited.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if (!hasFocus) {
					if (txtInvited.getText().toString().equals("")) {
						inviteFlag = true;
						tvVerInvite.setText("");
					} else if (!isEmailValid(txtInvited.getText())) {
						inviteFlag = false;
						tvVerInvite.setText(R.string.register_invalid_email);
					}
				} else {
					tvVerInvite.setText("");
					inviteFlag = true;
				}
			}
		});

		/**
		 * Fetch the MediaIdManager
		 */
		mMediaIdManager = MediaIdManager.getInstance();
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(getApplicationContext(),
				MainLoginSelectionActivity.class));
		finish();
	}

	@Override
	public void onClick(View v) {

		// Submit button

		v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.button_click));

		if (v == btnImgSubmit) {
			if (emailFlag && usernameFlag && passwordFlag && inviteFlag) {

				if ((!(txtEmail.getText().toString()).equals(""))
						&& (!(txtUserName.getText().toString()).equals(""))
						&& (!(txtPassword.getText().toString()).equals(""))
						&& (!(txtVerifiedPassword.getText().toString())
								.equals(""))) {

					if (registrationFlag) {
						emailFlag = false;
						usernameFlag = false;
						passwordFlag = false;
						inviteFlag = false;
						finalRegistrationVerification();
					} else {
						userNotationAlert(R.string.register_accept_disclaimer);
					}
				} else {
					userNotationAlert(R.string.register_field_missing);
				}
			} else {

				if (((txtEmail.getText().toString()).equals(""))
						|| ((txtUserName.getText().toString()).equals(""))
						|| ((txtPassword.getText().toString()).equals(""))) {
					userNotationAlert(R.string.register_field_missing);
				} else {

					if (!emailFlag)
						checkEmailId();
					if (!usernameFlag)
						checkUserName(false);
					if (!passwordFlag)
						checkPassword();
					if (!inviteFlag)
						checkInvitedId();
					checkVerifiedPassword();
				}
			}
		} else if (v == btnAddProfilePic) {
			Intent it = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
			startActivityForResult(it, 101);
		} else if (v == btnImgChecked) {
			if (!registrationFlag) {
				btnImgChecked.setImageResource(R.drawable.btn_check);
				registrationFlag = true;
			} else {
				btnImgChecked.setImageResource(R.drawable.btn_uncheck);
				registrationFlag = false;
			}
		}
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {

		case 101:
			if (resultCode == RESULT_OK) {
				Uri selectedImage = imageReturnedIntent.getData();
				mImagePath = ImageUtils.getInstance().getRealPathFromURI(
						getSoftRefActivity(), selectedImage);
				mMimeType = ImageUtils.getInstance().getMimeTypeFromURI(
						getSoftRefActivity(), selectedImage);

				memreasImageLoader.displayImage("file://" + mImagePath,
						profileImg, optionsGallery, animateFirstListener);

				this.profile_photo = "1";
				//
				// Fetch MediaId
				//
			}
		}
	}

	public void back(View v) {
		v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.button_click));

		startActivity(new Intent(getApplicationContext(),
				MainLoginSelectionActivity.class));
		finish();
	}

	private boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	private boolean isSecret(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}


	private boolean checkEmailId() {
		boolean flag = false;
		int id = 0;
		if (!isEmailValid(txtEmail.getText())) {
			emailFlag = false;
			tvVerEmail.setTextColor(Color.RED);
			tvVerEmail.setText(R.string.register_invalid_email);
			id = R.string.register_invalid_email;
		} else {
			flag = true;
			tvVerEmail.setText(R.string.register_valid_email);
			tvVerEmail.setTextColor(Color.GREEN);
			emailFlag = true;
		}
		if (!flag) {
			userNotationAlert(id);
		}
		return flag;
	}

	private void checkUserName(boolean isFinalVerify) {
		String username = txtUserName.getText().toString();
		tvVerUserName.setText("");
		tvVerUserName.setTextColor(Color.RED);

		int id = 0;

		if (username.length() < USERNAME_MIN) {
			tvVerUserName.setText(R.string.register_username_short);
			usernameFlag = false;
			id = R.string.register_username_short;
		} else if (username.length() >= USERNAME_MAX) {
			tvVerUserName.setText(R.string.register_username_long);
			usernameFlag = false;
			id = R.string.register_username_long;
		} else if (!verifyUserName(txtUserName, tvVerUserName)) {
			usernameFlag = false;
			id = R.string.register_invalid_username;
		}

		if (id == 0) {
			if (mApplication.isOnline()) {
				if (isFinalVerify) {
					btnImgSubmit.setEnabled(false);
				}
				if (mTaskCheckUserName != null) {
					mTaskCheckUserName.cancel(true);
					mTaskCheckUserName = null;
				}
				mTaskCheckUserName = new CheckUserNameFromServer(isFinalVerify);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)

					mTaskCheckUserName.executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, username);
				else
					mTaskCheckUserName.execute(username);
			}
		} else {
			// userNotationAlert(id);
		}
	}
	private boolean checkInvitedId() {
		boolean flag = false;
		int id = 0;

		if (txtInvited.getText().toString().equals("")) {
			flag = true;
			tvVerInvite.setText(R.string.register_valid_email);
			tvVerInvite.setTextColor(Color.GREEN);
			inviteFlag = true;
		} else if (!isEmailValid(txtInvited.getText())) {
			inviteFlag = false;
			tvVerInvite.setTextColor(Color.RED);
			tvVerInvite.setText(R.string.register_invalid_email);
			id = R.string.register_invalid_email;
		} else {
			flag = true;
			tvVerInvite.setText(R.string.register_valid_email);
			tvVerInvite.setTextColor(Color.GREEN);
			inviteFlag = true;
		}
		if (!flag) {
			userNotationAlert(id);
		}
		return flag;
	}

	private boolean verifyUserName(TextView userNameText, TextView unCheker) {
		String userName = userNameText.getText().toString();

		try {
			Pattern patt = Pattern.compile("[.^*;$]*");
			Matcher matcher = patt.matcher(userName);
			if (matcher.matches()) {
				unCheker.setText(R.string.register_invalid_username);
				unCheker.setTextColor(Color.RED);
				return false;
			} else {
				return true;
			}

		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean checkPassword() {
		String mainpassword = txtPassword.getText().toString();
		boolean flag = false;
		int id = 0;

		if (mainpassword.length() < PASSWORD_MIN) {
			tvVerPassword.setTextColor(Color.RED);
			tvVerPassword.setText(R.string.register_password_short);
			id = R.string.register_password_short;
			passwordFlag = false;
		} else if (mainpassword.length() > PASSWORD_MAX) {
			tvVerPassword.setText(R.string.register_password_long);
			tvVerPassword.setTextColor(Color.RED);
			id = R.string.register_password_long;
			passwordFlag = false;
		} else {
			flag = true;
			verifyPassword(txtPassword, tvVerPassword);
		}
		if (!flag)
			userNotationAlert(id);
		return flag;
	}

	private boolean checkVerifiedPassword() {
		String password = txtVerifiedPassword.getText().toString();
		boolean flag = false;
		int id = 0;

		if (password.length() < PASSWORD_MIN) {
			tvVerVerfyPass.setTextColor(Color.RED);
			tvVerVerfyPass.setText(R.string.register_verify_password_short);
			id = R.string.register_verify_password_short;
		} else if (password.length() > PASSWORD_MAX) {
			tvVerVerfyPass.setText(R.string.register_verify_password_long);
			tvVerVerfyPass.setTextColor(Color.RED);
			id = R.string.register_verify_password_long;
		} else {
			if (password.length() != 0
					&& !(txtPassword.getText().toString()).equals(password)) {
				tvVerPassword.setText(R.string.register_pass_not_matched);
				tvVerVerfyPass.setText(R.string.register_pass_not_matched);
				tvVerVerfyPass.setTextColor(Color.RED);
				tvVerPassword.setTextColor(Color.RED);
				passwordFlag = false;
				id = R.string.register_pass_not_matched;
			} else {
				verifyPassword(txtVerifiedPassword, tvVerVerfyPass);
				verifyPassword(txtPassword, tvVerPassword);
				passwordFlag = true;
				flag = true;
			}
		}
		if (!flag)
			userNotationAlert(id);
		return flag;
	}

	private void verifyPassword(TextView passwordText, TextView pwCheker) {
		String password = passwordText.getText().toString();
		if (!password.equals(txtPassword.getText().toString())) {
			pwCheker.setTextColor(Color.RED);
			pwCheker.setText(R.string.register_pass_not_matched);
			passwordFlag = false;
		} else if (password.length() < PASSWORD_MIN) {
			pwCheker.setTextColor(Color.RED);
			pwCheker.setText(R.string.register_password_short);
			passwordFlag = false;

		} else {
			passwordFlag = true;
			if (password.matches("^[a-zA-Z0-9~!@#$%^&*_| +=]+$")) {
				pwCheker.setText("Strong");
				pwCheker.setTextColor(Color.parseColor("#3399CC"));
			}
			if (password.matches("^[a-zA-Z0-9]+$")) {
				pwCheker.setText("Medium");
				pwCheker.setTextColor(Color.GREEN);
			}
			if (password.matches("^[a-zA-Z]+$")) {
				pwCheker.setText("Weak");
				pwCheker.setTextColor(Color.RED);
			}

		}
	}

	private void finalRegistrationVerification() {

		if (mApplication.isOnline()) {
			if (checkEmailId()) {
				if (checkInvitedId()) {

					if (usernameFlag) {
						String username = txtUserName.getText().toString();
						String password = txtPassword.getText().toString();
						String email = txtEmail.getText().toString();
						String secret = "freedom tower";
						this.device_id = Secure.getString(
								this.getContentResolver(), Secure.ANDROID_ID);
						// String invited_by = txtInvited.getText().toString();
						String invited_by = "";
						String event_id = "";

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							device_id = Secure.getString(
									this.getContentResolver(),
									Secure.ANDROID_ID);
							new RegistrationParser(this).executeOnExecutor(
									AsyncTask.THREAD_POOL_EXECUTOR, email,
									username, password, device_id, invited_by,
									event_id, this.profile_photo, secret);
						} else
							new RegistrationParser(this).execute(email,
									username, password, device_id, invited_by,
									event_id, this.profile_photo, secret);
					} else {
						checkUserName(true);
					}
				}
			}
		} else {
			WebNetworkAlert();
		}
	}

	@Override
	protected void onPause() {
		// Unregister since the activity is not visible
		receiverRegisterHandler(false);

		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// start Queue Service so Amazon client is ready...
		startQueueService();

		// Setup LocalBroadcastManager
		receiverRegisterHandler(true);

		btnImgSubmit.setOnClickListener(this);
	}

	private void receiverRegisterHandler(boolean register) {

		if (register && (mMessageReceiver == null)) {
			LocalBroadcastManager.getInstance(this).registerReceiver(
					mMessageReceiver, new IntentFilter("queue-progress"));
		} else if (mMessageReceiver != null) {
			LocalBroadcastManager.getInstance(this).unregisterReceiver(
					mMessageReceiver);
		}
	}

	private void startQueueService() {
		// Start the background service here and bind to it
		Intent intent = new Intent(this, QueueService.class);
		intent.putExtra("service", "start");
		startService(intent);
	}

	/*
	 * Background Queue Service related functions below...
	 */
	private boolean addedToQueue(GalleryBean media,
			MemreasTransferModel.Type type) {
		try {
			if (!media.isAddedToQueue()) {
				MemreasTransferModel transferModel = new MemreasTransferModel(
						media);
				// transferModel.setEventId(event_id);
				transferModel.setType(type);
				QueueAdapter.getInstance().getSelectedTransferModelQueue()
						.add(transferModel);
				//
				// start Queue Service if not running
				//
				this.startQueueService();
				//
				// Add to hashmap once uploaded
				//
				QueueAdapter
						.getInstance()
						.getSelectedMediaHashMap()
						.put(transferModel.getName(),
								QueueAdapter.getInstance()
										.getSelectedTransferModelQueue()
										.indexOf(transferModel));
				// Set a callback reference here so the activity can get the
				// percent....
				this.profilePicTransferModel = transferModel;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// handler for received Intents for the "my-event" event
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@SuppressWarnings("unused")
		@Override
		public synchronized void onReceive(Context context, Intent intent) {
			// Extract data included in the Intent

			String name = intent.getStringExtra("transferModelName");
			MemreasQueueStatus status = MemreasQueueStatus.valueOf(intent
					.getStringExtra("status"));

			if (status.equals(MemreasQueueStatus.IN_PROGRESS)) {
				mProgressDialog.setMessage("Uploading profile pic - "
						+ profilePicTransferModel.getProgress() + "%");
				mProgressDialog.show();
			} else if (status.equals(MemreasQueueStatus.COMPLETED)) {
				mProgressDialog.dismiss();
				callBackCloser("success", "profile uploaded");
			}
		} // end onReceive
	}; // end broadcast receiver...

	public void callBackUploader(String status, String message) {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(RegistrationActivity.this);
		}

		//
		// Fetch mediaId if profile pic
		//
		String mediaId = mMediaIdManager.fetchNextMediaId();


		mProgressDialog.setCancelable(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setTitle("Registration");
		mProgressDialog.setMessage("Uploading profile...");
		mProgressDialog.show();

		if (mImagePath != null) {
			GalleryBean profilePic = new GalleryBean(GalleryType.NOT_SYNC);
			String mediaName = ImageUtils.getInstance().getImageNameFromPath(
					mImagePath);
			String mediaNamePrefix = mediaName.substring(0,
					mediaName.lastIndexOf('.'));

			profilePic.setMediaUrl(mImagePath);
			profilePic.setLocalMediaPath(mImagePath);
			profilePic.setMediaName(mediaName);
			profilePic.setMediaNamePrefix(mediaNamePrefix);
			profilePic.setMediaType("image");
			profilePic.setMediaId(mediaId);
			profilePic.setMimeType(mMimeType);
			profilePic.setProfilePic(true);
			profilePic.setRegistration(true);

			if (addedToQueue(profilePic, Type.UPLOAD)) {
				profilePic.setAddedToQueue(true);
				callBackCloser("success", message); //registration already occurred
			} else {
				profilePic.setAddedToQueue(false);
				callBackCloser(status, message);
			}
		} else {
			callBackCloser(status, message);
		}
	}

	public void callBackCloser(String status, String message) {
		if (status.equalsIgnoreCase("success")) {
			Intent intent = new Intent(this, LoginActivity.class);
			intent.putExtra("is_registered", true);
			startActivity(intent);
			finish();
		} else {
			btnImgSubmit.setEnabled(true);
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();
		}
	}

	// check aysnc task
	class CheckUserNameFromServer extends AsyncTask<String, Void, String> {

		private boolean isFinalVerification;
		private ProgressDialog mProgressDialog;

		public CheckUserNameFromServer(boolean isFinalVerify) {
			this.isFinalVerification = isFinalVerify;
			mProgressDialog = new ProgressDialog(RegistrationActivity.this);
			mProgressDialog.setCancelable(true);
			mProgressDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            CheckUserNameFromServer.this.cancel(true);
                        }
                    });
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setTitle("Registration");
			mProgressDialog.setMessage("Checking username...");
            mProgressDialog.show();
            mProgressDialog.show();
		}

		@Override
		protected String doInBackground(final String... param) {

			xmlData = XMLGenerator.checkUserNameXML(param[0]);
			String status = "";
			try {
				//Log.i("RegistartionActivity url", "" + Common.SERVER_URL
				//		+ Common.CHECK_USER_NAME_ACTION);
				SaxParser.parse(Common.SERVER_URL
						+ Common.CHECK_USER_NAME_ACTION, xmlData,
						new CommonHandler(), "xml");
				final CommonGetSet checkUserName = CommonHandler.commonList;
				//Log.i("RegistartionActivity Log", " Server XML : " + xmlData);
				//Log.i("RegistartionActivity Log", " Server Status : "
				//		+ checkUserName.getStatus());
				status = checkUserName.getStatus();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return status;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (isFinalVerification)
				mProgressDialog.show();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			//Log.i("RegistartionActivity result", "" + result);
			if (result.equalsIgnoreCase("success")) {
				tvVerUserName.setText(R.string.register_username_taken);
				tvVerUserName.setTextColor(Color.RED);
				usernameFlag = false;
				if (isFinalVerification)
					userNotationAlert(R.string.register_invalid_username);
				else
					Toast.makeText(RegistrationActivity.this,
							getString(R.string.register_invalid_username),
							Toast.LENGTH_LONG).show();
				btnImgSubmit.setEnabled(true);
				loadingLayout.setVisibility(View.GONE);
			} else {
				txtPassword.requestFocus();
				tvVerUserName.setText(R.string.register_username_not_taken);
				tvVerUserName.setTextColor(Color.GREEN);
				usernameFlag = true;
				if (isFinalVerification && checkPassword()
						&& checkVerifiedPassword()) {
					String username = txtUserName.getText().toString();
					String password = txtPassword.getText().toString();
					String email = txtEmail.getText().toString();
					String device_id = Secure.getString(getContentResolver(),
                            Secure.ANDROID_ID);
					String invited_by = "";
					String event_id = "";
                    String secret = "freedom tower";
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						device_id = Secure.getString(getContentResolver(),
								Secure.ANDROID_ID);
						new RegistrationParser(RegistrationActivity.this)
								.executeOnExecutor(
										AsyncTask.THREAD_POOL_EXECUTOR, email,
										username, password, device_id,
										invited_by, event_id, profile_photo, secret);
						// new RegisterProcessLoader()
						// .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					} else
						new RegistrationParser(RegistrationActivity.this)
								.execute(email, username, password, device_id,
										invited_by, event_id, profile_photo, secret);
					// new RegisterProcessLoader().execute();
				}
			}
			txtPassword.requestFocus();
		}
	}
} // end class RegistrationActivity...