package com.example.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

//Parcelable vs serializable
public class Ride implements Parcelable{
	
	public enum RIDE_TYPE {
		OFFER,
		FIND
	}
	
//	private String userId;
	private String offeredUserId = "";
	
	private String userUserId = "";
	
	private String rideId = "";
	
	private String sourceAddress;
	
	private String destinationAddress;
	
	private String sourceId;
	
	private String destinationId;
	
	private LatLng source;
	
	private LatLng destination;
	
	private boolean isRoundTrip = true;
	
	private boolean isRecurring = true;
	
	private ArrayList<LatLng> wayPoints = new ArrayList<LatLng>();
//	private ArrayList<Marker> wayPoints = new ArrayList<Marker>();
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

	public ArrayList<LatLng> getWayPoints() {
		return wayPoints;
	}

	public void setWayPoints(ArrayList<LatLng> wayPoints) {
		this.wayPoints = wayPoints;
	}

	public void addWayPoint(LatLng wayPoint) {
		wayPoints.add(wayPoint);
	}

	public void removeWayPoint(LatLng wayPoint) {
		wayPoints.remove(wayPoint);
	}
	
	public String getRideId() {
		return rideId;
	}

	public void setRideId(String rideId) {
		this.rideId = rideId;
	}

	public String getOfferedUserId() {
		return offeredUserId;
	}

	public void setOfferedUserId(String offeredUserId) {
		this.offeredUserId = offeredUserId;
	}

	public String getUserUserId() {
		return userUserId;
	}

	public void setUserUserId(String secondUserId) {
		this.userUserId = secondUserId;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Parcelable.Creator<Ride> CREATOR = new Parcelable.Creator<Ride>() {
        public Ride createFromParcel(Parcel in) {
            return new Ride(in);
        }

        public Ride[] newArray(int size) {
            return new Ride[size];
        }
    };
    
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(offeredUserId);
		dest.writeString(userUserId);
		dest.writeString(rideId);
		
		dest.writeString(sourceAddress);
		dest.writeString(sourceId);
		dest.writeDouble(source.latitude);
		dest.writeDouble(source.longitude);
		
		dest.writeString(destinationAddress);
		dest.writeString(destinationId);
		dest.writeDouble(destination.latitude);
		dest.writeDouble(destination.longitude);
	
		boolean t [] = new boolean[2];
		t[0] = isRoundTrip;
		t[1] = isRecurring;
		dest.writeBooleanArray(t);
		
		dest.writeInt(wayPoints.size());
		
		for(LatLng wayPoint : wayPoints){
			dest.writeDouble(wayPoint.latitude);
			dest.writeDouble(wayPoint.longitude);
		}
		
	}
	
	public Ride(){
		
	}
	
	// example constructor that takes a Parcel and gives you an object populated with it's values
    public Ride(Parcel in) {
    	super();
    	
    	offeredUserId = in.readString();
		userUserId = in.readString();
		rideId = in.readString();
		
    	double latitude, longitude;
    	
    	sourceAddress = in.readString();
        sourceId = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        source = new LatLng(latitude, longitude);
        
        destinationAddress = in.readString();
        destinationId = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        destination = new LatLng(latitude, longitude);
        
        boolean[] t = new boolean[2]; 
        in.readBooleanArray(t);
        
        isRoundTrip = t[0];
        isRecurring = t[1];
        
        wayPoints = new ArrayList<LatLng>();
        int count = in.readInt();
        for(int i=0;i < count; i++){
        	latitude = in.readDouble();
            longitude = in.readDouble();
            wayPoints.add(new LatLng(latitude, longitude));
        }
    }
	

    protected static String KEY_LATITUDE = "latitude";
    protected static String KEY_LONGITUDE = "longitude";
    protected static String KEY_OFFERED_USER_ID = "offered_user_id";
    protected static String KEY_USER_USER_ID = "user_user_id";
    protected static String KEY_RIDE_ID = "ride_id";
}
