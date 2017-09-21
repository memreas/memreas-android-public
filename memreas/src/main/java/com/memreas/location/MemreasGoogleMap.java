package com.memreas.location;

import android.support.v4.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MemreasGoogleMap {

	protected GoogleMap mMap;
	protected FragmentManager fragmentManager;
	protected SupportMapFragment mapFragment;
	protected int resource;
	protected LatLng mLocation;
	 
	public MemreasGoogleMap(int resource, FragmentManager fragmentManager) {
		this.resource = resource;
		this.fragmentManager = fragmentManager;
		setUpMapIfNeeded();
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			if (mapFragment == null) {
				GoogleMapOptions options = new GoogleMapOptions();
				options = options.zoomControlsEnabled(true);
				mapFragment = SupportMapFragment.newInstance(options);
				fragmentManager.beginTransaction()
						.replace(resource, mapFragment).commit();
			}
			mMap = mapFragment.getMap();
			// Check if we were successful in obtaining the map.
			// if (mMap != null) {
			// setUpMap();
			// }
			if ((mMap != null) && (mLocation != null)) {
				showMarker();
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
				mLocation = new LatLng(point.latitude, point.longitude);
				showMarker();

			}
		});
		mMap.getUiSettings().setMyLocationButtonEnabled(true);
		mMap.getUiSettings().setAllGesturesEnabled(true);
		mMap.getUiSettings().setZoomControlsEnabled(true);
		//showMarker();
	}

	public void showMarker() {
		setUpMapIfNeeded();
		mMap.clear();
		if (mLocation != null) {
			mMap.addMarker(new MarkerOptions().position(mLocation));
			mMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(new CameraPosition(mLocation, 16, 0, 0)));
		}
	}

	public LatLng getmLocation() {
		return mLocation;
	}

	public void setmLocation(LatLng mLocation) {
		this.mLocation = mLocation;
	}

	// private String getAddressFromLocation(final double latitude,
	// final double longitude) {
	// try {
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// private class fetchAddressAsyncTask extends AsyncTask<Double, Void,
	// String> {
	//
	// private double latitude;
	// private double longitude;
	//
	// public fetchAddressAsyncTask() {
	// };
	//
	// protected String doInBackground(Double... params) {
	// InputStream stream = null;
	// String respone = null;
	// try {
	// URLConnection connection = new URL(
	// "http://maps.googleapis.com/maps/api/geocode/json?address="
	// + params[0] + "," + params[1] + "&sensor=true")
	// .openConnection();
	// connection.setConnectTimeout(10000);
	// connection.connect();
	//
	// stream = connection.getInputStream();
	// BufferedReader br = new BufferedReader(new InputStreamReader(
	// stream));
	// String read = br.readLine();
	// StringBuilder sb = new StringBuilder();
	//
	// while (read != null) {
	// sb.append(read);
	// read = br.readLine();
	// }
	//
	// respone = sb.toString();
	//
	// JSONObject jsonObject = new JSONObject(respone);
	// if (jsonObject != null) {
	// JSONArray results = jsonObject.getJSONArray("results");
	// return results.getJSONObject(1).getString(
	// "formatted_address");
	// }
	// } catch (ClientProtocolException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } finally {
	// if (stream != null) {
	// try {
	// stream.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	// return null;
	// };
	//
	// protected void onPostExecute(String result) {
	// mLoadingDialog.dismiss();
	// if (result != null) {
	// mEdtAddress.setText(result);
	// mAddress = result;
	// mLocation = new LatLng(latitude, longitude);
	// showMarker();
	// } else {
	// Toast.makeText(AddShareLocationActivity.this,
	// "address not found", Toast.LENGTH_LONG).show();
	// }
	// };
	//
	// protected void onPreExecute() {
	// mLoadingDialog.setMessage("Getting address info...");
	// mLoadingDialog.show();
	// };
	// };
} // end class
