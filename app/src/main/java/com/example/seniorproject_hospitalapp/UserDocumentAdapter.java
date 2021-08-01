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


public class UserDocumentAdapter extends RecyclerView.Adapter<UserDocumentAdapter.holder>{

    ArrayList<Map<String, Object>> data = new ArrayList<>();
    public UserDocumentAdapter(ArrayList<Map<String, Object>> data) {
        this.data = data;
        System.out.println("documents sent: "+data);
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        System.out.println("create view??");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.document_user_single_row, parent, false);
        return new UserDocumentAdapter.holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        holder.m_filename.setText(data.get(position).get("fileName").toString());
        holder.m_message.setText(data.get(position).get("message").toString());
        holder.m_pdfthumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.m_pdfthumb.getContext(), PdfViewer.class);
                intent.putExtra("fileURL", data.get(position).get("fileUri").toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.m_pdfthumb.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class holder extends RecyclerView.ViewHolder
    {
        TextView m_filename;
        TextView m_message;
        ImageView m_pdfthumb;
        public holder(@NonNull View itemView) {
            super(itemView);
            m_filename = itemView.findViewById(R.id.t_UserFileName);
            m_message = itemView.findViewById(R.id.t_UserFileMessage);
            m_pdfthumb = itemView.findViewById(R.id.i_pdfthumb3);
        }
    }
}
