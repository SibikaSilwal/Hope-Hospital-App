package com.example.seniorproject_hospitalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/*
* This class represents the DoctorProfileUser activity for User Interface.
* This class receives Doctor's information from an intent, and sets all the
* information in their respective text views in the activity. This activity is
* displayed only to users to view Doctor's profile and learn more about them.
*
*/
public class DoctorProfileUser extends GlobalMenuActivity {

    private Toolbar m_mainToolBar;
    private CircleImageView m_DocImage;
    private TextView m_DocName, m_DocPhone, m_DocEmail, m_DocBio;

    /**/
    /*

    NAME

            onCreate - initializes DoctorProfileUser activity..

    SYNOPSIS

            protected void onCreate(Bundle a_savedInstanceState);
                a_savedInstanceState     --> the activity's previously frozen state, if there was one

    DESCRIPTION

            This function initialized the DoctorProfileUser activity and links
            it to its respective layout resource file i.e. activity_doctor_profile_user
            which allows retrieving the Widgets and Views used in the layout to
            perform actions and handle events as required, by setting the events
            listeners.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            3:00pm 07/15/2021

    */
    /**/
    @Override
    protected void onCreate(Bundle a_savedInstanceState) {
        super.onCreate(a_savedInstanceState);
        setContentView(R.layout.activity_doctor_profile_user);
        SetupUI();
        setSupportActionBar(m_mainToolBar);
        GetSetDoctorInfo();
    }

    /**/
    /*

    NAME

            GetSetDoctorInfo - gets and sets doctor info in respective text views

    SYNOPSIS

            private void GetSetDoctorInfo()

    DESCRIPTION

            This function gets doctor information from the passing intent, and sets the
            information in their respective views.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            3:40pm 07/15/2021

    */
    /**/
    private void GetSetDoctorInfo(){
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

    /**/
    /*

    NAME

            SetupUI - initializes all UI components

    SYNOPSIS

            private void SetupUI()

    DESCRIPTION

            This function initializes all UI components to their respective Views
            from the layout :activity_doctor_profile_user.xml. Uses android method
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

            7:35pm 07/15/2021

    */
    /**/
    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_DocImage = findViewById(R.id.placeholderIMGDoc);
        m_DocName = findViewById(R.id.t_DocFullName1);
        m_DocEmail = findViewById(R.id.t_DocEmail1);
        m_DocBio = findViewById(R.id.t_docBio);
        m_DocPhone = findViewById(R.id.t_DocPhone1);
    }
}