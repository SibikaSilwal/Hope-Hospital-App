package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

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
import java.util.Collections;
import java.util.Map;

/*
* This class represents the ViewUserDocuments page for User Interface.
* This class allows user to see a list of their test result documents
* uploaded by the hospital along with a message with each document.
*
*/
public class ViewUserDocuments extends GlobalMenuActivity {
    private Toolbar m_mainToolBar;
    private ArrayList<Map<String, Object>> m_testDocumentsArr = new ArrayList<>();
    private RecyclerView m_recView;
    private UserDocumentAdapter m_adapter;

    private String m_userID = FirebaseAuth.getInstance().getUid();
    private FirebaseFirestore m_fstore = FirebaseFirestore.getInstance();
    private DocumentReference m_docReference = m_fstore.collection("Message").document(m_userID);

    /**/
    /*

    NAME

            onCreate - initializes ViewUserDocuments activity..

    SYNOPSIS

            protected void onCreate(Bundle a_savedInstanceState);
                a_savedInstanceState     --> the activity's previously frozen state, if there was one

    DESCRIPTION

            This function initialized the HomePage activity and links it to its
            respective layout resource file i.e. activity_view_users_documents.xml
            which allows retrieving the Widgets and Views used in the layout to
            perform actions and handle events as required, by setting the events
            listeners.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:20pm 03/20/2021

    */
    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_documents);

        SetupUI();
        setSupportActionBar(m_mainToolBar);

        m_recView.setLayoutManager(new LinearLayoutManager(this));

        GetTestDocuments();

    }

    /**/
    /*

    NAME

            GetTestDocuments - initializes all UI components

    SYNOPSIS

            private void GetTestDocuments()

    DESCRIPTION

            This function gets the test document information for user from database,
            adds each document's map (which has the fileName, message, and file's
            download URI) into an ArrayList and passes the array list to the
            UserDocument's recycler view adapter.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/20/2021

    */
    /**/
    private void GetTestDocuments(){
        m_docReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.getData().get("FileCounter")!=null){

                        //Gives the number of files (TestDocuments) stored in database for the user
                        Integer FileCount = Integer.parseInt(documentSnapshot.getData().get("FileCounter").toString()) ;

                        //Adds all the files and message's map in the m_testDocumentsArr ArrayList
                        for(int i = 1; i<=FileCount; i++){
                            m_testDocumentsArr.add((i-1),(Map<String,Object>) documentSnapshot.getData().get(Integer.toString(i)));
                        }

                        //reverses the array list so that the latest document appears on the top of the page
                        Collections.reverse(m_testDocumentsArr);

                        //passes the Document Array to the UserDocumentAdapter
                        m_adapter = new UserDocumentAdapter(m_testDocumentsArr);

                        //Sets the correct adapter for the recycler view, and thus populates the recycler view with documents data
                        m_recView.setAdapter(m_adapter);

                        //listens for any changes in the document array, and reflects changes in frontend.
                        m_adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    /**/
    /*

    NAME

            SetupUI - initializes all UI components

    SYNOPSIS

            private void SetupUI()

    DESCRIPTION

            This function initializes all UI components to their respective Views
            from the layout :activity_view_user_documents.xml. Uses android method
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

            7:30pm 03/20/2021

    */
    /**/
    private void SetupUI(){
        m_mainToolBar = findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("");
        m_recView = findViewById(R.id.rclviewUsersDoc);
    }
}