package com.example.testapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.map.DirectionsJSONParser;
import com.example.map.MapHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;


public abstract class MapActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{
  
	private static final String TAG = "MAP acrtivity";
	protected GoogleApiClient mGoogleApiClient;
	protected LocationRequest mLocationRequest;
	protected Location mCurrentLocation;
	protected GoogleMap map;
//	protected HashMap<Marker, IMapMarker> markerMap = new HashMap();
	protected Polyline CurrentRoute = null;
	protected LatLng selectedMarker = null;
	LatLngBounds.Builder latLngBuilder;
	  
	protected SlidingUpPanelLayout slidingUpPanelLayout;
	
	TextView SlidingPanelTitle;
	protected Button routeButton, editButton;
	
	private int LOCATION_UPDATE_INTERVAL = 30 * 1000; //30 sec
	private int LOCATION_UPDATE_FASTEST_INTERVAL = 10 * 1000; //10 sec
	
	protected void  showSlidingPanelWithTitle(String title) {
		slidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
		
		if(title != null){
			SlidingPanelTitle.setText(title);
		}
	}
	
	protected boolean handleMarkerClicked(Marker marker){
		return true;
	}
	  
	protected void handleMapLongPressed(LatLng point){}
	  
	protected void handleMapClicked(LatLng point){
		slidingUpPanelLayout.setPanelState(PanelState.HIDDEN);
	}
	
	protected void handleOnMapLoaded(){
		LatLngBounds bounds = latLngBuilder.build();
		
		int padding = 0; // offset from edges of the map in pixels
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 40);
		map.moveCamera(cu);
	}
	  
	protected void refreshMapBoundary(){
	    LatLngBounds bounds = latLngBuilder.build();
		
		int padding = 0; // offset from edges of the map in pixels
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 40);
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
			setContentView(R.layout.activity_main);
			
			mGoogleApiClient = new GoogleApiClient.Builder(this)
            	.addApi(LocationServices.API)
            	.addConnectionCallbacks(this)
            	.addOnConnectionFailedListener(this)
            	.build();
			mLocationRequest = LocationRequest.create()
			        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
			        .setInterval(LOCATION_UPDATE_INTERVAL)        // 10 seconds, in milliseconds
			        .setFastestInterval(LOCATION_UPDATE_FASTEST_INTERVAL); // 1 second, in milliseconds
			
			
			SlidingPanelTitle = (TextView)findViewById(R.id.SlidingPanelTitle);
			routeButton = (Button)findViewById(R.id.direction);
			editButton = (Button) findViewById(R.id.edit);
			editButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Toast.makeText(getApplicationContext(), "text", Toast.LENGTH_LONG).show();
					makeSlidingPanelItemsEditable(true);
				}
			});
			
			routeButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(mCurrentLocation != null && selectedMarker != null){
						double lat = mCurrentLocation.getLatitude();
						double longitude = mCurrentLocation.getLongitude();
						LatLng latLng = new LatLng(lat, longitude);
						drawRoute(latLng, selectedMarker);
					}else{
						Toast.makeText(getApplicationContext(), "Location not available", Toast.LENGTH_LONG).show();
					}
				}
			});
			
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			
			//map.moveCamera(CameraUpdateFactory.newLatLngZoom(rides.get(0).getSource().getPosition(), 15));
					
			map.setOnMarkerClickListener(new OnMarkerClickListener(){
                @Override
                public boolean onMarkerClick(Marker marker) {
                	return handleMarkerClicked(marker);
                }

            }); 
			
			map.setOnMapLongClickListener(new OnMapLongClickListener() {						
				@Override
				public void onMapLongClick(LatLng point) {
					handleMapLongPressed(point);
					
				}
			});					

			map.setOnMapClickListener(new OnMapClickListener() {
				
				@Override
				public void onMapClick(LatLng point) {
					handleMapClicked(point);
				}
			});
			
			map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() { 
				@Override 
				public void onMapLoaded() { 
//						//map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 15));
					  
						LatLngBounds bounds = latLngBuilder.build();
						int padding = 0; // offset from edges of the map in pixels
						CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 40);
						map.moveCamera(cu);
				 } 
			 });
					
