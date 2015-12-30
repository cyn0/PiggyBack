package com.example.datamodel;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.datamodel.AnotherUser.USER_TYPE;
import com.example.utils.TimeHelper;
import com.google.android.gms.maps.model.LatLng;

public class OfferRide extends Ride{

	private long startDate;
	
	private long startTime;
	
	private long returnTime;
	
	private double price = 0;
	
	private boolean isPriced = false;

	private ArrayList<AnotherUser> requestedUsers = new ArrayList<>();
	private ArrayList<AnotherUser> acceptedUsers = new ArrayList<>();
	private static String KEY_REQUESTED_USERS = "requested";
	private static String KEY_ACCEPTED_USERS = "accepted";
	public OfferRide(){}
	
	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getReturnTime() {
		return returnTime;
	}

	public void setReturnTime(long returnTime) {
		this.returnTime = returnTime;
	}
	
	public boolean isPriced() {
		return isPriced;
	}

	public void setPriced(boolean isPriced) {
		this.isPriced = isPriced;
		
		if(!this.isPriced){
			price = 0;
		}
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public ArrayList<AnotherUser> getRequestedUsers() {
		return requestedUsers;
	}

	public void setRequestedUsers(ArrayList<AnotherUser> requestedUsers) {
		this.requestedUsers = requestedUsers;
	}

	public ArrayList<AnotherUser> getAcceptedUsers() {
		return acceptedUsers;
	}

	public void setAcceptedUsers(ArrayList<AnotherUser> acceptedUsers) {
		this.acceptedUsers = acceptedUsers;
	}

	public JSONObject toJSON(){
		try {
			JSONObject root = new JSONObject();
			root.put("is_recurring", isRecurring());
			root.put("is_round_trip", isRoundTrip());
			
			JSONObject source = new JSONObject();
			source.put(KEY_LATITUDE, getSource().latitude);
			source.put(KEY_LONGITUDE, getSource().longitude);
			source.put("id", getSourceId());
			source.put("address", getSourceAddress());
			
			JSONObject destination = new JSONObject();
			destination.put(KEY_LATITUDE, getDestination().latitude);
			destination.put(KEY_LONGITUDE, getDestination().longitude);
			destination.put("id", getDestinationId());
			destination.put("address", getDestinationAddress());
			
			root.put("source", source);
			root.put("destination", destination);
			
			String time;

			time = TimeHelper.DateToStringServerFormat(startDate);        	
			root.put("start_date", time);
			
			time = TimeHelper.TimeToString(startTime);
			root.put("start_time", time);
			
			if(isRoundTrip()){
				time = TimeHelper.TimeToString(returnTime);
				root.put("return_time", time);
			}
			
			root.put("is_priced", isPriced);
			root.put("price", getPrice());
			
			JSONArray wayPoints = new JSONArray();
			ArrayList<LatLng> wp = getWayPoints();
			for(LatLng w : wp){
				JSONObject wayPoint = new JSONObject();
				wayPoint.put(KEY_LATITUDE, w.latitude);
				wayPoint.put(KEY_LONGITUDE, w.longitude);
				
				wayPoints.put(wayPoint);
			}
			
			root.put("waypoints", wayPoints);
//			root.put(KEY_USER_ID, User.getSharedInstance().getUserId());
			root.put(KEY_OFFERED_USER_ID, getOfferedUserId());
			root.put(KEY_USER_USER_ID, getUserUserId());
			root.put(KEY_RIDE_ID, getRideId());
			return root;
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return null;
	}
	
	public static OfferRide fromString(String input){
		try {
			OfferRide mOfferRide = new OfferRide();
			JSONObject root = new JSONObject(input);
			
//			mOfferRide.setUserId(root.getString(KEY_USER_ID));
			Log.d("input from fromStrinf", input);
			mOfferRide.setOfferedUserId(root.getString(KEY_OFFERED_USER_ID));
			mOfferRide.setUserUserId(root.getString(KEY_USER_USER_ID));
			
			mOfferRide.setRideId(root.getString("_id"));
			mOfferRide.setRecurring(root.getBoolean("is_recurring"));
			mOfferRide.setRoundTrip(root.getBoolean("is_round_trip"));
			
			double temp_lat, temp_long;
			
			JSONObject source = root.getJSONObject("source");
			temp_lat = source.getDouble(KEY_LATITUDE);
			temp_long = source.getDouble(KEY_LONGITUDE);
			LatLng sourceLatLng = new LatLng(temp_lat, temp_long);
			mOfferRide.setSource(sourceLatLng);
			mOfferRide.setSourceId(source.getString("id"));
			mOfferRide.setSourceAddress(source.getString("address"));
			
			JSONObject destination = root.getJSONObject("destination");
			temp_lat = destination.getDouble(KEY_LATITUDE);
			temp_long = destination.getDouble(KEY_LONGITUDE);
			LatLng destinationLatLng = new LatLng(temp_lat, temp_long);
			mOfferRide.setDestination(destinationLatLng);
			mOfferRide.setDestinationId(destination.getString("id"));
			mOfferRide.setDestinationAddress(destination.getString("address"));

			long newDate;
			
			String start_date = root.getString("start_date");
			newDate = TimeHelper.StringToDateServerFormat(start_date);
			mOfferRide.setStartDate(newDate);
			
			String start_time = root.getString("start_time");
			newDate = TimeHelper.StringToTime(start_time);
			mOfferRide.setStartTime(newDate);
						
			if(mOfferRide.isRoundTrip()){
				String return_time = root.getString("return_time");
				newDate = TimeHelper.StringToTime(return_time);
				mOfferRide.setReturnTime(newDate);
			}
			
			mOfferRide.setPriced(root.getBoolean("is_priced"));
			if(mOfferRide.isPriced()){
				mOfferRide.setPrice(root.getDouble("price"));
			}
			
			int length;
			
			JSONArray wp = root.getJSONArray("waypoints");
			length = wp.length();
			
			for(int i=0; i < length; i++){
				JSONObject w = wp.getJSONObject(i);
				temp_lat = w.getDouble(KEY_LATITUDE);
				temp_long = w.getDouble(KEY_LONGITUDE);
				LatLng wayPoint = new LatLng(temp_lat, temp_long);
				mOfferRide.addWayPoint(wayPoint);
			}
			
			if(!root.isNull(KEY_REQUESTED_USERS)){
				JSONArray jsonRequestedUsers = root.getJSONArray(KEY_REQUESTED_USERS);
				length = jsonRequestedUsers.length();
				ArrayList<AnotherUser> ru = new ArrayList<>();
				for(int i=0; i < length; i++){
					JSONObject u = jsonRequestedUsers.getJSONObject(i);
					AnotherUser user = AnotherUser.fromString(u.toString());
					user.setType(USER_TYPE.REQUEST);
					ru.add(user);
				}
				mOfferRide.setRequestedUsers(ru);
			}
			
			if(!root.isNull(KEY_ACCEPTED_USERS)){
				JSONArray jsonAcceptedUsers = root.getJSONArray(KEY_ACCEPTED_USERS);
				length = jsonAcceptedUsers.length();
				ArrayList<AnotherUser> au = new ArrayList<>();
				for(int i=0; i < length; i++){
					JSONObject u = jsonAcceptedUsers.getJSONObject(i);
					AnotherUser user = AnotherUser.fromString(u.toString());
					user.setType(USER_TYPE.ACCEPT);
					au.add(user);
				}
				mOfferRide.setAcceptedUsers(au);
			}
			
			
			return mOfferRide;
		}catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		
		dest.writeLong(startDate);
		dest.writeLong(startTime);
		dest.writeLong(returnTime);
		
		dest.writeDouble(price);
		
		boolean val[] = new boolean[1];
		val[0] = isPriced;
		dest.writeBooleanArray(val);
	}
	
	public static final Parcelable.Creator<OfferRide> CREATOR = new Parcelable.Creator<OfferRide>() {
        public OfferRide createFromParcel(Parcel in) {
            return new OfferRide(in);
        }

        public OfferRide[] newArray(int size) {
            return new OfferRide[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private OfferRide(Parcel in) {
    	super(in);
    	long time;
    	time = in.readLong();
    	startDate= time;
    	
    	time = in.readLong();
    	startTime = time;
    	
    	time = in.readLong();
    	returnTime = time;
    	
    	price = in.readDouble();
    	
    	boolean val[] = new boolean[1];
    	in.readBooleanArray(val);
    	isPriced = val[0];
    }
    
}
