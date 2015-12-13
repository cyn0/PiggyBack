package com.example.datamodel;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.utils.TimeHelper;
import com.google.android.gms.maps.model.LatLng;

public class OfferRide extends Ride{

	private Date startDate;
	
	private Date startTime;
	
	private Date returnTime;
	
	private double price = 0;
	
	private boolean isPriced = false;

	public OfferRide(){}
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getReturnTime() {
		return returnTime;
	}

	public void setReturnTime(Date returnTime) {
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

	public JSONObject toJSON(){
		try {
			JSONObject root = new JSONObject();
			root.put("is_recurring", isRecurring());
			root.put("is_round_trip", isRoundTrip());
			
			JSONObject source = new JSONObject();
			source.put("latitude", getSource().latitude);
			source.put("longitude", getSource().longitude);
			source.put("id", getSourceId());
			source.put("address", getSourceAddress());
			
			JSONObject destination = new JSONObject();
			destination.put("latitude", getDestination().latitude);
			destination.put("longitude", getDestination().longitude);
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
			
			mOfferRide.setRecurring(root.getBoolean("is_recurring"));
			mOfferRide.setRoundTrip(root.getBoolean("is_round_trip"));
			
			JSONObject source = root.getJSONObject("source");
			double source_latitude = source.getDouble("latitude");
			double source_longitude = source.getDouble("longitude");
			LatLng sourceLatLng = new LatLng(source_latitude, source_longitude);
			mOfferRide.setSource(sourceLatLng);
			mOfferRide.setSourceId(source.getString("id"));
			mOfferRide.setSourceAddress(source.getString("address"));
			
			JSONObject destination = root.getJSONObject("destination");
			double destination_latitude = destination.getDouble("latitude");
			double destination_longitude = destination.getDouble("longitude");
			LatLng destinationLatLng = new LatLng(destination_latitude, destination_longitude);
			mOfferRide.setDestination(destinationLatLng);
			mOfferRide.setDestinationId(destination.getString("id"));
			mOfferRide.setDestinationAddress(destination.getString("address"));

			SimpleDateFormat format;
			Date newDate;
			
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
			
			return mOfferRide;
		}catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
