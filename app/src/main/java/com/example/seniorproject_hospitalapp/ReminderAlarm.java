package com.example.seniorproject_hospitalapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


//class extending the Broadcast Receiver
public class ReminderAlarm extends BroadcastReceiver {
    MyFirebaseMessagingService m_MyFirebaseMessagingService__Object = new MyFirebaseMessagingService();
    private Appointment m_apptObject = new Appointment(); //Appointment class object to use the appointment class method (makeDate).
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    //the method will be fired when the alarm is triggerred
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getStringExtra("ScheduleType").equals("Setting Reminder.")){
            m_MyFirebaseMessagingService__Object.SendNotification(context,intent.getStringExtra("NotificationTitle"),
                    intent.getStringExtra("NotificationContent"));
            System.out.println("Alarm went off!");
        }

        //you can check the log that it is fired
        //Here we are actually not doing anything
        //but you can do any task here that you want to be done at a specific time everyday


        /*Intent intentHome = new Intent(context, HomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 , intentHome,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "fcm_default_channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_home)
                        .setContentTitle(intent.getStringExtra("NotificationTitle"))
                        .setContentText(intent.getStringExtra("NotificationContent"))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 , notificationBuilder.build());*/

        if(intent.getStringExtra("ScheduleType").equals("Cleaning Database.")){
            System.out.println("deleteing??");
            DeleteOldSchedules();
        }


    }
    //to delete the old schedules data
    private void DeleteOldSchedules(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String Yesterdaysdate = m_apptObject.makeDateString(day, month + 1, year);
        System.out.println("anythinggggg>>> " + Yesterdaysdate + " Deleted!");

        CollectionReference db_DOC= fStore.collection("Doctors");
        CollectionReference db_USER= fStore.collection("users");

        //cleaning doctor's collection
        db_DOC.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //System.out.println(document.getId() + " => " + document.getData());
                        ArrayList<Map<String, Object>> YesterdaysAppointments = (ArrayList<Map<String, Object>>) document.get(Yesterdaysdate);
                        if(YesterdaysAppointments!=null){
                            Map<String,Object> updates = new HashMap<>();
                            updates.put(Yesterdaysdate, FieldValue.delete());
                            db_DOC.document(document.getId()).update(updates);
                            System.out.println(document.getId()+" ----------doing");
                        }
                    }
                } else {
                    System.out.println("Error getting documents: "+task.getException());
                }
            }
        });

        //cleaning user's collection
        db_USER.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //System.out.println(document.getId() + " => " + document.getData());

                        ArrayList<Map<String, Object>> Appointments = (ArrayList<Map<String, Object>>) document.get("AppointmentsInfo");
                        int index =0;
                        if(Appointments!=null){
                            for(Map<String, Object> appt : Appointments){
                                if(appt.get("Day").equals(Yesterdaysdate)){
                                    System.out.println(document.getId()+" "+appt+" ----------doing " + index);
                                    db_USER.document(document.getId()).update("AppointmentsInfo", FieldValue.arrayRemove(index))
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    System.out.println("deleted!!!!!!!!!!!!!!!!!");
                                                }
                                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            System.out.println("why? "+e.getMessage());
                                        }
                                    });
                                    index++;
                                }
                            }
                        }
                    }
                } else {
                    System.out.println("Error getting documents: "+task.getException());
                }
            }
        });
    }
}

