package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

/**
 * This class represents the Login Page which allows the existing users to log in the application.
 * This is the first page that loads on opening the app for the first time.
 *
 * This class makes use of Firebase Email and Password based Authentication to sign in existing users
 * and authenticates them.
 * This class takes two user inputs--User Email and User Password, through Android's EditText view.
 * If user is authenticated successfully then, this class navigates user to the Home Page.
 *
 */

public class LoginPage extends AppCompatActivity {

    private EditText m_UserNameInput, m_UserPasswordInput;
    private Button m_LoginButton;
    private TextView m_GotoSignIn;
    private ProgressBar m_ProgressBar;
    private String m_name, m_password;
    private FirebaseAuth m_fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore m_fStore = FirebaseFirestore.getInstance();

    /**/
    /*

    NAME

            onCreate - initializes LoginPage activity..

    SYNOPSIS

            protected void onCreate(Bundle a_savedInstanceState);
                a_savedInstanceState     --> the activity's previously frozen state, if there was one

    DESCRIPTION

            This function initialized the LoginPage activity and links
            it to its respective layout resource file i.e. activity_login_page
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
        setContentView(R.layout.activity_login_page);
        SetupUI();

        m_LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Validate()){
                    //authenticate the user by calling signInWithEmailAndPassword method of Firebase authentication through FirebaseAuth object
                    m_fAuth.signInWithEmailAndPassword(m_name, m_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //On successful authentication Navigates Admin to Admin Home and Users to User Home
                                CheckIfAdmin(m_fAuth.getCurrentUser().getUid());
                            }else{
                                //If authentication fails, displays the error message to user.
                                Toast.makeText(LoginPage.this, "Error! "+ task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                m_ProgressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

        //Provides an option to user to go to registration page if they have not registered already.
        m_GotoSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this, RegistrationPage.class));
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

            This function checks for empty values for all the user
            inputs in the activity. It sets progress bar to visible
            while user credentials are being authenticated.

    RETURNS

            Returns False if the Email or Password  fields are empty.
            Returns true if none of the above cases occur.

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 01/19/2021

    */
    /**/
    private boolean Validate(){
        m_name = m_UserNameInput.getText().toString().trim();
        m_password = m_UserPasswordInput.getText().toString().trim();
        if(TextUtils.isEmpty(m_name)){
            m_UserNameInput.setError("Username or email is required.");
            return false;
        }

        if(TextUtils.isEmpty(m_password)){
            m_UserPasswordInput.setError("Password is required.");
            return false;
        }

        m_ProgressBar.setVisibility(View.VISIBLE);

        return true;
    }

    /**/
    /*

    NAME

            CheckIfAdmin - checks if the user is an admin or a regular user.

    SYNOPSIS

            private void CheckIfAdmin(String a_userId);
                a_userId     --> Id of recently logged in user.

    DESCRIPTION

            This function checks if the logging user is an admin or a regular user,
            by looking for "isAdmin" field in the Admin collection for the provided userId.
            If admin, sends the user to Admin Home activity. If not admin, sends
            the user to User Home activity.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:35pm 01/19/2021

    */
    /**/
    private void CheckIfAdmin(String a_userId){
        DocumentReference df = m_fStore.collection("Admin").document(a_userId);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getString("isAdmin")!=null){
                    //User is an admin
                    startActivity(new Intent(getApplicationContext(), AdminHome.class));
                    finish();
                }else{
                    //user is normal user
                    startActivity(new Intent(getApplicationContext(), HomePage.class));
                    finish();
                }
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
            from the layout :activity_login_page.xml. Uses android method
            findViewById that, "finds a view that was identified by the android:id
           XML attribute that was processed in onCreate(Bundle)." Src: Android Documentation
           (https://developer.android.com/reference/android/app/Activity#findViewById(int))

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 01/19/2021

    */
    /**/
    private void SetupUI(){
        m_LoginButton = findViewById(R.id.b_loginbutton);
        m_UserNameInput = findViewById(R.id.e_username);
        m_UserPasswordInput = findViewById(R.id.e_password);
        m_GotoSignIn = findViewById(R.id.t_signin);
        m_ProgressBar = findViewById(R.id.progressBar);
    }
}