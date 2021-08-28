package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/*
 * This class represents the ManageDocAppointment activity which provides an interface
 * for admins to view, and cancel Doctor's appointments for the upcoming 14 days.
 */
public class ManageDocAppointment extends AdminMenuActivity {
    private Toolbar m_mainToolBar;
    private RecyclerView m_recViewManageAppointment;
    private AppointmentManagerAdapter m_adapter;
    private ArrayList<Map<String, Object>> m_DocAppointments = new ArrayList<>();
    private ArrayList<String> m_DateArr = new ArrayList<>();
    private FirebaseFirestore m_fStore = FirebaseFirestore.getInstance();
    private String m_docID;

    //Appointment class object to use the appointment class method in ManageDocAppointment Class.
    private Appointment m_apptObject = new Appointment();

    /**/
    /*

    NAME

            onCreate - initializes ManageDocAppointment activity..

    SYNOPSIS

            protected void onCreate(Bundle a_savedInstanceState);
                a_savedInstanceState     --> the activity's previously frozen state, if there was one

    DESCRIPTION

            This function initialized the ManageDocAppointment activity and links it to its
            respective layout resource file i.e. activity_manage_doc_appointment.xml
            which allows retrieving the Widgets and Views used in the layout to
            perform actions and handle events as required, by setting the events
            listeners.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:20pm 02/17/2021

    */
    /**/
    @Override
    protected void onCreate(Bundle a_savedInstanceState) {
        super.onCreate(a_savedInstanceState);
        setContentView(R.layout.activity_manage_doc_appointment);

        SetupUI();
        setSupportActionBar(m_mainToolBar);

        ConstructDateArr();
        GetDocAppointments(m_docID);


        m_recViewManageAppointment.setLayoutManager(new LinearLayoutManager(this));

    }

    /**/
    /*

    NAME

            GetDocAppointments - gets doctor's appointments for next 14 days

    SYNOPSIS

            private void GetDocAppointments(String a_docID)

    DESCRIPTION

            This function accepts a doctor's id as an argument and gets all the
            appointments for the doctor for next 14 days. The function constructs
            an array of maps that contain the information for each appointment and
            passes the array to AppointmentManagerAdapter class.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 04/20/2021

    */
    /**/
    private void GetDocAppointments(String a_docID){
        DocumentReference docref = m_fStore.collection("Doctors").document(a_docID);

        //Retrieves the given Doctor's document fields from firestore database
        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                m_DocAppointments.clear();

                //loops through the 14 days date to get Doctor's appointments for each day
                for(String day : m_DateArr){

                    //gets the particular day's appointment info if exists
                    ArrayList<Map<String, Object>> apptforDay = (ArrayList<Map<String, Object>>) task.getResult().get(day);
                    int index = 0;

                    //checks if the doctor has any appointment for the day
                    if(apptforDay!= null){

                        //For all the appointments the doctor has for the day, creates a map for each appointment with its information
                        for (Map<String, Object> appt : apptforDay) {

                            if(!(boolean)appt.get("isAvailable")){
                                Map<String, Object> apptInfo = new HashMap<>();
                                apptInfo.put("index", index);
                                apptInfo.put("DocID", a_docID);
                                apptInfo.put("DocName", task.getResult().get("DocName"));
                                apptInfo.put("PatientID", appt.get("AppointmentID"));
                                apptInfo.put("time", appt.get("Time").toString());
                                apptInfo.put("day", day);
                                apptInfo.put("ReminderTime", appt.get("ReminderTime").toString());

                                //adds the appointment map to m_DocAppointments array which is passed to the Adapter class
                                m_DocAppointments.add(apptInfo);
                            }
                            index++;
                        }
                    }
                }
                //sets adapter for the appointment recycler view
                m_adapter = new AppointmentManagerAdapter(m_DocAppointments, ManageDocAppointment.this);
                m_recViewManageAppointment.setAdapter(m_adapter);
                m_adapter.notifyDataSetChanged();
            }
        });
    }

    /**/
    /*

    NAME

            SetupUI - initializes all UI components

    SYNOPSIS

            public void ConstructDateArr()

    DESCRIPTION

            This function Constructs a date array from today to 14 days from today.
            In the application, when a Patient makes an appointment with a doctor
            they are allowed to make appointment for any day between today and 14 days
            from today.Therefore, this function constructs a date array in the same format
            as it is saved in the database to get doc's appointments for the dates.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 04/20/2021

    */
    /**/
    public void ConstructDateArr() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        //creates a date string in the correct format 'Month Date Year'
        String Todaysdate = m_apptObject.MakeDateString(day, month + 1, year);
        m_DateArr.add(Todaysdate);
        for(int i =1; i<14; i++){
            cal.add(Calendar.DATE, 1);
            Todaysdate = m_apptObject.MakeDateString(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
            m_DateArr.add(Todaysdate);
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
            from the layout :activity_manage_doc_appointment.xml. Uses android method
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

            7:30pm 04/20/2021

    */
    /**/
    private void SetupUI() {
        m_mainToolBar = findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("");
        m_recViewManageAppointment = findViewById(R.id.recViewManageAppointment);
        m_docID = getIntent().getStringExtra("docID");
    }
}