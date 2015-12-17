package com.example.datamodel;

import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class Ride implements Parcelable{
	private String sourceAddress;
	
	private String destinationAddress;
	
	private String sourceId;
	
	private String destinationId;
	
	private LatLng source;
	
	private LatLng destination;
	
	private boolean isRoundTrip = true;
	
	private boolean isRecurring = true;
	
	private ArrayList<Marker> wayPoints = new ArrayList<Marker>();
	
	public String getSourceAddress() {
		return sourceAddress;
	}

	public void setSourceAddress(String sourceAddress) {
		this.sourceAddress = sourceAddress;
	}

	public String getDestinationAddress() {
		return destinationAddress;
	}

	public void setDestinationAddress(String destinationAddress) {
		this.destinationAddress = destinationAddress;
	}

	public LatLng getSource() {
		return source;
	}

	public void setSource(LatLng source) {
		this.source = source;
	}

	public LatLng getDestination() {
		return destination;
	}

	public void setDestination(LatLng destination) {
		this.destination = destination;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}

	public boolean isRoundTrip() {
		return isRoundTrip;
	}

	public void setRoundTrip(boolean isRoundTrip) {
		this.isRoundTrip = isRoundTrip;
	}
	
	public boolean isRecurring() {
		return isRecurring;
	}

	public void setRecurring(boolean isRecurring) {
		this.isRecurring = isRecurring;
	}

	public ArrayList<Marker> getWayPoints() {
		return wayPoints;
	}

	public void setWayPoints(ArrayList<Marker> wayPoints) {
		this.wayPoints = wayPoints;
	}

	public void addWayPoint(Marker wayPoint) {
		wayPoints.add(wayPoint);
	}

	public void removeWayPoint(Marker wayPoint) {
		wayPoints.remove(wayPoint);
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
	
	
}
