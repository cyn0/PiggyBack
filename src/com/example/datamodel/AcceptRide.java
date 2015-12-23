package com.example.datamodel;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.utils.TimeHelper;
import com.google.android.gms.maps.model.LatLng;

public class AcceptRide extends Ride{

	public JSONObject toJSON(){
		try {
			JSONObject root = new JSONObject();
			root.put(KEY_USER_ID, User.getSharedInstance().getUserId());
			root.put(KEY_RIDE_ID, getRideId());
			
			return root;
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return null;
	}
}
