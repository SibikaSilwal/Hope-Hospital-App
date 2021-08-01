package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SearchDoctorsAdmin extends AppCompatActivity {
    RecyclerView m_DocRecView;
    DoctorRecyclerViewAdapter m_adapter;
    private TextView m_AddNewDoc;
    EditText m_docID, m_docName;
    ConstraintLayout m_addNewDocLayout;
    Button m_AddDoc;
    Toolbar m_mainToolBar;
    String m_userID;
    DocumentReference m_docRef;

    FirebaseFirestore fStore;
    private SettingsPage m_settingsPageObj = new SettingsPage();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_doctors_admin);
        SetupUI();
        setSupportActionBar(m_mainToolBar);

        m_docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //identify if user is patient or admin
                if (documentSnapshot.getString("isAdmin") != null) {
                    m_AddNewDoc.setVisibility(View.VISIBLE);
                }else{
                    m_settingsPageObj.SetMargins(m_DocRecView, 0, 145,0,0);
                    m_addNewDocLayout.setVisibility(View.GONE);
                }
            }
        });

        m_DocRecView.setLayoutManager(new LinearLayoutManager(this));

        FirestoreRecyclerOptions<DoctorDataModel> options =
                new FirestoreRecyclerOptions.Builder<DoctorDataModel>()
                        .setQuery(fStore.collection("Doctors").orderBy("DocName"), DoctorDataModel.class)
                        .build();

        m_adapter = new DoctorRecyclerViewAdapter(options);
        m_DocRecView.setAdapter(m_adapter);

        m_AddDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStore.collection("Doctors").document(m_docID.getText().toString()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.getResult().exists()){
                                    System.out.println("Doctor already exists");
                                    Toast.makeText(getApplicationContext(), "The given id already exists. Please make sure you are giving the correct Doctor id.", Toast.LENGTH_LONG);
                                    return;
                                }
                                else{
                                    Map<String, Object> docData = new HashMap<>();
                                    docData.put("DocName", m_docName.getText().toString());
                                    docData.put("DocID", m_docID.getText().toString());
                                    fStore.collection("Doctors").document(m_docID.getText().toString())
                                            .set(docData);
                                }
                            }
                        });
                m_docName.setText("");
                m_docID.setText("");
            }
        });
    }

    /*Indicating Adapter to start and stop listening*/
    @Override
    protected void onStart() {
        super.onStart();
        m_adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        m_adapter.stopListening();
    }

    /*Search Menu*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ProcessSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ProcessSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void ProcessSearch(String a_UserName)
    {
        FirestoreRecyclerOptions<DoctorDataModel> options =
                new FirestoreRecyclerOptions.Builder<DoctorDataModel>()
                        .setQuery(fStore
                                        .collection("Doctors")
                                        .orderBy("DocName").startAt(a_UserName)
                                        .endAt(a_UserName+"\uf8ff")
                                , DoctorDataModel.class)
                        .build();
        m_adapter = new DoctorRecyclerViewAdapter(options);
        m_adapter.startListening();
        m_DocRecView.setAdapter(m_adapter);
    }
    public void AddNewDoctor(View a_view){
        if(m_addNewDocLayout.getVisibility()==View.GONE){
            m_addNewDocLayout.setVisibility(View.VISIBLE);
        }else{
            m_addNewDocLayout.setVisibility(View.GONE);
        }
        System.out.println("Calling??????????????visible|gone");
    }
    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("Hope Hospital App");
        m_AddDoc = findViewById(R.id.b_AddDoctor);
        m_docID = findViewById(R.id.e_DocID);
        m_docName = findViewById(R.id.e_DocName);
        m_AddNewDoc = findViewById(R.id.t_addNewDoc);
        m_addNewDocLayout = findViewById(R.id.addNewDocLayout);
        m_DocRecView = findViewById(R.id.recyclerViewDoc);
        fStore = FirebaseFirestore.getInstance();
        m_userID = FirebaseAuth.getInstance().getUid();
        m_docRef = fStore.collection("Admin").document(m_userID);
    }
}