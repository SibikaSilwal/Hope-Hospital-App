package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class GlobalDocAdmin extends AdminMenuActivity {
    Toolbar m_mainToolBar;
    private RecyclerView m_recview;
    private GlobalDocRecViewAdapter m_recviewAdapter;

    private EditText m_DocName;
    private TextView m_GlobalDocName;
    private Button m_changeFileBtn, m_uploadFileBtn, m_deleteFileBtn;
    private ImageView m_PDFicon;
    private Uri m_newFileURI;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private StorageReference storageReference, m_fileRef;
    private DocumentReference docReference;
    private String m_docId, m_fileName; //firestore doc id and filename to be uploaded //strings for pdf upload
    private String m_fileURL, m_finalURL,  m_docName; //strings for pdf view
    private WebView m_webView;

    /*new*/
    private String m_fileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_doc_admin);
        SetupUI();
        setSupportActionBar(m_mainToolBar);


        FirestoreRecyclerOptions<GlobalDocModel> options =
                new FirestoreRecyclerOptions.Builder<GlobalDocModel>()
                        .setQuery(fStore.collection("GlobalDoc").orderBy("fileName"), GlobalDocModel.class)
                        .build();

        m_recviewAdapter = new GlobalDocRecViewAdapter(options, GlobalDocAdmin.this);
        m_recview.setAdapter(m_recviewAdapter);

        m_PDFicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_fileName= m_DocName.getText().toString(); //uploading document's filename
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("*/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("*/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select File");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, 2000);
            }
        });



    }

    public void ChangePdf(String a_fileName ){ //called from GlobalDocRecylerAdapter Class
        System.out.println("Helloo there, its meeee");
        m_fileName= a_fileName; //uploading document's filename
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("*/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("*/*");
        Intent chooserIntent = Intent.createChooser(getIntent, "Select File");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, 2001);
    }

    //overriding the onactivityresult function, the "data" attribute has the img_uri,
    //different functions/intents can be invoking the onActivityResult function therefore,
    //its required/good to check the intent that is invoking the method, so we use reqcode
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2000){
            if(resultCode == Activity.RESULT_OK){ //do we have any result on the data? so check
                //Uri imageUri = data.getData();
                m_newFileURI= data.getData();
                //m_profileImage.setImageURI(imageUri);
                //UploadImagetoFirebase(imageUri, m_fileName);
            }
        }
        if(requestCode==2001){
            if(resultCode == Activity.RESULT_OK){ //do we have any result on the data? so check
                Uri imageUri = data.getData();
                //m_newFileURI= data.getData();
                //m_profileImage.setImageURI(imageUri);
                UploadImagetoFirebase(imageUri, m_fileName);
            }
        }
    }
    public void AddNewDocument(View a_view){
        if(m_newFileURI==null||m_fileName==null){
            Toast.makeText(GlobalDocAdmin.this, "Please make sure to choose a file and enter filename.", Toast.LENGTH_SHORT).show();
            return;
        }
        UploadImagetoFirebase(m_newFileURI,m_fileName);
        m_DocName.setText("");
        Toast.makeText(GlobalDocAdmin.this, "New ward was created successfully.", Toast.LENGTH_SHORT).show();
    }
    private void UploadImagetoFirebase(Uri a_imageuri, String a_fileName) {
        StorageReference fileRef = storageReference.child("GlobalDoc/"+a_fileName+"file.pdf");
        fileRef.putFile(a_imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(GlobalDocAdmin.this, "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        m_fileUrl = uri.toString(); //this is the download url of the uploaded file
                        DocumentReference docRef = fStore.collection("GlobalDoc").document(a_fileName);
                        Map<String, Object> fileDocNew = new HashMap<>();
                        fileDocNew.put("file",m_fileUrl);
                        fileDocNew.put("fileName",a_fileName );
                        docRef.set(fileDocNew);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GlobalDocAdmin.this, "Failed uploading image", Toast.LENGTH_SHORT).show();
                System.out.println("Failed upload");
            }
        });
    }

    private void SetupUI(){
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("Global Documents");
        m_recview = findViewById(R.id.globaldocreyclerview);
        m_recview.setLayoutManager(new LinearLayoutManager(this));
        m_uploadFileBtn = findViewById(R.id.b_GdocUpload);
        m_PDFicon=findViewById(R.id.globalDocFileIcon);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        m_DocName= findViewById(R.id.e_GDocUploadFileName);
        docReference = fStore.collection("GlobalDoc").document("PrivacyPolicyDocs");
        m_docId = "PrivacyPolicyDocs";
    }

    @Override
    protected void onStart() {
        super.onStart();
        m_recviewAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        m_recviewAdapter.stopListening();
    }
}