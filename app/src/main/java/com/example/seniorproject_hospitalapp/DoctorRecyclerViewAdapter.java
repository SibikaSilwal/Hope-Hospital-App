package com.example.seniorproject_hospitalapp;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorRecyclerViewAdapter extends FirestoreRecyclerAdapter<DoctorDataModel, DoctorRecyclerViewAdapter.holder>
{
    String m_userID =FirebaseAuth.getInstance().getUid();
    FirebaseFirestore m_fStore = FirebaseFirestore.getInstance();
    DocumentReference df = m_fStore.collection("Admin").document(m_userID);
    public DoctorRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<DoctorDataModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull holder holder, int position, @NonNull DoctorDataModel model) {
        holder.DocName.setText(model.getDocName());
        if(model.getDocEmail()!=null)holder.DocEmail.setText(model.getDocEmail());
        if(model.getDocPhone()!=null)holder.DocPhone.setText(model.getDocPhone());
        if(model.getProfileURL()!=null && model.getProfileURL()!=""){
            Picasso.get().load(model.getProfileURL()).into(holder.DocImg);
        }
        holder.DocImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //identify if user is patient or admin
                        if(documentSnapshot.getString("isAdmin")!=null){
                            //user is admin, send to DoctorProfileAdmin activity
                            Intent intent = new Intent(holder.DocImg.getContext(), DoctorProfileAdmin.class);
                            intent.putExtra("DocId", model.getDocID());
                            intent.putExtra("DocName", model.getDocName());
                            intent.putExtra("DocEmail", model.getDocEmail());
                            intent.putExtra("DocPhone", model.getDocPhone());
                            intent.putExtra("DocBio", model.getDocBio());
                            intent.putExtra("DocProfileUrl", model.getProfileURL());

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            holder.DocImg.getContext().startActivity(intent);
                        }else{
                            //user is normal user
                            Intent intent = new Intent(holder.DocImg.getContext(), DoctorProfileUser.class);
                            //intent.putExtra("DocId", model.getDocID());
                            intent.putExtra("DocName", model.getDocName());
                            intent.putExtra("DocEmail", model.getDocEmail());
                            intent.putExtra("DocPhone", model.getDocPhone());
                            intent.putExtra("DocBio", model.getDocBio());
                            intent.putExtra("DocProfileUrl", model.getProfileURL());

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            holder.DocImg.getContext().startActivity(intent);
                        }
                    }
                });

            }
        });
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.doctor_single_row, parent, false);
        return new DoctorRecyclerViewAdapter.holder(view);
    }

    class holder extends RecyclerView.ViewHolder
    {
        CircleImageView DocImg;
        TextView DocName, DocPhone, DocEmail;
        public holder(@NonNull View itemView) {
            super(itemView);
            DocImg = (CircleImageView)itemView.findViewById(R.id.imgplaceholderDoc);
            DocName = (TextView)itemView.findViewById(R.id.t_DocName);
            DocPhone= (TextView)itemView.findViewById(R.id.t_DocPhone);
            DocEmail = (TextView)itemView.findViewById(R.id.t_DocEmail);
        }
    }
}
