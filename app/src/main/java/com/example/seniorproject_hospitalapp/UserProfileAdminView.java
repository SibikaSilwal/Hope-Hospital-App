package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileAdminView extends AdminMenuActivity implements AdapterView.OnItemSelectedListener {
    Toolbar m_mainToolBar;
    TextView m_FullName, m_Email, m_Phone, m_WardList;
    CircleImageView m_Img;
    String m_UserID, m_ImgURL;
    Button m_SheduleApptBtn;
    Spinner m_wardNameSpinner;
    FirebaseFirestore fstore;
    String m_PatientWard;

    //ValueEventListener m_listener;
    ArrayList<String> m_WardNamelist=new ArrayList<>();
    ArrayList<String> m_PatientsWardList = new ArrayList<>();
    ArrayAdapter<String> m_wardNameArrAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_admin_view);
        SetupUI();
        setSupportActionBar(m_mainToolBar);

        if(m_ImgURL==null || m_ImgURL.trim().length() == 0){
            m_Img.setImageResource(R.drawable.profileavatar);
        }else{
            Picasso.get().load(m_ImgURL).into(m_Img);
        }

        m_SheduleApptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("schedulebtn: "+ m_PatientsWardList);
                Intent intent = new Intent(v.getContext(), Appointment.class);
                intent.putExtra("patientID", m_UserID);
                intent.putExtra("email", m_Email.getText().toString());
                intent.putExtra("phone", m_Phone.getText().toString());
                intent.putExtra("wards", m_PatientsWardList);
                startActivity(intent);
            }
        });

        m_WardNamelist = new ArrayList<String>();
        m_WardNamelist.add("Select a Ward.");
        m_wardNameArrAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, m_WardNamelist);
        m_wardNameSpinner.setAdapter(m_wardNameArrAdapter);
        //m_wardNameSpinner.setPrompt("Select a Ward.");
        Fetchdata();

        m_wardNameSpinner.setOnItemSelectedListener(this);

    }
    private void Fetchdata(){
        //populating the wardname spinner by adding the ward names to the m_wardnamelist arraylist
        fstore.collection("Wards")
                .whereNotEqualTo("WardName", null)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                m_WardNamelist.add((document.getData().get("WardName")).toString());
                            }
                            m_wardNameArrAdapter.notifyDataSetChanged();
                        } else {
                            System.out.println("Error getting Ward Names: "+ task.getException());
                        }
                    }
                });
        //getting the wards of the user/patient
        DocumentReference docRef = fstore.collection("users").document(m_UserID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                List<Object> wardNames = (List<Object>) task.getResult().get("WardName");
                System.out.println("wardnames:::::" + wardNames);
                if(wardNames!= null){
                    for( Object wardname: wardNames)
                    {
                        System.out.println("123: "+ wardname.toString() + "........."+wardname);
                        m_PatientWard = m_PatientWard.concat("\n"+wardname.toString()) ;
                        m_PatientsWardList.add(wardname.toString());
                    }
                    m_WardList.setText(m_PatientWard);
                    m_PatientWard = "Wards " + m_FullName.getText().toString() + " is in:";
                    System.out.println("Patientwardlist: "+ m_PatientsWardList);
                }
                System.out.println("out of if block");
            }
        });
    }

    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("Hope Hospital App");
        m_SheduleApptBtn= findViewById(R.id.b_ScheduleApptforUser);
        m_FullName= findViewById(R.id.t_UserFullName1);
        m_Email = findViewById(R.id.t_userEmail2);
        m_Phone= findViewById(R.id.t_userPhone1);
        m_Img= (CircleImageView)findViewById(R.id.placeholderIMG);
        m_WardList = findViewById(R.id.t_PatientWardList);

        m_FullName.setText(getIntent().getStringExtra("uName").toString());
        m_Email.setText(getIntent().getStringExtra("uEmail").toString());
        m_Phone.setText(getIntent().getStringExtra("uPhone").toString());
        m_UserID= getIntent().getStringExtra("userId").toString();
        m_ImgURL = getIntent().getStringExtra("imgURL");

        fstore = FirebaseFirestore.getInstance();
        m_wardNameSpinner = findViewById(R.id.wardSpinner);

        m_PatientWard = "Wards " + m_FullName.getText().toString() + " is in:";
        m_WardList.setText(m_PatientWard);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getItemAtPosition(position).equals("Select a Ward.")){
            //do nothing
        }else{
            DocumentReference docRef = fstore.collection("users").document(m_UserID);
            docRef.update("WardName", FieldValue.arrayUnion(parent.getItemAtPosition(position)));

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}