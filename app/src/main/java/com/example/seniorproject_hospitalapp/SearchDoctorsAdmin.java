package com.example.seniorproject_hospitalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SearchDoctorsAdmin extends AppCompatActivity {
    RecyclerView m_DocRecView;
    DoctorRecyclerViewAdapter m_adapter;
    EditText m_docID, m_docName;
    Button m_AddDoc;
    Toolbar m_mainToolBar;

    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_doctors_admin);
        SetupUI();
        setSupportActionBar(m_mainToolBar);

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
                Map<String, Object> docData = new HashMap<>();
                docData.put("DocName", m_docName.getText().toString());
                docData.put("DocID", m_docID.getText().toString());
                fStore.collection("Doctors").document(m_docID.getText().toString())
                        .set(docData);
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
        //System.out.println("menu??");
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

    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("Hope Hospital App");
        m_AddDoc = findViewById(R.id.b_AddDoctor);
        m_docID = findViewById(R.id.e_DocID);
        m_docName = findViewById(R.id.e_DocName);
        m_DocRecView = findViewById(R.id.recyclerViewDoc);
        fStore = FirebaseFirestore.getInstance();
    }
}