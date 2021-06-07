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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
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

public class UserProfileAdminView extends AdminMenuActivity implements AdapterView.OnItemSelectedListener {
    Toolbar m_mainToolBar;
    TextView m_FullName, m_Email, m_Phone, m_WardList, m_addDocumentHeader;
    EditText m_DocumentName, m_DocumentMessage;
    CircleImageView m_Img;
    ImageView m_pdfThumb;
    Button m_SheduleApptBtn, m_UploadDocBtn;
    RadioButton m_RadioYes, m_RadioNo;
    Spinner m_wardNameSpinner;
    FirebaseFirestore fstore;
    StorageReference storageReference;
    DocumentReference docref;
    Uri m_UploadDocumentURI;

    String m_UserID, m_ImgURL, m_PatientWard;
    Boolean m_IsTestFile = false;
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

        m_pdfThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //m_fileName= m_DocName.getText().toString(); //uploading document's filename
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
                UploadImagetoFirebase(m_UploadDocumentURI );
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2000){
            if(resultCode == Activity.RESULT_OK){ //do we have any result on the data? so check
                m_UploadDocumentURI = data.getData();
            }
        }
    }

    private void UploadImagetoFirebase(Uri a_Fileuri) {
        String fileName = m_DocumentName.getText().toString().trim().replaceAll("\\s", "") +".pdf";
        String fileNameValue = m_DocumentName.getText().toString().trim();
        String message = m_DocumentMessage.getText().toString().trim();
        //System.out.println("message: "+msgKey + "-> "+ message);
        StorageReference fileRef = storageReference.child("users/"+m_UserID+"/"+fileName);
        fileRef.getMetadata()
            .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    System.out.println("Failure, file exists");
                    Toast.makeText(UserProfileAdminView.this, "Upload Failed! Filename: "+fileName+" already exists.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(fileName.isEmpty()){
                    System.out.println("Please enter the Document Name");
                    Toast.makeText(UserProfileAdminView.this, "Please enter the Document Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                fileRef.putFile(a_Fileuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                DocumentReference MsgdocRef = fstore.collection("Message").document(m_UserID);
                                MsgdocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        Map<String, Object> msgFileContent = new HashMap<>();
                                        msgFileContent.put("message", message);
                                        msgFileContent.put("fileName", fileNameValue);
                                        msgFileContent.put("fileUri", uri.toString());
                                        Map<String, Object> MsgFileMap = new HashMap<>();
                                        if(task.getResult().exists()){
                                            System.out.println("value: "+task.getResult().get("FileCounter").toString());
                                            Integer FileCount = Integer.parseInt(task.getResult().get("FileCounter").toString()) ;
                                            FileCount = FileCount + 1;
                                            MsgdocRef.update("FileCounter", FieldValue.increment(1));
                                            MsgFileMap.put(FileCount.toString(), msgFileContent);
                                            MsgdocRef.update(MsgFileMap);
                                        }else{
                                            Map<String, Object> filecounter = new HashMap<>();
                                            filecounter.put("FileCounter", 1);
                                            MsgdocRef.set(filecounter);
                                            MsgFileMap.put("1", msgFileContent);
                                            MsgdocRef.update(MsgFileMap);
                                        }
                                    }
                                });
                            }
                        });
                        Toast.makeText(UserProfileAdminView.this, "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        if(m_IsTestFile){
                            Map<String, Object> newfile = new HashMap<>();
                            newfile.put("newTestResult", true);
                            docref.update(newfile);
                        }
                        System.out.println("Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfileAdminView.this, "Failed uploading image", Toast.LENGTH_SHORT).show();
                        System.out.println("Failed upload");
                    }
                });
            }
        });

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
    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("Hope Hospital App");
        m_SheduleApptBtn= findViewById(R.id.b_ScheduleApptforUser);
        m_FullName= findViewById(R.id.t_UserFullName1);
        m_Email = findViewById(R.id.t_userEmail2);
        m_Phone= findViewById(R.id.t_userPhone1);
        m_Img= (CircleImageView)findViewById(R.id.placeholderIMG);
        m_WardList = findViewById(R.id.t_PatientWardList);
        m_addDocumentHeader = findViewById(R.id.t_addDocumentHeading);
        m_DocumentName = findViewById(R.id.e_UploadDocName);
        m_DocumentMessage = findViewById(R.id.e_DocumentMsg);
        m_UploadDocBtn = findViewById(R.id.b_uploadPatientDoc);
        m_pdfThumb = findViewById(R.id.i_pdfthumb2);
        m_RadioNo = findViewById(R.id.radio_no);
        m_RadioYes = findViewById(R.id.radio_yes);
        m_FullName.setText(getIntent().getStringExtra("uName").toString());
        m_Email.setText(getIntent().getStringExtra("uEmail").toString());
        m_Phone.setText(getIntent().getStringExtra("uPhone").toString());
        m_UserID= getIntent().getStringExtra("userId").toString();
        m_ImgURL = getIntent().getStringExtra("imgURL");

        fstore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        docref = fstore.collection("users").document(m_UserID);

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