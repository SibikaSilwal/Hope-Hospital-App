package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.SnapshotMetadata;
import com.squareup.okhttp.internal.DiskLruCache;

public class HomePage extends GlobalMenuActivity {
    TextView m_fullName, m_email, m_verifyEmailMsg, m_Message;
    Button m_verifyButton;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String m_userId;
    DocumentReference docReference;
    Toolbar m_mainToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        SetupUI();
        //toolbar
        setSupportActionBar(m_mainToolBar);


        if(!fAuth.getCurrentUser().isEmailVerified()){
            m_verifyButton.setVisibility(View.VISIBLE);
            m_verifyEmailMsg.setVisibility(View.VISIBLE);
        }

        m_verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(HomePage.this, "Verification Email Sent.", Toast.LENGTH_SHORT).show();
                        m_verifyButton.setVisibility(View.GONE);
                        m_verifyEmailMsg.setVisibility(View.GONE);
                    }
                });
            }
        });
       /* docReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                m_fullName.setText(value.getString("fName"));
                m_email.setText(value.getString("email"));
            }
        });*/

    }

    //private void setSupportActionBar(Toolbar m_mainToolBar) {
    //}

    public void Logout(View a_view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginPage.class));
        finish();
    }

    private void SetupUI(){
        m_fullName = findViewById(R.id.t_userFullName);
        m_email = findViewById(R.id.t_userEmail);
        m_verifyEmailMsg = findViewById(R.id.t_verifyemail);
        m_verifyButton = findViewById(R.id.b_verifyEmail);
        m_Message = findViewById(R.id.t_message);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        m_userId = fAuth.getCurrentUser().getUid();
        docReference = fStore.collection("users").document(m_userId);
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("Hope Hospital App");

        docReference.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@com.google.firebase.database.annotations.Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        m_Message.setText("Welcome! You are signed in as " + snapshot.getData().get("fName").toString()+".");
                    }
        });

    }
}

