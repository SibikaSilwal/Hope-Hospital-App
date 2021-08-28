package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
//import android.widget.TimePicker;
//import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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

/*
 * This class represents the DoctorProfileAdmin activity which provides an interface
 * for admins to view and update Doctor' profile. This class provides all the functionalities
 * needed for an admin to update and set Doctor's information like set availabilities for
 * doctors, update doctor's profile data. And provides navigation option to view and manage
 * doctor's appointments through ManageDocAppointment page.
 *
 */
public class DoctorProfileAdmin extends AdminMenuActivity implements AdapterView.OnItemSelectedListener{
    private LinearLayout m_LinearLayoutDocAppt, m_LinearLyaoutEditDocProfile;
    Toolbar m_mainToolBar;
    CircleImageView m_Img;
    String m_DocID, m_DoctorWard, m_DocProfileURL, m_selectDate;
    TextView m_FullName, m_Email, m_Phone, m_AvailFrom, m_AvailTo, m_DocWardList;
    EditText m_EditDocPhone, m_EditDocEmail, m_EditDocBio;
    Button m_SetAvailBtn, m_datePickBtn;
    Spinner m_Wards;
    LinearLayout m_EditDoctorProfileLayout;
    private DatePickerDialog m_datePickerDialog;
    private TimePickerDialog timePickerDialog;
    static Integer m_StartHour, m_StartMinute , m_EndHour, m_EndMinute, m_StartTime, m_EndTime,m_Month, m_Year, m_DayOfMonth;
    static Boolean m_setAvailability = false;

    ArrayList<String> m_WardNamelist;
    ArrayAdapter<String> m_wardNameArrAdapter;

    //Appointment class object to use the appointment class method in ManageDocAppointment Class.
    private Appointment m_apptObject = new Appointment();

    FirebaseFirestore m_fstore = FirebaseFirestore.getInstance();
    StorageReference m_storageReference = FirebaseStorage.getInstance().getReference();
    DocumentReference m_docref;

