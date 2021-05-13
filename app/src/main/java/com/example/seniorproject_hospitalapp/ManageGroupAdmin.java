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

public class ManageGroupAdmin extends AdminMenuActivity {
    private RecyclerView m_recView;
    private WardGroupAdapter m_adapter;
    private EditText m_NewWardName;
    private Button m_uploadWardIcon, m_CreateWard;
    private String m_iconName;
    private FirebaseFirestore fstore;
    private FirebaseStorage fstorage;
    private StorageReference storageReference;
    Toolbar m_mainToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_group_admin);

        SetupUI();
        setSupportActionBar(m_mainToolBar);

        FirestoreRecyclerOptions<WardModel> options =
                new FirestoreRecyclerOptions.Builder<WardModel>()
                        .setQuery(fstore.collection("Wards").orderBy("WardName"), WardModel.class)
                        .build();
        GridLayoutManager gridlayout = new GridLayoutManager(this, 2);
        m_recView.setLayoutManager(gridlayout);
        m_adapter = new WardGroupAdapter(options);
        m_recView.setAdapter(m_adapter);

        m_uploadWardIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_iconName= m_NewWardName.getText().toString(); //uploading image's filename
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                //getIntent.setType("*/*");
                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //pickIntent.setType("*/*");
                Intent chooserIntent = Intent.createChooser(getIntent, "Select File");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
                startActivityForResult(chooserIntent, 2000);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2000){
            if(resultCode == Activity.RESULT_OK){ //do we have any result on the data? so check
                Uri imageUri = data.getData();
                //m_profileImage.setImageURI(imageUri);
                UploadImagetoFirebase(imageUri);
            }
        }
    }

    private void UploadImagetoFirebase(Uri a_imageuri) {
        StorageReference fileRef = storageReference.child("Wards/"+m_iconName+".png");
        fileRef.putFile(a_imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ManageGroupAdmin.this, "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String iconUrl = uri.toString(); //this is the download url of the uploaded file
                        DocumentReference docRef = fstore.collection("Wards").document(m_iconName);
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

    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("Hope Hospital App");
        m_recView = findViewById(R.id.recViewward);
        m_NewWardName= findViewById(R.id.e_newwardname);
        m_uploadWardIcon = findViewById(R.id.b_chooseWardIcon);
        //m_CreateWard = findViewById(R.id.b_addnewwardbtn);
        fstore = FirebaseFirestore.getInstance();
        fstorage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
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
}