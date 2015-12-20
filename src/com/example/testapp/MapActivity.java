package com.example.testapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datamodel.OfferRide;
import com.example.datamodel.Ride;
import com.example.datamodel.Ride.RIDE_TYPE;
import com.example.http.Httphandler;
import com.example.http.Httphandler.HttpDataListener;
import com.example.map.BaseMapActivity;
import com.example.utils.Constants;
import com.example.utils.TimeHelper;
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
	protected Location mCurrentLocation;
	protected Polyline CurrentRoute = null;
	protected LatLng selectedMarker = null;
	
	private Ride mRide;
	LatLngBounds.Builder latLngBuilder;
	  
	protected SlidingUpPanelLayout slidingUpPanelLayout;
	
	TextView SlidingPanelTitle;
	protected Button shareButton, editButton;
	
	private RIDE_TYPE RideType;
	protected void  showSlidingPanelWithTitle(String title) {
//		slidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
//		
//		if(title != null){
//			SlidingPanelTitle.setText(title);
//		}
	}
	
//	protected void addWayPoint(LatLng point){
//		
//	}
//	
//	protected void removeWayPoint(LatLng point){
//		
//	}

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
//		slidingUpPanelLayout.setPanelState(PanelState.HIDDEN);
	}
	
	protected void handleOnMapLoaded(){
		if(RideType == RideType.OFFER){
			refreshRoute();
			refreshMapBoundary();
		} 
		
	}
	  
	protected void refreshMapBoundary(){
		LatLng source = mRide.getSource();
		LatLng destination = mRide.getDestination();
		
		latLngBuilder = new LatLngBounds.Builder();
		latLngBuilder.include(source);
		latLngBuilder.include(destination);
	    
		for(LatLng waypoint : mRide.getWayPoints()){
			latLngBuilder.include(waypoint);
		}
		LatLngBounds bounds = latLngBuilder.build();
		
		int padding = 200; // offset from edges of the map in pixels
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
		map.moveCamera(cu);
	}
	  
//	  protected void addMarkers(List<IMapMarker> markers){
//		  latLngBuilder = new LatLngBounds.Builder();
//		  for(IMapMarker marker : markers){
//			  Marker mapMarker = map.addMarker(new MarkerOptions().position( marker.getPosition())
//		  			    .title(marker.getTitle())
//		  			    .snippet(marker.getSnippet()));
//			  
//			  markerMap.put(mapMarker, marker);
//			  latLngBuilder.include(marker.getPosition());
//		  }
//		  //refreshMapBoundary();
//	  }
	  

	
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_map);
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
			
			mRide = (Ride) getIntent().getParcelableExtra(Constants.OFFER_RIDE_OBJECT);
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
			
			//TO-do this is the way to differentiate?
			//if(mRide == null){
		    final String action = getIntent().getAction();
		    if (Intent.ACTION_VIEW.equals(action)) {
				fetchRideDetails();
				RideType = RIDE_TYPE.FIND;
			} else {
				RideType = RIDE_TYPE.OFFER;
				setViews();
				doInitialMapSetUps();
			}
			
			//map.moveCamera(CameraUpdateFactory.newLatLngZoom(rides.get(0).getSource().getPosition(), 15));
					
								
//					Ride ride = DummyCurrentRide.currentRide;
//					map.moveCamera(CameraUpdateFactory.newLatLngZoom(ride.getSource().getPosition(), 15));
//					marker = map.addMarker(new MarkerOptions().position(ride.getSource().getPosition())
//			  			    .title(ride.getSource().getTitle())
//			  			    .snippet(ride.getSource().getSnippet()));
//					showRoutesAndMarkers(ride);
//					break;
				
			
			
			setMapListeners();
			
