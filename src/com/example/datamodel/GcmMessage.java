package com.example.datamodel;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class GcmMessage implements Parcelable{

	enum GcmMessageType {
		TYPE_RIDE_ACCEPTED,
		TYPE_RIDE_DECLINED,
		TYPE_RIDE_REQUESTED,
		TYPE_RIDE_DELETED
	}
	private String TAG = "GCM MESSAGE";
	private String title;	
	private GcmMessageType msgType;
	private String message;
	private String offeredUserId = "";
	private String userUserId = "";
	private String rideId = "";
	
	public String getOfferedUserId() {
		return offeredUserId;
	}

	public void setOfferedUserId(String offeredUserId) {
		this.offeredUserId = offeredUserId;
	}

	public String getUserUserId() {
		return userUserId;
	}

	public void setUserUserId(String userUserId) {
		this.userUserId = userUserId;
	}

	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE_TYPE = "messageType";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_ANOTHER_USER = "anotherUser";
	public static final String KEY_OFFERED_USER = "offeredUser";
	public static final String KEY_RIDE_ID = "rideId";
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public GcmMessageType getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		GcmMessageType temp = null;
		switch(msgType){
			case "accept":
				this.msgType = GcmMessageType.TYPE_RIDE_ACCEPTED;
				break;				
			case "request":
				this.msgType = GcmMessageType.TYPE_RIDE_REQUESTED;
				break;
			case "decline":
				this.msgType = GcmMessageType.TYPE_RIDE_DECLINED;
				break;
			case "delete":
				this.msgType = GcmMessageType.TYPE_RIDE_DELETED;
				break;

		}
	}
	
	public String getRideId() {
		return rideId;
	}

	public void setRideId(String rideId) {
		this.rideId = rideId;
	}

	public static GcmMessage parseGcmMessage(Bundle extras){
		String content = (String)extras.get(GcmMessage.KEY_MESSAGE);
		String title = (String)extras.get(GcmMessage.KEY_TITLE);
		String contentType = (String)extras.get(GcmMessage.KEY_MESSAGE_TYPE);
		String rideId = (String)extras.get(GcmMessage.KEY_RIDE_ID);
		String oUser = (String)extras.get(GcmMessage.KEY_OFFERED_USER);
		String rUser = (String)extras.get(GcmMessage.KEY_ANOTHER_USER);
		
		GcmMessage gcmMessage = new GcmMessage();
		gcmMessage.setMessage(content);
		gcmMessage.setTitle(title);
		gcmMessage.setMsgType(contentType);
		gcmMessage.setOfferedUserId(oUser);
		gcmMessage.setUserUserId(rUser);
		gcmMessage.setRideId(rideId);
		return gcmMessage;
	}

	
	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<GcmMessage> CREATOR = new Parcelable.Creator<GcmMessage>() {
        public GcmMessage createFromParcel(Parcel in) {
            return new GcmMessage(in);
        }

        public GcmMessage[] newArray(int size) {
            return new GcmMessage[size];
        }
    };
    
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(message);
		switch(msgType){
			case TYPE_RIDE_ACCEPTED:
				dest.writeInt(0);
				break;
			case TYPE_RIDE_DECLINED:
				dest.writeInt(1);
				break;
			case TYPE_RIDE_REQUESTED:
				dest.writeInt(2);
				break;
			case TYPE_RIDE_DELETED:
				dest.writeInt(3);
				break;
		}
		dest.writeString(offeredUserId);
		dest.writeString(userUserId);
		
		
		
	}
	
	public GcmMessage(){}
	// example constructor that takes a Parcel and gives you an object populated with it's values
    public GcmMessage(Parcel in) {
    	super();
    	title = in.readString();
    	message = in.readString();
    	int rideType = in.readInt();
    	switch(rideType){
    		case 0:
    			msgType = GcmMessageType.TYPE_RIDE_ACCEPTED;
    			break;
    		case 1:
    			msgType = GcmMessageType.TYPE_RIDE_DECLINED;
    			break;
    		case 2:
    			msgType = GcmMessageType.TYPE_RIDE_REQUESTED;
    			break;
    		case 3:
    			msgType = GcmMessageType.TYPE_RIDE_DELETED;
    			break;
    	}
    	offeredUserId = in.readString();
		userUserId = in.readString();
	}

}
