package com.example.datamodel;

import java.io.Serializable;

import android.os.Bundle;
import android.util.Log;


public class Message implements Serializable{
	private static final long serialVersionUID = 1989644005305028089L;

	public enum GcmMessageType {
		TYPE_RIDE_ACCEPTED,
		TYPE_RIDE_DECLINED,
		TYPE_RIDE_REQUESTED,
		TYPE_RIDE_REQUEST_CANCELLED,
		TYPE_RIDE_DELETED
	}
	private String TAG = "GCM MESSAGE";
	private String title = "";	
	private GcmMessageType msgType;
	private String message = "";
	private String offeredUserId = "";
	private String userUserId = "";
	private String rideId = "";
	private String dummy = "dummyvalue";
	
	public String getOfferedUserId() {
		return offeredUserId;
	}

	public void setOfferedUserId(String offeredUserId) {
		this.offeredUserId = offeredUserId;
	}

	public String getUserUserId() {
		return this.userUserId;
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

	public String getMsgTypeString(){
		switch (msgType) {
			case TYPE_RIDE_ACCEPTED:
				return "accept";
			case TYPE_RIDE_DECLINED:
				return "decline";
			case TYPE_RIDE_DELETED:
				return "delete";
			case TYPE_RIDE_REQUEST_CANCELLED:
				return "request_cancel";
			case TYPE_RIDE_REQUESTED:
				return "request";
			default:
				int i = 1/0;
				return "";
		}
	}
	public void setMsgType(String msgType) {
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
			case "request_cancel":
				this.msgType = GcmMessageType.TYPE_RIDE_REQUEST_CANCELLED;
				break;
		}
	}
	
	public String getRideId() {
		return rideId;
	}

	public void setRideId(String rideId) {
		this.rideId = rideId;
	}

	public static Message parseGcmMessage(Bundle extras){
		String content = extras.getString(Message.KEY_MESSAGE);
		String title = extras.getString(Message.KEY_TITLE);
		String contentType = extras.getString(Message.KEY_MESSAGE_TYPE);
		String rideId = extras.getString(Message.KEY_RIDE_ID);
		String oUser = extras.getString(Message.KEY_OFFERED_USER, "dummy");
		String rUser = extras.getString(Message.KEY_ANOTHER_USER, "dummy");
		Message gcmMessage = new Message();
		gcmMessage.setMessage(content);
		gcmMessage.setTitle(title);
		gcmMessage.setMsgType(contentType);
		gcmMessage.setOfferedUserId(oUser);
		gcmMessage.setUserUserId(rUser);
		gcmMessage.setRideId(rideId);
		return gcmMessage;
	}
	
	
	
	@Override
	public String toString() {
		return "Message [title=" + title + ", msgType=" + msgType
				+ ", message=" + message + ", offeredUserId=" + offeredUserId
				+ ", userUserId=" + userUserId + ", rideId=" + rideId + ", msgtype=" + getMsgTypeString() + "]";
	}

}
