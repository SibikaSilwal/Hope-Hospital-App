package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class Appointment extends AdminMenuActivity implements AdapterView.OnItemSelectedListener {
    Toolbar m_mainToolBar;
    Spinner m_docNameSpinner;
    TextView m_PatientName, m_PatientEmail, m_PatientPhone;
    RecyclerView m_AppointmentTimeRecView;
    DocAvailabilityAdapter m_adapter;

    ArrayList<String> m_wards = new ArrayList<>();
    ArrayList<String> m_DocIDs = new ArrayList<>();
    ArrayList<String> m_DocName = new ArrayList<>();
    ArrayList<Map<String, Object>> m_DocAvail = new ArrayList<>();
    ArrayAdapter<String> m_DocNameArrAdapter;

    //DOC ID AND NAME MAP
    Map<String, String> m_docIDdocNameMap = new HashMap<>();
    String m_userID;
    FirebaseFirestore fstore;
    //FirebaseAuth fauth;
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
    //avails for day: !!!!![{isAvailable=true, StartMinute=52, StartHour=16, AppointmentID=0, EndHour=17, Time=16:52-17:52, EndMinute=52},
    // {isAvailable=true, StartMinute=52, StartHour=18, AppointmentID=0, EndHour=19, Time=18:52-19:52, EndMinute=52}]
    private void GetDocAvail(String a_docID){
        String daysofWeek[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        DocumentReference docref = fstore.collection("Doctors").document(a_docID);
        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                m_DocAvail.clear();
                for(String day : daysofWeek){
                    ArrayList<Map<String, Object>> availsForDay = (ArrayList<Map<String, Object>>) task.getResult().get(day);
                    int index = 0;
                    if(availsForDay!= null){
                        for (Map<String, Object> avail : availsForDay) {
                            if((boolean)avail.get("isAvailable")){
                                Map<String, Object> available = new HashMap<>();
                                String time = (String) avail.get("Time");
                                available.put("day", day);
                                available.put("time", time);
                                available.put("index", index);
                                m_DocAvail.add(available);
                            }
                            index++;
                        }
                    }else{}
                }
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getItemAtPosition(position).equals("Select Doctor.")){
            //do nothing
        }else{
            GetDocAvail(m_docIDdocNameMap.get(m_DocName.get(position)));
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
    private void SetupUI(){
        m_mainToolBar = findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("Hope Hospital App");
        m_docNameSpinner = findViewById(R.id.spinnerdocName);
        m_PatientName = findViewById(R.id.t_patientName);
        m_PatientEmail = findViewById(R.id.t_patientEmail);
        m_PatientPhone = findViewById(R.id.t_patientPhone);
        m_AppointmentTimeRecView = findViewById(R.id.recViewAvailability);
        m_wards = getIntent().getStringArrayListExtra("wards");
        m_userID = getIntent().getStringExtra("patientID");
        fstore = FirebaseFirestore.getInstance();

    }
}