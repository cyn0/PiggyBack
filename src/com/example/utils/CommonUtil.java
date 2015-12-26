package com.example.utils;

import java.util.List;
import java.util.Locale;

import com.example.datamodel.OfferRide;
import com.example.testapp.R;
import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;

public class CommonUtil {
	
	static CommonUtil mSharedInstance = new CommonUtil();
	
	public static CommonUtil getSharedInstance(){
		return mSharedInstance;
	}
	
//	public static void setSharedInstance(){
//		 mSharedInstance;
//	}
	
	public void shareTextMessage(String message, Context context){
//		String ride_id = jsonResponse.getString("ride_id");
//		String share_text = getString(R.string.share_text);
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		
		sendIntent.putExtra(Intent.EXTRA_TEXT, message);
		sendIntent.setType("text/plain");
		context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.share_with)));
	}
	
	public Address getAddress(Context context, LatLng latLng){
		Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
		try {
		    List<Address> address = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
		    return address.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
