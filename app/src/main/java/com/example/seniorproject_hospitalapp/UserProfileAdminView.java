package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/*
* This class represents the User Profile Admin view activity for admin interface.
* This class lets admin view the user's profile, and direct them to other important
* activities like schedule appointment for the particular patient, view their scheduled
* appointments. This also provides interface for admin to upload new files for patients
* and add patients to new wards.
*
*/
public class UserProfileAdminView extends AdminMenuActivity implements AdapterView.OnItemSelectedListener {
    private LinearLayout m_LinearLayoutScheduleAppt, m_LinearLayoutViewUserAppt, m_LinearLayoutUserInsurance;
    Toolbar m_mainToolBar;
    TextView m_FullName, m_Email, m_Phone, m_WardList;
    EditText m_DocumentName, m_DocumentMessage;
    CircleImageView m_Img;
    ImageView m_pdfThumb;
    Button m_UploadDocBtn;
    RadioButton m_RadioYes, m_RadioNo;
    Spinner m_wardNameSpinner;
    FirebaseFirestore m_fstore= FirebaseFirestore.getInstance();
    StorageReference m_storageReference = FirebaseStorage.getInstance().getReference();
    DocumentReference m_docref;
    Uri m_UploadDocumentURI;

    String m_UserID, m_ImgURL;
    Boolean m_IsTestFile = false;
    ArrayList<String> m_WardNamelist = new ArrayList<>();
    ArrayList<Object> m_PatientsWardList = new ArrayList<>();
    ArrayAdapter<String> m_wardNameArrAdapter;

    /**/
    /*

    NAME

            onCreate - initializes UserProfileAdminView activity..

    SYNOPSIS

            protected void onCreate(Bundle a_savedInstanceState);
                a_savedInstanceState     --> the activity's previously frozen state, if there was one

    DESCRIPTION

            This function initialized the UserProfileAdminView activity and links
            it to its respective layout resource file i.e. activity_user_profile_admin_view
            which allows retrieving the Widgets and Views used in the layout to
            perform actions and handle events as required, by setting the events
            listeners.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:24pm 03/02/2021

    */
    /**/
    @Override
    protected void onCreate(Bundle a_savedInstanceState) {
        super.onCreate(a_savedInstanceState);
        setContentView(R.layout.activity_user_profile_admin_view);
        SetupUI();
        setSupportActionBar(m_mainToolBar);


        m_WardNamelist = new ArrayList<String>();
        m_WardNamelist.add("Select a Ward.");
        m_wardNameArrAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, m_WardNamelist);
        m_wardNameSpinner.setAdapter(m_wardNameArrAdapter);


        PopulateWardSpinner();


        m_wardNameSpinner.setOnItemSelectedListener(this);

