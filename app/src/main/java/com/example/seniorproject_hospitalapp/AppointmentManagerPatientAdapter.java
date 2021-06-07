package com.example.seniorproject_hospitalapp;

import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppointmentManagerPatientAdapter extends RecyclerView.Adapter<AppointmentManagerPatientAdapter.holder> {

    private Context m_context;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String m_userID = fAuth.getUid();
    ArrayList<Map<String, Object>> data = new ArrayList<>();

    public AppointmentManagerPatientAdapter(ArrayList<Map<String, Object>> data, Context a_context) {
        this.data = data;
        this.m_context = a_context;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.appointment_patient_singlerow, parent, false);
        return new AppointmentManagerPatientAdapter.holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        holder.DoctorName.setText(data.get(position).get("Doctor").toString());
        holder.ApptTime.setText(data.get(position).get("Day").toString()+" "+data.get(position).get("Time").toString());
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
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private synchronized void RemoveApppointments(int position){
        DocumentReference docrefDoctor = fStore.collection("Doctors").document(data.get(position).get("DoctorID").toString());
        DocumentReference docRefUser = fStore.collection("users").document(m_userID);
        docRefUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                docRefUser.update("AppointmentsInfo", FieldValue.arrayRemove(data.get(position)))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        docrefDoctor.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                ArrayList<Map<String, Object>> dayArr = (ArrayList<Map<String, Object>>) task.getResult().get(data.get(position).get("Day").toString());
                                int index =0;
                                for(Map<String, Object> appt : dayArr){
                                    if((appt.get("AppointmentID").toString().equals(m_userID)) && (appt.get("Time").toString().equals(data.get(position).get("Time").toString()))){
                                        appt.put("isAvailable", true);
                                        appt.put("AppointmentID",0);
                                        dayArr.get(index).put("isAvailable", true);
                                        dayArr.get(index).put("AppointmentID",0);
                                        break;
                                    }
                                    index++;
                                }
                                docrefDoctor.update(data.get(position).get("Day").toString(), dayArr)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        data.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, data.size());
                                        Toast.makeText(m_context, "Appointment has been cancelled.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dayArr.clear();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(m_context, "Appointment could not be cancelled. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    class holder extends RecyclerView.ViewHolder
    {
        CircleImageView DoctorImg;
        TextView DoctorName, DoctorPhone, DoctorEmail, ApptTime;
        Button CancelAppt;
        public holder(@NonNull View itemView) {
            super(itemView);
            DoctorImg = (CircleImageView)itemView.findViewById(R.id.DoctorImage);
            DoctorName = (TextView)itemView.findViewById(R.id.t_apptDoctorName);
            DoctorPhone= (TextView)itemView.findViewById(R.id.t_apptDoctorPhone);
            DoctorEmail = (TextView)itemView.findViewById(R.id.t_apptDoctorEmail);
            ApptTime = (TextView)itemView.findViewById(R.id.t_apptTimePatient);
            CancelAppt = (Button) itemView.findViewById(R.id.b_cancelApptPatient);
        }
    }
}
