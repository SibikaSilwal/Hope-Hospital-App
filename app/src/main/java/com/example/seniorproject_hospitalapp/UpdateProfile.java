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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfile extends GlobalMenuActivity {
    //public static final String Tag = "TAG";
    private Toolbar m_mainToolBar;
    private TextView m_myWardsTxtView;;
    private EditText m_fullname, m_email, m_phonenum, m_Gender, m_BloodGroup, m_Address;
    private Button m_saveBtn;
    private CircleImageView m_profileImage;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser user;
    private StorageReference storageReference, profileRef;
    DocumentReference m_docRef;
    private String m_myWards="Wards you are in:", m_updatedfname, m_updatedemail, m_updatedphone, m_imageUrl, m_UserID=FirebaseAuth.getInstance().getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        SetupUI();
        setSupportActionBar(m_mainToolBar);
        GetWardList();
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
                if(m_updatedfname.isEmpty()||m_updatedemail.isEmpty()){
                    Toast.makeText(UpdateProfile.this, "Field(s) are empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //NEEED TO UPDATE THE EMAIL IN AUTHETICATION ALSO
                user.updateEmail(m_updatedemail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        m_docRef = fStore.collection("users").document(user.getUid());
                        Map<String, Object> edited = new HashMap<>();
                        edited.put("email",m_updatedemail);
                        edited.put("fName", m_updatedfname);
                        edited.put("phone", m_updatedphone);
                        edited.put("gender", m_Gender.getText().toString());
                        edited.put("bloodGroup", m_BloodGroup.getText().toString());
                        edited.put("address", m_Address.getText().toString());
                        edited.put("profileURL", m_imageUrl);
                        m_docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UpdateProfile.this, "Your information was updated successfully.", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(getApplicationContext(), UserProfile.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UpdateProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        GetAndSetUserInfo();
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

    private void SetInfo(EditText a_editTextView, Object a_data){
        if(a_data!=null){
            a_editTextView.setText(a_data.toString());
        }
    }

    private void GetAndSetUserInfo(){
        m_docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()) {
                    SetInfo(m_fullname, task.getResult().get("fName"));
                    SetInfo(m_email, task.getResult().get("email"));
                    SetInfo(m_phonenum, task.getResult().get("phone"));
                    SetInfo(m_Gender, task.getResult().get("gender"));
                    SetInfo(m_BloodGroup, task.getResult().get("bloodGroup"));
                    SetInfo(m_Address, task.getResult().get("address"));
                }
            }
        });
    }

    private void GetWardList(){
        m_docRef.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@com.google.firebase.database.annotations.Nullable DocumentSnapshot snapshot,
                                @com.google.firebase.database.annotations.Nullable FirebaseFirestoreException e) {
                List<Object> wardNames = (List<Object>) snapshot.getData().get("WardName");
                System.out.println(wardNames);
                if(wardNames!= null){
                    for( Object wardname: wardNames)
                    {
                        m_myWards = m_myWards.concat("\n"+wardname.toString()) ;
                    }
                    m_myWardsTxtView.setText(m_myWards);
                    m_myWards ="Wards you are in:";
                }else{
                    m_myWardsTxtView.setText("You do not belong to any wards currently.");
                }
                m_myWardsTxtView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                //SetMargins(m_appointmentsLayout, 0,m_myWardsTxtView.getMeasuredHeight()+250, 0,0);
            }

        });
    }
    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("");
        m_myWardsTxtView = findViewById(R.id.t_yourwards);
        m_fullname= findViewById(R.id.e_fullnameUpdate);
        m_email=findViewById(R.id.e_emailUpdate);
        m_phonenum= findViewById(R.id.e_phonenumUpdate);
        m_Gender = findViewById(R.id.e_gender);
        m_BloodGroup = findViewById(R.id.e_bloodGroup);
        m_Address = findViewById(R.id.e_address);
        m_saveBtn= findViewById(R.id.b_saveinfo);
        m_profileImage = findViewById(R.id.i_avatar);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user= fAuth.getCurrentUser();
        m_docRef = fStore.collection("users").document(m_UserID);
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