//					Ride ride = DummyCurrentRide.currentRide;
//					map.moveCamera(CameraUpdateFactory.newLatLngZoom(ride.getSource().getPosition(), 15));
//					marker = map.addMarker(new MarkerOptions().position(ride.getSource().getPosition())
//			  			    .title(ride.getSource().getTitle())
//			  			    .snippet(ride.getSource().getSnippet()));
//					showRoutesAndMarkers(ride);
//					break;
				
			
			
			
			slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
			slidingUpPanelLayout.setPanelState(PanelState.HIDDEN);
	        
			makeSlidingPanelItemsEditable(false);
	  }
	
	  
	  
	  public void drawRoute(LatLng source, LatLng destination){		  
		  String url = MapHelper.getDirectionsUrl(source, destination);
			 
          DownloadTask downloadTask = new DownloadTask();
          downloadTask.execute(url);
	  }
	  
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    //getMenuInflater().inflate(R.menu.activity_main, menu);
	    return true;
	  }
	  
	  
	  private class DownloadTask extends AsyncTask<String, Void, String>{
		  
	        // Downloading data in non-ui thread
	        @Override
	        protected String doInBackground(String... url) {
	 
	            String data = "";
	            try{
	                data = MapHelper.downloadUrl(url[0]);
	                Log.d("Response",data);
	            }catch(Exception e){
	                Log.d("Background Task",e.toString());
	            }
	            return data;
	        }
	 
	        @Override
	        protected void onPostExecute(String result) {
	            super.onPostExecute(result);
	            ParserTask parserTask = new ParserTask();
	 
	            parserTask.execute(result);
	        }
	    }
	  
	  
	  private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
		  
	        // Parsing the data in non-ui thread
	        @Override
	        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
	 
	            JSONObject jObject;
	            List<List<HashMap<String, String>>> routes = null;
	 
	            try{
	                jObject = new JSONObject(jsonData[0]);
	                DirectionsJSONParser parser = new DirectionsJSONParser();
	                routes = parser.parse(jObject);
	            }catch(Exception e){
	                e.printStackTrace();
	            }
	            return routes;
	        }
	 
	        // Executes in UI thread, after the parsing process
	        @Override
	        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
	            ArrayList<LatLng> points = null;
	            PolylineOptions lineOptions = null;
	            MarkerOptions markerOptions = new MarkerOptions();
	 
	            // Traversing through all the routes
	            for(int i=0;i<result.size();i++){
	                points = new ArrayList<LatLng>();
	                lineOptions = new PolylineOptions();
	 
	                // Fetching i-th route
	                List<HashMap<String, String>> path = result.get(i);
	 
	                // Fetching all the points in i-th route
	                for(int j=0;j<path.size();j++){
	                    HashMap<String,String> point = path.get(j);
	 
	                    double lat = Double.parseDouble(point.get("lat"));
	                    double lng = Double.parseDouble(point.get("lng"));
	                    LatLng position = new LatLng(lat, lng);
	                    
	                    points.add(position);
	                }
	 
	                // Adding all the points in the route to LineOptions
	                lineOptions.addAll(points);
	                lineOptions.width(5);
	                lineOptions.color(Color.RED);
	            }
	            if(CurrentRoute != null)
	            	CurrentRoute.remove();
	            
	            CurrentRoute = map.addPolyline(lineOptions);
	            
	            
	            
	        }
	    }
	  
	  
    @Override
	public void onLocationChanged(Location location) {
		   mCurrentLocation = location;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mGoogleApiClient.connect();
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    if (mGoogleApiClient.isConnected()) {
	        mGoogleApiClient.disconnect();
	    }
	}
	
	@Override
	public void onConnected(Bundle connectionHint) {
			 Log.i(TAG, "Location services connected.");
			 Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
			 if (location == null) {
				 Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_LONG).show();
				 LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
			 }
			 else {
				 mCurrentLocation = location;
				    Toast.makeText(getApplicationContext(), location.toString(), Toast.LENGTH_LONG).show();
			 };
	}

	@Override
	public void onConnectionSuspended(int cause) {
		Log.i(TAG, "Location services suspended.");
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i(TAG, "Location services failed.");
		
	}

	protected void makeSlidingPanelItemsEditable(boolean isEditable){
		EditText et =(EditText) findViewById(R.id.SlidingPanelTitle1);
		EditText et2 = (EditText) findViewById(R.id.SlidingPanelTitle2);
		
		if(isEditable){
			et.setFocusable(true);
	        et.setEnabled(true);
	        et.setClickable(true);
	        et.setFocusableInTouchMode(true);
	        et.setTextColor(Color.parseColor("#000000"));
	        
	        et2.setFocusable(true);
	        et2.setEnabled(true);
	        et2.setClickable(true);
	        et2.setFocusableInTouchMode(true);
	        et2.setTextColor(Color.parseColor("#000000"));
	        
		}else{
			et.setFocusable(false);
	        et.setClickable(false);
	        et.setFocusableInTouchMode(false);
	        et.setEnabled(false);
	        et.setTextColor(Color.parseColor("#33B5E5"));
	        
	        et2.setFocusable(false);
	        et2.setClickable(false);
	        et2.setFocusableInTouchMode(false);
	        et2.setEnabled(false);
	        et2.setTextColor(Color.parseColor("#33B5E5"));
		}
	}

	@Override
	public void onBackPressed() {
		
		if(slidingUpPanelLayout.getPanelState().equals(PanelState.ANCHORED)){
			slidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
		}else{
			super.onBackPressed();
		}
	}
	
	
}