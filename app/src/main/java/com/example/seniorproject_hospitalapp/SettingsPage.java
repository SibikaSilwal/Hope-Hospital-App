package com.example.seniorproject_hospitalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SettingsPage extends AppCompatActivity {
    private Toolbar m_mainToolBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    DocumentReference docReference;
    ArrayList<Map<String, Object>> m_appInfo = new ArrayList<Map<String, Object>>();
    RecyclerView m_myApptsRecView;
    LinearLayout m_appointmentsLayout;
    AppointmentManagerPatientAdapter m_adapter;
    String m_UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);
        SetupUI();
        setSupportActionBar(m_mainToolBar);
        GetAppointmentInfo();
        System.out.println("appt infot: "+m_appInfo);
        m_myApptsRecView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void GetAppointmentInfo(){
        docReference.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@com.google.firebase.database.annotations.Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if(snapshot.getData().get("AppointmentsInfo")!=null){
                    m_appInfo = (ArrayList<Map<String, Object>>) snapshot.getData().get("AppointmentsInfo");
                    Collections.reverse(m_appInfo);
                    System.out.println("appt info2: "+m_appInfo);
                    m_adapter = new AppointmentManagerPatientAdapter(m_appInfo, SettingsPage.this);
                    m_myApptsRecView.setAdapter(m_adapter);
                    m_adapter.notifyDataSetChanged();
                    System.out.println("adapter set");
                }
            }
        });
    }

    public void SetMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        }
    }

    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("");
        m_myApptsRecView = findViewById(R.id.recviewApptPatients);
        m_appointmentsLayout = findViewById(R.id.upcomingapptLinearLayout);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        if(getIntent().getStringExtra("patientID")!=null){
            m_UserID=getIntent().getStringExtra("patientID");
        }else{
            m_UserID = fAuth.getUid();
        }
        docReference = fStore.collection("users").document(m_UserID);
    }


}