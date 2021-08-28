package com.example.seniorproject_hospitalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

/*
* This class represents the User appointments page for both admin and user interface.
*
* This class extracts the user appointment information from database, passes it to
* its respective adapter class, and displays the information in a recycler view.
*
* */
public class UserAppointmentsPage extends AppCompatActivity {
    private Toolbar m_mainToolBar;
    private ArrayList<Map<String, Object>> m_appInfo = new ArrayList<Map<String, Object>>();
    private RecyclerView m_myApptsRecView;
    private AppointmentManagerPatientAdapter m_adapter;
    private String m_UserID;
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private DocumentReference docReference;

    /**/
    /*

    NAME

            onCreate - initializes UserAppointmentsPage activity..

    SYNOPSIS

            protected void onCreate(Bundle a_savedInstanceState);
                a_savedInstanceState     --> the activity's previously frozen state, if there was one

    DESCRIPTION

            This function initialized the UserAppointmentsPage activity and links
            it to its respective layout resource file i.e. activity_settings_page
            which allows retrieving the Widgets and Views used in the layout to
            perform actions and handle events as required, by setting the events
            listeners.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:24am 02/30/2021

    */
    /**/
    @Override
    protected void onCreate(Bundle a_savedInstanceState) {
        super.onCreate(a_savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        SetupUI();
        setSupportActionBar(m_mainToolBar);

        GetAppointmentInfo();
        m_myApptsRecView.setLayoutManager(new LinearLayoutManager(this));

    }


    /**/
    /*

    NAME

            GetAppointmentInfo - Retrieves user appointment information array from database

    SYNOPSIS

            private void GetAppointmentInfo()

    DESCRIPTION

            This function retrieves the user's appointment information from database, and
            passes the array to the adapter class's object

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 02/30/2021

    */
    /**/
    private void GetAppointmentInfo(){
        docReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                //passing user's information to UserDataModel class
                UserDataModel user = documentSnapshot.toObject(UserDataModel.class);

                //check if user has any appointments
                if(user.getAppointmentsInfo()!=null){

                    //get user appointments by calling the getAppointmentsInfo method is UserDataModel class
                    m_appInfo = user.getAppointmentsInfo();
                    //reverse the array so that the latest appointments show on top of the recycler view
                    Collections.reverse(m_appInfo);
                    //pass the array into the adapter object
                    m_adapter = new AppointmentManagerPatientAdapter(m_appInfo, UserAppointmentsPage.this, m_UserID);
                    //set adapter for the recycler view
                    m_myApptsRecView.setAdapter(m_adapter);
                    //makes sure that the changes are reflected instantly in the recycler view
                    m_adapter.notifyDataSetChanged();

                }
            }
        });
    }

    /**/
    /*

    NAME

            SetMargins - Sets left, right, top, bottom margin for the given view

    SYNOPSIS

            public void SetMargins (View view, int left, int top, int right, int bottom)

    DESCRIPTION

            This function is used to set the margin of a view dynamically. This function is
            used by other classes as needed therefore is a public function.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 02/30/2021

    */
    /**/
    public void SetMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
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
            from the layout :activity_settings_page.xml. Uses android method
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

            7:30pm 01/30/2021

    */
    /**/
    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("");
        m_myApptsRecView = findViewById(R.id.recviewApptPatients);
        if(getIntent().getStringExtra("patientID")!=null){
            m_UserID=getIntent().getStringExtra("patientID");
        }else{
            m_UserID = fAuth.getUid();
        }
        docReference = fStore.collection("users").document(m_UserID);
    }


}