package com.example.seniorproject_hospitalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

public class SettingsPage extends GlobalMenuActivity {
    private Toolbar m_mainToolBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    DocumentReference docReference;

    TextView m_myWardsTxtView, m_myAppoinment;
    String m_myWards=" ", m_UserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);
        SetupUI();
        setSupportActionBar(m_mainToolBar);
        GetWardList();
        GetAppointmentInfo();

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
                Map<String, String> appInfo =  (Map<String, String>) snapshot.getData().get("AppointmentInfo");

                if(appInfo!= null){
                    String DocName = appInfo.get("Doctor");
                    String Day =appInfo.get("Day");
                    String Time = appInfo.get("Time");
                    System.out.println("appinfo: " +appInfo);
                    String apptinfo = "Doctor: " + DocName +"\n" + "Day: " + Day + "\n" + "Time: " + Time;
                    m_myAppoinment.setText(apptinfo);
                }
            }

        });
    }

    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("Hope Hospital App");
        m_myWardsTxtView = findViewById(R.id.t_yourwards);
        m_myAppoinment = findViewById(R.id.t_apptInfo);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        m_UserID = fAuth.getUid();
        docReference = fStore.collection("users").document(m_UserID);

    }


}