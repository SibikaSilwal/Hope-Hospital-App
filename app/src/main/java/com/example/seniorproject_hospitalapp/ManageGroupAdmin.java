package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;
//import android.support.v7.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
//import android.widget.Toolbar;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

/*
 * This class represents the ManageGroupAdmin activity which provides an interface
 * for admins to view existing wards and add new wards in the database
 *
 */
public class ManageGroupAdmin extends AdminMenuActivity {
    private RecyclerView m_recView;
    private WardGroupAdapter m_adapter;
    private EditText m_NewWardName;
    private ImageView m_uploadWardIcon;
    private String m_iconName;
    private Uri m_WardIconUri;
    private Toolbar m_mainToolBar;

    private FirebaseFirestore m_fStore = FirebaseFirestore.getInstance();
    private StorageReference m_storageReference = FirebaseStorage.getInstance().getReference();

    /**/
    /*

    NAME

            onCreate - initializes ViewUserDocuments activity..

    SYNOPSIS

            protected void onCreate(Bundle a_savedInstanceState);
                a_savedInstanceState     --> the activity's previously frozen state, if there was one

    DESCRIPTION

            This function initialized the ManageGroupAdmin activity and links it to its
            respective layout resource file i.e. activity_manage_group_admin.xml
            which allows retrieving the Widgets and Views used in the layout to
            perform actions and handle events as required, by setting the events
            listeners.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:20pm 02/24/2021

    */
    /**/
    @Override
    protected void onCreate(Bundle a_savedInstanceState) {
        super.onCreate(a_savedInstanceState);
        setContentView(R.layout.activity_manage_group_admin);

        //Set up for initial UI components
        SetupUI();
        setSupportActionBar(m_mainToolBar);

        //creates a FirestoreRecyclerOptions object that queries Wards collection and orders result by Ward Name
        FirestoreRecyclerOptions<WardModel> options =
                new FirestoreRecyclerOptions.Builder<WardModel>()
                        .setQuery(m_fStore.collection("Wards").orderBy("WardName"), WardModel.class)
                        .build();

        GridLayoutManager gridlayout = new GridLayoutManager(this, 2);
        m_recView.setLayoutManager(gridlayout);

        //sets up the adapter for user's recycler view
        m_adapter = new WardGroupAdapter(options);
        m_recView.setAdapter(m_adapter);

        //invokes onActivityResult method to pick a new file when the upload icon is clicked
        m_uploadWardIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_iconName= m_NewWardName.getText().toString();
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent chooserIntent = Intent.createChooser(getIntent, "Select Ward Icon");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
                startActivityForResult(chooserIntent, 2000);
            }
        });

    }

    /**/
    /*

    NAME

            onActivityResult - fires new intent to pick file from the device

    SYNOPSIS

            private void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
                requestCode  --> an integer to identify the intent firing onActivityResult
                resultCode   --> an integer value that represents the status of the result of onActivityResult
                data         --> intent data returned from the launched intent

    DESCRIPTION

            This function overrides the onActivityResult function and is called when upload
            icon image view is clicked, to pick a new file from Device to upload to database.
            Different functions/intents can be invoking the onActivityResult function
            therefore, to check the intent that is invoking the method, its request code
            is checked, in this case request code 2000. The "data" attribute has
            the file uri, that has been picked from the device.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 02/24/2021

    */
    /**/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2000){
            if(resultCode == Activity.RESULT_OK){
                m_WardIconUri = data.getData();
            }
        }
    }

    /**/
    /*

    NAME

            UploadWardIconToFirebase - Uploads ward information in firebase database

    SYNOPSIS

            private void UploadWardIconToFirebase(Uri a_imageuri)
                a_imageuri     --> Upload uri of the chosen image

    DESCRIPTION

            This function uploads the passed file uri in the firebase storage. It
            also stores the file's download URI in Firestore database in "Wards"
            collection.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 02/24/2021

    */
    /**/
    private void UploadWardIconToFirebase(Uri a_imageuri) {
        StorageReference fileRef = m_storageReference.child("Wards/"+m_iconName+".png");
        fileRef.putFile(a_imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ManageGroupAdmin.this, "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String iconUrl = uri.toString(); //this is the download url of the uploaded file
                        DocumentReference docRef = m_fStore.collection("Wards").document(m_iconName);
                        Map<String, Object> WardDocNew = new HashMap<>();
                        WardDocNew.put("WardName",m_iconName);
                        WardDocNew.put("IconURI",iconUrl );
                        docRef.set(WardDocNew);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ManageGroupAdmin.this, "Failed uploading image", Toast.LENGTH_SHORT).show();
                System.out.println("Failed upload");
            }
        });
    }

    /**/
    /*

    NAME

            AddNewWard - calls the UploadWardIconToFirebase function

    SYNOPSIS

            public void AddNewWard(View a_view)
                a_view     --> view provided

    DESCRIPTION

            This function calls the UploadWardIconToFirebase function which then
            uploads the new ward to firebase database. This function gets called
            when the Add Ward button view is clicked.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            2:30pm 02/24/2021

    */
    /**/
    public void AddNewWard(View a_view){
        if(m_WardIconUri==null){
            Toast.makeText(ManageGroupAdmin.this, "Please choose a ward icon.", Toast.LENGTH_SHORT).show();
            return;
        }
        UploadWardIconToFirebase(m_WardIconUri);
        m_NewWardName.setText("");
        Toast.makeText(ManageGroupAdmin.this, "New ward was created successfully.", Toast.LENGTH_SHORT).show();
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

            7:30pm 02/24/2021

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

            7:30pm 02/24/2021

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

            SetupUI - initializes all UI components

    SYNOPSIS

            private void SetupUI()

    DESCRIPTION

            This function initializes all UI components to their respective Views
            from the layout :activity_manage_group_admin.xml. Uses android method
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

            7:30pm 02/24/2021

    */
    /**/
    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("");
        m_recView = findViewById(R.id.recViewward);
        m_NewWardName= findViewById(R.id.e_newwardname);
        m_uploadWardIcon = findViewById(R.id.b_chooseWardIcon);
    }

}