    /**/
    /*

    NAME

            onCreate - initializes DoctorProfileAdmin activity..

    SYNOPSIS

            protected void onCreate(Bundle a_savedInstanceState);
                a_savedInstanceState     --> the activity's previously frozen state, if there was one

    DESCRIPTION

            This function initializes the DoctorProfileAdmin activity and links it to its
            respective layout resource file i.e. activity_doctor_profile_admin.xml
            which allows retrieving the Widgets and Views used in the layout to
            perform actions and handle events as required, by setting the events
            listeners.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:20pm 03/15/2021

    */
    /**/
    @Override
    protected void onCreate(Bundle a_savedInstanceState) {
        super.onCreate(a_savedInstanceState);
        setContentView(R.layout.activity_doctor_profile_admin);
        SetupUI();
        setSupportActionBar(m_mainToolBar);
        InitDatePicker();

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
            public synchronized void onClick(View v) {
                //check if date and time are chosen
                if(m_selectDate==null || m_StartHour == null){
                    Toast.makeText(getApplicationContext(), "Please select both Date and Time.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //check if time is formatted correctly
                if((m_StartHour*60 + m_StartMinute)>(m_EndHour*60+m_EndMinute)){
                    Toast.makeText(getApplicationContext(), "Start time cannot be greater than end time!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String day = m_selectDate;

                Long reminderTime = GetReminderTime();

                //Creating an availability map to store in Doctor's document in the database
                Map<String, Object> AvailabilityData = new HashMap<>();
                AvailabilityData.put("Time", m_StartHour+":"+m_StartMinute+"-"+m_EndHour+":"+m_EndMinute);
                AvailabilityData.put("isAvailable", true);
                AvailabilityData.put("AppointmentID", 0);
                AvailabilityData.put("StartHour", m_StartHour);
                AvailabilityData.put("StartMinute", m_StartMinute);
                AvailabilityData.put("EndHour", m_EndHour);
                AvailabilityData.put("EndMinute", m_EndMinute);
                AvailabilityData.put("ReminderTime", reminderTime);

                m_docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        List<Map<String, Object>> DayAvailability = (List<Map<String, Object>>) task.getResult().get(day);
                        if (DayAvailability != null) {
                            for (Map<String, Object> avail : DayAvailability) {
                                CheckTimeOverlaps((long)avail.get("StartHour"),(long)avail.get("StartMinute"),(long)avail.get("EndHour"),(long)avail.get("EndMinute"));
                            }
                        }else{
                            m_setAvailability = true;
                        }
                        if(m_setAvailability){
                            m_docref.update(day, FieldValue.arrayUnion(AvailabilityData));
                            Toast.makeText(getApplicationContext(), "Availability set.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //profile image on click: pick image to change Doctor's profile image
        m_Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        m_WardNamelist = new ArrayList<String>();
        m_WardNamelist.add("Select a Ward.");
        m_wardNameArrAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, m_WardNamelist);
        m_Wards.setAdapter(m_wardNameArrAdapter);
        PopulateWardSpinner();

        m_Wards.setOnItemSelectedListener(this);

    }

    /**/
    /*

    NAME

            CheckTimeOverlaps - checks if two time ranges overlaps

    SYNOPSIS

            private void CheckTimeOverlaps(long a_StartHour, long a_StartMinute, long a_EndHour, long a_EndMinute)
                a_StartHour    --> time range start hour. Eg. in 12:30 - 1:30, a_StartHour = 12
                a_StartMinute  --> time range start minute. Eg. in 12:30 - 1:30, a_StartMinute = 30
                a_EndHour      --> time range end hour. Eg. in 12:30 - 1:30, a_EndHour = 1
                a_EndMinute    --> time range end minute. Eg. in 12:30 - 1:30, a_EndMinute = 30

    DESCRIPTION

            This function accepts time range in the form of start hours/minute and end hour/minutes.
            It checks if the given time range overlaps with the time range about to be set as
            Doctors availability. If availability overlaps it toasts a message to the user notifying
            them about the  situation. This makes sure that no overlapping availabilities are set for
            Doctors in the database.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/15/2021

    */
    /**/
    private void CheckTimeOverlaps(long a_StartHour, long a_StartMinute, long a_EndHour, long a_EndMinute){
        long AvailStart = a_StartHour* 60 + a_StartMinute;
        long AvailEnd = a_EndHour * 60 + a_EndMinute;
        if((AvailStart<= m_EndTime) && (AvailEnd>=m_StartTime)){
            m_setAvailability = false;
            Toast.makeText(getApplicationContext(), "Can't set Availability. Availability overlaps with the existing availability.", Toast.LENGTH_SHORT).show();
            return;
        }else{
            m_setAvailability = true;
        }
    }

    /**/
    /*

    NAME

            GetReminderTime - Gets reminder time for the new availability being set

    SYNOPSIS

            private Long GetReminderTime()

    DESCRIPTION

            This function computes the reminder time in millisecond for new availability being set.
            The reminder time should always be an hour before the appointment time.

    RETURNS

            Long: reminder time in millisecond

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/15/2021

    */
    /**/
    private Long GetReminderTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,m_Year);
        calendar.set(Calendar.MONTH, m_Month);
        calendar.set(Calendar.DAY_OF_MONTH, m_DayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, m_StartHour);
        calendar.set(Calendar.MINUTE, m_StartMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.HOUR, -1);
        Long reminderTime = calendar.getTimeInMillis();
        return reminderTime;
    }
    /**/
    /*

    NAME

            DoctorAppointments - navigates user to ManageDocAppointment Page

    SYNOPSIS

            public void DoctorAppointments(View a_view)
                a_view     --> view provided

    DESCRIPTION

            This function navigates user to ManageDocAppointment. The function
            gets called when the ViewDoctorAppointments grid view is clicked

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/15/2021

    */
    /**/
    public void DoctorAppointments(View a_view){
        Intent i = new Intent(a_view.getContext(), ManageDocAppointment.class);
        i.putExtra("docID", m_DocID);
        startActivity(i);
    }

    /**/
    /*

    NAME

            EditDoctorProfile - sets visibility for Doctor's edit profile view

    SYNOPSIS

            public void EditDoctorProfile(View a_view)
                a_view     --> view provided

    DESCRIPTION

            This function toggles visibility for Doctor's edit profile view betwwen
            'visible' and 'gone'. The function gets called when the Edi doctor profile
            grid view is clicked.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            8:30pm 03/15/2021

    */
    /**/
    public void EditDoctorProfile(View a_view){
        if(m_EditDoctorProfileLayout.getVisibility()==View.GONE){
            m_EditDoctorProfileLayout.setVisibility(View.VISIBLE);
        }else{
            m_EditDoctorProfileLayout.setVisibility(View.GONE);
        }
    }

    /**/
    /*

    NAME

            InitDatePicker - Initializes date picker dialog

    SYNOPSIS

            private void InitDatePicker()

    DESCRIPTION

            This function accepts date inputs from users to set availability
            for doctors for the given data. It uses android's DatePickerDialog
            class which gives a date picker pop up view, where users can pick a date.
            Then th date values (year, month, and day) are saved in the respective
            variable and are later stored in the database by another function.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/15/2021

    */
    /**/
    private void InitDatePicker()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        m_datePickBtn.setText("Please select a date.");
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                m_Month = month;
                m_Year = year;
                m_DayOfMonth = dayOfMonth;
                m_selectDate = m_apptObject.MakeDateString(dayOfMonth,month+1, year);
                m_datePickBtn.setText(m_selectDate);
            }
        };
        int style = AlertDialog.THEME_HOLO_LIGHT;

        m_datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        m_datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis()- 10000);
    }

