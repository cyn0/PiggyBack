package com.example.testapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.example.map.DirectionsJSONParser;
import com.example.map.MapHelper;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


public abstract class BaseMapActivity extends Activity {
  
	private static final String TAG = "MAP acrtivity";
	protected LocationRequest mLocationRequest;
	protected Location mCurrentLocation;
	protected GoogleMap map;
	protected Polyline CurrentRoute = null;
	protected LatLng selectedMarker = null;
	//LatLngBounds.Builder latLngBuilder;
	  	
	
	protected abstract boolean handleMarkerClicked(Marker marker);
	  
	protected abstract void handleMapLongPressed(LatLng point);
	  
	protected abstract void handleMapClicked(LatLng point);
	
	protected abstract void handleOnMapLoaded();
	  
	protected void refreshMapBoundary(){
//	    LatLngBounds bounds = latLngBuilder.build();
//		
//		int padding = 0; // offset from edges of the map in pixels
//		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 40);
//		map.moveCamera(cu);
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
	  
	protected Marker addMarker(LatLng position){
		 return map.addMarker(new MarkerOptions().position( position));
	  			    //.title(marker.getTitle())
	  			    //.snippet(marker.getSnippet()));
	}
	
	protected void setMapListeners(){
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
				handleOnMapLoaded();
			 } 
		 });

	}
	
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);			
	  }
	
	  
	  
	  public void drawRoute(LatLng source, LatLng destination, ArrayList<Marker> wayPoints){		  
		  String url = MapHelper.getDirectionsUrl(source, destination, wayPoints);
			 
          DownloadTask downloadTask = new DownloadTask();
          downloadTask.execute(url);
	  }
	  
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    //getMenuInflater().inflate(R.menu.activity_main, menu);
	    return true;
	  }
	  
	  
	  private class DownloadTask extends AsyncTask<String, Void, String>{
		  
		    boolean success = true;
	        // Downloading data in non-ui thread
	        @Override
	        protected String doInBackground(String... url) {
	 
	            String data = "";
	            try{
	                data = MapHelper.downloadUrl(url[0]);
	                Log.d("Response",data);
	            }catch(Exception e){
	                Log.d("Background Task",e.toString());
	                success = false;
	            }
	            return data;
	        }
	 
	        @Override
	        protected void onPostExecute(String result) {
	            super.onPostExecute(result);
	            if(success){
	            	ParserTask parserTask = new ParserTask();
	            	parserTask.execute(result);
	            }else{
	            	Toast.makeText(getApplicationContext(), "Unable to reach server", Toast.LENGTH_LONG).show();
	            }
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
	public void onBackPressed() {
		
//		if(slidingUpPanelLayout.getPanelState().equals(PanelState.ANCHORED)){
//			slidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
//		}else{
//			super.onBackPressed();
//		}
	}
	
	
}