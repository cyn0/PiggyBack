package com.example.testapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.autocomplete.MyAutoComplete;
import com.example.autocomplete.PlaceArrayAdapter;
import com.example.autocomplete.MyAutoComplete.AutoCompleteListener;
import com.example.autocomplete.PlaceArrayAdapter.PlaceAutocomplete;
import com.example.datamodel.OfferRide;
import com.example.datamodel.User;
import com.example.feeds.CustomExpandableListAdapter;
import com.example.feeds.CustomListAdapter;
import com.example.http.Httphandler;
import com.example.http.Httphandler.HttpDataListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.ui.PlacePicker;

public class FeedsFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    PlaceArrayAdapter mPlaceArrayAdapter;
    private String TAG = "FeedsFragment";
    Button chooseSource, chooseDestination, done;
    AutoCompleteTextView sourceTextView, destinationTextView;
    
    Place source, destination;
    OfferRide mOfferRide;
    Activity mActivity;
    // newInstance constructor for creating fragment with arguments
    public static FeedsFragment newInstance(int page, String title) {
    	FeedsFragment fragmentFirst = new FeedsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        
        mOfferRide = new OfferRide();
        mActivity = getActivity();
    }
    
    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);

        ExpandableListView expListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        final ArrayList<OfferRide> listItems = new ArrayList<OfferRide>();
 
        final CustomExpandableListAdapter adapter = new CustomExpandableListAdapter(getActivity(), listItems);
 
        // setting list adapter
        expListView.setAdapter(adapter);
        
        
//        listView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				handleListClicked((OfferRide)adapter.getItem(position));
//			}
//		});
        
        
        
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();
 
        Httphandler.getSharedInstance().getUser(User.getSharedInstance().getUserId(), new HttpDataListener() {
			
			@Override
			public void onError(Exception e) {
				pDialog.dismiss();
				e.printStackTrace();
				Toast.makeText(mActivity, getString(R.string.feeds_error), Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onDataAvailable(String response) {		
				User user = User.fromString(response);
				
				Log.d(TAG, "Accepted rides - "+ user.getAcceptedRides().size() + " Offered rides " + user.getOfferedRides().size());
				if(user.getAcceptedRides().size() > 0){
					adapter.addSeparatorItem(0);
					OfferRide tempRide = new OfferRide();
					tempRide.setSourceAddress("Accepted rides");
					listItems.add(tempRide);
					listItems.addAll(user.getAcceptedRides());
				}
				
				if(user.getOfferedRides().size() > 0){
					if(user.getAcceptedRides().size() == 0){
						adapter.addSeparatorItem(0);
					}else{
						adapter.addSeparatorItem(user.getAcceptedRides().size() + 1);
					}
					OfferRide tempRide1 = new OfferRide();
					tempRide1.setSourceAddress("Offered rides");
					listItems.add(tempRide1);
					listItems.addAll(user.getOfferedRides());
					
				}
				
				adapter.notifyDataSetChanged();
				pDialog.dismiss();
			}
		});
        // changing action bar color
//        getActivity().getActionBar().setBackgroundDrawable(
//                new ColorDrawable(Color.parseColor("#1b1b1b")));
 
        // Creating volley request obj
//        JsonArrayRequest movieReq = new JsonArrayRequest(url,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        Log.d(TAG, response.toString());
//                        hidePDialog();
// 
//                        // Parsing json
//                        for (int i = 0; i < response.length(); i++) {
//                            try {
// 
//                                JSONObject obj = response.getJSONObject(i);
//                                Movie movie = new Movie();
//                                movie.setTitle(obj.getString("title"));
//                                movie.setThumbnailUrl(obj.getString("image"));
//                                movie.setRating(((Number) obj.get("rating"))
//                                        .doubleValue());
//                                movie.setYear(obj.getInt("releaseYear"));
// 
//                                // Genre is json array
//                                JSONArray genreArry = obj.getJSONArray("genre");
//                                ArrayList<String> genre = new ArrayList<String>();
//                                for (int j = 0; j < genreArry.length(); j++) {
//                                    genre.add((String) genreArry.get(j));
//                                }
//                                movie.setGenre(genre);
// 
//                                // adding movie to movies array
//                                movieList.add(movie);
// 
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
// 
//                        }
// 
//                        // notifying list adapter about data changes
//                        // so that it renders the list view with updated data
//                        adapter.notifyDataSetChanged();
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        VolleyLog.d(TAG, "Error: " + error.getMessage());
//                        hidePDialog();
// 
//                    }
//                });
// 
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(movieReq);
        return view;
    }

    private void handleListClicked(OfferRide ride){
		FragmentManager fragmentManager = ((FragmentActivity)mActivity).getSupportFragmentManager();
		fragmentManager
			.beginTransaction()
			.replace(R.id.container,
				RideDetailsFragment.newInstance(2, "Offer a ride", ride.getRideId()))
			.addToBackStack(null)
			.commit();
		
    }
    
}