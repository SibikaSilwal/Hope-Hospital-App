package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the Home page in Patient / User interface. This class
 * mostly provides navigation option to the users from the activity itself, to
 * the four most important pages in the user interface of the application i.e.
 * View Test Results, View Appointments, Schedule New Appointments, and User
 * Personal Journal.
 * This class also updates user token if it has changed.
 * This class provides a summary of current day's activity to the user, as a text
 * displayed in the bottom of the page.
 */
public class HomePage extends GlobalMenuActivity {
    private Toolbar m_mainToolBar;
    private ConstraintLayout m_HomeGridParent;
    private LinearLayout m_LinearLayoutTestResult, m_LinearLayoutYourAppt, m_LinearLayoutScheduleAppt, m_LinearLayoutYourJournal;
    private TextView m_fullName, m_Message, m_TodaysItem;
    private String m_userName, m_userEmail, m_userPhone;
    private ArrayList<Object> m_UserWardList = new ArrayList<>();
    private ArrayList<Map<String, Object>> m_userAppt = new ArrayList<>();

    private UserDataModel m_userObject;

    //UserAppointmentsPage class object to use the UserAppointmentsPage class methods.
    private UserAppointmentsPage m_UserAppointmentsPageObj = new UserAppointmentsPage();

    //Appointment class object to use the appointment class methods.
    private Appointment m_apptObject = new Appointment();

    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private String m_userId = fAuth.getCurrentUser().getUid();
    private DocumentReference docReference;

    //conversion rate from pixel to device for the device
    private float m_pixeltodpConversion = Resources.getSystem().getDisplayMetrics().density;
    private int m_HomeGridBoxMarginLeftRight, m_HomeGridBoxMarginTopBottom;

    /**/
    /*

    NAME

            onCreate - initializes HomePage activity..

    SYNOPSIS

            protected void onCreate(Bundle a_savedInstanceState);
                a_savedInstanceState     --> the activity's previously frozen state, if there was one

    DESCRIPTION

            This function initialized the HomePage activity and links
            it to its respective layout resource file i.e. activity_home_page
            which allows retrieving the Widgets and Views used in the layout to
            perform actions and handle events as required, by setting the events
            listeners.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:24pm 01/19/2021

    */
    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        
        SetupUI();

        //toolbar
        setSupportActionBar(m_mainToolBar);

        //Gets user's devices token, and updates it in database if it is different from before
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>()
        {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }

