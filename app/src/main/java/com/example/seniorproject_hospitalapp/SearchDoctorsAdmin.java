package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
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

/*
 * This class represents the SearchDoctorsAdmin activity which provides an interface
 * for admins and patients to view and search from the list of all the Doctors.
 * This class has a recycler view that lists all the Doctors with their
 * general information and provides an interface such that clicking on any of the Doctor's
 * card will take the users to their profile page. It also implements the search functionality
 * through which users can directly search for the patient they are looking for by typing
 * in their name. Since this is a shared activity between user and admin, it checks for the
 * user role and shows and hide contents accordingly
 *
 */
public class SearchDoctorsAdmin extends AppCompatActivity {
    private RecyclerView m_DocRecView;
    private DoctorRecyclerViewAdapter m_adapter;
    private TextView m_AddNewDoc;
    private EditText m_docID, m_docName;
    private ConstraintLayout m_addNewDocLayout;
    private Button m_AddDoc;
    private Toolbar m_mainToolBar;
    private String m_userID = FirebaseAuth.getInstance().getUid();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private DocumentReference m_docRef = fStore.collection("Admin").document(m_userID);
    private UserAppointmentsPage m_settingsPageObj = new UserAppointmentsPage();

    /**/
    /*

    NAME

            onCreate - initializes ViewUserDocuments activity..

    SYNOPSIS

            protected void onCreate(Bundle a_savedInstanceState);
                a_savedInstanceState     --> the activity's previously frozen state, if there was one

    DESCRIPTION

            This function initialized the SearchDoctorsAdmin activity and links it to its
            respective layout resource file i.e. activity_search_doctors_admin.xml
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
        setContentView(R.layout.activity_search_doctors_admin);
        SetupUI();
        setSupportActionBar(m_mainToolBar);

        //if user is not Admin, hide the "Add Doctor" view
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


        //On click listener for Add Doctor Button that provides an interface to admins to add new Doctors to Database
        m_AddDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String DoctorName = m_docName.getText().toString().trim();
                String DoctorID = m_docID.getText().toString().trim();

                if(TextUtils.isEmpty(DoctorName) || TextUtils.isEmpty(DoctorID)){
                    //displays error message to admin if the fields are empty
                    Toast.makeText(getApplicationContext(), "Please enter both Doctor Name and Doctor ID. ", Toast.LENGTH_LONG).show();
                    return;
                }
                //gets a reference for Doctor's collection from the database
                fStore.collection("Doctors").document(m_docID.getText().toString()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            //checks if the given id already exists in the database
                            if(task.getResult().exists()){

                                //displays error message to admin if the id already exists
                                Toast.makeText(getApplicationContext(), "The given id already exists. " +
                                        "Please make sure you are giving the correct Doctor id.", Toast.LENGTH_LONG).show();
                                return;
                            }
                            else{
                                Map<String, Object> docData = new HashMap<>();
                                docData.put("DocName", DoctorName);
                                docData.put("DocID", DoctorID);
                                fStore.collection("Doctors").document(m_docID.getText().toString())
                                        .set(docData);
                            }
                            m_docName.setText("");
                            m_docID.setText("");
                        }
                    });
            }
        });

        m_DocRecView.setLayoutManager(new LinearLayoutManager(this));

        //creates a FirestoreRecyclerOptions object that queries Doctors collection and orders result by Doctor's full name
        FirestoreRecyclerOptions<DoctorDataModel> options =
                new FirestoreRecyclerOptions.Builder<DoctorDataModel>()
                        .setQuery(fStore.collection("Doctors").orderBy("DocName"), DoctorDataModel.class)
                        .build();

        m_adapter = new DoctorRecyclerViewAdapter(options);
        m_DocRecView.setAdapter(m_adapter);
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

    /**/
    /*

    NAME

            ProcessSearch - initializes search process to find a Doctor from Doctors list

    SYNOPSIS

            private void ProcessSearch(String a_DoctorName)
                a_DoctorName  --> username entered by Users to search for

    DESCRIPTION

            This function accepts user input for the Doctor they are looking
            for in the patients' recycler view. This function fires up a Firestore
            query with an "oderBy" attribute that looks for the Doctor's name
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
    private void ProcessSearch(String a_DoctorName)
    {
        FirestoreRecyclerOptions<DoctorDataModel> options =
                new FirestoreRecyclerOptions.Builder<DoctorDataModel>()
                        .setQuery(fStore
                                        .collection("Doctors")
                                        .orderBy("DocName").startAt(a_DoctorName)
                                        .endAt(a_DoctorName+"\uf8ff")
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
    }

    /**/
    /*

    NAME

            SetupUI - initializes all UI components

    SYNOPSIS

            private void SetupUI()

    DESCRIPTION

            This function initializes all UI components to their respective Views
            from the layout :activity_search_doctors_admin.xml. Uses android method
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
    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("");
        m_AddDoc = findViewById(R.id.b_AddDoctor);
        m_docID = findViewById(R.id.e_DocID);
        m_docName = findViewById(R.id.e_DocName);
        m_AddNewDoc = findViewById(R.id.t_addNewDoc);
        m_addNewDocLayout = findViewById(R.id.addNewDocLayout);
        m_DocRecView = findViewById(R.id.recyclerViewDoc);
    }
}