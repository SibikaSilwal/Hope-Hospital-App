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

/*
* This class extends FirebaseMessagingService and listens to new messages being received
* from Firebase Cloud Messaging. These messages are sent by the trigger cloud function
* which is deployed using the node.js environment and Firebase CLI. It schedules a notification
* or sends the notification immediately to user’s device depending upon the message title it receives.
*
 * */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
    }

    /**/
    /*

    NAME

            onMessageReceived - receives user notification and performs tasks depending on the message


    SYNOPSIS

            public void onMessageReceived(RemoteMessage remoteMessage)
                remoteMessage   --> Remote message that has been received.

    DESCRIPTION

            This function listens for the messages coming from Firebase Cloud Messaging.
            Depending on the title of the message this function either schedules a notification
            or sends the notification by calling the appropriate functions.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:24pm 05/30/2021

    */
    /**/
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData().get("title").equals("New Appointment Set.")){

            //stores the reminder time received from the body of the message to reminder variable
            long reminder = Long.parseLong(remoteMessage.getData().get("body"));

            // Creating date format
            DateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy HH:mm");

            // Creating date from milliseconds, + 3600000 because the reminder time is an hour before the actual appointment
            Date apptTime = new Date(reminder + 3600000);

            // calling the SetNotificationAlarm function with the reminder time and formatted appointment time
            SetNotificationAlarm(Long.parseLong(remoteMessage.getData().get("body")) ,dateFormatter.format(apptTime));
        }else{
            SendNotification(this, remoteMessage.getData().get("title"),remoteMessage.getData().get("body") );
        }

    }

    /**/
    /*

    NAME

            SendNotification - sends notification to user's device


    SYNOPSIS

            public void SendNotification(Context a_context, String a_msgTitle, String a_msgBody)
                a_context   --> notification context
                a_msgTitle  --> notification message title
                a_msgBody   --> notification message body

    DESCRIPTION

            This function constructs a user notification using NotificationCompat.Builder class, and
            notifies the user by sending the notification using NotificationManagerCompat class. The
            notification is sent with its title and body message as provided in the function arguments

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:24pm 05/30/2021

    */
    /**/
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

    /**/
    /*

    NAME

            onReceive - calls the desired functions when a broadcast is received

    SYNOPSIS

            private void SetNotificationAlarm(long a_time, String a_AppointmentTime)
                a_time            --> time in millisecond at which the alarm is to be fired
                a_AppointmentTime --> user's appointment time to be used in notification's body

    DESCRIPTION

            This function makes use of AlarmManager class that schedules a task for the
            received time in millisecond (a_time). The alarm is received by the ReminderAlarm
            class that extends the BroadcastReceiver class. This functions fires a pending intent
            to send notification to users about their upcoming appointment.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:24pm 05/30/2021

    */
    /**/
    private void SetNotificationAlarm(long a_time, String a_AppointmentTime) {
        //getting the alarm manager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //creating a new intent specifying the broadcast receiver
        Intent i = new Intent(this, ReminderAlarm.class);
        i.putExtra("ScheduleType", "Setting Reminder.");
        i.putExtra("NotificationTitle", "Reminder: You have an appointment today!");
        i.putExtra("NotificationContent", "Your appointment for, " + a_AppointmentTime + "\nis in an hour.");

        //Need to make sure that the request code in pi is different to allow multiple alarms to be scheduled.
        int uniqueRequestCode = (int)System.currentTimeMillis();

        //creating a pending intent using the intent
        PendingIntent pi = PendingIntent.getBroadcast(this, uniqueRequestCode, i, 0);

        //setting the alarm that will be fired exactly at the given time
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, a_time, pi);

    }

}
