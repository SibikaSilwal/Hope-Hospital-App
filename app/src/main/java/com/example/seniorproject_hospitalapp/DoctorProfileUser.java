package com.example.seniorproject_hospitalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorProfileUser extends GlobalMenuActivity {

    private Toolbar m_mainToolBar;
    private CircleImageView m_DocImage;
    private TextView m_DocName, m_DocPhone, m_DocEmail, m_DocBio;
    //private FirebaseFirestore m_fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile_user);
        SetupUI();
        setSupportActionBar(m_mainToolBar);
        GetDoctorInfo();
    }

    private void GetDoctorInfo(){
        m_DocName.setText(getIntent().getStringExtra("DocName"));
        if(getIntent().getStringExtra("DocProfileUrl")!=null){
            Picasso.get().load(getIntent().getStringExtra("DocProfileUrl")).into(m_DocImage);
        }
        if(getIntent().getStringExtra("DocEmail")!=null){
            m_DocEmail.setText(getIntent().getStringExtra("DocEmail"));
        }
        if(getIntent().getStringExtra("DocPhone")!=null){
            m_DocPhone.setText(getIntent().getStringExtra("DocPhone"));
        }
        if(getIntent().getStringExtra("DocBio")!=null){
            m_DocBio.setText(getIntent().getStringExtra("DocBio"));
        }
    }

    private void SetupUI(){

        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_DocImage = findViewById(R.id.placeholderIMGDoc);
        m_DocName = findViewById(R.id.t_DocFullName1);
        m_DocEmail = findViewById(R.id.t_DocEmail1);
        m_DocBio = findViewById(R.id.t_docBio);
        m_DocPhone = findViewById(R.id.t_DocPhone1);
        //m_fStore = FirebaseFirestore.getInstance();


    }
}