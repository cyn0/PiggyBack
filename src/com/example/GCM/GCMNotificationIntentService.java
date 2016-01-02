package com.example.GCM;


import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.datamodel.Message;
import com.example.utils.Constants;
import com.example.utils.Notification;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMNotificationIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCMNotificationIntentService";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
//				Notification.sendNotification("Notification","Send error: " + extras.toString(), this, 1);
				Log.e(TAG, "send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
//				Notification.sendNotification("Notification","Deleted messages on server: "	+ extras.toString(), this, 1);
				Log.e(TAG, "Deleted messaged on server");
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				handleGCMMessage(extras);
				Log.d(TAG, "Received: " + extras.toString());
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);

		//mlisteners.onGCMMsgReceive();
	}
	
	 
	private void handleGCMMessage(Bundle extras){
		Message gcmMessage = Message.parseGcmMessage(extras);
		Log.d("immediately after parsing", gcmMessage.toString());
//		if(getNotification)
		
		Notification.sendNotification(gcmMessage, this, NOTIFICATION_ID);
		
//		Intent intent = new Intent(Constants.UPDATE_LIST_VIEW);
//	    intent.putExtra(Constants.GCM_MSG_OBJECT, gcmMessage);
//	    //send broadcast
//	    sendBroadcast(intent);
	}
	
	
	
}
