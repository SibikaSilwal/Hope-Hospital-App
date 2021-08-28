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

/*
 *  This is a recycler view adapter class for ManageDocAppointment activity's recycler view.
 *  This class provides an interface to display and control the Doctor Appointment Data in the recycler view
 */
public class AppointmentManagerAdapter extends RecyclerView.Adapter<AppointmentManagerAdapter.DoctorApptViewHolder> {

    private Context m_context;
    private FirebaseFirestore m_fStore = FirebaseFirestore.getInstance();
    private ArrayList<Map<String, Object>> m_data = new ArrayList<>();

    /**/
    /*

    NAME

            AppointmentManagerPatientAdapter - constructor for AppointmentManagerAdapter class

    SYNOPSIS

            public AppointmentManagerAdapter(ArrayList<Map<String, Object>> a_data, Context a_context)
                a_data     --> Doctor Appointment data array passed from ManageDocAppointment class
                a_context  --> application context

    DESCRIPTION

            This function initializes the global array m_data with the Doctor Appointments Array,
            m_context to the context passed

    RETURNS

            Nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 04/20/2021

    */
    /**/
    public AppointmentManagerAdapter(ArrayList<Map<String, Object>> a_data, Context a_context) {
        this.m_data = a_data;
        this.m_context = a_context;
    }

    /**/
    /*

    NAME

            onCreateViewHolder - creates empty view for the Single Row layout

    SYNOPSIS

            public DoctorApptViewHolder onCreateViewHolder(@NonNull ViewGroup a_parent, int a_viewType)
                a_parent --> parent ViewGroup
                a_viewType --> view position in the recycler view

    DESCRIPTION

            This function is called to create a new RecyclerView.ViewHolder. The created view
            is inflated using the single row resource file doc_appointment_manage_singlerow.xml. This
            inflated view is passed to the Holder class's constructor which initializes the views
            contained inside it, and is returned or passed to the onBindViewHolder

    RETURNS

            DoctorApptViewHolder

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 04/20/2021

    */
    /**/
    @NonNull
    @Override
    public DoctorApptViewHolder onCreateViewHolder(@NonNull ViewGroup a_parent, int a_viewType) {
        LayoutInflater inflater = LayoutInflater.from(a_parent.getContext());
        View view = inflater.inflate(R.layout.doc_appointment_manage_singlerow, a_parent, false);
        return new DoctorApptViewHolder(view);
    }

