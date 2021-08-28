package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
//import java.util.Collection;
//import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* This class represents the Appointment page in Admin and User interface.
* This class sets all the necessary function and views for users and/or
* admin to make an appointment for a patient with the Doctors only in
* their wards
*
*/
public class Appointment extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Toolbar m_mainToolBar;
    private Spinner m_docNameSpinner;
    private TextView m_PatientName, m_PatientEmail, m_PatientPhone, m_apptDate;
    private RecyclerView m_AppointmentTimeRecView;
    private DocAvailabilityAdapter m_adapter;

    private DatePickerDialog m_datePickerDialog;
    private ArrayList<String> m_wards = new ArrayList<>();
    private ArrayList<String> m_DocIDs = new ArrayList<>();
    private ArrayList<String> m_DocName = new ArrayList<>();
    private ArrayList<Map<String, Object>> m_DocAvail = new ArrayList<>();
    private ArrayAdapter<String> m_DocNameArrAdapter;

    //DOC ID AND NAME MAP
    private Map<String, String> m_docIDdocNameMap = new HashMap<>();
    private String m_userID, m_selectDate, m_selectDocID;

    private FirebaseFirestore m_fStore = FirebaseFirestore.getInstance();

    /**/
    /*

    NAME

            onCreate - initializes Appointment activity..

    SYNOPSIS

            protected void onCreate(Bundle a_savedInstanceState);
                a_savedInstanceState     --> the activity's previously frozen state, if there was one

    DESCRIPTION

            This function initialized the Appointment activity and links
            it to its respective layout resource file i.e. activity_appointment
            which allows retrieving the Widgets and Views used in the layout to
            perform actions and handle events as required, by setting the events
            listeners.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:24pm 03/10/2021

    */
    /**/
    @Override
    protected void onCreate(Bundle a_savedInstanceState) {
        super.onCreate(a_savedInstanceState);
        setContentView(R.layout.activity_appointment);

        SetupUI();
        CreateDocNameDocIdMap();
        setSupportActionBar(m_mainToolBar);

        InitDatePicker();

        m_DocName.add("Select Doctor.");
        GetDoctorsforWard(m_wards);

        m_DocNameArrAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, m_DocName);
        m_docNameSpinner.setAdapter(m_DocNameArrAdapter);



        m_docNameSpinner.setOnItemSelectedListener(this);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        m_AppointmentTimeRecView.setLayoutManager(layoutManager);
    }

    /**/
    /*

    NAME

            GetDocAvail - initializes all UI components

    SYNOPSIS

            private void GetDocAvail(String a_docID)

    DESCRIPTION

            This function gets all the available time slots for the selected
            doctor, creates an array of the maps of the available times slots,
            and sets the adapter for availability recycler view for the page

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021

    */
    /**/
    private void GetDocAvail(String a_docID){
        DocumentReference docref = m_fStore.collection("Doctors").document(a_docID);
        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                m_DocAvail.clear();
                //index keeps track of the exact selected availability in the recycler view used in this class
                int index = 0;
                //get doctors timeSlots for the given date
                ArrayList<Map<String, Object>> timeSlots = (ArrayList<Map<String, Object>>) task.getResult().get(m_selectDate);

                //check if doctor has any timeSlots for the date
                if(timeSlots!= null){

                    //for all the timeSlots for the date, get the available timeSlots
                    for (Map<String, Object> avail : timeSlots) {

                        if((boolean)avail.get("isAvailable")){

                            //create a map that stores all the necessary information about the available timeSlot
                            Map<String, Object> available = new HashMap<>();
                            String time = (String) avail.get("Time");
                            Long reminder = (long) avail.get("ReminderTime");
                            available.put("day", m_selectDate);
                            available.put("time", time);
                            available.put("index", index);
                            available.put("reminderTime", reminder);
                            m_DocAvail.add(available);

                        }
                        index++;

                    }
                }//else{}
                //}

                //Adds a Doctor and use ID map at the end of the Availability array map being sent to the adapter class
                Map<String, Object> DocandUserID = new HashMap<>();
                DocandUserID.put("docID", a_docID);
                DocandUserID.put("userID", m_userID);
                DocandUserID.put("Doctor", task.getResult().get("DocName").toString());
                m_DocAvail.add(DocandUserID);

                //Sets adapter for the appointment times recycler view
                m_AppointmentTimeRecView.setAdapter(new DocAvailabilityAdapter(m_DocAvail, Appointment.this));
            }
        });
    }

    /**/
    /*

    NAME

            GetDoctorsforWard - initializes all UI components

    SYNOPSIS

            private void GetDoctorsforWard(ArrayList<String> a_Wardlist)
                a_Wardlist   --> list of wards to get the doctors serving in

    DESCRIPTION

            This function populates the global arrays m_DocIds and m_DocName with
            the unique Doctor's serving in each ward from the wardlist provided.
            The function makes sure that a doctor is not repeatedly added in the
            list of doctors ids and name.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021

    */
    /**/
    private void GetDoctorsforWard(ArrayList<String> a_Wardlist) {

        //Gets Doctors name for each ward in the ward list
        for (String ward : a_Wardlist) {

            //gets ids of the doctors serving in the ward
            DocumentReference wardDocumentref = m_fStore.collection("Wards").document(ward);
            wardDocumentref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    //Stores the doctors ids for the ward in a local list
                    List<String> DocIds = (List<String>) task.getResult().get("WardDoctorID");

                    //checks if the list is empty i.e. checks if at least one doctor serves in the ward
                    if (DocIds != null) {

                        //for each doctor that serves in the ward, adds that doctor to the m_DoctorName array
                        for (String DocId : DocIds) {

                            //checks if the doctor was already in the list because the doctor might also be serving in other wards
                            if (!m_DocIDs.contains(DocId)) {

                                m_DocIDs.add(DocId);

                                //Doctor's document reference to get the Doctor's name from their ids.
                                DocumentReference docref = m_fStore.collection("Doctors").document(DocId);
                                docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        m_DocName.add(task.getResult().get("DocName").toString());
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
    }

    /**/
    /*

    NAME

            onItemSelected - is invoked when an item in Doctor's name dropdown view has been selected.

    SYNOPSIS

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                parent   --> The AdapterView where the selection happened
                view     --> The view within the AdapterView that was clicked
                position --> The position of the view in the adapter
                id       --> The row id of the item that is selected

    DESCRIPTION

            This function is called when a user selects a Doctor from the Doctor's names
            dropdown to make an appointment. If the placeholder text "Select Doctor" is
            selected nothing happens. Else, the selected doctor's id is stored in the
            m_selectDocID variable, and GetDocAvail function is called if the date has
            already been selected as well.

            Synopsis Src: Android Documentation
           (https://developer.android.com/reference/android/widget/AdapterView.OnItemSelectedListener)


    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021

    */
    /**/
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getItemAtPosition(position).equals("Select Doctor.")){
            //do nothing
        }else{
            //stores the Selected Doctor's ids in m_selectDocID from m_docIDdocNameMap map
            m_selectDocID = m_docIDdocNameMap.get(m_DocName.get(position));
            //once the id is received, calls GetDocAvail function if the appointment date is also selected
            if(m_selectDate!=null){
                GetDocAvail(m_selectDocID);
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    /**/
    /*

    NAME

            CreateDocNameDocIdMap - populates m_docIDdocNameMap with Doctor's id : Doctor's name

    SYNOPSIS

            private void CreateDocNameDocIdMap()

    DESCRIPTION

            This function creates  Doctors Id : Doctor's Name map for all the doctors in the
            Firestore Database.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021

    */
    /**/
    private void CreateDocNameDocIdMap(){
        //Doctors CollectionReference from FireStore Database
        CollectionReference docref = m_fStore.collection("Doctors");
        docref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //for each Doctor document in the Doctor's collection stores the Doctor's name and id in m_docIDdocNameMap
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        m_docIDdocNameMap.put(document.get("DocName").toString(),document.get("DocID").toString());
                    }
                }
            }
        });
    }

    /**/
    /*

    NAME

            InitDatePicker - initializes the Date Picker Dialogue box

    SYNOPSIS

            private void InitDatePicker()

    DESCRIPTION

            This function initializes m_datePickerDialog box. This function gets
            the selected doctor's availability for the picked date.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 01/20/2021

    */
    /**/
    public void InitDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;

                //stores the selected date in m_selectDate string, after converting it to a Date String
                m_selectDate = MakeDateString(dayOfMonth,month, year);

                //set text of the Appointment Date view to the selected date
                m_apptDate.setText(m_selectDate);

                //Calls GetDocAvail, if a Doctor is selected too
                if(m_selectDocID!=null) GetDocAvail(m_selectDocID);
            }
        };

        //Date picker Dialogue box theme
        int style = AlertDialog.THEME_HOLO_LIGHT;

        Calendar calendar = Calendar.getInstance();

        //initializing the global date picker dialogue
        m_datePickerDialog = new DatePickerDialog(this, style, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        //setting a minimum date for the Date Picker Dialog to be the current date, (-10000 because a minimum date of the exact current time could not be set)
        m_datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis()- 10000);

        //setting a maximum date for 14 days from the current date
        calendar.add(Calendar.DATE, 14);
        m_datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

    }

    /**/
    /*

    NAME

            OpenDatePicker - Opens the Data picker dialogue box

    SYNOPSIS

            public void OpenDatePicker(View a_view)
                a_view   --> clicked view

    DESCRIPTION

            This function is called when the "Choose Date" text view is clicked.
            This functions opens the dialogue box for m_datePickerDialog

    RETURNS

            Nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021

    */
    /**/
    public void OpenDatePicker(View a_view)
    {
        m_datePickerDialog.show();
    }


    /**/
    /*

    NAME

            makeDateString - Returns Date String as "Month_Name Day Year"

    SYNOPSIS

            public String makeDateString(int a_day, int a_month, int a_year)
                a_day   --> day of the date
                a_month --> month of the date
                a_year  --> year of the date

    DESCRIPTION

            This function returns a Date String as "Month_Name Day Year" for
            the given month, day, and year.

    RETURNS

            Date String

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021
    */
    /**/

    public String MakeDateString(int a_day, int a_month, int a_year)
    {
        return GetMonthFormat(a_month) + " " + a_day + " " + a_year;
    }

    /**/
    /*

    NAME

            GetMonthFormat - returns the Month's name

    SYNOPSIS

            private String GetMonthFormat(int a_month)
                a_month  --> Selected months's in integer form

    DESCRIPTION

            This function returns the month for given month integer

    RETURNS

            Month Name

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021

    */
    /**/

    private String GetMonthFormat(int a_month)
    {
        if(a_month == 1)
            return "JAN";
        if(a_month == 2)
            return "FEB";
        if(a_month == 3)
            return "MAR";
        if(a_month == 4)
            return "APR";
        if(a_month == 5)
            return "MAY";
        if(a_month == 6)
            return "JUN";
        if(a_month == 7)
            return "JUL";
        if(a_month == 8)
            return "AUG";
        if(a_month == 9)
            return "SEP";
        if(a_month == 10)
            return "OCT";
        if(a_month == 11)
            return "NOV";
        if(a_month == 12)
            return "DEC";

        //default
        return "JAN";
    }

    /**/
    /*

    NAME

            SetupUI - initializes all UI components

    SYNOPSIS

            private void SetupUI()

    DESCRIPTION

            This function initializes all UI components to their respective Views
            from the layout :activity_appointment.xml. Uses android method
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

            7:30pm 03/10/2021

    */
    /**/
    private void SetupUI(){
        m_mainToolBar = findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("");
        m_docNameSpinner = findViewById(R.id.spinnerdocName);
        m_PatientName = findViewById(R.id.t_patientName);
        m_PatientEmail = findViewById(R.id.t_patientEmail);
        m_PatientPhone = findViewById(R.id.t_patientPhone);
        m_apptDate = findViewById(R.id.t_apptDate);
        m_AppointmentTimeRecView = findViewById(R.id.recViewAvailability);
        m_wards = getIntent().getStringArrayListExtra("wards");
        m_userID = getIntent().getStringExtra("patientID");

        //set text for Patients info
        m_PatientName.setText(getIntent().getStringExtra("name"));
        m_PatientEmail.setText(getIntent().getStringExtra("email"));
        m_PatientPhone.setText(getIntent().getStringExtra("phone"));

    }
}