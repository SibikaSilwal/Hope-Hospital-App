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

/*
 *  This is a recycler view adapter class for GlobalDocAdmin activity's recycler view.
 *  This class provides an interface to display and control the Global Documents Data
 * in the recycler view.
 */
public class GlobalDocRecViewAdapter extends FirestoreRecyclerAdapter<GlobalDocModel, GlobalDocRecViewAdapter.DocumentViewHolder>
{
    private Context m_context;
    private FirebaseFirestore m_fStore;
    private StorageReference m_storageReference;


    /**/
    /*

    NAME

            GlobalDocRecViewAdapter - constructor for GlobalDocRecViewAdapter class

    SYNOPSIS

            public GlobalDocRecViewAdapter(@NonNull FirestoreRecyclerOptions<DoctorDataModel> options, Context a_context)
                options    --> instance of FirestoreRecyclerOptions class
                a_context  --> Application context passed from GlobalDocAdmin class

    DESCRIPTION

            This function is constructor for GlobalDocRecViewAdapter class. It initializes the
            global m_context to the received context from GlobalDocAdmin class

    RETURNS

            Nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 02/17/2021

    */
    /**/
    public GlobalDocRecViewAdapter(@NonNull FirestoreRecyclerOptions<GlobalDocModel> options, Context a_context) {
        super(options);
        this.m_context = a_context;

    }

    /**/
    /*

    NAME

            onBindViewHolder - Binds the new item's view with its respective data / content

    SYNOPSIS

            protected void onBindViewHolder(@NonNull UserViewHolder a_holder, int a_position, @NonNull GlobalDocModel a_model)
                a_holder   --> Holder object for the given item
                a_position --> position of the data array to be populated in the recycler view
                a_model    --> an instance of GlobalDocModel class

    DESCRIPTION

            This function binds the view passed by onCreateViewHolder to its actual contents
            that is received from the object of GlobalDocModel and methods of the class.
            It also sets an onClickListener for buttons and pdf icons for each card view /
            item.

    RETURNS

            Nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 02/17/2021

    */
    /**/
    @Override
    protected void onBindViewHolder(@NonNull DocumentViewHolder a_holder, int a_position, @NonNull GlobalDocModel a_model) {
        a_holder.m_fileNameTxt.setText(a_model.getFileName());

        //takes user to pdf viewer intent to view the clicked pdf
        a_holder.m_pdfthumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(a_holder.m_pdfthumb.getContext(), PdfViewer.class);
                intent.putExtra("fileURL", a_model.getFile());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                a_holder.m_pdfthumb.getContext().startActivity(intent);
            }
        });

        //Calls ChangePdf function from the GlobalDocAdmin class
        a_holder.m_changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_context instanceof GlobalDocAdmin) {
                    ((GlobalDocAdmin)m_context).ChangePdf(a_model.getFileName());
                }
            }
        });

        //Deletes the pdf from the database and reflects the change in the page
        a_holder.m_deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_fStore = FirebaseFirestore.getInstance();
                m_storageReference = FirebaseStorage.getInstance().getReference();

                // Create a reference to the file to delete
                StorageReference deleteFileRef = m_storageReference.child("GlobalDoc/"+a_model.getFileName()+ "file.pdf");
                DocumentReference docref = m_fStore.collection("GlobalDoc").document(a_model.getFileName());
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

    /**/
    /*

    NAME

            onCreateViewHolder - creates empty view for the Single Row layout

    SYNOPSIS

            public Holder onCreateViewHolder(@NonNull ViewGroup a_parent, int a_viewType)
                a_parent --> parent ViewGroup
                a_viewType --> view position in the recycler view

    DESCRIPTION

            This function is called to create a new RecyclerView.ViewHolder. The created view is
            inflated using the single row resource file globaldoc_singlerow.xml. This inflated
            view is passed to the DocumentViewHolder class's constructor which initializes
            the views contained inside it, and is returned or passed to the onBindViewHolder

    RETURNS

            DocumentViewHolder

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 02/17/2021

    */
    /**/
    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup a_parent, int a_viewType) {
        View view = LayoutInflater.from(a_parent.getContext()).inflate(R.layout.globaldoc_singlerow,a_parent, false);
        return new DocumentViewHolder(view);
    }

    /*
     * This class is a View Holder class for GlobalDocAdmin's recycler view adapter.
     * This class pulls the reference of single row layout associated with the GlobalDocRecViewAdapter.
     *
     * */
    class DocumentViewHolder extends RecyclerView.ViewHolder
    {
        ImageView m_pdfthumb;
        Button m_deleteBtn, m_changeBtn;
        TextView m_fileNameTxt;

        /**/
        /*

        NAME

                DocumentViewHolder - constructor for UserViewHolder class

        SYNOPSIS

                public DocumentViewHolder(@NonNull View itemView)
                    itemView --> Single item in the recycler view

        DESCRIPTION

                The constructor of DocumentViewHolder class initializes all the member
                variables and views to their respective ids in the layout resource file

        RETURNS

                Nothing

        AUTHOR

                Sibika Silwal

        DATE

                7:30pm 02/17/2021

        */
        /**/
        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            m_pdfthumb = itemView.findViewById(R.id.i_pdfthumb1);
            m_deleteBtn = itemView.findViewById(R.id.b_GDocDelete);
            m_changeBtn = itemView.findViewById(R.id.b_GDocChange);
            m_fileNameTxt = itemView.findViewById(R.id.t_GlobalDocName1);
        }
    }


}

