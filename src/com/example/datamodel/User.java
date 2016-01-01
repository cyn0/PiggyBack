package com.example.datamodel;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.example.testapp.MainActivity;
import com.example.testapp.MapActivity;
import com.example.utils.Constants;

public class User {

	public enum UserType {
		OFFERER,
		REQUESTER
	}
	
	public static User mSharedInstance = new User();
	
	final String USER_ID = "user_id";
	final String GCM_ID = "gcm_id";
	final String PHONE_NUM = "phone_number";
	
	final static String KEY_OFFERED_RIDES = "offered_rides";
	final static String KEY_ACCEPTED_RIDES = "accepted_rides";
	final static String KEY_REQUESTED_RIDES = "requested_rides";
	
	ArrayList<OfferRide> mOfferedRides = new ArrayList<OfferRide>();
	ArrayList<OfferRide> mAcceptedRides = new ArrayList<OfferRide>();
	ArrayList<OfferRide> mRequestedRides = new ArrayList<OfferRide>();
	public static User getSharedInstance(){
		return mSharedInstance;
	}
	
	private Context getContext(){
		Context context = MainActivity.mContext;
		if(context != null){
			return context;
		} else{
			return MapActivity.mContext;
		}
	}
	
	
	
	public String getGCMRegistrationId(){
		Context context = getContext();
		String gcm_id = context.getSharedPreferences(Constants.APP_SETTINGS, context.MODE_PRIVATE).getString(Constants.A_GCM_ID, "");
		Log.d("gcm_id", gcm_id);
		return gcm_id;	
	}
	
	public void setGCMRegistrationId(String registrationId){
		Context context = getContext();
		Editor editor = context.getSharedPreferences(Constants.APP_SETTINGS, context.MODE_PRIVATE).edit();
    	editor.putString(Constants.A_GCM_ID, registrationId);
    	editor.commit();
	}
	
	public void setPhoneNumber(String phoneNumber){
		Context context = getContext();
		Editor editor = context.getSharedPreferences(Constants.APP_SETTINGS, context.MODE_PRIVATE).edit();
    	editor.putString(Constants.A_PHONE_NUMBER, phoneNumber);
    	editor.commit();
	}
	
	public String getPhoneNumber(){
		Context context = getContext();
		return context.getSharedPreferences(Constants.APP_SETTINGS, context.MODE_PRIVATE).getString(Constants.A_PHONE_NUMBER, "");
	}
	
	public void setUserId(String userId){
		Context context = getContext();
		Editor editor = context.getSharedPreferences(Constants.APP_SETTINGS, context.MODE_PRIVATE).edit();
    	editor.putString(Constants.A_USER_ID, userId);
    	editor.commit();
	}
	
	public String getUserId(){
		Context context = getContext();
		return context.getSharedPreferences(Constants.APP_SETTINGS, context.MODE_PRIVATE).getString(Constants.A_USER_ID, "");
	}
	
	public void setRegistrationStatus(boolean status){
		Context context = getContext();
		Editor editor = context.getSharedPreferences(Constants.APP_SETTINGS, context.MODE_PRIVATE).edit();
    	editor.putBoolean(Constants.A_REGISTRATION_STATUS, status);
    	editor.commit();
	}
	
	public boolean getRegistrationStatus(){
		Context context = getContext();
		return context.getSharedPreferences(Constants.APP_SETTINGS, context.MODE_PRIVATE).getBoolean(Constants.A_REGISTRATION_STATUS, false);
	}
	
	public void setOfferedRides(ArrayList<OfferRide> rides){
		this.mOfferedRides = rides;
	}
	
	public void setAcceptedRides(ArrayList<OfferRide> rides){
		this.mAcceptedRides = rides;
	}
	
	public void setRequestedRides(ArrayList<OfferRide> rides){
		this.mRequestedRides = rides;
	}
	
	public ArrayList<OfferRide> getOfferedRides(){
		return this.mOfferedRides;
	}
	
	public ArrayList<OfferRide> getAcceptedRides(){
		return this.mAcceptedRides;
	}
	
	public ArrayList<OfferRide> getRequestedRides(){
		return this.mRequestedRides;
	}
	
	public String toString(){
		Context context = getContext();
		JSONObject root = new JSONObject();
		try{
			root.put(GCM_ID, getGCMRegistrationId());
			root.put(PHONE_NUM, getPhoneNumber());
		}catch(Exception e){
			e.printStackTrace();
		}
		return root.toString();
	}
	
	public static User fromString(String input){
		User user = new User();
		try{
			JSONObject root = new JSONObject(input);
			if(!root.isNull(KEY_OFFERED_RIDES)){
				JSONArray jsonOfferedRides = root.getJSONArray(KEY_OFFERED_RIDES);
				ArrayList<OfferRide> offerRides = new ArrayList<OfferRide>();
				for(int i=0; i<jsonOfferedRides.length(); i++){
					JSONObject jsonOfferedRide = jsonOfferedRides.getJSONObject(i);
					OfferRide ride = OfferRide.fromString(jsonOfferedRide.toString());
					offerRides.add(ride);
				}
				user.setOfferedRides(offerRides);
			}
			
			if(!root.isNull(KEY_ACCEPTED_RIDES)){
				JSONArray jsonAcceptedRides = root.getJSONArray(KEY_ACCEPTED_RIDES);
				ArrayList<OfferRide> acceptRides = new ArrayList<OfferRide>();
				for(int i=0; i<jsonAcceptedRides.length(); i++){
					JSONObject jsonAcceptRide = jsonAcceptedRides.getJSONObject(i);
					OfferRide ride = OfferRide.fromString(jsonAcceptRide.toString());
					acceptRides.add(ride);
				}
				user.setAcceptedRides(acceptRides);
			}
			
			if(!root.isNull(KEY_REQUESTED_RIDES)){
				JSONArray jsonRequestedRides = root.getJSONArray(KEY_REQUESTED_RIDES);
				ArrayList<OfferRide> requestRides = new ArrayList<OfferRide>();
				for(int i=0; i<jsonRequestedRides.length(); i++){
					JSONObject jsonRequestRide = jsonRequestedRides.getJSONObject(i);
					OfferRide ride = OfferRide.fromString(jsonRequestRide.toString());
					requestRides.add(ride);
				}
				user.setRequestedRides(requestRides);
			}
		} catch(JSONException e){
			e.printStackTrace();
		}
		
		return user;
	}
}
