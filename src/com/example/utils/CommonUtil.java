package com.example.utils;

import com.example.datamodel.OfferRide;
import com.example.testapp.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;

public class CommonUtil {
	
	public static void shareTextMessage(String message, Context context){
//		String ride_id = jsonResponse.getString("ride_id");
//		String share_text = getString(R.string.share_text);
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		
		sendIntent.putExtra(Intent.EXTRA_TEXT, message);
		sendIntent.setType("text/plain");
		context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.share_with)));
	}
	
	public static String getGCMRegistrationId(Context context){
		return context.getSharedPreferences(Constants.APP_SETTINGS, context.MODE_PRIVATE).getString(Constants.GCM_ID, "");
	}
	
	public static void setGCMRegistrationId(Context context, String registrationId){
		Editor editor = context.getSharedPreferences(Constants.APP_SETTINGS, context.MODE_PRIVATE).edit();
    	editor.putString(Constants.A_GCM_ID, registrationId);
    	editor.commit();
	}
}
