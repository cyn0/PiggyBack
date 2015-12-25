package com.example.testapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.autocomplete.MyAutoComplete;
import com.example.autocomplete.PlaceArrayAdapter;
import com.example.autocomplete.MyAutoComplete.AutoCompleteListener;
import com.example.autocomplete.PlaceArrayAdapter.PlaceAutocomplete;
import com.example.datamodel.OfferRide;
import com.example.datamodel.User;
import com.example.feeds.CustomListAdapter;
import com.example.http.Httphandler;
import com.example.http.Httphandler.HttpDataListener;
import com.example.utils.Constants;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.ui.PlacePicker;

public class FeedsFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    PlaceArrayAdapter mPlaceArrayAdapter;
    
    Button chooseSource, chooseDestination, done;
    AutoCompleteTextView sourceTextView, destinationTextView;
    
    Place source, destination;
    
    OfferRide mOfferRide;
    
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
    }
    
    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);
        

        ListView listView = (ListView) view.findViewById(R.id.list);
        final ArrayList<OfferRide> ridesList = new ArrayList<>();
        
        final CustomListAdapter adapter = new CustomListAdapter(getActivity(), ridesList);
        listView.setAdapter(adapter);
 
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
 
        Httphandler.getSharedInstance().getUser(User.getSharedInstance().getUserId(), new HttpDataListener() {
			
			@Override
			public void onError(Exception e) {
				pDialog.dismiss();
				
			}
			
			@Override
			public void onDataAvailable(String response) {		
				User user = User.fromString(response);
				ridesList.addAll(user.getAcceptedRides());
				ridesList.addAll(user.getOfferedRides());
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
    @Override
	public void onActivityResult(final int requestCode,
                                    int resultCode, Intent data) {
 
    	if(resultCode == Activity.RESULT_OK){
    		final Place place = PlacePicker.getPlace(data, getActivity());
            String attributions = PlacePicker.getAttributions(data);
            String address = place.getAddress().toString();
            String placeId = place.getId();
            
            //TO-DO : make sures that it is not possible to get address here
            if(TextUtils.isEmpty(address) || address == null){
            	MyAutoComplete autoComplete = new MyAutoComplete(getActivity(), null, new AutoCompleteListener() {
					
					@Override
					public void onPlaceAvailable(PlaceBuffer places) {
						Place place = places.get(0);
						
						if(requestCode == 1){
							String address1 = place.getAddress().toString();
		                    if(TextUtils.isEmpty(address1) || address1 == null){
		                    		address1 = place.getLatLng().toString();
		                    }
							mOfferRide.setSourceAddress(place.getAddress().toString());
			            	sourceTextView.setText(address1);
			            	
						} else if(requestCode == 2){
							String address1 = place.getAddress().toString();
		                    if(TextUtils.isEmpty(address1) || address1 == null){
		                    		address1 = place.getLatLng().toString();
		                    }
		                    mOfferRide.setDestinationAddress(place.getAddress().toString());
		                    destinationTextView.setText(address1);
						}
					}
					
					@Override
					public void onItemSelected(PlaceAutocomplete selectedplace) {}
				});
            	
            	autoComplete.getPlaceById(placeId);
            } else {
            	if(requestCode == 1){
					mOfferRide.setSourceAddress(address);
	            	sourceTextView.setText(address);
	            	
				} else if(requestCode == 2){
                    mOfferRide.setDestinationAddress(address);
                    destinationTextView.setText(address);
				}
            }
            
            if(requestCode == 1){
            	mOfferRide.setSource(place.getLatLng());
            	mOfferRide.setSourceId(place.getId());
            } else if(requestCode == 2){
            	mOfferRide.setDestination(place.getLatLng());
            	mOfferRide.setDestinationId(place.getId());
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
            
    	} else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    	
    	
    }
    
}