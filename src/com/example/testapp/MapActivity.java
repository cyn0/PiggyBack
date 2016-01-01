package com.example.testapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datamodel.OfferRide;
import com.example.datamodel.Ride;
import com.example.datamodel.User;
import com.example.datamodel.Ride.RIDE_TYPE;
import com.example.http.Httphandler;
import com.example.http.Httphandler.HttpDataListener;
import com.example.map.BaseMapActivity;
import com.example.utils.CommonUtil;
import com.example.utils.Constants;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

public class MapActivity extends BaseMapActivity{
  
	private static final String TAG = "MAP acrtivity";
	public static Context mContext;
	protected Location mCurrentLocation;
	protected Polyline CurrentRoute = null;
	protected LatLng selectedMarker = null;
	
	private Ride mRide;
	private User muser;
	LatLngBounds.Builder latLngBuilder;
	  
	protected SlidingUpPanelLayout slidingUpPanelLayout;
	
	TextView SlidingPanelTitle;
	protected Button slideButton;
	
	private RIDE_TYPE RideType;
	
	enum SlideButtonState {
		DONE,
		REQUEST,
		REQUESTED,
		ACCEPTED,
		SHARE
	}
	private SlideButtonState currentSlideButtonState;
	private boolean isOpenFromLink = false;
	private boolean isInitialRouteDrawn = false;
	private boolean isMapLoaded = false;
	protected void addWayPoint(LatLng point){
		Marker marker = addMarker(point);
		marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.waypoint_marker));		
		mRide.addWayPoint(marker.getPosition());
		refreshRoute();
	}
	
	protected void removeWayPoint(Marker marker){
		if(mRide.getWayPoints().contains(marker.getPosition())){
			marker.remove();
			mRide.removeWayPoint(marker.getPosition());
			refreshRoute();
		}
	}
	
	protected boolean handleMarkerClicked(Marker marker){
		removeWayPoint(marker);
		return true;
	}
	  
	protected void handleMapLongPressed(LatLng point){
		addWayPoint(point);
	}
	  
	protected void handleMapClicked(LatLng point){
		slidingUpPanelLayout.setPanelState(PanelState.HIDDEN);
	}
	
	protected void handleOnMapLoaded(){
		isMapLoaded = true;
		if(!isInitialRouteDrawn){
			refreshRoute();
		} 
		
	}
	  
	protected void refreshMapBoundary(){
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				LatLng source = mRide.getSource();
				LatLng destination = mRide.getDestination();
				
				latLngBuilder = new LatLngBounds.Builder();
				latLngBuilder.include(source);
				latLngBuilder.include(destination);
			    
				for(LatLng waypoint : mRide.getWayPoints()){
					latLngBuilder.include(waypoint);
				}
				final LatLngBounds bounds = latLngBuilder.build();
				
				final int padding = 150; 
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					
					@Override
					public void run() {
						CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
						map.moveCamera(cu);
					}
				});
				
			}
		});
		
	}
	  
	
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_map);
			mContext = getApplicationContext();
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
			slidingUpPanelLayout.setPanelState(PanelState.ANCHORED);
			slidingUpPanelLayout.setAnchorPoint(0.5f);
			mRide = (OfferRide) getIntent().getParcelableExtra(Constants.OFFER_RIDE_OBJECT);
			
//			mRide = new OfferRide();
//			mRide.setDestination(new LatLng(13.078384999999999,  80.264411));
//			mRide.setSource(new LatLng( 12.9969278, 80.2563313));
//			mRide.setSourceAddress("Gandhi Irwin Rd, Ansari Estate, Egmore, Chennai, Tamil Nadu 600008, India");
//			mRide.setDestinationAddress("Mahatma Gandhi Rd, Shastri Nagar, Adyar, Chennai, Tamil Nadu 600020, India");
//			
//			mRide.setRecurring(true);
//			mRide.setRoundTrip(false);
//			
//			//Specific to OfferRide
//			OfferRide mOfferRide = (OfferRide)mRide;
//			mOfferRide.setStartDate(new Date(1,2,2016+1900));
//			mOfferRide.setStartTime(new Date(0, 0, 0, 8, 30));
//			mOfferRide.setReturnTime(new Date(0, 0, 0, 19, 30));
			

			//if(mRide == null){
		    final String action = getIntent().getAction();
		    if (Intent.ACTION_VIEW.equals(action)) {
				fetchRideDetails();
				isOpenFromLink = true;
			} else {
				setViews();
				doInitialMapSetUps();
			}
		    
			setMapListeners();
			
	  }
	
