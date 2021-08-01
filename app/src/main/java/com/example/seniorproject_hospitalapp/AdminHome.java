package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdminHome extends AdminMenuActivity {
    private ConstraintLayout m_AdminHomeGridParent;
    private LinearLayout m_LinearLayoutDoctors, m_LinearLayoutPatients, m_LinearLayoutWards, m_LinearLayoutDocuments;
    //public static final String Tag2 = "TAG";
    Toolbar m_mainToolBar;
    EditText msg;
    Button save, m_DocSearch;//, m_uploadFileBtn, m_ViewPDFBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    private StorageReference storageReference;
    String newMsg;
    private String m_docId, m_fileName; //firestore doc id and filename to be uploaded
    private float m_pixeltodpConversion = Resources.getSystem().getDisplayMetrics().density;
    private int m_AdminHomeGridBoxMarginLeftRight, m_AdminHomeGridBoxMarginTopBottom;

    SettingsPage m_SettingsClassObject = new SettingsPage();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        SetupUI();
        setSupportActionBar(m_mainToolBar);

        //CleanDatabase();
    }

    public void Doctors(View a_view){
        startActivity(new Intent(getApplicationContext(), SearchDoctorsAdmin.class));
    }
    public void Users(View a_view){
        startActivity(new Intent(getApplicationContext(), SearchUsersAdmin.class));
    }
    public void Wards(View a_view){
        startActivity(new Intent(getApplicationContext(), ManageGroupAdmin.class));
    }
    public void Documents(View a_view){
        startActivity(new Intent(getApplicationContext(), GlobalDocAdmin.class));
    }
    private void SetLeftRightMarginForGrids(View a_view){
        //System.out.println("margin: "+ m_HomeGridBoxMarginLeftRight + "conversion: "+m_pixeltodpConversion+" width: "+m_HomeGridParent.getWidth());
        m_SettingsClassObject.SetMargins(a_view, m_AdminHomeGridBoxMarginLeftRight,m_AdminHomeGridBoxMarginTopBottom, m_AdminHomeGridBoxMarginLeftRight,m_AdminHomeGridBoxMarginTopBottom );
    }

    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("Hope Hospital App");
        m_AdminHomeGridParent = findViewById(R.id.AdminHomeGridLayoutParent);
        m_LinearLayoutDoctors = findViewById(R.id.linearLayoutDoctors);
        m_LinearLayoutPatients = findViewById(R.id.LinearLayoutPatient);
        m_LinearLayoutWards = findViewById(R.id.linearLayoutWards);
        m_LinearLayoutDocuments = findViewById(R.id.linearLayoutDocuments);
        m_AdminHomeGridParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                m_AdminHomeGridParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);//width is ready
                m_AdminHomeGridBoxMarginLeftRight = (int) ((m_AdminHomeGridParent.getWidth() - 330*m_pixeltodpConversion))/2;
                m_AdminHomeGridBoxMarginTopBottom= (int) (15*m_pixeltodpConversion);
                SetLeftRightMarginForGrids(m_LinearLayoutDoctors);
                SetLeftRightMarginForGrids(m_LinearLayoutPatients);
                SetLeftRightMarginForGrids(m_LinearLayoutWards);
                SetLeftRightMarginForGrids(m_LinearLayoutDocuments);
            }
        });
        //save = findViewById(R.id.b_try);
        //msg=findViewById(R.id.e_try);
        //m_DocSearch = findViewById(R.id.b_gotoDocSearch);
       // m_uploadFileBtn = findViewById(R.id.b_uploadFile);
        //m_ViewPDFBtn= findViewById(R.id.b_viewpdf);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void CleanDatabase() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        //getting the alarm manager
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //creating a new intent specifying the broadcast receiver
        Intent i = new Intent(this, ReminderAlarm.class);
        i.putExtra("ScheduleType", "Cleaning Database.");
        //creating a pending intent using the intent
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);

        //setting the repeating alarm that will be fired every day
        //am.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
        am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);

        DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");
        System.out.println("time: "+ simple.format(cal.getTimeInMillis()));
    }

    /* //overriding the onactivityresult function, the "data" attribute has the img_uri,
    //different functions/intents can be invoking the onActivityResult function therefore,
    //its required/good to check the intent that is invoking the method, so we use reqcode
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(Tag2, "here 1???: ");
        if(requestCode==2000){
            if(resultCode == Activity.RESULT_OK){ //do we have any result on the data? so check
                Uri imageUri = data.getData();
                Log.d(Tag2, "here 2???: ");
                //m_profileImage.setImageURI(imageUri);
                UploadImagetoFirebase(imageUri, m_docId, m_fileName);
            }
        }
    }

    private void UploadImagetoFirebase(Uri a_imageuri, String a_documendID, String a_fileName) {
        StorageReference fileRef = storageReference.child("GlobalDoc/"+a_documendID+a_fileName);
        fileRef.putFile(a_imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminHome.this, "Image Uploaded", Toast.LENGTH_SHORT).show();

                Log.d(Tag2, "Uploaded???: ");
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Picasso.get().load(uri).into(m_profileImage);
                        Toast.makeText(AdminHome.this, "File loaded successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminHome.this, "Failed uploading image", Toast.LENGTH_SHORT).show();
            }
        });
    }
    */
}