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

public class RegistrationPage extends AppCompatActivity {

    private EditText reg_Fullname, reg_Email, reg_Password, reg_Confirmpassword;
    private Button reg_Button;
    private TextView goto_Login;
    String m_name , m_password, m_confirmPassword, m_email;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);
        SetupUI();

        //if the user is already registered, send them to next activity
        if(fAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), HomePage.class));
        }
        /*Direct user to login page if already have an account*/
        goto_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationPage.this, LoginPage.class));
            }
        });

        /*register users*/
        reg_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Validate()){
                    //register user to firebase database.
                    fAuth.createUserWithEmailAndPassword(m_email, m_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //task means the task of registering the user
                            if(task.isSuccessful()){
                                userID = fAuth.getCurrentUser().getUid(); //gets the current user id of currently regitered user
                                DocumentReference docReference = fstore.collection("users").document(userID);
                                Map<String, Object> user = new HashMap<>();
                                user.put("uID", userID);
                                user.put("fName", m_name);
                                user.put("email", m_email);
                                user.put("isUser", "1");
                                docReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Tag", "onSuccess: user Profile is created for "+ userID);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Tag", "onFailure: " + e.toString());
                                    }
                                });
                                startActivity(new Intent(getApplicationContext(), HomePage.class));
                            }else{
                                Toast.makeText(RegistrationPage.this, "Error! "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean Validate(){
        //boolean result = false;

        m_name = reg_Fullname.getText().toString().trim();
        m_password = reg_Password.getText().toString().trim();
        m_confirmPassword = reg_Confirmpassword.getText().toString().trim();
        m_email = reg_Email.getText().toString().trim();

        if(TextUtils.isEmpty(m_name)){
            reg_Fullname.setError("Full name is required.");
            return false;
        }

        if(TextUtils.isEmpty(m_email)){
            reg_Email.setError("Email is required.");
            return false;
        }

        if(TextUtils.isEmpty(m_password)){
            reg_Password.setError("Password is required.");
            return false;
        }

        if(m_password.length() < 6){
            reg_Password.setError("Password must have at least 6 characters.");
            return false;
        }

        if(!m_password.equals(m_confirmPassword)){
            reg_Confirmpassword.setError("Password did not match");
            return false;
        }
        return true;
    }

    private void SetupUI(){
        reg_Button = findViewById(R.id.b_register);
        reg_Fullname = findViewById(R.id.e_fullname);
        reg_Password = findViewById(R.id.e_regpassword);
        reg_Confirmpassword = findViewById(R.id.e_confrimpass);
        reg_Email = findViewById(R.id.e_email);
        goto_Login = findViewById(R.id.t_gotologin);
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
    }
}