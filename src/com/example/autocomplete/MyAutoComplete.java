package com.example.autocomplete;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.autocomplete.PlaceArrayAdapter.PlaceAutocomplete;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MyAutoComplete implements
	GoogleApiClient.OnConnectionFailedListener,
	GoogleApiClient.ConnectionCallbacks{
	
	private static final String LOG_TAG = "MainActivity";
//    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(8.4, 68.7), new LatLng(37.430610, 97.25));
 
    private FragmentActivity mFragmentActivity;
    
    public interface AutoCompleteListener{
    	public void onItemSelected(PlaceAutocomplete selectedplace);
    	public void onPlaceAvailable(PlaceBuffer places);
    } 
    
    AutoCompleteListener mAutoCompleteListener;
    public MyAutoComplete(FragmentActivity fragmentActivity, AutoCompleteTextView autoCompleteTextView, AutoCompleteListener autoCompleteListener){
    	mFragmentActivity = fragmentActivity;
    	mGoogleApiClient = new GoogleApiClient.Builder(mFragmentActivity)
    		.addApi(Places.GEO_DATA_API)
        	//.enableAutoManage(fragmentActivity, GOOGLE_API_CLIENT_ID, this)
        	.addConnectionCallbacks(this)
        	.build();
		mAutocompleteTextView = autoCompleteTextView;
		mPlaceArrayAdapter = new PlaceArrayAdapter(mFragmentActivity, android.R.layout.simple_list_item_1,
				BOUNDS_INDIA, null);

		if(mAutocompleteTextView != null){
			mAutocompleteTextView.setThreshold(3);
			mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
			mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);
		}
		
		mAutoCompleteListener = autoCompleteListener;
		mGoogleApiClient.connect();
    }
    
    public void getPlaceById(String placeId){
	    PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
	    		.getPlaceById(mGoogleApiClient, placeId);
	    placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
	    Log.i(LOG_TAG, "Fetching details for ID: " + placeId);

    }
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
		    final String placeId = String.valueOf(item.placeId);
		    Log.i(LOG_TAG, "Selected: " + item.description);
		    mAutoCompleteListener.onItemSelected(item);
		    
		    getPlaceById(placeId);

		}
    };
    
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
    	@Override
    	public void onResult(PlaceBuffer places) {
    		if (!places.getStatus().isSuccess()) {
    			Log.e(LOG_TAG, "Place query did not complete. Error: " +
                places.getStatus().toString());
    			return;
    		}
    // Selecting the first object buffer.
    mAutoCompleteListener.onPlaceAvailable(places);
    
    final Place place = places.get(0);
    Log.d("place", "address-" + place.getAddress());
    Log.d("place", "" + place.getLatLng());
//    CharSequence attributions = places.getAttributions();
//
//    mNameTextView.setText(Html.fromHtml(place.getName() + ""));
//    mAddressTextView.setText(Html.fromHtml(place.getAddress() + ""));
//    mIdTextView.setText(Html.fromHtml(place.getId() + ""));
//    mPhoneTextView.setText(Html.fromHtml(place.getPhoneNumber() + ""));
//    mWebTextView.setText(place.getWebsiteUri() + "");
//    if (attributions != null) {
//        mAttTextView.setText(Html.fromHtml(attributions.toString()));
//    }
    	}
    };
    
    
    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");
 
    }
 
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());
 
        Toast.makeText(mFragmentActivity,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }
 
    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }
}
