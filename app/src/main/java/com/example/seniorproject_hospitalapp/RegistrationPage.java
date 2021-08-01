package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
/**
* This class represents the Registration Page which allows the new users to create an account in the application.
*
* This class makes use of Firebase Email and Password based Authentication to register and authenticate users.
* Therefore, this class takes three main user inputs--User Email, User Full Name, and User Password,
* through Android's EditText view.
* If user is created successfully then, this class navigates user to the Home Page.
*/

public class RegistrationPage extends AppCompatActivity {

    private EditText m_RegisterFullName, m_RegisterEmail, m_RegisterPassword, m_RegisterConfirmPassword;
    private Button m_RegisterButton;
    private TextView m_GotoLogin;
    private String m_name, m_password, m_confirmPassword, m_email, m_UserID;
    private FirebaseAuth m_fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore m_fStore = FirebaseFirestore.getInstance();

    /**/
    /*

    NAME

            onCreate - initializes RegistrationPage activity..

    SYNOPSIS

            protected void onCreate(Bundle a_savedInstanceState);
                a_savedInstanceState     --> the activity's previously frozen state, if there was one

    DESCRIPTION

            This function initialized the RegistrationPage activity and links
            it to its respective layout resource file i.e. activity_registration_page
            which allows retrieving the Widgets and Views used in the layout to
            perform actions and handle events as required, by setting the events
            listeners.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:04pm 01/19/2021

    */
    /**/
    @Override
    protected void onCreate(Bundle a_savedInstanceState) {
        super.onCreate(a_savedInstanceState);

        //links the activity page to its respective layout resource file i.e. activity_registration_page
        setContentView(R.layout.activity_registration_page);

        //initializes all UI components
        SetupUI();

        //if the user is already registered, sends them to HomePage activity
        if(m_fAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), HomePage.class));
        }

        //Provides an option for to login for users who already have an account and Directs user to login page
        m_GotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationPage.this, LoginPage.class));
            }
        });

        /*register users*/
        m_RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validates user inputs
                if(Validate())
                {
                    //Creates new user with email and password in firebase database
                    m_fAuth.createUserWithEmailAndPassword(m_email, m_password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            //stores user information in firestore collection named 'users'.
                            if(task.isSuccessful())
                            {
                                //gets the current user id of currently regitered user
                                m_UserID = m_fAuth.getCurrentUser().getUid();
                                DocumentReference docReference = m_fStore.collection("users").document(m_UserID);
                                Map<String, Object> user = new HashMap<>();
                                user.put("uID", m_UserID);
                                user.put("fName", m_name);
                                user.put("email", m_email);
                                docReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //Sends user to HomePage activity after successful registration.
                                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                                    }
                                });
                            }else{
                                Toast.makeText(RegistrationPage.this,
                                        "Error! "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    /**/
    /*

    NAME

            Validate - validates user inputs.

    SYNOPSIS

            private boolean Validate();

    DESCRIPTION

            This function validates and checks for empty values fo all the user
            inputs in the activity.

    RETURNS

            Returns False if the Email, Full Name, and Password  fields are empty.
            Returns False if the Password field entry is less than 6 characters.
            Returns False if the Password and Confirm Password fields entry do not
            match.
            Returns true if none of the above cases occur.

    AUTHOR

            Sibika Silwal

    DATE

            7:14pm 01/19/2021

    */
    /**/
    private boolean Validate(){
        m_name = m_RegisterFullName.getText().toString().trim();
        m_password = m_RegisterPassword.getText().toString().trim();
        m_confirmPassword = m_RegisterConfirmPassword.getText().toString().trim();
        m_email = m_RegisterEmail.getText().toString().trim();

        if(TextUtils.isEmpty(m_name)){
            m_RegisterFullName.setError("Full name is required.");
            return false;
        }

        if(TextUtils.isEmpty(m_email)){
            m_RegisterEmail.setError("Email is required.");
            return false;
        }

        if(TextUtils.isEmpty(m_password)){
            m_RegisterPassword.setError("Password is required.");
            return false;
        }

        if(m_password.length() < 6){
            m_RegisterPassword.setError("Password must have at least 6 characters.");
            return false;
        }

        if(!m_password.equals(m_confirmPassword)){
            m_RegisterConfirmPassword.setError("Password did not match");
            return false;
        }
        return true;
    }

    /**/
    /*

    NAME

            SetupUI - initializes all UI components

    SYNOPSIS

            private void SetupUI()

    DESCRIPTION

            This function initializes all UI components to their respective Views
            from the layout :activity_registration_page.xml. Uses android method
            findViewById that, "finds a view that was identified by the android:id
           XML attribute that was processed in onCreate(Bundle)." Src: Android Documentation
           (https://developer.android.com/reference/android/app/Activity#findViewById(int))

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:04pm 01/19/2021

    */
    /**/
    private void SetupUI(){
        m_RegisterButton = findViewById(R.id.b_register);
        m_RegisterFullName = findViewById(R.id.e_fullname);
        m_RegisterPassword = findViewById(R.id.e_regpassword);
        m_RegisterConfirmPassword = findViewById(R.id.e_confrimpass);
        m_RegisterEmail = findViewById(R.id.e_email);
        m_GotoLogin = findViewById(R.id.t_gotologin);
    }
}