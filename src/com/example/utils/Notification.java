package com.example.utils;

import com.example.datamodel.Message;
import com.example.testapp.MainActivity;
import com.example.testapp.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class Notification {

public static void sendNotification(Message gcmMessage, Context context, int notifId) {
		
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(notifId);
		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Log.d("From notification", gcmMessage.toString());
		Intent intent = new Intent(context, MainActivity.class);
		intent.putExtra(Constants.NOTIFICATION, true);
		intent.putExtra(Constants.SOME_NAME_FOR_GCM_OBJECT, gcmMessage);
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,	intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(gcmMessage.getTitle())
				.setStyle(new NotificationCompat.BigTextStyle().bigText(gcmMessage.getMessage()))
				.setContentText(gcmMessage.getMessage())
				.setSound(soundUri)
				.setAutoCancel(true);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(notifId, mBuilder.build());
	}
	
}