                //Update token in database if new
                String token = task.getResult();
                Map<String, Object> deviceToken = new HashMap<>();
                deviceToken.put("token", token);
                docReference.update(deviceToken);
                }
        });

    }

    /**/
    /*

    NAME

            TestResult - navigates user to ViewUserDocuments Activity

    SYNOPSIS

            private void TestResult(View a_view)
                a_view     --> view provided

    DESCRIPTION

            This function navigates user to ViewUserDocuments which displays the list of all
            test results uploaded for the particular user. This function is provided
            in the onClick attribute in xml file for the TestResult Grid in homepage. The function
            gets called when the TestResult view is clicked

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            2:30pm 07/26/2021

    */
    /**/
    public void TestResult(View a_view){
        startActivity(new Intent(getApplicationContext(), ViewUserDocuments.class));
    }

    /**/
    /*

    NAME

            UserAppts - navigates user to UserAppointmentsPage

    SYNOPSIS

            private void UserAppts(View a_view)
                a_view     --> view provided

    DESCRIPTION

            This function navigates user to UserAppointmentsPage. This function is provided
            in the onClick attribute in xml file for the Appointments Grid in homepage. The function
            gets called when the Appointments view is clicked

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            2:30pm 07/26/2021

    */
    /**/
    public void UserAppts(View a_view){
        startActivity(new Intent(getApplicationContext(), UserAppointmentsPage.class));
    }

    /**/
    /*

    NAME

            ScheduleAppt - navigates user to AppointmentActivity

    SYNOPSIS

            private void ScheduleAppt(View a_view)
                a_view     --> view provided

    DESCRIPTION

            This function navigates user to AppointmentActivity. This function is provided
            in the onClick attribute in xml file for the Schedule Appointment Grid in homepage. The function
            gets called when the Schedule Appointment view is clicked. The required parameter are
            passed to the Schedule appointment intent from home intent.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            2:30pm 07/26/2021

    */
    /**/
    public void ScheduleAppt(View a_view){
        Intent intent = new Intent(a_view.getContext(), Appointment.class);
        intent.putExtra("patientID", m_userId);
        intent.putExtra("name", m_userName);
        intent.putExtra("email", m_userEmail);
        intent.putExtra("phone", m_userPhone);
        intent.putExtra("wards", m_UserWardList);
        startActivity(intent);
    }

    /**/
    /*

    NAME

            Journal - navigates user to UserJournalActivity

    SYNOPSIS

            private void Journal(View a_view)
                a_view     --> view provided

    DESCRIPTION

            This function navigates user to UserJournalActivity. This function is provided
            in the onClick attribute in xml file for the Journal Grid in homepage. The function
            gets called when the Journal view is clicked

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            2:30pm 07/26/2021

    */
    /**/
    public void Journal(View a_view){
        startActivity(new Intent(getApplicationContext(), UserJournalActivity.class));
    }

    /**/
    /*

    NAME

            SetLeftRightMarginForGrids - sets the same left and right margin for the provided views

    SYNOPSIS

            private void SetLeftRightMarginForGrids(View a_view)
                a_view     --> view provided to set the margins for

    DESCRIPTION

            This function calls SetMargins function from UserAppointmentsPage class for the provided view.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30am 07/25/2021

    */
    /**/
    private void SetLeftRightMarginForGrids(View a_view){
        m_UserAppointmentsPageObj.SetMargins(a_view,
                                        m_HomeGridBoxMarginLeftRight,
                                        m_HomeGridBoxMarginTopBottom,
                                        m_HomeGridBoxMarginLeftRight,
                                        m_HomeGridBoxMarginTopBottom );
    }

    /**/
    /*

    NAME

            GetTodaysItem - retrieves and displays current day's appointment for patient

    SYNOPSIS

            private void GetTodaysItem()

    DESCRIPTION

            This function retrieves the current day's appointment information from database
            if any and displays it on the homepage as a reminder for the patient about their
            scheduled appointment. After the appointment time has passed, it displays the
            appointment information in past tense, as an earlier activity of the day.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30am 07/25/2021

    */
    /**/
    private void GetTodaysItem(){
        if(m_userAppt!=null){
            Calendar calendar = Calendar.getInstance();
            long timeInMillis = calendar.getTimeInMillis();
            String TodaysDate = m_apptObject
                                .MakeDateString(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.YEAR));
            String todaysItem = "";

            for(int i=m_userAppt.size()-1; i>=0; i--){

                //checks if the appointment dates match the current day's date
                if(m_userAppt.get(i).get("Day").toString().equals(TodaysDate)){

                    //gets exact time for the appointment
                    long apptTime = Long.parseLong(m_userAppt.get(i).get("reminderTime").toString()) + 3600000;

                    //determine whether it was a past appointment today, or a future appointment today
                    if(apptTime > timeInMillis){
                        m_TodaysItem.setText(todaysItem+"+ You have an appointment today from "+m_userAppt.get(i).get("Time").toString()+
                                      " with Dr. " + m_userAppt.get(i).get("Doctor").toString());
                    }else{
                        m_TodaysItem.setText(todaysItem+"+ You had an appointment today from "+m_userAppt.get(i).get("Time").toString()+
                                " with Dr. " + m_userAppt.get(i).get("Doctor").toString());
                    }
                    todaysItem = m_TodaysItem.getText().toString()+"\n";
                }
            }
        }
    }

    /**/
    /*

    NAME

            SetupUI - initializes all UI components

    SYNOPSIS

            private void SetupUI()

    DESCRIPTION

            This function initializes all UI components to their respective Views
            from the layout :activity_home_page.xml. Uses android method
            findViewById that, "finds a view that was identified by the android:id
           XML attribute that was processed in onCreate(Bundle)." Src: Android Documentation
           (https://developer.android.com/reference/android/app/Activity#findViewById(int))
           It also calls all other helper functions used in setting up the UI and getting
           contents for all the Views.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 01/20/2021

    */
    /**/
    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("");
        //m_fullName = findViewById(R.id.t_userFullName);
        m_TodaysItem = findViewById(R.id.t_todaysItem);
        m_HomeGridParent = findViewById(R.id.HomeGridLayoutParent);
        m_LinearLayoutScheduleAppt = findViewById(R.id.linearLayoutScheduleAppt);
        m_LinearLayoutYourJournal = findViewById(R.id.linearLayoutYourJournal);
        m_LinearLayoutTestResult = findViewById(R.id.linearLayoutYourTestResult);
        m_LinearLayoutYourAppt = findViewById(R.id.LinearLayoutYourAppt);
        m_Message = findViewById(R.id.t_message);

        docReference = fStore.collection("users").document(m_userId);

        //Sets appropriate margin according to device size to the four grid layouts used on the homepage
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

        //retrieves necessary information about the user from the database, and initializes the UserObject with the data
        docReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                m_userObject = documentSnapshot.toObject(UserDataModel.class);
                m_userName = m_userObject.getfName();
                m_userEmail = m_userObject.getEmail();
                m_userPhone = m_userObject.getPhone();
                m_Message.setText("Hello, \n" + m_userName+" !");
                m_UserWardList = m_userObject.getWardName();
                m_userAppt = m_userObject.getAppointmentsInfo();
                GetTodaysItem();
            }
        });
    }


}