        m_pdfThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("*/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("*/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select File");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, 2000);
            }
        });

        m_UploadDocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_UploadDocumentURI==null){
                    Toast.makeText(UserProfileAdminView.this, "You have not uploaded any file.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String fileName = m_DocumentName.getText().toString().trim().replaceAll("\\s", "");
                if(fileName.isEmpty()){
                    System.out.println("Please enter the Document Name");
                    Toast.makeText(UserProfileAdminView.this, "Please enter the Document Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                String message = m_DocumentMessage.getText().toString().trim();

                //Define a storage reference for the test file being uploaded
                StorageReference fileRef = m_storageReference.child("users/"+m_UserID+"/"+fileName+".pdf");

                //Check if a file with same name already exists for the user, if yes, show error message
                fileRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        Toast.makeText(UserProfileAdminView.this, "Upload Failed! Filename: "+fileName+" already exists.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                })
                //if file does not exist already, call UploadImagetoFirebase
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        UploadFileAndMessage(fileRef, m_UploadDocumentURI, fileName, message);
                    }
                });

            }
        });
    }

    /**/
    /*

    NAME

            onActivityResult - fires new intent to pick file from the device

    SYNOPSIS

            private void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
                requestCode  --> an integer to identify the intent firing onActivityResult
                resultCode   --> an integer value that represents the status of the result of onActivityResult
                data         --> intent data returned from the launched intent

    DESCRIPTION

            This function overrides the onActivityResult function and is called when the profile
            image view is clicked, to pick a new profile image from Device's Media Gallery.
            Different functions/intents can be invoking the onActivityResult function
            therefore, to check the intent that is invoking the method, its request code
            is checked, in this case request code 2000. The "data" attribute has the img_uri, that
            has been picked from the gallery. The image uri is stored in m_UploadDocumentURI


    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/2/2021

    */
    /**/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2000){
            if(resultCode == Activity.RESULT_OK){
                m_UploadDocumentURI = data.getData();
            }
        }
    }

    /**/
    /*

    NAME

            UploadFileAndMessage - Uploads file with their messages for users in firebase database

    SYNOPSIS

            private void UploadFileAndMessage(StorageReference a_storageRef, Uri a_Fileuri, String a_fileName, String a_Message)
                a_storageRef  --> Storage reference in Firebase Storage to upload the file in
                a_Fileuri     --> Upload uri of the chosen file
                a_fileName    --> Name of the file being uploaded
                a_Message     --> Message with the file

    DESCRIPTION

            This function uploads the passed file uri in the firebase storage for the given user. It
            also stores the file's download URI along with their name, and message for patient in the
            Firestore database in "Message" collection user's document. It identifies if the uploaded
            file is a new test result. If yes, it updates the information in database, which is used
            in sending notification to user about their new test result upload

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/2/2021

    */
    /**/
    private void UploadFileAndMessage(StorageReference a_storageRef, Uri a_Fileuri, String a_fileName, String a_Message) {
        //uploads given file URI to firebase storage
        a_storageRef.putFile(a_Fileuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //on successfully uploading the file, gets its download URL
                a_storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        //Message document reference for the given user, that stores fileURL, name, and message associated
                        DocumentReference MsgdocRef = m_fstore.collection("Message").document(m_UserID);

                        MsgdocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                //Map that stores all the Test file information
                                Map<String, Object> msgFileContent = new HashMap<>();
                                msgFileContent.put("message", a_Message);
                                msgFileContent.put("fileName", a_fileName);
                                msgFileContent.put("fileUri", uri.toString());
                                Map<String, Object> MsgFileMap = new HashMap<>();

                                Integer FileCount=1;
                                //checks if the user previously has test files
                                if(task.getResult().exists()){
                                    //if yes, increments the fileCount by 1
                                    FileCount = Integer.parseInt(task.getResult().get("FileCounter").toString()) ;
                                    FileCount = FileCount + 1;
                                    MsgdocRef.update("FileCounter", FieldValue.increment(1));
                                }else{
                                    //if no, sets fileCount as 1
                                    Map<String, Object> filecounter = new HashMap<>();
                                    filecounter.put("FileCounter", 1);
                                    MsgdocRef.set(filecounter);
                                }
                                //Updates the Message Document with new test file and its message
                                MsgFileMap.put(FileCount.toString(), msgFileContent);
                                MsgdocRef.update(MsgFileMap);
                            }
                        });
                    }
                });
                Toast.makeText(UserProfileAdminView.this, "File Uploaded Successfully", Toast.LENGTH_SHORT).show();

                //if the uploaded file is a test result, updates the newTestResult in database
                if(m_IsTestFile){
                    m_docref.update("newTestResult", true);
                }
                m_DocumentName.setText("");
                m_DocumentMessage.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfileAdminView.this, "Failed uploading image", Toast.LENGTH_SHORT).show();
            }
        });
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

            7:30pm 03/2/2021

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

            onRadioButtonClicked - Gets user input for radio buttons

    SYNOPSIS

            public void onRadioButtonClicked(View a_view)
                a_view  --> view passed, in this case the Radio Button View

    DESCRIPTION

            This function is called when the radio button view is clicked. If
            clicked on "Yes" the m_IsTestFile variable is set true, else it is
            set to be false

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/02/2021

    */
    /**/
    public void onRadioButtonClicked(View a_view){
        boolean checked = ((RadioButton) a_view).isChecked();
        // Check which radio button was clicked
        switch(a_view.getId()) {
            case R.id.radio_yes:
                if (checked)
                    m_IsTestFile = true;
                    break;
            case R.id.radio_no:
                if (checked)
                    m_IsTestFile = false;
                    break;
        }
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

            7:30pm 03/02/2021

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

            onItemSelected - is invoked when an item in WardList dropdown view is selected.

    SYNOPSIS

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                parent   --> The AdapterView where the selection happened
                view     --> The view within the AdapterView that was clicked
                position --> The position of the view in the adapter
                id       --> The row id of the item that is selected

    DESCRIPTION

            This function is called when an admin selects a Ward from the Ward's names
            dropdown to add to patient's ward list. If the placeholder text "Select a Ward."
            is selected nothing happens. Else, the selected ward is added to the patient's
            ward list in the database, and is reflected immediately in the app interface

            Synopsis Src: Android Documentation
           (https://developer.android.com/reference/android/widget/AdapterView.OnItemSelectedListener)


    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/2/2021

    */
    /**/
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getItemAtPosition(position).equals("Select a Ward.")){
            //do nothing
        }else{
            DocumentReference docRef = m_fstore.collection("users").document(m_UserID);
            docRef.update("WardName", FieldValue.arrayUnion(parent.getItemAtPosition(position)))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        m_PatientsWardList.add(parent.getItemAtPosition(position));
                        m_WardList.setText(m_WardList.getText()+"\n"+parent.getItemAtPosition(position));
                    }
                });
        }

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    /**/
    /*

    NAME

            ScheduleAppt - navigates admin to AppointmentActivity

    SYNOPSIS

            public void ScheduleAppt(View a_view)
                a_view     --> view provided

    DESCRIPTION

            This function navigates user to AppointmentActivity. This function is provided
            in the onClick attribute in xml file for the Schedule Appointment Grid in User Profile
            page for admin. The function gets called when the Schedule Appointment view is clicked.
            The required parameter are passed to the Schedule appointment intent.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            2:30pm 07/26/2021

    */
    /**/
    public void SchedulePatientAppt(View a_view){
        Intent intent = new Intent(a_view.getContext(), Appointment.class);
        intent.putExtra("patientID", m_UserID);
        intent.putExtra("name", m_FullName.getText().toString());
        intent.putExtra("email", m_Email.getText().toString());
        intent.putExtra("phone", m_Phone.getText().toString());
        intent.putExtra("wards", m_PatientsWardList);
        startActivity(intent);
    }
    /**/
    /*

    NAME

            ViewPatientAppointments - navigates user to UserAppointmentsPage

    SYNOPSIS

            public void ViewPatientAppointments(View a_view)
                a_view     --> view provided

    DESCRIPTION

            This function navigates user to UserAppointmentsPage. The function
            gets called when the ViewPatientAppointments grid view is clicked

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            2:30pm 07/26/2021

    */
    /**/
    public void ViewPatientAppointments(View a_view){
        Intent intent = new Intent(a_view.getContext(), UserAppointmentsPage.class);
        intent.putExtra("patientID", m_UserID);
        startActivity(intent);
    }

    /**/
    /*

    NAME

            ViewPatientInsurance - navigates user to UserInsuranceUpload

    SYNOPSIS

            private void ViewPatientInsurance(View a_view)
                a_view     --> view provided

    DESCRIPTION

            This function navigates user to UserInsuranceUpload. The function
            gets called when the ViewPatientInsurance button is is clicked

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            2:30pm 03/2/2021

    */
    /**/
    public void ViewPatientInsurance(View a_view){
        Intent intent = new Intent(a_view.getContext(), UserInsuranceUpload.class);
        intent.putExtra("patientID", m_UserID);
        startActivity(intent);
    }

    /**/
    /*

    NAME

            SetupUI - initializes all UI components

    SYNOPSIS

            private void SetupUI()

    DESCRIPTION

            This function initializes all UI components to their respective Views
            from the layout :activity_user_profile_admin_view.xml. Uses android method
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

            7:30pm 03/02/2021

    */
    /**/
    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("");
        m_FullName= findViewById(R.id.t_UserFullName1);
        m_Email = findViewById(R.id.t_userEmail2);
        m_Phone= findViewById(R.id.t_userPhone1);
        m_Img= (CircleImageView)findViewById(R.id.placeholderIMG);
        m_WardList = findViewById(R.id.t_PatientWardList);
        m_wardNameSpinner = findViewById(R.id.wardSpinner);
        m_DocumentName = findViewById(R.id.e_UploadDocName);
        m_DocumentMessage = findViewById(R.id.e_DocumentMsg);
        m_UploadDocBtn = findViewById(R.id.b_uploadPatientDoc);
        m_pdfThumb = findViewById(R.id.i_pdfthumb2);
        m_RadioNo = findViewById(R.id.radio_no);
        m_RadioYes = findViewById(R.id.radio_yes);
        m_LinearLayoutScheduleAppt = findViewById(R.id.linearLayoutScheduleAppt);
        m_LinearLayoutViewUserAppt = findViewById(R.id.linearLayoutViewUserAppts);
        m_LinearLayoutUserInsurance = findViewById(R.id.LinearLayoutUserInsurance);

        //setting texts in textviews
        SetInfo(m_FullName, getIntent().getStringExtra("uName"));
        SetInfo(m_Email, getIntent().getStringExtra("uEmail"));
        SetInfo(m_Phone, getIntent().getStringExtra("uPhone"));
        m_UserID= getIntent().getStringExtra("userId").toString();
        m_ImgURL = getIntent().getStringExtra("imgURL");
        if(m_ImgURL!=null && !m_ImgURL.isEmpty()){
            Picasso.get().load(m_ImgURL).into(m_Img);
        }

        m_docref = m_fstore.collection("users").document(m_UserID);

        //retrieves necessary information about the user from the database, and initializes the UserObject with the data
        m_docref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserDataModel userObject = documentSnapshot.toObject(UserDataModel.class);
                m_PatientsWardList = userObject.getWardName();
                m_WardList.setText(userObject.getWardNameAsString("Wards patient is in"));

            }
        });
    }

}