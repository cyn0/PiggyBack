package com.example.utils;

import com.example.testapp.MainActivity;
import com.example.testapp.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

public class Notification {

public static void sendNotification(String title, String msg, Context context,int notifId) {
		
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				new Intent(context, MainActivity.class), 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(title)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg)
				.setSound(soundUri);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(notifId, mBuilder.build());
	}
	
}
