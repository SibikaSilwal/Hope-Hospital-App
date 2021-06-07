package com.example.seniorproject_hospitalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SettingsPage extends GlobalMenuActivity {
    private Toolbar m_mainToolBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    DocumentReference docReference;
    ArrayList<Map<String, Object>> m_appInfo = new ArrayList<Map<String, Object>>();
    TextView m_myWardsTxtView;
    RecyclerView m_myApptsRecView;
    AppointmentManagerPatientAdapter m_adapter;
    String m_myWards=" ", m_UserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);
        SetupUI();
        setSupportActionBar(m_mainToolBar);
        GetWardList();
        GetAppointmentInfo();
        System.out.println("appt infot: "+m_appInfo);
        m_myApptsRecView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void GetWardList(){
        docReference.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@com.google.firebase.database.annotations.Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                List<Object> wardNames = (List<Object>) snapshot.getData().get("WardName");
                System.out.println(wardNames);
                if(wardNames!= null){
                    for( Object wardname: wardNames)
                    {
                        m_myWards = m_myWards.concat("\n"+wardname.toString()) ;
                    }
                    m_myWardsTxtView.setText(m_myWards);
                    m_myWards ="";
                }
            }

        });
    }

    private void GetAppointmentInfo(){
        docReference.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@com.google.firebase.database.annotations.Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                m_appInfo = (ArrayList<Map<String, Object>>) snapshot.getData().get("AppointmentsInfo");
                System.out.println("appt info2: "+m_appInfo);
                m_adapter = new AppointmentManagerPatientAdapter(m_appInfo, SettingsPage.this);
                m_myApptsRecView.setAdapter(m_adapter);
                m_adapter.notifyDataSetChanged();
                System.out.println("adapter set");
            }
        });
    }

    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("Hope Hospital App");
        m_myWardsTxtView = findViewById(R.id.t_yourwards);
        m_myApptsRecView = findViewById(R.id.recviewApptPatients);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        m_UserID = fAuth.getUid();
        docReference = fStore.collection("users").document(m_UserID);
    }


}