//	@Override
//	public void onBackPressed() {
//		if(slidingUpPanelLayout.getPanelState().compareTo(PanelState.EXPANDED) == 0){
//			slidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
//		}else{
//			super.onBackPressed();
//		}
//	}
	
	public void doInitialMapSetUps(){
		LatLng source = mRide.getSource();
		LatLng destination = mRide.getDestination();
		
		addMarker(source);
		addMarker(destination);
		
		ArrayList<LatLng> wPoints = mRide.getWayPoints();
		for(LatLng wp : wPoints) {
			Marker marker = addMarker(wp);
			marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.waypoint_marker));
		}
		
		switch(RideType){
			case OFFER:
				Toast.makeText(mContext, "Long press on map, to add a way point", Toast.LENGTH_LONG).show();
				break;
		}
	}
	
	public void setViews(){
		
		SlidingPanelTitle = (TextView)findViewById(R.id.SlidingPanelTitle);
		slideButton = (Button)findViewById(R.id.share);
		FragmentManager fragmentManager = getSupportFragmentManager();
	
		if(CommonUtil.getSharedInstance().amIOfferer((OfferRide)mRide)){
	    	RideType = RIDE_TYPE.OFFER;
	    } else {
	    	RideType = RIDE_TYPE.FIND;
	    }
		
		fragmentManager
			.beginTransaction()
			.replace(R.id.container,
				RideDetailsFragment.newInstance(2, "Ride details", mRide.getRideId(), (OfferRide)mRide, muser ))
			.commit();
	
		switch(RideType){
			case OFFER:
				SlidingPanelTitle.setText("MY RIDE");
				if(isOpenFromLink){
					setSlideButton(SlideButtonState.SHARE);
				} else {
					setSlideButton(SlideButtonState.DONE);
				}
				break;
			
			case FIND:
				SlidingPanelTitle.setText("OFFERED RIDE");
				setSlideButton(SlideButtonState.REQUEST);
				break;
		}

		slideButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handleSlideButtonClicked();
			}
		});
	}
	
	public void handleSlideButtonClicked(){
		
		switch(RideType){
		case OFFER:
			switch(currentSlideButtonState){
				case SHARE:
					CommonUtil.getSharedInstance().shareMessage(mRide.getRideId(), MapActivity.this);
					break;
				case DONE:
					postRideDetails();
					break;
				default:
					int i = 1/0;
			}
			break;
		
		case FIND:
			requestRide();
			break;
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if(resultCode == 0 && requestCode == 1){
	    	Intent intent = new Intent(mContext, MainActivity.class);
	    	startActivity(intent);
	    }
	}
	
	public void requestRide(){
		final ProgressDialog progress = ProgressDialog.show(this, getString(R.string.server_request_dialog_title),
			    getString(R.string.server_request_dialog_message), true);
		Httphandler.getSharedInstance().requestRide((OfferRide)mRide, new HttpDataListener() {
			
			@Override
			public void onError(Exception e) {
				Log.e(TAG , e.getLocalizedMessage());
				progress.dismiss();
				Toast.makeText(getApplicationContext(), getString(R.string.internet_error), Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onDataAvailable(String response) {
				try{
					progress.dismiss();
					JSONObject jsonResponse = new JSONObject(response);
					boolean success = jsonResponse.getBoolean("success");
					if(success){
						setSlideButton(SlideButtonState.REQUESTED);
						Toast.makeText(getApplicationContext(), getString(R.string.request_success), Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getApplicationContext(), getString(R.string.request_error), Toast.LENGTH_LONG).show();
					}
			}catch (JSONException e){
				e.printStackTrace();
			}
				
			}
		});
	}
	public void postRideDetails(){
		final ProgressDialog progress = ProgressDialog.show(this, getString(R.string.server_request_dialog_title),
			    getString(R.string.server_request_dialog_title), true);
		HttpDataListener mDataListener = new HttpDataListener() {				
			@Override
			public void onError(Exception e) {			
				Log.e(TAG , e.getLocalizedMessage());
				progress.dismiss();
				Toast.makeText(getApplicationContext(), getString(R.string.internet_error), Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onDataAvailable(String response) {
				try{
						progress.dismiss();
						JSONObject jsonResponse = new JSONObject(response);
						boolean success = jsonResponse.getBoolean("success");
						if(success){
							String ride_id = jsonResponse.getString("ride_id");
							mRide.setRideId(ride_id);
							setSlideButton(SlideButtonState.SHARE);
						} else {
							Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
						}
				}catch (JSONException e){
					e.printStackTrace();
				}
			}
		};
		if(TextUtils.isEmpty(mRide.getRideId())){
			Httphandler.getSharedInstance().postNewRide((OfferRide)mRide, mDataListener);
		} else {
			Httphandler.getSharedInstance().updateRide((OfferRide)mRide, mDataListener);
		}
		
	}
	
	//To-Do handle ride not present case
	public void fetchRideDetails(){
		final Intent intent = getIntent();
		final ProgressDialog progress = ProgressDialog.show(this, getString(R.string.server_request_dialog_title),
			    getString(R.string.server_request_dialog_message), true);
	    //You will probably want to use intent.getDataString() rather than getData() if you care about the full URL including the querystring.
	    final List<String> segments = intent.getData().getPathSegments();
	    if (segments.size() > 0) {
	       	String ride_id = segments.get(0);
	        HttpDataListener mDataListener = new HttpDataListener() {				
				@Override
				public void onError(Exception e) {
					slidingUpPanelLayout.setPanelState(PanelState.HIDDEN);
					progress.dismiss();
					new AlertDialog.Builder(MapActivity.this)
					    .setTitle("Error!")
					    .setCancelable(false)
					    .setMessage(getString(R.string.internet_error))
					    .setIcon(android.R.drawable.ic_dialog_alert)
					    .show();
					Toast.makeText(getApplicationContext(), getString(R.string.internet_error), Toast.LENGTH_LONG).show();
					
				}
				
				@Override
				public void onDataAvailable(String response) {
					progress.dismiss();
					
					//TO-DO take care of this
					if(response.equals("{}")){
						Toast.makeText(mContext, getString(R.string.ride_not_found), Toast.LENGTH_LONG).show();
						slidingUpPanelLayout.setPanelState(PanelState.HIDDEN);
					} else {
						mRide = OfferRide.fromString(response);
						mRide.setUserUserId(User.getSharedInstance().getUserId());
						fetchUserData();
						setViews();
						doInitialMapSetUps();
						
						if(isMapLoaded)
							refreshRoute();
					}
				}
	        };
	        Httphandler.setSharedInstance(new Httphandler());
			Httphandler.getSharedInstance().getRide(ride_id, mDataListener);
	    }
	}
	
	private void refreshRoute(){
		Log.d(TAG, "Drawing route");
		isInitialRouteDrawn = true;
		drawRoute(mRide.getSource(), mRide.getDestination(), mRide.getWayPoints());
		refreshMapBoundary();
	}
	
	private void fetchUserData(){
		
        HttpDataListener dataListener = new HttpDataListener(){
				
				@Override
				public void onError(Exception e) {
					e.printStackTrace();
					
				}
				
				@Override
				public void onDataAvailable(String response) {		
					muser = User.fromString(response);
					
					for(OfferRide tempRide : muser.getAcceptedRides()){
						if(mRide.getRideId().equals(tempRide.getRideId())){
							setSlideButton(SlideButtonState.ACCEPTED);
						}
					}
					for(OfferRide tempRide : muser.getRequestedRides()){
						if(mRide.getRideId().equals(tempRide.getRideId())){
							setSlideButton(SlideButtonState.REQUESTED);
						}
					}
				}
			};
			
		Httphandler.getSharedInstance().getUser(User.getSharedInstance().getUserId(), dataListener);

	}
	
	private void setSlideButton(SlideButtonState state){
		currentSlideButtonState = state;
		switch(state){
			case REQUEST:
				slideButton.setText("REQUEST");
				break;			
			case REQUESTED:
				slideButton.setText("REQUESTED");
				slideButton.setClickable(false);
				slideButton.setBackgroundDrawable(null);
				break;
			case ACCEPTED:
				slideButton.setText("ACCEPTED");
				slideButton.setClickable(false);
				slideButton.setBackgroundDrawable(null);
				break;
			case DONE:
				slideButton.setText("DONE");
				slideButton.setBackgroundColor(0xFFFFBB33);
				break;
			case SHARE:
				slideButton.setText("SHARE");
				slideButton.setBackgroundColor(0xFF99CC00);
				break;
		}
	}
}