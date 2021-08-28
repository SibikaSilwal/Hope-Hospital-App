package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/*
 * This class represents the GlobalDocAdmin activity which provides an interface
 * for admins to view, add, change, and delete the Global documents of their
 * organization.
 */
public class GlobalDocAdmin extends AdminMenuActivity {
    private Toolbar m_mainToolBar;
    private RecyclerView m_recview;
    private GlobalDocRecViewAdapter m_recviewAdapter;
    private EditText m_DocName;
    private ImageView m_PDFicon;
    private Uri m_newFileURI;
    private String m_fileName, m_fileUrl;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    /**/
    /*

    NAME

            onCreate - initializes ViewUserDocuments activity..

    SYNOPSIS

            protected void onCreate(Bundle a_savedInstanceState);
                a_savedInstanceState     --> the activity's previously frozen state, if there was one

    DESCRIPTION

            This function initialized the GlobalDocAdmin activity and links it to its
            respective layout resource file i.e. activity_global_doc_admin.xml
            which allows retrieving the Widgets and Views used in the layout to
            perform actions and handle events as required, by setting the events
            listeners.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:20pm 02/17/2021

    */
    /**/
    @Override
    protected void onCreate(Bundle a_savedInstanceState) {
        super.onCreate(a_savedInstanceState);
        setContentView(R.layout.activity_global_doc_admin);

        //Set up for initial UI components
        SetupUI();
        setSupportActionBar(m_mainToolBar);

        //creates a FirestoreRecyclerOptions object that queries GlobalDoc collection and orders result by fileName
        FirestoreRecyclerOptions<GlobalDocModel> options =
                new FirestoreRecyclerOptions.Builder<GlobalDocModel>()
                        .setQuery(fStore.collection("GlobalDoc").orderBy("fileName"), GlobalDocModel.class)
                        .build();

        m_recviewAdapter = new GlobalDocRecViewAdapter(options, GlobalDocAdmin.this);
        m_recview.setAdapter(m_recviewAdapter);

        //invokes onActivityResult method to pick a new file when the add pdf icon is clicked
        m_PDFicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_fileName= m_DocName.getText().toString(); //uploading document's filename
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("*/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("*/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select File");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, 2000);
            }
        });

    }

    /**/
    /*

    NAME

            ChangePdf - invokes onActivityResult method

    SYNOPSIS

           public void ChangePdf(String a_fileName)
                a_fileName    --> Name of the file for the file being picked from device

    DESCRIPTION

            This function fires up onActivityResult method with request code 2001 which
            allows users to pick a file from their device.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 02/17/2021

    */
    /**/
    public void ChangePdf(String a_fileName){
        m_fileName= a_fileName;
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("*/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("*/*");
        Intent chooserIntent = Intent.createChooser(getIntent, "Select File");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, 2001);
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

            This function overrides the onActivityResult function and is called when the pdf
            icon view is clicked, to pick a new file from Device to upload to database.
            Different functions/intents can be invoking the onActivityResult function
            therefore, to check the intent that is invoking the method, its request code
            is checked, in this case request code 2000 and 2001. The "data" attribute has
            the file uri, that has been picked from the device.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 02/17/2021

    */
    /**/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //2000 while uploading a new file in database
        if(requestCode==2000){
            if(resultCode == Activity.RESULT_OK){
                m_newFileURI= data.getData();
            }
        }
        //2001 while changing an existing file
        if(requestCode==2001){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                UploadDocumentToFirebase(imageUri, m_fileName);
            }
        }
    }

    /**/
    /*

    NAME

            UploadDocumentToFirebase - Uploads global documents in firebase database

    SYNOPSIS

            private void UploadDocumentToFirebase(Uri a_fileUri, String a_fileName)
                a_fileUri     --> Upload uri of the chosen file
                a_fileName    --> Name of the file being uploaded

    DESCRIPTION

            This function uploads the passed file uri in the firebase storage. It
            also stores the file's download URI in Firestore database in "Global Doc"
            collection.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/2/2021

    */
    /**/
    private void UploadDocumentToFirebase(Uri a_fileUri, String a_fileName) {
        StorageReference fileRef = storageReference.child("GlobalDoc/"+a_fileName+"file.pdf");
        fileRef.putFile(a_fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(GlobalDocAdmin.this, "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //the download url of the uploaded file
                        m_fileUrl = uri.toString();

                        //storing it in firestore database as string
                        DocumentReference docRef = fStore.collection("GlobalDoc").document(a_fileName);
                        Map<String, Object> fileDocNew = new HashMap<>();
                        fileDocNew.put("file",m_fileUrl);
                        fileDocNew.put("fileName",a_fileName );
                        docRef.set(fileDocNew);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GlobalDocAdmin.this, "Failed uploading image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**/
    /*

    NAME

            AddNewDocument - calls the UploadDocumentToFirebase function

    SYNOPSIS

            public void AddNewDocument(View a_view)
                a_view     --> view provided

    DESCRIPTION

            This function calls the UploadDocumentToFirebase function which then
            uploads the new file to firebase database. This function gets called
            when the Upload button view is clicked.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            2:30pm 02/17/2021

    */
    /**/
    public void AddNewDocument(View a_view){
        if(m_newFileURI==null||m_fileName==null){
            Toast.makeText(GlobalDocAdmin.this, "Please make sure to choose a file and enter filename.", Toast.LENGTH_SHORT).show();
            return;
        }
        UploadDocumentToFirebase(m_newFileURI,m_fileName);
        m_DocName.setText("");
        Toast.makeText(GlobalDocAdmin.this, "New file uploaded successfully.", Toast.LENGTH_SHORT).show();
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

            7:30pm 02/17/2021

    */
    /**/
    @Override
    protected void onStart() {
        super.onStart();
        m_recviewAdapter.startListening();
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

            7:30pm 02/17/2021

    */
    /**/
    @Override
    protected void onStop() {
        super.onStop();
        m_recviewAdapter.stopListening();
    }

    /**/
    /*

    NAME

            SetupUI - initializes all UI components

    SYNOPSIS

            private void SetupUI()

    DESCRIPTION

            This function initializes all UI components to their respective Views
            from the layout :activity_global_doc_admin.xml. Uses android method
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

            7:30pm 02/17/2021

    */
    /**/
    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("");
        m_recview = findViewById(R.id.globaldocreyclerview);
        m_recview.setLayoutManager(new LinearLayoutManager(this));
        m_PDFicon=findViewById(R.id.globalDocFileIcon);
        m_DocName= findViewById(R.id.e_GDocUploadFileName);
    }


}