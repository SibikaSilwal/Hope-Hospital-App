package com.example.seniorproject_hospitalapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


/*
* This is ReminderAlarm class which extends the BroadcastReceiver class. Android apps can
* send or receive broadcast messages from the Android system and other Android apps.
* These broadcasts are sent when an event of interest occurs. In this case, when
* ReminderAlarm intent is called with ScheduleType, "Setting Reminder." This intent then
* calls SendNotification function from MyFirebaseMessagingService class, which sends
* notification to user's device with the given message and title.
*
* */
public class ReminderAlarm extends BroadcastReceiver {

    private MyFirebaseMessagingService m_MyFirebaseMessagingService__Object = new MyFirebaseMessagingService();

    /**/
    /*

    NAME

            onReceive - calls the desired functions when a broadcast is received

    SYNOPSIS

            public void onReceive(Context context, Intent intent)
                context     --> The Context in which the receiver is running.
                intent      --> The Intent being received.

    DESCRIPTION

            The system package manager registers the Broadcast receiver when the app is
            installed. The receiver then becomes a separate entry point into the app which
            means that the system can start the app and deliver the broadcast if the app is not
            currently running. This method is called when the BroadcastReceiver is receiving an
            Intent broadcast.
            Src: https://developer.android.com/reference/android/content/BroadcastReceiver#onReceive

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:24pm 05/30/2021

    */
    /**/
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getStringExtra("ScheduleType").equals("Setting Reminder.")){
            m_MyFirebaseMessagingService__Object.SendNotification(context,intent.getStringExtra("NotificationTitle"),
                    intent.getStringExtra("NotificationContent"));
        }

    }

}

