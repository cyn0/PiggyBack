package com.example.GCM;


import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				Notification.sendNotification("Notification","Send error: " + extras.toString(), this, 1);
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				Notification.sendNotification("Notification","Deleted messages on server: "
						+ extras.toString(), this, 1);
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				handleGCMMessage(extras);
				Log.i(TAG, "Received: " + extras.toString());
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
		
		sendBroadcast(new Intent("UniqueMsgs"));
		//mlisteners.onGCMMsgReceive();
	}
	
	 
	private void handleGCMMessage(Bundle extras){
		String content = (String)extras.get("msg");
		String title = (String)extras.get("title");
		String time = (String)extras.get("time");
		String msgtype = (String)extras.get("type");
		//msgtype = msgtype.trim();
		Log.e("feesd",extras.toString());
//		Log.d("shared pref", "" + getSharedPreferences(constant.APP_SETTINGS, MODE_PRIVATE).contains(msgtype));
//		boolean getNotification = getSharedPreferences(constant.APP_SETTINGS, MODE_PRIVATE)
//				.getBoolean(msgtype, true);
//		
//		if(getNotification)
			Notification.sendNotification("Notification", title,this, NOTIFICATION_ID);
	}
	
}
