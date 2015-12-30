package com.example.utils;

import java.util.List;
import java.util.Locale;

import com.example.datamodel.OfferRide;
import com.example.datamodel.User;
import com.example.testapp.MainActivity;
import com.example.testapp.MapActivity;
import com.example.testapp.R;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;

public class CommonUtil {
	
	static CommonUtil mSharedInstance = new CommonUtil();
	
	public static CommonUtil getSharedInstance(){
		return mSharedInstance;
	}
	
//	public static void setSharedInstance(){
//		 mSharedInstance;
//	}
	private Context getContext(){
		Context context = MainActivity.mContext;
		if(context != null){
			return context;
		} else{
			return MapActivity.mContext;
		}
	}
	
	public void shareMessage(String ride_id, Activity mContext){
		String message =  mContext.getString(R.string.share_text);
		message += " http://ShareDrive.com/" + ride_id;
		
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, message);
		sendIntent.setType("text/plain");
		sendIntent.putExtra("exit_on_sent", true);
//		mContext.startActivity(Intent.createChooser(sendIntent, mContext.getResources().getText(R.string.share_with)));
		mContext.startActivityForResult(Intent.createChooser(sendIntent, mContext.getResources().getText(R.string.share_with)), 1); //(Intent.createChooser(sendIntent, mContext.getResources().getText(R.string.share_with)));
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
	
	public String getContactName(String phoneNumber) {
	    ContentResolver cr = getContext().getContentResolver();
	    Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
	    Cursor cursor = cr.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
	    if (cursor == null) {
	        return null;
	    }
	    String contactName = null;
	    if(cursor.moveToFirst()) {
	        contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
	    }

	    if(cursor != null && !cursor.isClosed()) {
	        cursor.close();
	    }

	    return contactName;
	}
	
	public boolean amIOfferer(OfferRide ride){
		String myId = User.getSharedInstance().getUserId();
		String offererId = ride.getOfferedUserId();
		return myId.equals(offererId);
	}
}
