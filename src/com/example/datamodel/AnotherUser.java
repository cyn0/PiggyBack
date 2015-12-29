package com.example.datamodel;

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

	public static AnotherUser fromString(String input){
		AnotherUser user = new AnotherUser();
		try{
			JSONObject root = new JSONObject(input);
			user.setId(root.getString("_id"));
			user.setPhone_number(root.getString(KEY_PHONE_NUMBER));
			String name = CommonUtil.getSharedInstance().getContactName(user.getPhone_number());
			user.setContact_name(name);
		} catch(JSONException e){
			e.printStackTrace();
		}
		
		return user;
	}
}