    /**/
    /*

    NAME

            OpenDatePicker - navigates user to UserAppointmentsPage

    SYNOPSIS

            public void OpenDatePicker(View a_view)
                a_view     --> view provided

    DESCRIPTION

            This function shows date picker dialog box. The function
            gets called when the datepicker button view is clicked

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            8:30pm 03/15/2021

    */
    /**/
    public void OpenDatePicker(View a_view){
        m_datePickerDialog.show();
    }

    /**/
    /*

    NAME

            SelectStartTime - gets availability start time input

    SYNOPSIS

            private void SelectStartTime(TextView a_DateTimeTextView)
                a_DateTimeTextView    --> day value passed as an integer

    DESCRIPTION

            This function accepts time inputs from users to set availability
            start time for doctors. It uses android's TimePickerDialog class which
            gives a time picker pop up view, where users can pick a time, whose
            values (hours and minutes) are saved in the respective variable and
            are later stored in the database by another function.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/15/2021

    */
    /**/
    private void SelectStartTime(TextView a_DateTimeTextView){
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
                        m_StartTime = m_StartHour * 60 + m_StartMinute;
                    }
                };

              timePickerDialog = new TimePickerDialog(DoctorProfileAdmin.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),false);
              timePickerDialog.setTitle("Select availability start time.");
              timePickerDialog.show();
    }

    /**/
    /*

    NAME

            SelectEndTime - gets availability end time input

    SYNOPSIS

            private void SelectEndTime(TextView a_DateTimeTextView)
                a_DateTimeTextView    --> day value passed as an integer

    DESCRIPTION

            This function is accepts time inputs from users to set availability
            end time for doctors. It uses android's TimePickerDialog class which
            gives a time picker pop up view, where users can pick a time, whose
            values (hours and minutes) are saved in the respective variable and
            are later stored in the database by another function.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/15/2021

    */
    /**/
    private void SelectEndTime(TextView a_DateTimeTextView){
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
                m_EndTime = m_EndHour * 60 + m_EndMinute;
            }
        };
        timePickerDialog = new TimePickerDialog(DoctorProfileAdmin.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),false);
        timePickerDialog.setTitle("Select availability end time.");
        timePickerDialog.show();
    }

    /**/
    /*

    NAME

            PopulateWardSpinner - populates ward spinner with ward names

    SYNOPSIS

            private void PopulateWardSpinner()

    DESCRIPTION

            This function queries the database, gets all the ward names registered
            in the database, and adds all ward names to the Ward spinner.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/15/2021

    */
    /**/
    private void PopulateWardSpinner() {

        //populating the wardname spinner by adding the ward names to the m_wardnamelist arraylist
        m_fstore.collection("Wards")
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
                        }
                    }
                });
    }

    /**/
    /*

    NAME

            onItemSelected - is invoked when an item in WardList dropdown view is selected.

    SYNOPSIS

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                parent   --> The AdapterView where the selection happened
                view     --> The view within the AdapterView that was clicked
                position --> The position of the view in the adapter
                id       --> The row id of the item that is selected

    DESCRIPTION

            This function is called when an admin selects a Ward from the Ward's names
            dropdown to add to doctor's ward list. If the placeholder text "Select a Ward."
            is selected nothing happens. Else, the selected ward is added to the doctor's
            ward array, ward's doctors array in the database and is reflected immediately in
            the app interface

            Synopsis Src: Android Documentation
           (https://developer.android.com/reference/android/widget/AdapterView.OnItemSelectedListener)


    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/15/2021

    */
    /**/
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getItemAtPosition(position).equals("Select a Ward.")){
            //do nothing
        }else{
            //Adds Ward to the Doctor's document
            DocumentReference docRefDoctor = m_fstore.collection("Doctors").document(m_DocID);
            docRefDoctor.update("WardName", FieldValue.arrayUnion(parent.getItemAtPosition(position)));

            //Adds Doctor to the Ward Document
            DocumentReference docRefWard = m_fstore.collection("Wards").document(parent.getItemAtPosition(position).toString());
            docRefWard.update("WardDoctorID", FieldValue.arrayUnion(m_DocID));

            //reflect the change in the app interface
            m_DocWardList.setText(m_DocWardList.getText()+"\n"+parent.getItemAtPosition(position));
        }
    }

    //This is a required function since the class implements AdapterView.OnItemSelectedListener
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

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

            This function overrides the onActivityResult function and is called when the Doctor's
            profile image view is clicked, to pick a new profile image from Device's Media Gallery.
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

            7:30pm 03/15/2021

    */
    /**/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                m_Img.setImageURI(imageUri);
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
            to the Doctor's profile image bucket. It also saves the Doctor's profile uri as
            a string in FireStore inside the Doctor's document. To display the picked image
            in the image view, this function makes use of Picasso library and load the image
            into profile image view.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:50pm 03/15/2021

    */
    /**/
    private void UploadImagetoFirebase(Uri a_imageuri) {
        StorageReference fileRef = m_storageReference.child("Doctors/"+m_DocID+"profile.jpg");
        fileRef.putFile(a_imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(m_Img);
                        m_docref.update("profileURL", uri.toString());
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

    /**/
    /*

    NAME

            EditDocProfile - saves Doctor's new information in database

    SYNOPSIS

            public void EditDocProfile(View a_view)
                a_view     --> view provided

    DESCRIPTION

            This function is set as an OnClick function for Save button in
            activity_doctor_profile_admin.xml. This function takes the user
            inputs for Doctor's information and updates the received data
            in the database.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/15/2021

    */
    /**/
    public void EditDocProfile(View a_view){
        Map<String, Object> DocProfile = new HashMap<>();
        DocProfile.put("DocEmail", m_EditDocEmail.getText().toString());
        DocProfile.put("DocPhone", m_EditDocPhone.getText().toString());
        DocProfile.put("DocBio", m_EditDocBio.getText().toString());
        m_docref.update(DocProfile);
        Toast.makeText(this, "Doctor's profile updated.", Toast.LENGTH_SHORT).show();
    }

    /**/
    /*

    NAME

            SetInfo - Sets passed string text in the passed views

    SYNOPSIS

            private void SetInfo(TextView a_View, String a_data)
                a_View  --> View to set the text on
                a_data  --> Text to be set on the view

    DESCRIPTION

            This function checks for null for the provided data, and if not null
            sets the data / text to the view that is passed as an argument

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/15/2021

    */
    /**/
    private void SetInfo(TextView a_View, String a_data){
        if(a_data!=null){
            a_View.setText(a_data);
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
            from the layout :activity_doctor_profile_admin.xml. Uses android method
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

            7:30pm 03/15/2021

    */
    /**/
    private void SetupUI() {
        m_mainToolBar = findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("");
        m_FullName = findViewById(R.id.t_DocFullName1);
        m_Email = findViewById(R.id.t_DocEmail1);
        m_Phone= findViewById(R.id.t_DocPhone1);
        m_Img = findViewById(R.id.placeholderIMGDoc);
        m_FullName.setText(getIntent().getStringExtra("DocName"));

        m_EditDoctorProfileLayout = findViewById(R.id.Expandable_Layout);
        m_EditDocEmail = findViewById(R.id.e_DocEmail);
        m_EditDocPhone = findViewById(R.id.e_DocPhone);
        m_EditDocBio = findViewById(R.id.e_DocBio);
        m_DocID= getIntent().getStringExtra("DocId");


        if(getIntent().getStringExtra("DocProfileUrl")!=null){
            m_DocProfileURL = getIntent().getStringExtra("DocProfileUrl");
            Picasso.get().load(m_DocProfileURL).into(m_Img);
        }

        //setting Doctor's email, phone, and bio
        SetInfo(m_Email,getIntent().getStringExtra("DocEmail"));
        SetInfo(m_EditDocEmail,getIntent().getStringExtra("DocEmail"));
        SetInfo(m_Phone,getIntent().getStringExtra("DocPhone"));
        SetInfo(m_EditDocPhone,getIntent().getStringExtra("DocPhone"));
        SetInfo(m_EditDocBio,getIntent().getStringExtra("DocBio"));

        m_AvailFrom = findViewById(R.id.t_availfrom);
        m_AvailTo = findViewById(R.id.t_availTo);
        m_SetAvailBtn = findViewById(R.id.b_setAvail);
        m_LinearLayoutDocAppt = findViewById(R.id.linearLayoutDocAppt);
        m_LinearLyaoutEditDocProfile = findViewById(R.id.linearLayoutEditDocProfile);

        m_datePickBtn = findViewById(R.id.b_datePickerBtn);

        m_DocWardList = findViewById(R.id.t_docWardList);
        m_Wards = findViewById(R.id.spinnerDocWard);

        m_DoctorWard = "Wards Doctor " + m_FullName.getText().toString() + " serves in:";
        m_DocWardList.setText(m_DoctorWard);

        m_docref = m_fstore.collection("Doctors").document(m_DocID);

        //retrieves necessary information about the user from the database, and initializes the UserObject with the data
        m_docref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                DoctorDataModel userObject = documentSnapshot.toObject(DoctorDataModel.class);
                m_DocWardList.setText(userObject.GetDocWardAsString());

            }
        });

    }
}