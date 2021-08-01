package com.example.seniorproject_hospitalapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppointmentManagerAdapter extends RecyclerView.Adapter<AppointmentManagerAdapter.holder> {

    private Context m_context;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    ArrayList<Map<String, Object>> data = new ArrayList<>();
    public AppointmentManagerAdapter(ArrayList<Map<String, Object>> data, Context a_context) {
        this.data = data;
        this.m_context = a_context;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.doc_appointment_manage_singlerow, parent, false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        DocumentReference docRefUser = fStore.collection("users").document(data.get(position).get("PatientID").toString());
        docRefUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                holder.PatientName.setText(task.getResult().get("fName").toString());
                holder.PatientPhone.setText(task.getResult().get("phone").toString());
                holder.PatientEmail.setText(task.getResult().get("email").toString());
                holder.ApptTime.setText(data.get(position).get("day").toString()+" at "+data.get(position).get("time").toString());
                if(task.getResult().get("profileURL")!=null){
                    Picasso.get().load(task.getResult().get("profileURL").toString()).into(holder.PatientImg);
                }
            }
        });
        holder.CancelAppt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread removeApptsfromDatabase = new Thread(new Runnable() {
                    public void run()
                    {
                        RemoveApppointments(position);
                    }});
                removeApptsfromDatabase.start();
            }
        });

        holder.MarkDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread removeApptsfromDatabase = new Thread(new Runnable() {
                    public void run()
                    {
                        RemoveApppointments(position);
                    }});
                removeApptsfromDatabase.start();
                Toast.makeText(m_context, "Appointment marked as completed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private synchronized void RemoveApppointments(int position){
        DocumentReference docrefDoctor = fStore.collection("Doctors").document(data.get(position).get("DocID").toString());
        DocumentReference docRefUser = fStore.collection("users").document(data.get(position).get("PatientID").toString());
        docRefUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> appttoCancel = new HashMap<>();
                appttoCancel.put("Time",data.get(position).get("time"));
                appttoCancel.put("Day", data.get(position).get("day"));
                appttoCancel.put("Doctor",data.get(position).get("DocName"));
                appttoCancel.put("DoctorID",data.get(position).get("DocID"));
                appttoCancel.put("reminderTime",data.get(position).get("ReminderTime"));
                docRefUser.update("AppointmentsInfo", FieldValue.arrayRemove(appttoCancel))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("appt to cancel: "+ appttoCancel + " user db update complete");
                        docrefDoctor.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                ArrayList<Map<String, Object>> dayArr = (ArrayList<Map<String, Object>>) task.getResult().get(data.get(position).get("day").toString());
                                dayArr.get((int)data.get(position).get("index")).put("isAvailable", true);
                                dayArr.get((int)data.get(position).get("index")).put("AppointmentID",0);
                                docrefDoctor.update(data.get(position).get("day").toString(), dayArr)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        System.out.println("Doctor db update complete " + data.size());
                                        System.out.println("Postion before remove "+ position);
                                        data.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, data.size());
                                        System.out.println("data remove completed  "+ data.size());
                                        Toast.makeText(m_context, "Appointment has been cancelled.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dayArr.clear();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(m_context, "Appointment could not be updated. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    class holder extends RecyclerView.ViewHolder
    {
        CircleImageView PatientImg;
        TextView PatientName, PatientPhone, PatientEmail, ApptTime;
        Button CancelAppt, MarkDone;
        public holder(@NonNull View itemView) {
            super(itemView);
            PatientImg = (CircleImageView)itemView.findViewById(R.id.patientImage);
            PatientName = (TextView)itemView.findViewById(R.id.t_apptPatientName);
            PatientPhone= (TextView)itemView.findViewById(R.id.t_apptPatientPhone);
            PatientEmail = (TextView)itemView.findViewById(R.id.t_apptPatientEmail);
            ApptTime = (TextView)itemView.findViewById(R.id.t_apptTime);
            CancelAppt = (Button) itemView.findViewById(R.id.b_cancelAppt);
            MarkDone = (Button) itemView.findViewById(R.id.b_apptCompleted);
        }
    }
}
