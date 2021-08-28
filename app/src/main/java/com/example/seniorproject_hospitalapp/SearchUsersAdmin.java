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

/*
* This class represents the SearchUsersAdmin activity which provides an interface
* for admins to view and search from the list of all the users (Patients) using the
* application. This class has a recycler view that lists all the Patients with their
* general information and provides an interface such that clicking on any of the patient's
* card will take the admin to their profile page. It also implements the search functionality
* through which Admin can directly search for the patient they are looking for by typing
* in their name.
* */
public class SearchUsersAdmin extends AppCompatActivity {
    Toolbar m_mainToolBar;
    RecyclerView m_recycleView;
    SearchViewAdapter m_adapter;
    FirebaseFirestore m_fstore;

    /**/
    /*

    NAME

            onCreate - initializes ViewUserDocuments activity..

    SYNOPSIS

            protected void onCreate(Bundle a_savedInstanceState);
                a_savedInstanceState     --> the activity's previously frozen state, if there was one

    DESCRIPTION

            This function initialized the SearchUsersAdmin activity and links it to its
            respective layout resource file i.e. activity_search_users_admin.xml
            which allows retrieving the Widgets and Views used in the layout to
            perform actions and handle events as required, by setting the events
            listeners.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:20pm 03/10/2021

    */
    /**/
    @Override
    protected void onCreate(Bundle a_savedInstanceState) {
        super.onCreate(a_savedInstanceState);
        setContentView(R.layout.activity_search_users_admin);

        //Set up for initial UI components
        SetupUI();
        setSupportActionBar(m_mainToolBar);

        m_recycleView.setLayoutManager(new LinearLayoutManager(this));

        //creates a FirestoreRecyclerOptions object that queries users collection and orders result by user's full name
        FirestoreRecyclerOptions<UserDataModel> options =
                new FirestoreRecyclerOptions.Builder<UserDataModel>()
                        .setQuery(m_fstore.collection("users").orderBy("fName"), UserDataModel.class)
                        .build();

        //sets up the adapter for user's recycler view
        m_adapter = new SearchViewAdapter(options, getApplicationContext());
        m_recycleView.setAdapter(m_adapter);


    }


    /**/
    /*

    NAME

            onStart - This function overrides the onStart function which is called just after the onCreate()
            function is called at the launch of an activity. This function when called tells the adapter to
            start listening for new items for recycler view

    SYNOPSIS

            protected void onStart()

    DESCRIPTION

            This function overrides the onStart.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021

    */
    /**/
    @Override
    protected void onStart() {
        super.onStart();
        m_adapter.startListening();
    }

    /**/
    /*

    NAME

            onStop - tells the adapter to stop listening

    SYNOPSIS

            protected void onStop()

    DESCRIPTION

            This function overrides onStop method which is called when an activity is in background.
            This function tells the adapter to stop listening since the activity is no longer active

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021

    */
    /**/
    @Override
    protected void onStop() {
        super.onStop();
        m_adapter.stopListening();
    }

    /**/
    /*

    NAME

            onCreateOptionsMenu - creates option menu with search feature for this activity page

    SYNOPSIS

            public boolean onCreateOptionsMenu(Menu a_menu)
                a_menu  --> Menu class object that provides an interface for managing the items in the menu.

    DESCRIPTION

            This function specifies the options menu for SearchUsersAdmin activity. It overrides
            onCreateOptionsMenu(), and inflates the menu resource file search_menu.xml into the Menu
            passed in the argument. It then created an object of SearchView class that provides an
            interface to enter a search query and submit a request to a search provider in this case
            the ProcessSearch function. It then Shows a list of query suggestions or results, if available,
            and allows the user to pick a suggestion or result to launch into.


    RETURNS

            True or False

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021

    */
    /**/
    @Override
    public boolean onCreateOptionsMenu(Menu a_menu) {
        getMenuInflater().inflate(R.menu.search_menu, a_menu);
        MenuItem item = a_menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
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
        return super.onCreateOptionsMenu(a_menu);
    }

    /**/
    /*

    NAME

            ProcessSearch - initializes search process to find a User from Users list

    SYNOPSIS

            private void ProcessSearch(String a_UserName)
                a_UserName  --> username entered by Users to search for

    DESCRIPTION

            This function accepts user input for the Patient they are looking
            for in the patients' recycler view. This function fires up a Firestore
            query with an "oderBy" attribute that looks for the Patients' name
            starting and ending at the string provided in user input. It then sets
            the adapter for the resulted query.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021

    */
    /**/
    private void ProcessSearch(String a_UserName) {

        FirestoreRecyclerOptions<UserDataModel> options =
                new FirestoreRecyclerOptions.Builder<UserDataModel>()
                        .setQuery(m_fstore
                                        .collection("users")
                                        .orderBy("fName").startAt(a_UserName)
                                        .endAt(a_UserName + "\uf8ff")
                                , UserDataModel.class)
                        .build();
        m_adapter = new SearchViewAdapter(options, getApplicationContext());
        m_adapter.startListening();
        m_recycleView.setAdapter(m_adapter);

    }

    /**/
    /*

    NAME

            SetupUI - initializes all UI components

    SYNOPSIS

            private void SetupUI()

    DESCRIPTION

            This function initializes all UI components to their respective Views
            from the layout :activity_search_users_admin.xml. Uses android method
            findViewById that, "finds a view that was identified by the android:id
           XML attribute that was processed in onCreate(Bundle)." Src: Android Documentation
           (https://developer.android.com/reference/android/app/Activity#findViewById(int))
           It also calls all other helper functions used in setting up the UI and getting
           contents for all the Views.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021

    */
    /**/
    private void SetupUI() {
        m_recycleView = findViewById(R.id.rclview);
        m_fstore = FirebaseFirestore.getInstance();
        m_mainToolBar = findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("");
    }

}
