package com.example.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapHelper {
	public static String getDirectionsUrl(LatLng origin,LatLng dest, ArrayList<LatLng> wayPoints){
		 
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
 
        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
 
        // Sensor enabled
        String sensor = "sensor=false";
 
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
 
        if(wayPoints != null && wayPoints.size() > 0){
        	String waypoints = "waypoints=optimize:true"; //"|Barossa+Valley,SA|Clare,SA|Connawarra,SA|McLaren+Vale,SA";
        	
        	for(LatLng latLng : wayPoints){
        		//LatLng latLng = wayPoint.getPosition();
        		waypoints = waypoints + "|" + latLng.latitude+","+ latLng.longitude;
        	}
        	parameters = parameters + "&" + waypoints;
        }
        
        // Output format
        String output = "json";
        
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
 
        return url;
    }
	/*Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
    .title("Hamburg"));
Marker kiel = map.addMarker(new MarkerOptions()
    .position(KIEL)
    .title("Kiel")
    .snippet("Kiel is cool")
    .icon(BitmapDescriptorFactory
        .fromResource(R.drawable.ic_launcher)));

// Move the camera instantly to hamburg with a zoom of 15.
map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));

// Zoom in, animating the camera.
map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);*/
	
	public static String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
 
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
 
            // Connecting to url
            urlConnection.connect();
 
            // Reading data from url
            iStream = urlConnection.getInputStream();
 
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
 
            StringBuffer sb = new StringBuffer();
 
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
 
            data = sb.toString();
 
            br.close();
 
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
