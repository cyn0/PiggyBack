package com.example.utils;

import com.example.datamodel.OfferRide;
import com.example.testapp.R;

import android.content.Context;
import android.content.Intent;

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
	
}
