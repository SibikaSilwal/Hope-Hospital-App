package com.example.seniorproject_hospitalapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
//import com.google.api.client.googleapis.notifications;
import org.json.JSONObject;

import android.os.Handler;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    //private NotificationUtils notificationUtils;
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        System.out.println("Refreshed token:"+token);
    }

    /*If the notification message is received while the app is running on the foreground, it is shown as a toast message.*/
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        System.out.println("working fine here?");
        /*Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                System.out.println("working fine here2?");
                Notification notification = new NotificationCompat.Builder(getApplicationContext(), "123")
                        .setContentTitle(remoteMessage.getData().get("title"))
                        .setContentText(remoteMessage.getData().get("body"))
                        .setSmallIcon(R.drawable.ic_home)
                        .build();
                NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
                manager.notify(123, notification);
                System.out.println("message: "+remoteMessage.getData().get("title"));
            }
        });*/
        sendNotification(remoteMessage.getData().get("title"),remoteMessage.getData().get("body") );


        /*
        Handler handler = new Handler(Looper.getMainLooper());
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            remoteMessage.getNotification();
            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(),remoteMessage.getNotification().getTitle()+"\n"+remoteMessage.getNotification().getBody(),Toast.LENGTH_LONG).show();
                }
            });
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                //handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }*/
    }

    private void sendNotification( String a_msgTitle, String a_msgBody) {
        Intent intent = new Intent(this, HomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "fcm_default_channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_home)
                        .setContentTitle(a_msgTitle)
                        .setContentText(a_msgBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
