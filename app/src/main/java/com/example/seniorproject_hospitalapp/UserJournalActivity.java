package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/*
* This class represents the Journal page in user interface. This class allows patients
* to jot down their thoughts and feelings while they are going through some difficult times
* in their lives. In class implements Base64 encryption which makes it a 100% safe for patients
* to write about their feelings without having to worry about anyone else stealing or reading
* them.
*
* */
public class UserJournalActivity extends GlobalMenuActivity {
    private Toolbar m_mainToolBar;
    private EditText m_userNotes;
    private Button m_saveBtn;
    private FirebaseAuth m_fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore m_fStore = FirebaseFirestore.getInstance();
    private String m_userID = m_fAuth.getUid();
    private DocumentReference m_docReference = m_fStore.collection("UserJournal").document(m_userID);

    /**/
    /*

    NAME

            onCreate - initializes UserJournalActivity activity..

    SYNOPSIS

            protected void onCreate(Bundle a_savedInstanceState);
                a_savedInstanceState     --> the activity's previously frozen state, if there was one

    DESCRIPTION

            This function initialized the UserJournalActivity activity and links
            it to its respective layout resource file i.e. activity_user_journal
            which allows retrieving the Widgets and Views used in the layout to
            perform actions and handle events as required, by setting the events
            listeners.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:24pm 01/19/2021

    */
    /**/
    @Override
    protected void onCreate(Bundle a_savedInstanceState) {
        super.onCreate(a_savedInstanceState);
        setContentView(R.layout.activity_user_journal);
        SetupUI();
        setSupportActionBar(m_mainToolBar);

        GetNotes();

        //Saves encrypted journal notes in the database
        m_saveBtn.setOnClickListener(new View.OnClickListener() {

            //Base64 library used below, requires  API level 26 (Oreo)
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                byte[] noteBytes = new byte[0];
                try {

                    //Encodes this String into a sequence of bytes using the UTF-8 charset, and stores the result into the byte array.
                    noteBytes = m_userNotes.getText().toString().getBytes("UTF-8");
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                //encodes the byte array into a string using Base64 encoder
                String encodedNote = Base64.getEncoder().encodeToString(noteBytes);

                //saves the encryptes journal notes in the databse
                Map<String, Object> notes = new HashMap<>();
                notes.put("Thoughts",encodedNote);
                m_docReference.set(notes, SetOptions.merge());
                Toast.makeText(UserJournalActivity.this, "Your notes are saved.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**/
    /*

    NAME

            GetNotes - initializes all UI components

    SYNOPSIS

            private void GetNotes()

    DESCRIPTION

            This function retrieves the encoded user's journal notes from database, decodes it using
            Base64 decoder and displays the notes in a plain text form to the user.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 04/15/2021

    */
    /**/
    private void GetNotes(){
        m_docReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            //Base64 library used below, requires  API level 26 (Oreo)
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    //gets the encoded string from database as is
                    String notes = task.getResult().get("Thoughts").toString().trim();
                    //stores the decodes the string notes using a Base64 decoder, and stores in a byte array named decoded
                    byte[] decoded = Base64.getDecoder().decode(notes);
                    String decryptedNote="";
                    try {
                        //decodes the byte array to a human readable string using character set UTF-8
                        decryptedNote = new String(decoded, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //Displays user's journal notes in plain text in the application's page
                    m_userNotes.setText(decryptedNote);
                }else{
                    //if user did not have any journal notes before, displays the following message
                    m_userNotes.setText("A safe space for you to note down your thoughts... \n Start writing today! \n");
                }
            }
        });
    }
    /**/
    /*

    NAME

            SetupUI - initializes all UI components

    SYNOPSIS

            private void SetupUI()

    DESCRIPTION

            This function initializes all UI components to their respective Views
            from the layout :activity_user_journal.xml. Uses android method
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

            7:30pm 04/15/2021

    */
    /**/
    private void SetupUI() {
        m_mainToolBar = findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("");
        m_userNotes = findViewById(R.id.e_userJournalNotes);
        m_saveBtn = findViewById(R.id.b_saveNotes);
    }
}