package com.example.datamodel;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.utils.CommonUtil;

public class AnotherUser {
	public enum USER_TYPE {
		ACCEPT,
		REQUEST
	}
	
	private USER_TYPE user_type;
	private String phone_number;
	private String contact_name;
	private String id;
	
	private static String KEY_PHONE_NUMBER = "phone_number";
	ArrayList<OfferRide> mOfferedRides = new ArrayList<OfferRide>();
	ArrayList<OfferRide> mAcceptedRides = new ArrayList<OfferRide>();
	ArrayList<OfferRide> mRequestedRides = new ArrayList<OfferRide>();
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}
	
	public void setType(USER_TYPE ut){
		this.user_type = ut;
	}
	
	public USER_TYPE getType(){
		return this.user_type;
	}
	
	public String getContact_name() {
		return contact_name;
	}

	public void setContact_name(String contact_name) {
		this.contact_name = contact_name;
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
	
	public static AnotherUser fromString(String input){
		AnotherUser user = new AnotherUser();
		try{
			JSONObject root = new JSONObject(input);
			user.setId(root.getString("_id"));
			user.setPhone_number(root.getString(KEY_PHONE_NUMBER));
			String name = CommonUtil.getSharedInstance().getContactName(user.getPhone_number());
			user.setContact_name(name);
			
			if(!root.isNull(User.KEY_ACCEPTED_RIDES)){
				JSONArray jsonAcceptedRides = root.getJSONArray(User.KEY_ACCEPTED_RIDES);
				ArrayList<OfferRide> acceptRides = new ArrayList<OfferRide>();
				for(int i=0; i<jsonAcceptedRides.length(); i++){
					JSONObject jsonAcceptRide = jsonAcceptedRides.getJSONObject(i);
					OfferRide ride = OfferRide.fromString(jsonAcceptRide.toString());
					acceptRides.add(ride);
				}
				user.setAcceptedRides(acceptRides);
			}
			
			if(!root.isNull(User.KEY_REQUESTED_RIDES)){
				JSONArray jsonRequestedRides = root.getJSONArray(User.KEY_REQUESTED_RIDES);
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
