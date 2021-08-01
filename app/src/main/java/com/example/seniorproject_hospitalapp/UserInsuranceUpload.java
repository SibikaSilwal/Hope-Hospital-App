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

public class UserInsuranceUpload extends AppCompatActivity {
    private TextView m_DownloadFront, m_DownloadBack;
    private ImageView m_InsuranceFront, m_InsuranceBack, m_ImageViewtoSet;
    String m_UserId, m_StoragePath, m_fileName, m_Extension, m_InsuranceFrontDownloadURL, m_InsuranceBackDownloadURL;
    private FirebaseFirestore m_fStore;
    DocumentReference m_docRef;
    private StorageReference m_storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_insurance_upload);

        SetupUI();
        m_InsuranceFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickFileFromDevice(2010, "users/"+m_UserId+"/", "insuranceFront","", m_InsuranceFront);
            }
        });
        m_InsuranceBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickFileFromDevice(2010, "users/"+m_UserId+"/", "insuranceBack","", m_InsuranceBack);
            }
        });
        m_DownloadFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_InsuranceFrontDownloadURL!=null){
                    DownloadImg(UserInsuranceUpload.this, "insuranceFront", DIRECTORY_DOWNLOADS, m_InsuranceFrontDownloadURL);
                    Toast.makeText(getApplicationContext(), "Image Saved.", Toast.LENGTH_SHORT);
                }else{
                    Toast.makeText(getApplicationContext(), "Patient has not uploaded any insurance file.", Toast.LENGTH_SHORT);
                }
            }
        });
        m_DownloadBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_InsuranceBackDownloadURL!=null){
                    DownloadImg(UserInsuranceUpload.this, "insuranceBack", DIRECTORY_DOWNLOADS, m_InsuranceBackDownloadURL);
                    Toast.makeText(getApplicationContext(), "Image Saved.", Toast.LENGTH_SHORT);
                }else{
                    Toast.makeText(getApplicationContext(), "Patient has not uploaded any insurance file.", Toast.LENGTH_SHORT);
                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2010){
            if(resultCode == Activity.RESULT_OK){ //do we have any result on the data? so check
                Uri imageUri = data.getData();
                m_ImageViewtoSet.setImageURI(imageUri);
                UploadImagetoFirebase(imageUri,m_StoragePath, m_fileName, m_Extension);
            }
        }
    }
    public void PickFileFromDevice(int a_RequestCode, String a_StoragePath, String a_fileName, String a_extension, @Nullable View a_imageView){
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

    private void UploadImagetoFirebase(Uri a_imageuri, String a_StoragePath, String a_fileName, String a_extension) {
        StorageReference fileRef = m_storageReference.child(a_StoragePath+a_fileName+a_extension);
        fileRef.putFile(a_imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(UserInsuranceUpload.this, "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
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
                System.out.println("Failed upload");
            }
        });
    }

    private void DownloadImg(Context a_context, String a_fileName, String a_destination, String a_fileURL){
        DownloadManager downloadManager = (DownloadManager)a_context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadURI = Uri.parse(a_fileURL);
        DownloadManager.Request request = new DownloadManager.Request(downloadURI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(a_context, a_destination, a_fileName);
        downloadManager.enqueue(request);
    }
    private void SetupUI(){
        m_DownloadFront = findViewById(R.id.t_front);
        m_DownloadBack = findViewById(R.id.t_back);
        m_InsuranceFront = findViewById(R.id.insuranceFront);
        m_InsuranceBack = findViewById(R.id.insuranceBack);
        m_UserId = getIntent().getStringExtra("patientID");
        m_fStore = FirebaseFirestore.getInstance();
        m_docRef = m_fStore.collection("users").document(m_UserId);
        m_storageReference = FirebaseStorage.getInstance().getReference();

        m_docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                m_InsuranceFrontDownloadURL=task.getResult().get("insuranceFront").toString();
                m_InsuranceBackDownloadURL=task.getResult().get("insuranceBack").toString();
                if(m_InsuranceFrontDownloadURL!=null){
                    Picasso.get().load(m_InsuranceFrontDownloadURL).into(m_InsuranceFront);
                }
                if(m_InsuranceBackDownloadURL!=null){
                    Picasso.get().load(m_InsuranceBackDownloadURL).into(m_InsuranceBack);
                }
            }
        });
    }
}