//			slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
//			slidingUpPanelLayout.setPanelState(PanelState.HIDDEN);
//	        
//			makeSlidingPanelItemsEditable(false);
	  }
	
	  
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    //getMenuInflater().inflate(R.menu.activity_main, menu);
	    return true;
	  }
	  
	
	@Override
	public void onBackPressed() {
		if(slidingUpPanelLayout.getPanelState().compareTo(PanelState.EXPANDED) == 0){
			slidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
		}else{
			super.onBackPressed();
		}
	}
	
	public void doInitialMapSetUps(){
		LatLng source = mRide.getSource();
		LatLng destination = mRide.getDestination();
		
		addMarker(source);
		addMarker(destination);
		//refreshRoute(source, destination, mRide.getWayPoints());
		
		
	}
	
	public void setViews(){
		
		SlidingPanelTitle = (TextView)findViewById(R.id.SlidingPanelTitle);
		shareButton = (Button)findViewById(R.id.share);
		editButton = (Button) findViewById(R.id.edit);
		TextView sourceAddress = (TextView)findViewById(R.id.sourceAddress);
		TextView destinationAddress = (TextView)findViewById(R.id.destinationAddress);
		TextView wayPoints = (TextView)findViewById(R.id.wayPoints);
		
		switch(RideType){
			case OFFER:
				SlidingPanelTitle.setText("MY RIDE");
				editButton.setText("EDIT");
				break;
			
			case FIND:
				SlidingPanelTitle.setText("OFFERED RIDE");
				editButton.setText("ACCEPT");
				break;
		}
		String text = "";
//		ArrayList<La>mRide.getWayPoints();
		wayPoints.setText(text);
		editButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "text", Toast.LENGTH_LONG).show();
			}
		});
		
		shareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handleShareButtonClicked();
			}
		});
		
		
		sourceAddress.setText(mRide.getSourceAddress());
		destinationAddress.setText(mRide.getDestinationAddress());
		
		TextView temp;
		
		temp = (TextView)findViewById(R.id.tripType);
		if(mRide.isRecurring()){
			temp.setText("Recurring");
		}else{
			temp.setText("One time");
		}
		
		temp = (TextView)findViewById(R.id.roundTrip);
		if(mRide.isRoundTrip()){
			temp.setText("Yes");
		}else{
			temp.setText("No");
		}
		
		//OfferRide specific
		OfferRide mOfferRide = (OfferRide)mRide;
		
		temp = (TextView)findViewById(R.id.startDate);
		temp.setText(TimeHelper.DateToString(mOfferRide.getStartDate()));
		
		temp = (TextView)findViewById(R.id.startTime);
		temp.setText(TimeHelper.TimeToString(mOfferRide.getStartTime()));
		
		temp = (TextView)findViewById(R.id.returnTime);
		temp.setText(TimeHelper.TimeToString(mOfferRide.getReturnTime()));
	}
	
	public void handleShareButtonClicked(){
		final ProgressDialog progress = ProgressDialog.show(this, getString(R.string.server_request_dialog_title),
			    getString(R.string.server_request_dialog_title), true);
		HttpDataListener mDataListener = new HttpDataListener() {				
			@Override
			public void onError(Exception e) {			
				//Toast.makeText(getApplicationContext(), "Something went wrong. Please try after some time", Toast.LENGTH_LONG).show();
				Log.e(TAG , e.getLocalizedMessage());
				progress.dismiss();
				Toast.makeText(getApplicationContext(), getString(R.string.internet_error), Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onDataAvailable(String response) {
				try{
						JSONObject jsonResponse = new JSONObject(response);
						boolean success = jsonResponse.getBoolean("success");
						if(success){
							String ride_id = jsonResponse.getString("ride_id");
							shareMessage(ride_id);
							progress.dismiss();
						} else {
							progress.dismiss();
							Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
						}
				}catch (JSONException e){
					e.printStackTrace();
				}
			}
		};
		
		Httphandler.getSharedInstance().postNewRide((OfferRide)mRide, mDataListener);

	}
	
	
	public void shareMessage(String ride_id){
		String message =  getString(R.string.share_text);
		message += " http://ShareDrive.com/" + ride_id;
		//CommonUtil.shareTextMessage(message, getApplicationContext());
		
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, message);
		sendIntent.setType("text/plain");
		startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_with)));
	}
	
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
					//Hide the sliding layout
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
					mRide = OfferRide.fromString(response);
					progress.dismiss();
					setViews();
					doInitialMapSetUps();
					refreshRoute();
					refreshMapBoundary();
				}
	        };
	        Httphandler.setSharedInstance(new Httphandler());
			Httphandler.getSharedInstance().getRide(ride_id, mDataListener);
	    }
	}
	
	private void refreshRoute(){
		drawRoute(mRide.getSource(), mRide.getDestination(), mRide.getWayPoints());
		refreshMapBoundary();
	}
}