package com.example.seniorproject_hospitalapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DocAvailabilityAdapter extends RecyclerView.Adapter<DocAvailabilityAdapter.holder> {

    ArrayList<Map<String, Object>> data = new ArrayList<>();
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();

    public DocAvailabilityAdapter(ArrayList<Map<String, Object>> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.available_time_single_row, parent, false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        holder.day.setText(data.get(position).get("day").toString());
        holder.time.setText(data.get(position).get("time").toString());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Appointment set");
                DocumentReference docrefDoctor = fstore.collection("Doctors").document(data.get(data.size() -1).get("docID").toString());
                docrefDoctor.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        ArrayList<Map<String, Object>> dayArr = (ArrayList<Map<String, Object>>) task.getResult().get(data.get(position).get("day").toString());
                        dayArr.get((int)data.get(position).get("index")).put("isAvailable", false);
                        dayArr.get((int)data.get(position).get("index")).put("AppointmentID", data.get(data.size() -1).get("userID"));
                        docrefDoctor.update(data.get(position).get("day").toString(), dayArr);
                        dayArr.clear();
                    }
                });

                DocumentReference docrefUser = fstore.collection("users").document(data.get(data.size() -1).get("userID").toString());
                Map<String, Object> apptinfo = new HashMap<>();
                apptinfo.put("Time", holder.time.getText().toString());
                apptinfo.put("Day", holder.day.getText().toString());
                apptinfo.put("Doctor", data.get(data.size() -1).get("Doctor"));
                docrefUser.update("AppointmentInfo", apptinfo);
                //Toast.makeText(Appointment.class, "Can't set Availability. Availability overlaps with the existing availability.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size()-1;
    }

    class holder extends RecyclerView.ViewHolder{
        TextView day;
        TextView time;
        public holder(@NonNull View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.t_AvailDay);
            time = itemView.findViewById(R.id.t_availTime);

        }
    }

}
