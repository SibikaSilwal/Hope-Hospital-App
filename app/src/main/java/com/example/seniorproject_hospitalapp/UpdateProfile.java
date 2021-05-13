package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends GlobalMenuActivity {
    public static final String Tag = "TAG";
    private Toolbar m_mainToolBar;
    private EditText m_fullname, m_email, m_phonenum;
    private Button m_saveBtn;
    private ImageView m_profileImage;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser user;
    private StorageReference storageReference, profileRef;
    private String m_updatedfname, m_updatedemail, m_updatedphone, m_imageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        SetupUI();
        setSupportActionBar(m_mainToolBar);

        m_profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(UpdateProfile.this, "Clicked...", Toast.LENGTH_SHORT).show()
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        m_saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_updatedfname= m_fullname.getText().toString();
                m_updatedemail=m_email.getText().toString();
                m_updatedphone=m_phonenum.getText().toString();
                if(m_updatedfname.isEmpty()||m_updatedemail.isEmpty()||m_updatedphone.isEmpty()){
                    Toast.makeText(UpdateProfile.this, "Field(s) are empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //NEEED TO UPDATE THE EMAIL IN AUTHETICATION ALSO
                user.updateEmail(m_updatedemail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference docRef = fStore.collection("users").document(user.getUid());
                        Map<String, Object> edited = new HashMap<>();
                        edited.put("email",m_updatedemail);
                        edited.put("fName", m_updatedfname);
                        edited.put("phone", m_updatedphone);
                        edited.put("profileURL", m_imageUrl);
                        docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UpdateProfile.this, "Updates Successful.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UpdateProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        Toast.makeText(UpdateProfile.this, "Email updated to: ."+m_updatedemail, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        Intent data = getIntent();
        String fullName = data.getStringExtra("fullname");
        String email = data.getStringExtra("email");
        String phone = data.getStringExtra("phone");
        m_imageUrl = data.getStringExtra("profileURL");
        SetUserInfo(fullName, email, phone);


        //Log.d(Tag, "oncreate: "+fullName + " "+ email +" "+ phone);
    }
    //overriding the onactivityresult function, the "data" attribute has the img_uri,
    //different functions/intents can be invoking the onActivityResult function therefore,
    //its required/good to check the intent that is invoking the method, so we use reqcode
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            if(resultCode == Activity.RESULT_OK){ //do we have any result on the data? so check
                Uri imageUri = data.getData();
                m_profileImage.setImageURI(imageUri);
                UploadImagetoFirebase(imageUri);
            }
        }

    }

    private void UploadImagetoFirebase(Uri a_imageuri) {
        StorageReference fileRef = storageReference.child("users/"+user.getUid()+"profile.jpg");
        fileRef.putFile(a_imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(UserProfile.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        m_imageUrl= uri.toString();
                        System.out.println("Image uri, you there??? "+ m_imageUrl);
                        Picasso.get().load(uri).into(m_profileImage);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateProfile.this, "Failed uploading image", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void SetUserInfo(String name, String email, String phone){
        m_fullname.setText(name);
        m_email.setText(email);
        m_phonenum.setText(phone);
    }

    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("Hope Hospital App");
        m_fullname= findViewById(R.id.e_fullnameUpdate);
        m_email=findViewById(R.id.e_emailUpdate);
        m_phonenum= findViewById(R.id.e_phonenumUpdate);
        m_saveBtn= findViewById(R.id.b_saveinfo);
        m_profileImage = findViewById(R.id.i_avatar);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user= fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        profileRef = storageReference.child("users/"+user.getUid()+"profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(m_profileImage);
            }
        });
    }
}