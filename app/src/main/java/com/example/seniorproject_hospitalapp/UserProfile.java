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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UserProfile extends GlobalMenuActivity {
    TextView m_fullName, m_email, m_phone;
    ImageView m_profileImg;
    Button m_changeProfileBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference, profileRef;
    String m_userId, m_userProfileURI;
    DocumentReference docReference;
    Toolbar m_mainToolBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        SetupUI();
        setSupportActionBar(m_mainToolBar);

        docReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                m_fullName.setText(value.getString("fName"));
                m_email.setText(value.getString("email"));
                m_phone.setText(value.getString("phone"));
                m_userProfileURI = value.getString("profileURL");
            }
        });

        m_changeProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), UpdateProfile.class);
                i.putExtra("fullname", m_fullName.getText().toString()); //sending the data in key value pair to updateprofile activity
                i.putExtra("email", m_email.getText().toString());
                i.putExtra("phone", m_phone.getText().toString());
                i.putExtra("profileURL", m_userProfileURI);
                startActivity(i);

               // Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //startActivityForResult(openGalleryIntent, 1000);
            }
        });
    }

    /*//overriding the onactivityresult function, the "data" attribute has the img_uri,
    //different functions/intents can be invoking the onActivityResult function therefore,
    //its required/good to check the intent that is invoking the method, so we use reqcode
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            if(resultCode == Activity.RESULT_OK){ //do we have any result on the data? so check
                Uri imageUri = data.getData();
                m_profileImg.setImageURI(imageUri);
                UploadImagetoFirebase(imageUri);
            }
        }

    }
     */

    /*private void UploadImagetoFirebase(Uri a_imageuri) {
        StorageReference fileRef = storageReference.child("users/"+m_userId+"profile.jpg");
        fileRef.putFile(a_imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(UserProfile.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(m_profileImg);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfile.this, "Failed uploading image", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    private void SetupUI(){
        m_fullName = findViewById(R.id.t_userFullName);
        m_email = findViewById(R.id.t_userEmail);
        m_phone=findViewById(R.id.t_phone);
        m_changeProfileBtn= findViewById(R.id.b_changeprofile);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        m_userId = fAuth.getCurrentUser().getUid();
        profileRef = storageReference.child("users/"+m_userId+"profile.jpg");
        docReference = fStore.collection("users").document(m_userId);
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("Hope Hospital App");
        m_profileImg= findViewById(R.id.i_userimg);
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(m_profileImg);
            }
        });
    }
}