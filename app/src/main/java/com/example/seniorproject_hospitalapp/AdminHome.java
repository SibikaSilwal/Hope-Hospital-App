package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class AdminHome extends AdminMenuActivity {
    public static final String Tag2 = "TAG";
    Toolbar m_mainToolBar;
    EditText msg;
    Button save, m_DocSearch;//, m_uploadFileBtn, m_ViewPDFBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    private StorageReference storageReference;
    String newMsg;
    private String m_docId, m_fileName; //firestore doc id and filename to be uploaded
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        SetupUI();
        setSupportActionBar(m_mainToolBar);



        /*save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminHome.this, "Checking toast", Toast.LENGTH_SHORT).show();
                newMsg = msg.getText().toString();
                DocumentReference df = fStore.collection("GlobalDoc").document("PrivacyPolicyDocs");
                Map<String, Object> message = new HashMap<>();
                message.put("msg1", newMsg);
                df.set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AdminHome.this, "Operation Successful", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminHome.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });*/

        m_DocSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHome.this, SearchDoctorsAdmin.class));
            }
        });
    }

    public void Logout(View a_view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginPage.class));
        finish();
    }

    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("Hope Hospital App");
        //save = findViewById(R.id.b_try);
        //msg=findViewById(R.id.e_try);
        m_DocSearch = findViewById(R.id.b_gotoDocSearch);
       // m_uploadFileBtn = findViewById(R.id.b_uploadFile);
        //m_ViewPDFBtn= findViewById(R.id.b_viewpdf);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        //Log.d("Tag2", "set up ui 4???: ");
    }

    /* //overriding the onactivityresult function, the "data" attribute has the img_uri,
    //different functions/intents can be invoking the onActivityResult function therefore,
    //its required/good to check the intent that is invoking the method, so we use reqcode
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(Tag2, "here 1???: ");
        if(requestCode==2000){
            if(resultCode == Activity.RESULT_OK){ //do we have any result on the data? so check
                Uri imageUri = data.getData();
                Log.d(Tag2, "here 2???: ");
                //m_profileImage.setImageURI(imageUri);
                UploadImagetoFirebase(imageUri, m_docId, m_fileName);
            }
        }
    }

    private void UploadImagetoFirebase(Uri a_imageuri, String a_documendID, String a_fileName) {
        StorageReference fileRef = storageReference.child("GlobalDoc/"+a_documendID+a_fileName);
        fileRef.putFile(a_imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminHome.this, "Image Uploaded", Toast.LENGTH_SHORT).show();

                Log.d(Tag2, "Uploaded???: ");
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Picasso.get().load(uri).into(m_profileImage);
                        Toast.makeText(AdminHome.this, "File loaded successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminHome.this, "Failed uploading image", Toast.LENGTH_SHORT).show();
            }
        });
    }
    */
}