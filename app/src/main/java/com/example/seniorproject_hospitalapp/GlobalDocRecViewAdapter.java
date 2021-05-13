package com.example.seniorproject_hospitalapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class GlobalDocRecViewAdapter extends FirestoreRecyclerAdapter<GlobalDocModel, GlobalDocRecViewAdapter.viewHolder>
{
    private Context m_context;
    private FirebaseFirestore fStore;
    private StorageReference storageReference;
    private DocumentReference documentReference;



    public GlobalDocRecViewAdapter(@NonNull FirestoreRecyclerOptions<GlobalDocModel> options, Context a_context) {
        super(options);
        this.m_context = a_context;

    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull GlobalDocModel model) {
        holder.m_fileNameTxt.setText(model.getFileName());

        holder.m_pdfthumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("filename: "+  model.getFile());
                Intent intent = new Intent(holder.m_pdfthumb.getContext(), PdfViewer.class);
                intent.putExtra("fileURL", model.getFile());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.m_pdfthumb.getContext().startActivity(intent);
            }
        });

        holder.m_changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_context instanceof GlobalDocAdmin) {
                    ((GlobalDocAdmin)m_context).ChangePdf(model.getFileName());
                }
            }
        });

        holder.m_deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStore = FirebaseFirestore.getInstance();
                storageReference = FirebaseStorage.getInstance().getReference();
                //documentReference = fStore.collection("GlobalDoc").document("PrivacyPolicyDocs");
                // Create a reference to the file to delete
                StorageReference deleteFileRef = storageReference.child("GlobalDoc/"+model.getFileName()+ "file.pdf");
                DocumentReference docref = fStore.collection("GlobalDoc").document(model.getFileName());
                docref.delete();
                // Delete the file
                deleteFileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(m_context, "File Deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(m_context, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.globaldoc_singlerow,parent, false);
        return new viewHolder(view);
    }

    class viewHolder extends RecyclerView.ViewHolder
    {
        ImageView m_pdfthumb;
        Button m_deleteBtn, m_changeBtn;
        TextView m_fileNameTxt;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            m_pdfthumb = itemView.findViewById(R.id.i_pdfthumb1);
            m_deleteBtn = itemView.findViewById(R.id.b_GDocDelete);
            m_changeBtn = itemView.findViewById(R.id.b_GDocChange);
            m_fileNameTxt = itemView.findViewById(R.id.t_GlobalDocName1);
        }
    }

    private void UploadImagetoFirebase(Uri a_imageuri, String a_fileName) {

        StorageReference fileRef = storageReference.child("GlobalDoc/"+a_fileName+"file.pdf");
        fileRef.putFile(a_imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(m_context, "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String m_fileUrl = uri.toString(); //this is the download url of the uploaded file
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
                Toast.makeText(m_context, "Failed uploading image", Toast.LENGTH_SHORT).show();
            }
        });
    }


}

