package com.example.seniorproject_hospitalapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchViewAdapter extends FirestoreRecyclerAdapter<UserDataModel, SearchViewAdapter.holder>
{
    Context m_context;
    //FirestoreRecyclerOptions<UserDataModel> m_data;
    public SearchViewAdapter(@NonNull FirestoreRecyclerOptions<UserDataModel> options, Context context) {
        super(options);
        this.m_context = context;
        //this.m_data = options;
    }

    @Override
    protected void onBindViewHolder(@NonNull holder holder, int position, @NonNull UserDataModel model) {
        //final UserDataModel tempModel = model.get(position);

        holder.UserName.setText(model.getfName());
        holder.UserPhone.setText(model.getPhone());
        holder.UserEmail.setText(model.getEmail());
        String userID = model.getuID();
        if(model.getProfileURL()!=null && model.getProfileURL()!=""){
            Picasso.get().load(model.getProfileURL()).into(holder.UserImg);
        }

        holder.UserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.UserImg.getContext(), UserProfileAdminView.class);
                intent.putExtra("userId", userID);
                intent.putExtra("uName", model.getfName());
                intent.putExtra("uEmail", model.getEmail());
                intent.putExtra("uPhone", model.getPhone());
                intent.putExtra("imgURL", model.getProfileURL());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.UserImg.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.usersinglerow, parent, false);
        return new holder(view); //returning the references of holder class
    }

    class holder extends RecyclerView.ViewHolder
    {
        CircleImageView UserImg;
        TextView UserName, UserPhone, UserEmail;
        public holder(@NonNull View itemView) {
            super(itemView);
            UserImg = (CircleImageView)itemView.findViewById(R.id.imgplaceholder);
            UserName = (TextView)itemView.findViewById(R.id.t_usersName);
            UserPhone= (TextView)itemView.findViewById(R.id.t_usersPhone);
            UserEmail = (TextView)itemView.findViewById(R.id.t_usersEmail);
        }
    }
}
