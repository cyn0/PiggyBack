package com.example.datamodel;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.example.testapp.MainActivity;
import com.example.testapp.MapActivity;
import com.example.utils.Constants;

public class User {

	public static User mSharedInstance = new User();
	
	final String USER_ID = "user_id";
	final String GCM_ID = "gcm_id";
	final String PHONE_NUM = "phone_number";
	Context mContext;
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
		//return context.getSharedPreferences(Constants.APP_SETTINGS, context.MODE_PRIVATE).getString(Constants.A_PHONE_NUMBER, "");
		return "770884030";
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
}
