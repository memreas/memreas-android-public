package com.memreas.location;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.memreas.base.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class PlacesAutoCompleteAdapter extends ArrayAdapter<SearchResult>
		implements Filterable {

	private ArrayList<SearchResult> resultList;

	public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	@Override
	public int getCount() {
		return resultList.size();
	}

	@Override
	public SearchResult getItem(int index) {
		return resultList.get(index);
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if (constraint != null) {
					// Retrieve the autocomplete results.
					resultList = autocomplete(constraint.toString());

					// Assign the data to the FilterResults
					filterResults.values = resultList;
					filterResults.count = resultList.size();
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				if (results != null && results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
		};
		return filter;
	}

	private ArrayList<SearchResult> autocomplete(String input) {
		ArrayList<SearchResult> resultList = null;

		InputStream stream = null;
		String respone = null;
		URLConnection connection = null;
		try {
			StringBuilder sb = new StringBuilder(Common.PLACES_API_BASE
					+ Common.TYPE_AUTOCOMPLETE + Common.OUT_JSON);
			sb.append("?sensor=false&key=" + Common.PLACES_API_KEY);
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));

			connection = new URL(sb.toString()).openConnection();
			connection.setConnectTimeout(10000);
			connection.connect();

			stream = connection.getInputStream();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(stream));
			String read = br.readLine();
			sb = new StringBuilder();

			while (read != null) {
				sb.append(read);
				read = br.readLine();
			}

			respone = sb.toString();
			// respone = NetworkUtils.doRequest(sb.toString());

			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(respone);
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList<SearchResult>(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				resultList.add(new SearchResult(predsJsonArray.getJSONObject(i)
						.getString("reference"), predsJsonArray
						.getJSONObject(i).getString("description")));
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(stream!=null){
				try {
					stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return resultList;
	}
}
