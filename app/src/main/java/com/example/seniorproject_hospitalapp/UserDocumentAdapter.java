package com.example.seniorproject_hospitalapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

/*
*  This is a recycler view adapter class for ViewUserDocument activity's recycler view.
*  This class provides an interface to display and control the User Document Data in the recycler view
*/
public class UserDocumentAdapter extends RecyclerView.Adapter<UserDocumentAdapter.Holder>{

    //User document array which is initialized in the constructor
    ArrayList<Map<String, Object>> m_DocumentDataArr = new ArrayList<>();

    /**/
    /*

    NAME

            UserDocumentAdapter - constructor for UserDocumentAdapter class

    SYNOPSIS

            public UserDocumentAdapter(ArrayList<Map<String, Object>> a_data)
                a_data --> User Document data array passed from ViewUserDocuments class

    DESCRIPTION

            This function initializes the global array m_DocumentDataArr with the
            User Documents Array passed from ViewUserDocuments class

    RETURNS

            Nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/20/2021

    */
    /**/
    public UserDocumentAdapter(ArrayList<Map<String, Object>> a_data) {
        this.m_DocumentDataArr = a_data;
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

            This function is called to create a new RecyclerView.ViewHolder, the created view
            is inflated using the single row resource file document_user_single_row.xml. This
            inflated view is passed to the Holder class's constructor which initializes the views
            contained inside it, and is returned or passed to the onBindViewHolder

    RETURNS

            Holder

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/20/2021

    */
    /**/
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup a_parent, int a_viewType) {
        LayoutInflater inflater = LayoutInflater.from(a_parent.getContext());
        View view = inflater.inflate(R.layout.document_user_single_row, a_parent, false);
        return new UserDocumentAdapter.Holder(view);
    }

    /**/
    /*

    NAME

            onBindViewHolder - Binds the new item's view with its respective data / content

    SYNOPSIS

            public void onBindViewHolder(@NonNull Holder a_holder, int a_position)
                a_holder --> Holder object for the given item
                a_position --> position of the data array to be populated in the recycler view

    DESCRIPTION

            This function binds the view passed by onCreateViewHolder to its actual contents
            that is received from the data array. It also sets an onClickListener for the
            pdf icon in each view to open the pdf in the PdfViewer activity

    RETURNS

            Nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/20/2021

    */
    /**/
    @Override
    public void onBindViewHolder(@NonNull Holder a_holder, int a_position) {
        a_holder.m_filename.setText(m_DocumentDataArr.get(a_position).get("fileName").toString());
        a_holder.m_message.setText(m_DocumentDataArr.get(a_position).get("message").toString());
        a_holder.m_pdfthumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(a_holder.m_pdfthumb.getContext(), PdfViewer.class);
                intent.putExtra("fileURL", m_DocumentDataArr.get(a_position).get("fileUri").toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                a_holder.m_pdfthumb.getContext().startActivity(intent);
            }
        });
    }

    /**/
    /*

    NAME

            getItemCount - gets the size of the m_data array

    SYNOPSIS

            public int getItemCount()

    DESCRIPTION

            This function gives the size of the m_data array i.e. the User Documents Array

    RETURNS

            data Array Count

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/20/2021

    */
    /**/
    @Override
    public int getItemCount() {
        return m_DocumentDataArr.size();
    }

    /*
    * This class is a View Holder class for ViewUserDocument's recycler view adapter.
    * This class pulls the reference of single row layout associated with the ViewUserDocument
    * class's recycler view.
    *
    * */
    class Holder extends RecyclerView.ViewHolder
    {
        TextView m_filename;
        TextView m_message;
        ImageView m_pdfthumb;
        public Holder(@NonNull View itemView) {
            super(itemView);
            m_filename = itemView.findViewById(R.id.t_UserFileName);
            m_message = itemView.findViewById(R.id.t_UserFileMessage);
            m_pdfthumb = itemView.findViewById(R.id.i_pdfthumb3);
        }
    }
}
