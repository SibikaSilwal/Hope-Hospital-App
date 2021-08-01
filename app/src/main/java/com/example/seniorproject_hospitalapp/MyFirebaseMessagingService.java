package com.example.seniorproject_hospitalapp;

import android.app.AlarmManager;
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
//import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
//import com.google.api.client.googleapis.notifications;
import org.json.JSONObject;

import android.os.Handler;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    //private NotificationUtils notificationUtils;
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        System.out.println("Refreshed token:"+token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData().get("title").equals("New Appointment Set.")){
            System.out.println("Caalled notification???");
            long reminder = Long.parseLong(remoteMessage.getData().get("body"));
            // Creating date format
            DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
            // Creating date from milliseconds
            // using Date() constructor
            //Date alarmtime = new Date(reminder);
            Date result = new Date(reminder + 3600000);
            System.out.println("alarm will go off at: "+ simple.format(reminder));
            // Formatting Date and passing in the function
            setAlarm(Long.parseLong(remoteMessage.getData().get("body")) ,simple.format(result)); //add try catch here
        }else{
            SendNotification(this, remoteMessage.getData().get("title"),remoteMessage.getData().get("body") );
        }

    }

    public void SendNotification(Context a_context, String a_msgTitle, String a_msgBody) {
        Intent intent = new Intent(a_context, HomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(a_context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "fcm_default_channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(a_context, channelId)
                        .setSmallIcon(R.drawable.toolbar_logo2)
                        .setContentTitle(a_msgTitle)
                        .setContentText(a_msgBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        //NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(a_context);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void setAlarm(long a_time, String a_AppointmentTime) {
        //getting the alarm manager
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //creating a new intent specifying the broadcast receiver
        Intent i = new Intent(this, ReminderAlarm.class);
        i.putExtra("ScheduleType", "Setting Reminder.");
        i.putExtra("NotificationTitle", "Reminder: You have an appointment today!");
        i.putExtra("NotificationContent", "Your appointment for, " + a_AppointmentTime + "\nis in an hour.");
        //creating a pending intent using the intent
        int uniqueRequestCode = (int)System.currentTimeMillis();
        PendingIntent pi = PendingIntent.getBroadcast(this, uniqueRequestCode, i, 0);


        /*Need to make sure that the request code in pi is different to allow multiple alarms to be scheduled...*/
        //setting the repeating alarm that will be fired every day
        am.setExact(AlarmManager.RTC_WAKEUP, a_time, pi);
        //am.setRepeating(AlarmManager.RTC_WAKEUP, a_time, AlarmManager.INTERVAL_DAY, pi);

    }

}
