package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.TimePickerDialog;
//import android.widget.TimePicker;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
//import com.wdullaer.materialdatetimepicker.time.TimePickerDialog ;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class DoctorProfileAdmin extends AdminMenuActivity implements AdapterView.OnItemSelectedListener{
    Toolbar m_mainToolBar;
    CircleImageView m_Img;
    String m_DocID, m_DoctorWard, m_DocProfileURL;
    TextView m_FullName, m_Email, m_Phone, m_AvailFrom, m_AvailTo, m_DocWardList;
    Button m_SetAvailBtn, m_DocAppointmentsBtn;
    CheckBox m_Sunday, m_Monday, m_Tuesday, m_Wednesday, m_Thursday, m_Friday, m_Saturday;
    Spinner m_Wards;
    private TimePickerDialog timePickerDialog;// = new TimePickerDialog();
    static Integer m_StartHour, m_StartMinute , m_EndHour, m_EndMinute, m_NewStartTime, m_NewEndTime;
    static Boolean setAvailability = false;
    public ArrayList<String> checkedList = new ArrayList<String>() ;

    ArrayList<String> m_WardNamelist;
    ArrayAdapter<String> m_wardNameArrAdapter;

    FirebaseFirestore fstore;
    StorageReference storageReference;
    static DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile_admin);
        SetupUI();
        setSupportActionBar(m_mainToolBar);

        m_AvailFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectStartTime(m_AvailFrom);
            }
        });
        m_AvailTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectEndTime(m_AvailTo);
            }
        });
        m_SetAvailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedList.clear();
                onCheckboxClicked();
                System.out.println(checkedList);
                //AvailData
                Map<String, Object> AvailabilityData = new HashMap<>();
                AvailabilityData.put("Time", m_StartHour+":"+m_StartMinute+"-"+m_EndHour+":"+m_EndMinute);
                AvailabilityData.put("isAvailable", true);
                AvailabilityData.put("AppointmentID", 0);
                AvailabilityData.put("StartHour", m_StartHour);
                AvailabilityData.put("StartMinute", m_StartMinute);
                AvailabilityData.put("EndHour", m_EndHour);
                AvailabilityData.put("EndMinute", m_EndMinute);
                    for(int i =0; i<checkedList.size(); i++) {
                        String day = checkedList.get(i);

                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                List<Map<String, Object>> DayAvailability = (List<Map<String, Object>>) task.getResult().get(day);
                                if (DayAvailability != null) {
                                    for (Map<String, Object> avail : DayAvailability) {
                                        long AvailStart = (long)avail.get("StartHour") * 60 + (long)avail.get("StartMinute");
                                        long AvailEnd = (long)avail.get("EndHour") * 60 + (long)avail.get("EndMinute");
                                        //(StartA <= EndB) and (EndA >= StartB)
                                        if((AvailStart<= m_NewEndTime) && (AvailEnd>=m_NewStartTime)){
                                            setAvailability = false;
                                            // check thisssssssssssss......DayAvailability.clear();
                                        }else{
                                            setAvailability = true;
                                        }
                                    }
                                }else{
                                    setAvailability = true;
                                }
                                if(setAvailability){
                                    docRef.update(day, FieldValue.arrayUnion(AvailabilityData));
                                    Toast.makeText(getApplicationContext(), "Availability set.", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    System.out.println("Availability overlaps with the existing availability.");
                                    Toast.makeText(getApplicationContext(), "Can't set Availability. Availability overlaps with the existing availability.", Toast.LENGTH_SHORT).show();
                                }
                            }

                        });

                    }
                }
        });

        m_Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(UpdateProfile.this, "Clicked...", Toast.LENGTH_SHORT).show()
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        m_WardNamelist = new ArrayList<String>();
        m_WardNamelist.add("Select a Ward.");
        m_wardNameArrAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, m_WardNamelist);
        m_Wards.setAdapter(m_wardNameArrAdapter);
        Fetchdata();

        m_Wards.setOnItemSelectedListener(this);

        m_DocAppointmentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ManageDocAppointment.class);
                i.putExtra("docID", m_DocID); //sending the data in key value pair to updateprofile activity
                startActivity(i);
            }
        });
    }

    public void onCheckboxClicked(){
        if (m_Sunday.isChecked())
        {
            checkedList.add("Sunday");
        }
        if (m_Monday.isChecked())
        {
            checkedList.add("Monday");
        }
        if (m_Tuesday.isChecked())
        {
            checkedList.add("Tuesday");
        }
        if (m_Wednesday.isChecked())
        {
            checkedList.add("Wednesday");
        }
        if (m_Thursday.isChecked())
        {
            checkedList.add("Thursday");
        }
        if (m_Friday.isChecked())
        {
            checkedList.add("Friday");
        }
        if (m_Saturday.isChecked())
        {
            checkedList.add("Saturday");
        }
    }

    public void SelectStartTime(TextView a_DateTimeTextView){
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
              @Override
              public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        a_DateTimeTextView.setText(hourOfDay + ":" + minute);
                        m_StartHour = hourOfDay;
                        m_StartMinute = minute;
                        m_NewStartTime = m_StartHour * 60 + m_StartMinute;
                    }
                };

              timePickerDialog = new TimePickerDialog(DoctorProfileAdmin.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),false);
              timePickerDialog.setTitle("Select availability start time.");
              timePickerDialog.show();
    }

    public void SelectEndTime(TextView a_DateTimeTextView){
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                a_DateTimeTextView.setText(hourOfDay + ":" + minute);
                m_EndHour = hourOfDay;
                m_EndMinute = minute;
                m_NewEndTime = m_EndHour * 60 + m_EndMinute;
            }
        };
        new TimePickerDialog(DoctorProfileAdmin.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),false).show();
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
        //getting the wards of the doctors
        docRef.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                List<Object> wardNames = (List<Object>) snapshot.getData().get("WardName");
                if(wardNames!= null){
                    for( Object wardname: wardNames)
                    {
                        m_DoctorWard = m_DoctorWard.concat("\n"+wardname.toString()) ;
                        //System.out.println("wards: "+ wardname.toString());
                        //System.out.println(m_PatientWard);
                    }
                    m_DocWardList.setText(m_DoctorWard);
                    m_DoctorWard = "Wards Doctor " + m_FullName.getText().toString() + " serves in:";
                }
            }

        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getItemAtPosition(position).equals("Select a Ward.")){
            //do nothing
        }else{
            //updates Doctor's document with wardname
            DocumentReference docRefDoctor = fstore.collection("Doctors").document(m_DocID);
            docRefDoctor.update("WardName", FieldValue.arrayUnion(parent.getItemAtPosition(position)));
            //updates ward document with doctor's id
            DocumentReference docRefWard = fstore.collection("Wards").document(parent.getItemAtPosition(position).toString());
            docRefWard.update("WardDoctorID", FieldValue.arrayUnion(m_DocID));
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            if(resultCode == Activity.RESULT_OK){ //do we have any result on the data? so check
                Uri imageUri = data.getData();
                m_Img.setImageURI(imageUri);
                UploadImagetoFirebase(imageUri);
            }
        }
    }

    private void UploadImagetoFirebase(Uri a_imageuri) {
        StorageReference fileRef = storageReference.child("Doctors/"+m_DocID+"profile.jpg");
        fileRef.putFile(a_imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(UserProfile.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //m_imageUrl= uri.toString();
                        //System.out.println("Image uri, you there??? "+ m_imageUrl);
                        Picasso.get().load(uri).into(m_Img);
                        Map<String, Object> edited = new HashMap<>();
                        edited.put("profileURL", uri.toString());
                        docRef.update(edited);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DoctorProfileAdmin.this, "Failed uploading image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SetupUI() {
        m_mainToolBar = findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("Hope Hospital App");
        m_FullName = findViewById(R.id.t_DocFullName1);
        m_Email = findViewById(R.id.t_DocEmail);
        m_Img = findViewById(R.id.placeholderIMGDoc);
        m_FullName.setText(getIntent().getStringExtra("DocName").toString());
        //m_Email.setText(getIntent().getStringExtra("uEmail").toString());
        //m_Phone.setText(getIntent().getStringExtra("uPhone").toString());
        m_DocID= getIntent().getStringExtra("DocId").toString();
        if(getIntent().getStringExtra("DocProfileUrl")!=null){
            m_DocProfileURL = getIntent().getStringExtra("DocProfileUrl").toString();
        }

        if(m_DocProfileURL!=null && m_DocProfileURL!=""){
            Picasso.get().load(m_DocProfileURL).into(m_Img);
        }

        m_AvailFrom = findViewById(R.id.t_availfrom);
        m_AvailTo = findViewById(R.id.t_availTo);
        m_SetAvailBtn = findViewById(R.id.b_setAvail);
        m_DocAppointmentsBtn = findViewById(R.id.b_manageDocAppt);

        m_Sunday = findViewById(R.id.SundayCB);
        m_Monday = findViewById(R.id.MondayCB);
        m_Tuesday = findViewById(R.id.TuesdayCB);
        m_Wednesday = findViewById(R.id.WednesdayCB);
        m_Thursday = findViewById(R.id.ThursdayCB);
        m_Friday = findViewById(R.id.FridayCB);
        m_Saturday = findViewById(R.id.SaturdayCB);

        m_DocWardList = findViewById(R.id.t_docWardList);
        m_Wards = findViewById(R.id.spinnerDocWard);
        m_DoctorWard = "Wards Doctor " + m_FullName.getText().toString() + " serves in:";
        m_DocWardList.setText(m_DoctorWard);

        fstore = FirebaseFirestore.getInstance();
        docRef = fstore.collection("Doctors").document(m_DocID);
        storageReference = FirebaseStorage.getInstance().getReference();
    }
}