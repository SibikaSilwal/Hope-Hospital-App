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

public class UserJournalActivity extends GlobalMenuActivity {
    private Toolbar m_mainToolBar;
    private EditText m_userNotes;
    private Button m_saveBtn;
    private String m_userID;
    private FirebaseAuth m_fAuth;
    private FirebaseFirestore m_fStore;
    private DocumentReference m_docReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_journal);
        SetupUI();
        setSupportActionBar(m_mainToolBar);

        GetNotes();
        m_saveBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                byte[] noteBytes = new byte[0];
                try {
                    noteBytes = m_userNotes.getText().toString().getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String encodedNote = Base64.getEncoder().encodeToString(noteBytes);
                Map<String, Object> notes = new HashMap<>();
                notes.put("Thoughts",encodedNote);
                m_docReference.set(notes, SetOptions.merge());
                Toast.makeText(UserJournalActivity.this, "Your notes are saved.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void GetNotes(){
        m_docReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    String notes = task.getResult().get("Thoughts").toString().trim();
                    byte[] decoded = Base64.getDecoder().decode(notes);
                    String decryptedNote="";
                    try {
                        decryptedNote = new String(decoded, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    m_userNotes.setText(decryptedNote);
                }else{
                    m_userNotes.setText("A safe space for you to note down your thoughts... \n Start writing today! \n");
                }
            }
        });
    }

    private void SetupUI() {
        m_mainToolBar = findViewById(R.id.mtoolbar);
        //m_mainToolBar.setTitle("Hope Hospital App");
        m_userNotes = findViewById(R.id.e_userJournalNotes);
        m_saveBtn = findViewById(R.id.b_saveNotes);
        m_fAuth = FirebaseAuth.getInstance();
        m_fStore = FirebaseFirestore.getInstance();
        m_userID = m_fAuth.getUid();
        m_docReference = m_fStore.collection("UserJournal").document(m_userID);
    }
}