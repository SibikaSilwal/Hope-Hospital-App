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
import java.util.HashMap;
import java.util.Map;

public class ManageDocAppointment extends AdminMenuActivity {
    Toolbar m_mainToolBar;
    RecyclerView m_recViewManageAppointment;
    AppointmentManagerAdapter m_adapter;
    ArrayList<Map<String, Object>> m_DocAppointments = new ArrayList<>();
    FirebaseFirestore fStore;
    String m_docID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_doc_appointment);

        SetupUI();
        Thread t1 = new Thread(new Runnable() {
            public void run()
            {
                GetDocAppointmentIDs(m_docID);
            }});
        t1.start();
        //GetDocAppointmentIDs(m_docID);
        setSupportActionBar(m_mainToolBar);
        m_recViewManageAppointment.setLayoutManager(new LinearLayoutManager(this));

    }

    private void GetDocAppointmentIDs(String a_docID){
        String daysofWeek[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        DocumentReference docref = fStore.collection("Doctors").document(a_docID);
        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                m_DocAppointments.clear();
                for(String day : daysofWeek){
                    ArrayList<Map<String, Object>> apptforDay = (ArrayList<Map<String, Object>>) task.getResult().get(day);
                    int index = 0;
                    if(apptforDay!= null){
                        for (Map<String, Object> appt : apptforDay) {
                            if(!(boolean)appt.get("isAvailable")){
                                Map<String, Object> apptInfo = new HashMap<>();
                                apptInfo.put("index", index);
                                apptInfo.put("DocID", a_docID);
                                apptInfo.put("DocName", task.getResult().get("DocName"));
                                apptInfo.put("PatientID", appt.get("AppointmentID"));
                                apptInfo.put("time", appt.get("Time").toString());
                                apptInfo.put("day", day);
                                m_DocAppointments.add(apptInfo);
                            }
                            index++;
                        }
                    }else{}
                }
                m_adapter = new AppointmentManagerAdapter(m_DocAppointments, ManageDocAppointment.this);
                m_recViewManageAppointment.setAdapter(m_adapter);
                m_adapter.notifyDataSetChanged();
            }
        });
    }

    private void SetupUI() {
        m_mainToolBar = findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("Hope Hospital App");
        m_recViewManageAppointment = findViewById(R.id.recViewManageAppointment);
        fStore = FirebaseFirestore.getInstance();
        m_docID = getIntent().getStringExtra("docID");
    }
}