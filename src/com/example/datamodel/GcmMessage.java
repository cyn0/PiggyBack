package com.example.datamodel;

import android.os.Bundle;
import android.util.Log;

public class GcmMessage {

	enum GcmMessageType {
		TYPE_RIDE_ACCEPTED
	}
	private String TAG = "GCM MESSAGE";
	private String title;	
	private GcmMessageType msgType;
	private String message;

	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE_TYPE = "messageType";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_ACCEPTED_USER = "acceptedUser";
	public static final String KEY_OFFERED_USER = "offeredUser";
	
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
		if(msgType.equals("accept")){
			this.msgType = GcmMessageType.TYPE_RIDE_ACCEPTED;
		}
	}
	
	public static GcmMessage parseGcmMessage(Bundle extras){
		String content = (String)extras.get(GcmMessage.KEY_MESSAGE);
		String title = (String)extras.get(GcmMessage.KEY_TITLE);
		String contentType = (String)extras.get(GcmMessage.KEY_MESSAGE_TYPE);
		
		GcmMessage gcmMessage = new GcmMessage();
		gcmMessage.setMessage(content);
		gcmMessage.setTitle(title);
		gcmMessage.setMsgType(contentType);
		return gcmMessage;
	}

	
}
