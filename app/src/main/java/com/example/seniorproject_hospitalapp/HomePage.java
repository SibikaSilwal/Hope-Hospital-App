package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.SnapshotMetadata;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.okhttp.internal.DiskLruCache;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the Login Page which allows the existing users to log in the application.
 * This is the first page that loads on opening the app for the first time.
 *
 * This class makes use of Firebase Email and Password based Authentication to sign in existing users
 * and authenticates them.
 * This class takes two user inputs--User Email and User Password, through Android's EditText view.
 * If user is authenticated successfully then, this class navigates user to the Home Page.
 *
 */
public class HomePage extends GlobalMenuActivity {
    private ConstraintLayout m_HomeGridParent;
    private LinearLayout m_LinearLayoutTestResult, m_LinearLayoutYourAppt, m_LinearLayoutScheduleAppt, m_LinearLayoutYourJournal;
    TextView m_fullName, m_Message, m_TodaysItem;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String m_userId, m_userName, m_userEmail, m_userPhone;
    ArrayList<String> m_UserWardList = new ArrayList<>();
    DocumentReference docReference;
    Toolbar m_mainToolBar;
    SettingsPage m_SettingsClassObject = new SettingsPage();
    DoctorProfileAdmin m_DoctorProfileAdminClassObject = new DoctorProfileAdmin();
    private UserClass m_UserObject;

    private float m_pixeltodpConversion = Resources.getSystem().getDisplayMetrics().density;
    private int m_HomeGridBoxMarginLeftRight, m_HomeGridBoxMarginTopBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        SetupUI();
        //toolbar
        setSupportActionBar(m_mainToolBar);
        GetTodaysItem();
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Map<String, Object> deviceToken = new HashMap<>();
                    deviceToken.put("token", token);
                    docReference.update(deviceToken);
                }
            });

    }

    public void TestResult(View a_view){
        startActivity(new Intent(getApplicationContext(), ViewUserDocuments.class));
    }
    public void UserAppts(View a_view){
        startActivity(new Intent(getApplicationContext(), SettingsPage.class));
    }
    public void ScheduleAppt(View a_view){
        Intent intent = new Intent(a_view.getContext(), Appointment.class);
        intent.putExtra("patientID", m_userId);
        intent.putExtra("name", m_userName);
        intent.putExtra("email", m_userEmail);
        intent.putExtra("phone", m_userPhone);
        intent.putExtra("wards", m_UserWardList);
        startActivity(intent);
    }
    public void Journal(View a_view){
        startActivity(new Intent(getApplicationContext(), UserJournalActivity.class));
    }
    public void Logout(View a_view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginPage.class));
        finish();
    }

    private void SetLeftRightMarginForGrids(View a_view){
        System.out.println("margin: "+ m_HomeGridBoxMarginLeftRight + "conversion: "+m_pixeltodpConversion+" width: "+m_HomeGridParent.getWidth());
        m_SettingsClassObject.SetMargins(a_view, m_HomeGridBoxMarginLeftRight,m_HomeGridBoxMarginTopBottom, m_HomeGridBoxMarginLeftRight,m_HomeGridBoxMarginTopBottom );
    }

    private void GetTodaysItem(){
        docReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Calendar calendar = Calendar.getInstance();
                String TodaysDate = m_DoctorProfileAdminClassObject.makeDateString(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.YEAR));
                long timeInMillis = calendar.getTimeInMillis();
                int count = 0;
                if(task.getResult().get("AppointmentsInfo")!=null){
                    ArrayList<Map<String, Object>> userAppt = (ArrayList<Map<String, Object>>) task.getResult().get("AppointmentsInfo");
                    String todaysItem = "";
                    System.out.println("date: "+ TodaysDate);
                    for(int i=userAppt.size()-1; i>=0; i--){
                        if(userAppt.get(i).get("Day").toString().equals(TodaysDate)){
                            if((Long.parseLong(userAppt.get(i).get("reminderTime").toString()))+3600000>timeInMillis){
                                m_TodaysItem.setText(todaysItem+"You have an appointment today from "+userAppt.get(i).get("Time").toString()+
                                        " with Dr. " + userAppt.get(i).get("Doctor").toString());
                                System.out.println("here?? "+userAppt);
                            }else{
                                m_TodaysItem.setText(todaysItem+"You had an appointment today from "+userAppt.get(i).get("Time").toString()+
                                        " with Dr. " + userAppt.get(i).get("Doctor").toString());
                                System.out.println("here?? ");
                            }
                            todaysItem = m_TodaysItem.getText().toString()+"\n";
                        }
                    }
                }
            }
        });
    }
    private String CheckAndReturnInfo(Object a_data){
        if(a_data!=null){
            return a_data.toString();
        }
        return "";
    }
    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("");
        m_fullName = findViewById(R.id.t_userFullName);
        m_TodaysItem = findViewById(R.id.t_todaysItem);
        m_HomeGridParent = findViewById(R.id.HomeGridLayoutParent);
        m_LinearLayoutScheduleAppt = findViewById(R.id.linearLayoutScheduleAppt);
        m_LinearLayoutYourJournal = findViewById(R.id.linearLayoutYourJournal);
        m_LinearLayoutTestResult = findViewById(R.id.linearLayoutYourTestResult);
        m_LinearLayoutYourAppt = findViewById(R.id.LinearLayoutYourAppt);
        m_Message = findViewById(R.id.t_message);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        m_userId = fAuth.getCurrentUser().getUid();
        m_UserObject = new UserClass(m_userId);
        try {
            m_UserObject.join();
            m_userName = m_UserObject.getM_FullName();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        docReference = fStore.collection("users").document(m_userId);

        m_HomeGridParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                m_HomeGridParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);//width is ready
                m_HomeGridBoxMarginLeftRight = (int) ((m_HomeGridParent.getWidth() - 330*m_pixeltodpConversion))/2;
                m_HomeGridBoxMarginTopBottom= (int) (15*m_pixeltodpConversion);
                SetLeftRightMarginForGrids(m_LinearLayoutScheduleAppt);
                SetLeftRightMarginForGrids(m_LinearLayoutYourJournal);
                SetLeftRightMarginForGrids(m_LinearLayoutTestResult);
                SetLeftRightMarginForGrids(m_LinearLayoutYourAppt);
            }
        });


        m_userEmail = m_UserObject.getM_Email();
        m_userPhone = m_UserObject.getM_Phone();
        m_UserWardList = m_UserObject.getM_WardNames();
        m_Message.setText("Hello, \n" + m_userName+" !");
        /*docReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                m_Message.setText("Hello, \n" + task.getResult().get("fName").toString()+" !");
                m_userName = task.getResult().getData().get("fName").toString();
                m_userEmail=CheckAndReturnInfo(task.getResult().get("email"));
                m_userPhone=CheckAndReturnInfo(task.getResult().get("phone"));
                System.out.println("name: "+m_userEmail+m_userPhone+task.getResult().get("email"));
                if(task.getResult().getData().get("WardName")!=null){
                    m_UserWardList = (ArrayList<String>)task.getResult().getData().get("WardName");
                }else{
                    m_UserWardList.add("You are not added to any wards yet to make an appointment.");
                }
            }
        });*/
    }


}

