package com.example.seniorproject_hospitalapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class WardGroupAdapter extends FirestoreRecyclerAdapter<WardModel, WardGroupAdapter.viewHolder> {

    public WardGroupAdapter(@NonNull FirestoreRecyclerOptions<WardModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull WardModel model) {
        holder.m_WardName.setText(model.getWardName());
        if(model.getIconURI()!=null && model.getIconURI()!=""){
            Picasso.get().load(model.getIconURI()).into(holder.m_WardIcon);
        }
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ward_singlerow,parent, false);
        return new WardGroupAdapter.viewHolder(view);
    }


    class viewHolder extends RecyclerView.ViewHolder
    {
        ImageView m_WardIcon;
        TextView m_WardName;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            m_WardIcon = itemView.findViewById(R.id.wardIcon);
            m_WardName = itemView.findViewById(R.id.t_wardName);
        }
    }
}
