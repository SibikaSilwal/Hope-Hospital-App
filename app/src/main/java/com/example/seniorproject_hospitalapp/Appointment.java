package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.skyhope.eventcalenderlibrary.CalenderEvent;
import com.skyhope.eventcalenderlibrary.listener.CalenderDayClickListener;
import com.skyhope.eventcalenderlibrary.model.DayContainerModel;
import com.skyhope.eventcalenderlibrary.model.Event;

import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Appointment extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Toolbar m_mainToolBar;
    Spinner m_docNameSpinner;
    TextView m_PatientName, m_PatientEmail, m_PatientPhone, m_apptDate;
    RecyclerView m_AppointmentTimeRecView;
    DocAvailabilityAdapter m_adapter;

    private DatePickerDialog m_datePickerDialog;
    ArrayList<String> m_wards = new ArrayList<>();
    ArrayList<String> m_DocIDs = new ArrayList<>();
    ArrayList<String> m_DocName = new ArrayList<>();
    ArrayList<Map<String, Object>> m_DocAvail = new ArrayList<>();
    ArrayAdapter<String> m_DocNameArrAdapter;

    //DOC ID AND NAME MAP
    Map<String, String> m_docIDdocNameMap = new HashMap<>();
    private String m_userID, m_selectDate, m_selectDocID;
    //FirebaseAuth fauth;
    //avails for day: !!!!![{isAvailable=true, StartMinute=52, StartHour=16, AppointmentID=0, EndHour=17, Time=16:52-17:52, EndMinute=52},
    // {isAvailable=true, StartMinute=52, StartHour=18, AppointmentID=0, EndHour=19, Time=18:52-19:52, EndMinute=52}]
    private void GetDocAvail(String a_docID){
        String daysofWeek[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        DocumentReference docref = fstore.collection("Doctors").document(a_docID);
        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                m_DocAvail.clear();
                //for(String day : daysofWeek){
                ArrayList<Map<String, Object>> availsForDay = (ArrayList<Map<String, Object>>) task.getResult().get(m_selectDate);
                int index = 0;
                if(availsForDay!= null){
                    for (Map<String, Object> avail : availsForDay) {
                        if((boolean)avail.get("isAvailable")){
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
                }else{}
                //}
                Map<String, Object> DocandUserID = new HashMap<>();
                DocandUserID.put("docID", a_docID);
                DocandUserID.put("userID", m_userID);
                DocandUserID.put("Doctor", task.getResult().get("DocName").toString());

                m_DocAvail.add(DocandUserID);

                //if(available!=null){m_DocAvail.add(available);}
                //System.out.println(a_docID + " AVailability: "+ m_DocAvail);
                m_AppointmentTimeRecView.setAdapter(new DocAvailabilityAdapter(m_DocAvail, Appointment.this));
            }
        });
    }
    FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        SetupUI();
        Thread t1 = new Thread(new Runnable() {
            public void run()
            {
                CreateDocNameDocIdMap();
            }});
        t1.start();

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
        m_adapter = new DocAvailabilityAdapter(m_DocAvail, Appointment.this);

        m_AppointmentTimeRecView.setAdapter(m_adapter);

    }


    private void GetDoctorsforWard(ArrayList<String> a_Wardlist) {
        for (String ward : a_Wardlist) {
            DocumentReference docref = fstore.collection("Wards").document(ward);
            docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    List<String> DocIds = (List<String>) task.getResult().get("WardDoctorID");
                    if (DocIds != null) {
                        for (String DocId : DocIds) {
                            if (!m_DocIDs.contains(DocId)) {
                                m_DocIDs.add(DocId);
                                DocumentReference docref = fstore.collection("Doctors").document(DocId);
                                docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        //m_DocIDs.add(DocId);
                                        m_DocName.add(task.getResult().get("DocName").toString());
                                        System.out.println("Docid: "+ DocId + "DocName: "+task.getResult().get("DocName").toString());
                                    }
                                });
                            }
                        }
                    }
                    System.out.println("Docid: "+ m_DocIDs + "DocName: "+m_DocName);
                }
            });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getItemAtPosition(position).equals("Select Doctor.")){
            //do nothing
        }else{
            m_selectDocID = m_docIDdocNameMap.get(m_DocName.get(position));
            System.out.println("dateeeeeeeee"+m_selectDate);
            if(m_selectDate!=null){
                GetDocAvail(m_selectDocID);
            }
            //
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void CreateDocNameDocIdMap(){
        CollectionReference docref = fstore.collection("Doctors");
        docref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        m_docIDdocNameMap.put(document.get("DocName").toString(),document.get("DocID").toString());
                    }
                }
                System.out.println("aLL DOCTORS: "+ m_docIDdocNameMap);
            }
        });
    }

    public void InitDatePicker()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String Todaysdate = makeDateString(day,month+1, year);
        m_apptDate.setText(Todaysdate);
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                //String date = makeDateString(dayOfMonth,month, year);
                m_selectDate = makeDateString(dayOfMonth,month, year);
                System.out.println("being called? " + m_selectDate);
                m_apptDate.setText(m_selectDate);
                System.out.println("doc id here: "+ m_selectDocID);
                GetDocAvail(m_selectDocID);
            }
        };
        int style = AlertDialog.THEME_HOLO_LIGHT;

        m_datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        Calendar calendar = Calendar.getInstance();
        m_datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis()- 10000); //-10000 because DatePicker dialog could not set a minimum date of the exact current time
        calendar.add(Calendar.DATE, 14);
        m_datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

    }
    public void OpenDatePicker(View view)
    {
        m_datePickerDialog.show();
    }

    public String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }
    private void SetupUI(){
        m_mainToolBar = findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("Hope Hospital App");
        m_docNameSpinner = findViewById(R.id.spinnerdocName);
        m_PatientName = findViewById(R.id.t_patientName);
        m_PatientEmail = findViewById(R.id.t_patientEmail);
        m_PatientPhone = findViewById(R.id.t_patientPhone);
        m_apptDate = findViewById(R.id.t_apptDate);
        m_AppointmentTimeRecView = findViewById(R.id.recViewAvailability);
        m_wards = getIntent().getStringArrayListExtra("wards");
        m_userID = getIntent().getStringExtra("patientID");
        fstore = FirebaseFirestore.getInstance();

        //set text for Patients info
        m_PatientName.setText(getIntent().getStringExtra("name"));
        m_PatientEmail.setText(getIntent().getStringExtra("email"));
        m_PatientPhone.setText(getIntent().getStringExtra("phone"));
        System.out.println("info?: "+getIntent().getStringExtra("email")+" "+getIntent().getStringExtra("phone"));
    }
}