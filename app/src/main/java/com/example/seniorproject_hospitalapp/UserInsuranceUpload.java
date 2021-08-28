package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

/*
* This class represents the UserInsuranceUpload activity for both user and admin interface.
* This class allows users to view, download, and upload their insurance card image in the
* database which can be downloaded by admins / hospital employees as needed.
*
*/
public class UserInsuranceUpload extends AppCompatActivity {
    private TextView m_DownloadFront, m_DownloadBack;
    private ImageView m_InsuranceFront, m_InsuranceBack, m_ImageViewtoSet;
    String m_UserId, m_StoragePath, m_fileName, m_Extension, m_InsuranceFrontDownloadURL, m_InsuranceBackDownloadURL;
    private FirebaseFirestore m_fStore;
    DocumentReference m_docRef;
    private StorageReference m_storageReference;

    /**/
    /*

    NAME

            onCreate - initializes UserInsuranceUpload activity..

    SYNOPSIS

            protected void onCreate(Bundle a_savedInstanceState);
                a_savedInstanceState     --> the activity's previously frozen state, if there was one

    DESCRIPTION

            This function initialized the HomePage activity and links it to its
            respective layout resource file i.e. activity_user_insurance_upload
            which allows retrieving the Widgets and Views used in the layout to
            perform actions and handle events as required, by setting the events
            listeners.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:24pm 01/19/2021

    */
    /**/
    @Override
    protected void onCreate(Bundle a_savedInstanceState) {
        super.onCreate(a_savedInstanceState);
        setContentView(R.layout.activity_user_insurance_upload);

        SetupUI();

        //called when InsuranceFront image view is clicked, which then allows users to pick a new image for their insurance card front
        m_InsuranceFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickFileFromDevice(2010, "users/"+m_UserId+"/", "insuranceFront","", m_InsuranceFront);
            }
        });

        //called when InsuranceBack image view is clicked, which then allows users to pick a new image for their insurance card back
        m_InsuranceBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickFileFromDevice(2010, "users/"+m_UserId+"/", "insuranceBack","", m_InsuranceBack);
            }
        });

        //called when download front button is clicked, which download's the patient's insurance front in the user's device
        m_DownloadFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_InsuranceFrontDownloadURL!=""){
                    DownloadImg(UserInsuranceUpload.this, "insuranceFront", DIRECTORY_DOWNLOADS, m_InsuranceFrontDownloadURL);
                    Toast.makeText(getApplicationContext(), "Image Saved.", Toast.LENGTH_SHORT);
                }else{
                    Toast.makeText(getApplicationContext(), "Patient has not uploaded any insurance file.", Toast.LENGTH_SHORT);
                }
            }
        });

        //called when download front button is clicked, which download's the patient's insurance front in the user's device
        m_DownloadBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_InsuranceBackDownloadURL!=""){
                    DownloadImg(UserInsuranceUpload.this, "insuranceBack", DIRECTORY_DOWNLOADS, m_InsuranceBackDownloadURL);
                    Toast.makeText(getApplicationContext(), "Image Saved.", Toast.LENGTH_SHORT);
                }else{
                    Toast.makeText(getApplicationContext(), "Patient has not uploaded any insurance file.", Toast.LENGTH_SHORT);
                }
            }
        });

    }

    /**/
    /*

    NAME

            onActivityResult - retrieves and sets user information in the UpdateProfile activity

    SYNOPSIS

            private void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
                requestCode  --> an integer to identify the intent firing onActivityResult
                resultCode   --> an integer value that represents the status of the result of onActivityResult
                data         --> intent data returned from the launched intent

    DESCRIPTION

            This function overrides the onActivityResult function and is called when the insurance
            image views are clicked, to pick insurance card image from Device's Media Gallery.
            Different functions/intents can be invoking the onActivityResult function
            therefore, to check the intent that is invoking the method, its request code
            is checked, in this case request code 2010. The "data" attribute has the img_uri, that
            has been picked from the gallery. The image uri is passed to the UploadImagetoFirebase
            function and is called.


    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 07/10/2021

    */
    /**/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2010){
            if(resultCode == Activity.RESULT_OK){ //do we have any result on the data? so check
                Uri imageUri = data.getData();
                m_ImageViewtoSet.setImageURI(imageUri);
                UploadImageToFirebase(imageUri,m_StoragePath, m_fileName, m_Extension);
            }
        }
    }

    /**/
    /*

    NAME

            PickFileFromDevice - Starts intent to pick a file from Media Store in user's device

    SYNOPSIS

            private void PickFileFromDevice(int a_RequestCode, String a_StoragePath, String a_fileName,
                                            String a_extension, @Nullable View a_imageView)
                a_RequestCode  --> an integer id identifying the firing intent
                a_StoragePath  --> storage path to later store the picked picture in the database
                a_fileName     --> filename for the picked image
                a_extension    --> extension for the picked image
                a_imageView    --> image view to set the picked image to

    DESCRIPTION

            This function starts an android intent, to allow users to pick their insurance image
            file from their Media library in their device

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 07/10/2021

    */
    /**/
    private void PickFileFromDevice(int a_RequestCode, String a_StoragePath, String a_fileName, String a_extension, @Nullable View a_imageView){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("*/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("*/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select File");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
        m_StoragePath = a_StoragePath;
        m_fileName = a_fileName;
        m_Extension = a_extension;
        m_ImageViewtoSet = (ImageView)a_imageView;
        startActivityForResult(chooserIntent, a_RequestCode);
    }

    /**/
    /*

    NAME

            UploadImageToFirebase - initializes all UI components

    SYNOPSIS

            private void UploadImageToFirebase(Uri a_imageuri, String a_StoragePath, String a_fileName, String a_extension)
                a_imageuri     --> image uri of the image being uplaoded
                a_StoragePath  --> Storage path for the file to upload in Firebase Storage
                a_fileName     --> name of the file being uploaded
                a_extension    --> extension of the file being uploaded

    DESCRIPTION

            This function uploads image (user's insurance card) to Firebase Storage, and stores the image's download url
            in users collection

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 07/10/2021

    */
    /**/
    private void UploadImageToFirebase(Uri a_imageuri, String a_StoragePath, String a_fileName, String a_extension) {

        //Firebase Storage reference for the given file
        StorageReference fileRef = m_storageReference.child(a_StoragePath+a_fileName+a_extension);

        //uploads the file to the given storage reference in firebase storage database
        fileRef.putFile(a_imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(UserInsuranceUpload.this, "File Uploaded Successfully", Toast.LENGTH_SHORT).show();

                //Gets download url of the recently uploaded file
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        //Stored the download url as a string in the Firestore database in User's collection
                        DocumentReference docRef = m_fStore.collection("users").document(m_UserId);
                        Map<String, Object> NewFileInfo = new HashMap<>();
                        NewFileInfo.put(a_fileName,uri.toString());
                        docRef.set(NewFileInfo, SetOptions.merge());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserInsuranceUpload.this, "Failed uploading image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**/
    /*

    NAME

            DownloadImg - Download's image file in the user's device

    SYNOPSIS

            private void DownloadImg(Context a_context, String a_fileName, String a_destination, String a_fileURL)
                a_context     --> current context of the application
                a_fileName    --> Name of the file to be downloaded
                a_destination --> Destination in user's device where the file would be downloaded
                a_fileURL     --> download URL of the file

    DESCRIPTION

            This function receives a download URL of an image as one of it parameter, and downloads
            the image in user's device

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 07/10/2021

    */
    /**/
    private void DownloadImg(Context a_context, String a_fileName, String a_destination, String a_fileURL){
        DownloadManager downloadManager = (DownloadManager)a_context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadURI = Uri.parse(a_fileURL);
        DownloadManager.Request request = new DownloadManager.Request(downloadURI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(a_context, a_destination, a_fileName);
        downloadManager.enqueue(request);
    }

    /**/
    /*

    NAME

            SetupUI - initializes all UI components

    SYNOPSIS

            private void SetupUI()

    DESCRIPTION

            This function initializes all UI components to their respective Views
            from the layout :activity_user_insurance_upload.xml. Uses android method
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

            7:30pm 07/10/2021

    */
    /**/
    private void SetupUI(){
        m_DownloadFront = findViewById(R.id.t_front);
        m_DownloadBack = findViewById(R.id.t_back);
        m_InsuranceFront = findViewById(R.id.insuranceFront);
        m_InsuranceBack = findViewById(R.id.insuranceBack);
        m_UserId = getIntent().getStringExtra("patientID");
        m_fStore = FirebaseFirestore.getInstance();
        m_docRef = m_fStore.collection("users").document(m_UserId);
        m_storageReference = FirebaseStorage.getInstance().getReference();

        //retrieves insurance information about the user from the database, and initializes the UserObject with the data
        m_docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserDataModel userObject = documentSnapshot.toObject(UserDataModel.class);
                m_InsuranceFrontDownloadURL = userObject.getInsuranceFront();
                m_InsuranceBackDownloadURL = userObject.getInsuranceBack();
                if(m_InsuranceFrontDownloadURL!="")
                    Picasso.get().load(m_InsuranceFrontDownloadURL).into(m_InsuranceFront);

                if(m_InsuranceBackDownloadURL!="")
                    Picasso.get().load(m_InsuranceBackDownloadURL).into(m_InsuranceBack);

            }
        });
    }
}