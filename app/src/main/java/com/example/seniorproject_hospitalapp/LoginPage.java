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

public class LoginPage extends AppCompatActivity {
    private Toolbar m_mainToolBar;
    private static final Object TAG = "tag" ;
    private EditText userName, userPassword;
    private Button loginbutton;
    private TextView gotosignin;
    private ProgressBar progressBar;
    String m_name, m_password;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        SetupUI();

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Validate()){
                    //authenticate the user
                    fAuth.signInWithEmailAndPassword(m_name, m_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //System.out.print("User id: "+fAuth.getCurrentUser().getUid());
                                CheckifAdmin(fAuth.getCurrentUser().getUid());
                                //startActivity(new Intent(getApplicationContext(), HomePage.class));
                            }else{
                                Toast.makeText(LoginPage.this, "Error! "+ task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

        gotosignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this, RegistrationPage.class));
            }
        });
    }

    private boolean Validate(){
        m_name = userName.getText().toString().trim();
        m_password = userPassword.getText().toString().trim();
        if(TextUtils.isEmpty(m_name)){
            userName.setError("Username or email is required.");
            return false;
        }

        if(TextUtils.isEmpty(m_password)){
            userPassword.setError("Password is required.");
            return false;
        }

        progressBar.setVisibility(View.VISIBLE);

        return true;
    }

    private void CheckifAdmin(String a_userId){
        //System.out.print("User id: "+a_userId);
        //Log.d("tag", "USER ID"+ a_userId);
        DocumentReference df = fStore.collection("Admin").document(a_userId);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //identify if user is id or admin
                if(documentSnapshot.getString("isAdmin")!=null){
                    //user is admin, send to admin activity
                    Log.d("tag", "Here??");
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

    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("Hope Hospital App");
        loginbutton = findViewById(R.id.b_loginbutton);
        userName = findViewById(R.id.e_username);
        userPassword = findViewById(R.id.e_password);
        gotosignin = findViewById(R.id.t_signin);
        progressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
    }
}