    /**/
    /*

    NAME

            onBindViewHolder - Binds the new item's view with its respective data / content

    SYNOPSIS

            public void onBindViewHolder(@NonNull DoctorApptViewHolder a_holder, int a_position)
                a_holder --> Holder object for the given item
                a_position --> position of the data array to be populated in the recycler view

    DESCRIPTION

            This function binds the view passed by onCreateViewHolder to its actual contents
            that is received from the m_data array. It also sets an onClickListener for the
            Cancel appointment button, and MarkDone button.

    RETURNS

            Nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 04/20/2021

    */
    /**/
    @Override
    public void onBindViewHolder(@NonNull DoctorApptViewHolder a_holder, int a_position) {
        DocumentReference docRefUser = m_fStore.collection("users").document(m_data.get(a_position).get("PatientID").toString());
        docRefUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                a_holder.PatientName.setText(task.getResult().get("fName").toString());
                a_holder.PatientPhone.setText(task.getResult().get("phone").toString());
                a_holder.PatientEmail.setText(task.getResult().get("email").toString());
                a_holder.ApptTime.setText(m_data.get(a_position).get("day").toString()+" at "+m_data.get(a_position).get("time").toString());
                if(task.getResult().get("profileURL")!=null){
                    Picasso.get().load(task.getResult().get("profileURL").toString()).into(a_holder.PatientImg);
                }
            }
        });
        a_holder.CancelAppt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveAppointments(a_position);
            }
        });

        a_holder.MarkDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveAppointments(a_position);
                Toast.makeText(m_context, "Appointment marked as completed.", Toast.LENGTH_SHORT).show();
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

                This function gives the size of the m_data array i.e. the Doctor's Appointment Array

        RETURNS

                data Array Count

        AUTHOR

                Sibika Silwal

        DATE

                7:30pm 04/20/2021

        */
    /**/
    @Override
    public int getItemCount() {
        return m_data.size();
    }

    /**/
        /*

        NAME

                RemoveAppointments - Removes appointments from users and doctor's collections in database

        SYNOPSIS

                private void RemoveAppointments(int a_position)
                    a_position --> Array index / view position for the appointment being removed

        DESCRIPTION

                This function removes the doctor's appointment on cancelling a particular appointment
                in the ManageDocAppointment Page's recycler view. This function receives the index of the
                appointment being cancelled and updates User collection in database by creating a map of
                the appointment being deleted and asking the database to delete that given map from
                users document's "AppointmentsInfo" array. After updating the user's collection, it also
                updates the Doctor's collection by finding the right appointment map and sets the appointment
                spot as available.

        RETURNS

                Nothing

        AUTHOR

                Sibika Silwal

        DATE

                7:30pm 04/20/2021

        */
    /**/
    private void RemoveAppointments(int a_position){
        DocumentReference documentRefDoctor = m_fStore.collection("Doctors").document(m_data.get(a_position).get("DocID").toString());
        DocumentReference documentRefUser = m_fStore.collection("users").document(m_data.get(a_position).get("PatientID").toString());

        //update the AppointmentsInfo array in users collection in database
        documentRefUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                //create the map for the appointment to be removed from user's document
                Map<String, Object> appttoCancel = new HashMap<>();
                appttoCancel.put("Time",m_data.get(a_position).get("time"));
                appttoCancel.put("Day", m_data.get(a_position).get("day"));
                appttoCancel.put("Doctor",m_data.get(a_position).get("DocName"));
                appttoCancel.put("DoctorID",m_data.get(a_position).get("DocID"));
                appttoCancel.put("reminderTime",m_data.get(a_position).get("ReminderTime"));

                //removes appointment from AppointmentsInfo array in users document
                documentRefUser.update("AppointmentsInfo", FieldValue.arrayRemove(appttoCancel))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        documentRefDoctor.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                //gets the appointment array from Doctor's Document for the day of appointment cancellation
                                ArrayList<Map<String, Object>> dayArr = (ArrayList<Map<String, Object>>) task.getResult().get(m_data.get(a_position).get("day").toString());

                                //updates the cancelled appointment for the day
                                dayArr.get((int)m_data.get(a_position).get("index")).put("isAvailable", true);
                                dayArr.get((int)m_data.get(a_position).get("index")).put("AppointmentID",0);
                                documentRefDoctor.update(m_data.get(a_position).get("day").toString(), dayArr)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //reflects the change in the ManageDocAppointment page
                                        m_data.remove(a_position);
                                        notifyItemRemoved(a_position);
                                        notifyItemRangeChanged(a_position, m_data.size());
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

    /*
     * This class is a View Holder class for ManageDocAppointment's recycler view adapter.
     * This class pulls the reference of single row layout associated with the AppointmentManagerAdapter.
     *
     * */
    class DoctorApptViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView PatientImg;
        TextView PatientName, PatientPhone, PatientEmail, ApptTime;
        Button CancelAppt, MarkDone;

        /**/
        /*

        NAME

                DoctorApptViewHolder - constructor for DoctorApptViewHolder class

        SYNOPSIS

                public DoctorApptViewHolder(@NonNull View a_itemView)
                    a_itemView --> Single item in the recycler view

        DESCRIPTION

                The constructor of DoctorApptViewHolder class initializes all the member
                variables and views to their respective ids in the layout resource file

        RETURNS

                Nothing

        AUTHOR

                Sibika Silwal

        DATE

                7:30pm 04/20/2021

        */
        /**/
        public DoctorApptViewHolder(@NonNull View a_itemView) {
            super(a_itemView);
            PatientImg = (CircleImageView)a_itemView.findViewById(R.id.patientImage);
            PatientName = (TextView)a_itemView.findViewById(R.id.t_apptPatientName);
            PatientPhone= (TextView)a_itemView.findViewById(R.id.t_apptPatientPhone);
            PatientEmail = (TextView)a_itemView.findViewById(R.id.t_apptPatientEmail);
            ApptTime = (TextView)a_itemView.findViewById(R.id.t_apptTime);
            CancelAppt = (Button) a_itemView.findViewById(R.id.b_cancelAppt);
            MarkDone = (Button) a_itemView.findViewById(R.id.b_apptCompleted);
        }
    }
}
