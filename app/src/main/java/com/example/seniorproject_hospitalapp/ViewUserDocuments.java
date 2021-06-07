package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class ViewUserDocuments extends GlobalMenuActivity {
    Toolbar m_mainToolBar;
    ArrayList<Map<String, Object>> m_testDocumentsArr = new ArrayList<>();
    String m_userID;
    RecyclerView m_recView;
    UserDocumentAdapter m_adapter;
    FirebaseAuth m_fAuth;
    FirebaseFirestore m_fstore;
    FirebaseStorage storage;
    DocumentReference m_docReference;
    StorageReference listRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_documents);

        SetupUI();
        setSupportActionBar(m_mainToolBar);

        m_recView.setLayoutManager(new LinearLayoutManager(this));
        GetTestDocuments();

    }

    private void GetTestDocuments(){
        m_docReference.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@com.google.firebase.database.annotations.Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                Integer FileCount = Integer.parseInt(snapshot.getData().get("FileCounter").toString()) ;
                for(int i = 1; i<=FileCount; i++){
                    m_testDocumentsArr.add((i-1),(Map<String,Object>) snapshot.getData().get(Integer.toString(i)));
                    //System.out.println("map: "+snapshot.getData().get(Integer.toString(i)));
                }
                //m_testDocumentsArr = (ArrayList<Map<String, Object>>) snapshot.getData().get("AppointmentsInfo");
                //System.out.println("appt info2: "+m_appInfo);
                m_adapter = new UserDocumentAdapter(m_testDocumentsArr);
                m_recView.setAdapter(m_adapter);
                m_adapter.notifyDataSetChanged();
                //System.out.println("document array: " + m_testDocumentsArr);
            }
        });
    }
    private void SetupUI(){
        m_mainToolBar = findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("Hope Hospital App");
        m_recView = findViewById(R.id.rclviewUsersDoc);
        m_fAuth = FirebaseAuth.getInstance();
        m_userID= m_fAuth.getUid();
        storage = FirebaseStorage.getInstance();
        listRef = storage.getReference().child("users/"+m_userID);
        m_fstore = FirebaseFirestore.getInstance();
        m_docReference = m_fstore.collection("Message").document(m_userID);

    }
}