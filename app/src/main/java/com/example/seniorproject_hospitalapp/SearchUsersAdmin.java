package com.example.seniorproject_hospitalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

public class SearchUsersAdmin extends AppCompatActivity {
    Toolbar m_mainToolBar;
    RecyclerView m_recycleView;
    SearchViewAdapter m_adapter;
    FirebaseFirestore fstore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users_admin);
        //String testArr[] = {"C", "c++", "Java", "ruby","C", "c++", "Java", "ruby","C", "c++", "Java", "ruby"};
        SetupUI();
        setSupportActionBar(m_mainToolBar);
        m_recycleView.setLayoutManager(new LinearLayoutManager(this));

        FirestoreRecyclerOptions<UserDataModel> options =
                new FirestoreRecyclerOptions.Builder<UserDataModel>()
                .setQuery(fstore.collection("users").orderBy("fName"), UserDataModel.class)
                .build();
        //System.out.println("Failed here 2?");
        m_adapter = new SearchViewAdapter(options, getApplicationContext());
        m_recycleView.setAdapter(m_adapter);


        //System.out.println("Failed here: "+options);
    }

    private void SetupUI(){
        m_recycleView = findViewById(R.id.rclview);
        fstore = FirebaseFirestore.getInstance();
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("Hope Hospital App");
    }

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
        FirestoreRecyclerOptions<UserDataModel> options =
                new FirestoreRecyclerOptions.Builder<UserDataModel>()
                        .setQuery(fstore
                                .collection("users")
                                .orderBy("fName").startAt(a_UserName)
                                .endAt(a_UserName+"\uf8ff")
                                , UserDataModel.class)
                        .build();
        m_adapter = new SearchViewAdapter(options, getApplicationContext()); //(options, getApplicationContext())
        m_adapter.startListening();
        m_recycleView.setAdapter(m_adapter);
    }
}