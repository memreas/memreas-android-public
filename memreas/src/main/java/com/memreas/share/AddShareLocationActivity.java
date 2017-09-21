package com.memreas.share;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.memreas.R;
import com.memreas.base.BaseActivity;
import com.memreas.base.Common;
import com.memreas.location.PlacesAutoCompleteAdapter;
import com.memreas.location.SearchResult;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class AddShareLocationActivity extends BaseActivity implements
		AnimationListener, AdapterView.OnItemClickListener {

	public static int requestCode = 1001;
	private AutoCompleteTextView mEdtAddress;

	private Animation mAnim = null;

	private final int AS_NONE = 0;
	private final int AS_OPTION = 1;
	private final int AS_OK = 2;
	private final int AS_CANCEL = 3;
	private final int AS_SEARCH = 4;

	private int state_Anim = AS_NONE;
	private LatLng mLocation;
	private String mAddress;
	private ProgressDialog mLoadingDialog;

	private GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.share_location_map_popup);

		double longitude = getIntent().getDoubleExtra("longitude", 0);
		double latitude = getIntent().getDoubleExtra("latitude", 0);
		if (latitude != 0 || longitude != 0) {
			mLocation = new LatLng(latitude, longitude);
		}
		mAddress = getIntent().getStringExtra("address");

		super.onCreate(savedInstanceState);

		// For hiding soft keyboard
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		mEdtAddress = (AutoCompleteTextView) findViewById(R.id.edt_adress);
		mEdtAddress.setText(mAddress);
		mEdtAddress.setThreshold(2);
		mEdtAddress.setAdapter(new PlacesAutoCompleteAdapter(this,
				R.layout.search_autocomplete_item));
		mEdtAddress.setOnItemClickListener(this);

		mAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.button_click);
		mAnim.setAnimationListener(this);

		mLoadingDialog = new ProgressDialog(this);
		mLoadingDialog.setCancelable(false);

		setUpMapIfNeeded();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	@Override
	public void onAnimationEnd(Animation animation) {

		if (state_Anim == AS_OPTION) {
			startActivityForResult(
					new Intent(this, OptionMemreasActivity.class), 0);
		}

		if (state_Anim == AS_OK) {
			Intent intent = new Intent();
			if (mLocation != null) {
				intent.putExtra("latitude", mLocation.latitude);
				intent.putExtra("longitude", mLocation.longitude);
				intent.putExtra("address", mEdtAddress.getText().toString());
				setResult(RESULT_OK, intent);
				finish();
			}
		}

		if (state_Anim == AS_CANCEL)
			finish();

		if (state_Anim == AS_SEARCH)

			state_Anim = AS_NONE;
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

	public void optionBtn(View v) {

		state_Anim = AS_OPTION;
		v.startAnimation(mAnim);
	}

	public void okButton(View v) {

		state_Anim = AS_OK;
		v.startAnimation(mAnim);
	}

	public void cancelButton(View v) {

		state_Anim = AS_CANCEL;
		v.startAnimation(mAnim);
	}

	public void search(View v) {

		state_Anim = AS_SEARCH;
		v.startAnimation(mAnim);
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);
			if (fragment == null) {
				GoogleMapOptions options = new GoogleMapOptions();
				options = options.zoomControlsEnabled(true);
				fragment = SupportMapFragment.newInstance(options);
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.map, fragment).commit();
			}
			mMap = fragment.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	/**
	 * This is where we can add markers or lines, add listeners or move the
	 * camera. In this case, we just add a marker near Africa.
	 * <p>
	 * This should only be called once and when we are sure that {@link #mMap}
	 * is not null.
	 */
	private void setUpMap() {
		mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {
				getAddressFromLocation(point.latitude, point.longitude);

			}
		});
		mMap.getUiSettings().setMyLocationButtonEnabled(true);
		mMap.getUiSettings().setAllGesturesEnabled(true);
		mMap.getUiSettings().setZoomControlsEnabled(true);
		showMarker();
	}

	public void showMarker() {
		mMap.clear();
		if (mLocation != null) {
			mMap.addMarker(new MarkerOptions().position(mLocation));
			mMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(new CameraPosition(mLocation, 16, 0, 0)));
		}
	}

	private String getAddressFromLocation(final double latitude,
			final double longitude) {
		try {
			new AsyncTask<Double, Void, String>() {
				protected String doInBackground(Double... params) {
					InputStream stream = null;
					String respone = null;
					try {
						URLConnection connection = new URL(
								"http://maps.googleapis.com/maps/api/geocode/json?address="
										+ params[0] + "," + params[1]
										+ "&sensor=true").openConnection();
						connection.setConnectTimeout(10000);
						connection.connect();

						stream = connection.getInputStream();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(stream));
						String read = br.readLine();
						StringBuilder sb = new StringBuilder();

						while (read != null) {
							sb.append(read);
							read = br.readLine();
						}

						respone = sb.toString();

						JSONObject jsonObject = new org.json.JSONObject(respone);
						if (jsonObject != null) {
							JSONArray results = jsonObject
									.getJSONArray("results");
							return results.getJSONObject(1).getString(
									"formatted_address");
						}
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						if (stream != null) {
							try {
								stream.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					return null;
				};

				protected void onPostExecute(String result) {
					mLoadingDialog.dismiss();
					if (result != null) {
						mEdtAddress.setText(result);
						mAddress = result;
						mLocation = new LatLng(latitude, longitude);
						showMarker();
					} else {
						Toast.makeText(AddShareLocationActivity.this,
								"address not found", Toast.LENGTH_LONG).show();
					}
				};

				protected void onPreExecute() {
					mLoadingDialog.setMessage("Getting address info...");
					mLoadingDialog.show();
				};
			}.execute(latitude, longitude);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		SearchResult result = ((PlacesAutoCompleteAdapter) (mEdtAddress
				.getAdapter())).getItem(position);
		mAddress = result.getAddress();
		new AsyncTask<String, Void, LatLng>() {
			protected LatLng doInBackground(String... params) {
				InputStream stream = null;
				String respone = null;
				try {
					URLConnection connection = new URL(
							Common.PLACES_API_DETAILS + "?reference="
									+ params[0] + "&key="
									+ Common.PLACES_API_KEY).openConnection();
					connection.setConnectTimeout(10000);
					connection.connect();

					stream = connection.getInputStream();
					BufferedReader br = new BufferedReader(
							new InputStreamReader(stream));
					String read = br.readLine();
					StringBuilder sb = new StringBuilder();

					while (read != null) {
						sb.append(read);
						read = br.readLine();
					}

					respone = sb.toString();
					JSONObject jsonObject = new JSONObject(respone);
					if (jsonObject != null) {
						JSONObject result = jsonObject.getJSONObject("result");
						JSONObject geometry = result.getJSONObject("geometry");
						JSONObject location = geometry
								.getJSONObject("location");
						return new LatLng(location.getDouble("lat"),
								location.getDouble("lng"));
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (stream != null) {
						try {
							stream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				return null;
			};

			protected void onPostExecute(LatLng result) {
				mLoadingDialog.dismiss();
				if (result != null) {
					mLocation = result;
					showMarker();
				} else {
					Toast.makeText(AddShareLocationActivity.this,
							"Can't get your address", Toast.LENGTH_LONG).show();
				}
			};

			protected void onPreExecute() {
				mLoadingDialog.setMessage("Getting address info...");
				mLoadingDialog.show();
			};
		}.execute(result.getReference());
	}
}