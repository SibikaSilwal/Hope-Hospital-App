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

/**
 * This class represents the UpdateProfile page in Patient / User interface. This
 * class allows user to view their profile information and update them if necessary.
 */

public class UpdateProfile extends GlobalMenuActivity {

    private Toolbar m_mainToolBar;
    private TextView m_myWardsTxtView;;
    private EditText m_fullname, m_email, m_phonenum, m_Gender, m_BloodGroup, m_Address;
    private Button m_saveBtn;
    private CircleImageView m_profileImage;
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private String m_UserID=FirebaseAuth.getInstance().getUid();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseUser user = fAuth.getCurrentUser();
    private DocumentReference m_docRef = fStore.collection("users").document(m_UserID);
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    /**/
    /*

    NAME

            onCreate - initializes UpdateProfile activity..

    SYNOPSIS

            protected void onCreate(Bundle a_savedInstanceState);
                a_savedInstanceState     --> the activity's previously frozen state, if there was one

    DESCRIPTION

            This function initialized the UpdateProfile activity and links
            it to its respective layout resource file i.e. activity_update_profile
            which allows retrieving the Widgets and Views used in the layout to
            perform actions and handle events as required, by setting the events
            listeners.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            12:20pm 01/25/2021

    */
    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        SetupUI();
        setSupportActionBar(m_mainToolBar);

        /*Invokes the onActivityResult function to open the device's gallery, when clicking on profile image*/
        m_profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        /*On clicking the save Button, updates the user information in database*/
        m_saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedName= m_fullname.getText().toString();
                String updatedEmail=m_email.getText().toString();
                if(updatedName.isEmpty()||updatedEmail.isEmpty()){
                    Toast.makeText(UpdateProfile.this, "Field(s) are empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //NEEED TO UPDATE THE EMAIL IN AUTHETICATION ALSO
                user.updateEmail(updatedEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        UserDataModel currentUser = new UserDataModel();

                        //Calling UserDataModel's class UpdateGenericInfo function, that updates the information in database
                        currentUser.UpdateGenericInfo(m_UserID,
                                                      updatedEmail,
                                                      updatedName,
                                                      m_phonenum.getText().toString(),
                                                      m_Gender.getText().toString(),
                                                      m_BloodGroup.getText().toString(),
                                                      m_Address.getText().toString(),
                                             UpdateProfile.this);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**/
    /*

    NAME

            onActivityResult - fires new intent to pick image from Media

    SYNOPSIS

            private void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
                requestCode  --> an integer to identify the intent firing onActivityResult
                resultCode   --> an integer value that represents the status of the result of onActivityResult
                data         --> intent data returned from the launched intent

    DESCRIPTION

            This function overrides the onActivityResult function and is called when the profile
            image view is clicked, to pick a new profile image from Device's Media Gallery.
            Different functions/intents can be invoking the onActivityResult function
            therefore, to check the intent that is invoking the method, its request code
            is checked, in this case request code 1000. The "data" attribute has the img_uri, that
            has been picked from the gallery. The image uri is passed to the UploadImagetoFirebase
            function and is called.


    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            6:30pm 01/25/2021

    */
    /**/
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

    /**/
    /*

    NAME

            UploadImagetoFirebase - uploads the given image uri to Firebase Storage

    SYNOPSIS

            UploadImagetoFirebase(Uri a_imageUri);
                a_imageUri  ---> The picked Image's URI to be uploaded to database

    DESCRIPTION

            This function receives an image uri and uploads that image to Firebase Storage
            to the User's profile image bucket. It also saves the user's profile uri as
            a string in FireStore inside the user's document. To display the picked image
            in the image view, this function makes use of Picasso library and load the image
            into profile image view.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            6:50pm 01/25/2021

    */
    /**/
    private void UploadImagetoFirebase(Uri a_imageUri) {
        StorageReference fileRef = storageReference.child("users/"+user.getUid()+"profile.jpg");
        fileRef.putFile(a_imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl= uri.toString();
                        Picasso.get().load(uri).into(m_profileImage);
                        UserDataModel user = new UserDataModel();
                        user.setProfileURL(m_UserID, imageUrl);
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

    /**/
    /*

    NAME

            GetAndSetUserInfo - retrieves and sets user information in the UpdateProfile activity

    SYNOPSIS

            private void GetAndSetUserInfo()

    DESCRIPTION

            This function retrieves the user's profile information from database
            displays it on the UpdateProfile page in their respective text views.
            This function passes all the user information to UserDataModel class
            object, and makes use of the UserDataModel class function to get user
            information.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            6:30pm 01/25/2021

    */
    /**/
    private void GetAndSetUserInfo(){
        m_docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserDataModel user = documentSnapshot.toObject(UserDataModel.class);
                m_fullname.setText(user.getfName());
                m_email.setText(user.getEmail());
                m_phonenum.setText(user.getPhone());
                m_Gender.setText(user.getGender());
                m_BloodGroup.setText(user.getBloodGroup());
                m_Address.setText(user.getAddress());
                m_myWardsTxtView.setText(user.getWardNameAsString("Wards you are in:"));
                if(user.getProfileURL()!=null&&user.getProfileURL()!="")
                    Picasso.get().load(user.getProfileURL()).into(m_profileImage);
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
            from the layout :activity_update_profile.xml. Uses android method
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

            5:30pm 01/20/2021

    */
    /**/
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

        GetAndSetUserInfo();
    }
}