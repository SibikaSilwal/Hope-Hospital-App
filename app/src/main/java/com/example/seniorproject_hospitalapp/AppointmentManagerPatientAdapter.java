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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/*
 *  This is a recycler view adapter class for UserAppointmentsPage activity's recycler view.
 *  This class provides an interface to display and control the User Appointment Data in the recycler view
 */
public class AppointmentManagerPatientAdapter extends RecyclerView.Adapter<AppointmentManagerPatientAdapter.PateintApptViewholder> {

    private Context m_context;
    private FirebaseFirestore m_fStore = FirebaseFirestore.getInstance();
    private String m_userID;
    private ArrayList<Map<String, Object>> m_ApptDataArr = new ArrayList<>();

    /**/
    /*

    NAME

            AppointmentManagerPatientAdapter - constructor for UserDocumentAdapter class

    SYNOPSIS

            public AppointmentManagerPatientAdapter(ArrayList<Map<String, Object>> a_data, Context a_context, String a_UserID)
                a_data     --> User Appointment data array passed from UserAppointmentsPage class
                a_context  --> application context
                a_UserID   --> User's id

    DESCRIPTION

            This function initializes the global array m_ApptDataArr with the User Appointments Array,
            m_context to the context passed, and m_userID to the particular users id passed from
            UserAppointmentsPage class

    RETURNS

            Nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/20/2021

    */
    /**/
    public AppointmentManagerPatientAdapter(ArrayList<Map<String, Object>> a_data, Context a_context, String a_UserID) {
        this.m_ApptDataArr = a_data;
        this.m_context = a_context;
        this.m_userID = a_UserID;
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

            This function is called to create a new RecyclerView.ViewHolder. The created view
            is inflated using the single row resource file document_user_single_row.xml. This
            inflated view is passed to the Holder class's constructor which initializes the views
            contained inside it, and is returned or passed to the onBindViewHolder

    RETURNS

            PateintApptViewholder

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/20/2021

    */
    /**/
    @NonNull
    @Override
    public PateintApptViewholder onCreateViewHolder(@NonNull ViewGroup a_parent, int a_viewType) {
        LayoutInflater inflater = LayoutInflater.from(a_parent.getContext());
        View view = inflater.inflate(R.layout.appointment_patient_singlerow, a_parent, false);
        return new AppointmentManagerPatientAdapter.PateintApptViewholder(view);
    }

    /**/
    /*

    NAME

            onBindViewHolder - Binds the new item's view with its respective data / content

    SYNOPSIS

            public void onBindViewHolder(@NonNull PateintApptViewholder a_holder, int a_position)
                a_holder --> Holder object for the given item
                a_position --> position of the data array to be populated in the recycler view

    DESCRIPTION

            This function binds the view passed by onCreateViewHolder to its actual contents
            that is received from the m_data array. It also sets an onClickListener for the
            Cancel appointment button, and sets the "PastAppointment" view visible for past
            appointments, and Cancel Appointment visible for future appointments

    RETURNS

            Nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/20/2021

    */
    /**/
    @Override
    public void onBindViewHolder(@NonNull PateintApptViewholder a_holder, int a_position) {
        a_holder.DoctorName.setText("Dr. "+m_ApptDataArr.get(a_position).get("Doctor").toString());
        GetDoctorInfo(a_position, a_holder);
        a_holder.ApptTime.setText(m_ApptDataArr.get(a_position).get("Day").toString()+" "+m_ApptDataArr.get(a_position).get("Time").toString());
        if((Long.parseLong(m_ApptDataArr.get(a_position).get("reminderTime").toString()))+3600000< Calendar.getInstance().getTimeInMillis()){
            a_holder.PastAppointment.setVisibility(View.VISIBLE);
            a_holder.CancelAppt.setVisibility(View.GONE);
        }
        a_holder.CancelAppt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveApppointments(a_position);
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
        return m_ApptDataArr.size();
    }

    /**/
        /*

        NAME

                GetDoctorInfo - gets doctor's information for each appointment

        SYNOPSIS

                private void GetDoctorInfo(int a_position, PateintApptViewholder a_holder)
                    a_position --> single row view position in recycler view to populate the doctor's info
                    a_holder   --> object of te PateintApptViewholder class

        DESCRIPTION

                This function gets the doctor's information for each appointment and
                sets the text in their respective views inside a single row view

        RETURNS

                Nothing

        AUTHOR

                Sibika Silwal

        DATE

                7:30pm 03/20/2021

        */
    /**/
    private void GetDoctorInfo(int a_position, PateintApptViewholder a_holder){
        DocumentReference docrefDoctor = m_fStore.collection("Doctors").document(m_ApptDataArr.get(a_position).get("DoctorID").toString());
        docrefDoctor.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().get("DocEmail")!=null) {
                    a_holder.DoctorEmail.setText(task.getResult().get("DocEmail").toString());
                }else{
                    a_holder.DoctorEmail.setText("Doctor's Email");
                }
                if(task.getResult().get("DocPhone")!=null){
                    a_holder.DoctorPhone.setText(task.getResult().get("DocPhone").toString());
                }else{
                    a_holder.DoctorPhone.setText("Doctor's Phone");
                }
                if(task.getResult().get("profileURL")!=null){
                    Picasso.get().load(task.getResult().get("profileURL").toString()).into(a_holder.DoctorImg);
                }else{
                    a_holder.DoctorImg.setImageResource(R.drawable.doctor_avatar);;
                }
            }
        });
    }

    /**/
        /*

        NAME

                RemoveApppointments - Removes appointments from users and doctor's collections in database

        SYNOPSIS

                private void RemoveApppointments(int a_position)
                    a_position --> Array index / view position for the appointment being removed

        DESCRIPTION

                This function removes the patient's appointment on cancelling a particular appointment
                in the UserAppointmentsPage recycler view. This function receives the index of the appointment
                being cancelled and updates the "AppointmentsInfo" array in User collection in database by
                deleting the particular appointment information. After updating the user's collection, it also
                updates the Doctor's collection by finding the right appointment map and sets the appointment
                spot as available.

        RETURNS

                Nothing

        AUTHOR

                Sibika Silwal

        DATE

                7:30pm 03/20/2021

        */
    /**/
    private void RemoveApppointments(int a_position){
        DocumentReference docrefDoctor = m_fStore.collection("Doctors").document(m_ApptDataArr.get(a_position).get("DoctorID").toString());
        DocumentReference docRefUser = m_fStore.collection("users").document(m_userID);

        //update the AppointmentsInfo array in users collection in database
        docRefUser.update("AppointmentsInfo", FieldValue.arrayRemove(m_ApptDataArr.get(a_position)))
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    docrefDoctor.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            //gets the appointment array from Doctor's Document for the day of appointment cancellation
                            ArrayList<Map<String, Object>> dayArr = (ArrayList<Map<String, Object>>) task.getResult().get(m_ApptDataArr.get(a_position).get("Day").toString());
                            int index =0;

                            //searches for the particular appointment to be updated
                            for(Map<String, Object> appt : dayArr){
                                if((appt.get("AppointmentID").toString().equals(m_userID))
                                    && (appt.get("Time").toString().equals(m_ApptDataArr.get(a_position).get("Time").toString()))){

                                    dayArr.get(index).put("isAvailable", true);
                                    dayArr.get(index).put("AppointmentID",0);
                                    break;

                                }
                                index++;
                            }

                            //updates the Appointment array in Doctor's collection by setting the cancelled appoint as available
                            docrefDoctor.update(m_ApptDataArr.get(a_position).get("Day").toString(), dayArr).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    m_ApptDataArr.remove(a_position);
                                    notifyItemRemoved(a_position);
                                    notifyItemRangeChanged(a_position, m_ApptDataArr.size());
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

    /*
     * This class is a View Holder class for UserAppointmentsPage's recycler view adapter.
     * This class pulls the reference of single row layout associated with the AppointmentManagerPatientAdapter.
     *
     * */
    class PateintApptViewholder extends RecyclerView.ViewHolder
    {
        CircleImageView DoctorImg;
        TextView DoctorName, DoctorPhone, DoctorEmail, ApptTime, PastAppointment;
        Button CancelAppt;

        /**/
        /*

        NAME

                PateintApptViewholder - constructor for PateintApptViewholder class

        SYNOPSIS

                public PateintApptViewholder(@NonNull View a_itemView)
                    a_itemView --> Single item in the recycler view

        DESCRIPTION

                The constructor of PateintApptViewholder class initializes all the member
                variables and views to their respective ids in the layout resource file

        RETURNS

                Nothing

        AUTHOR

                Sibika Silwal

        DATE

                7:30pm 03/20/2021

        */
        /**/
        public PateintApptViewholder(@NonNull View a_itemView) {
            super(a_itemView);
            DoctorImg = (CircleImageView)a_itemView.findViewById(R.id.DoctorImage);
            DoctorName = (TextView)a_itemView.findViewById(R.id.t_apptDoctorName);
            DoctorPhone= (TextView)a_itemView.findViewById(R.id.t_apptDoctorPhone);
            DoctorEmail = (TextView)a_itemView.findViewById(R.id.t_apptDoctorEmail);
            ApptTime = (TextView)a_itemView.findViewById(R.id.t_apptTimePatient);
            PastAppointment = (TextView)a_itemView.findViewById(R.id.t_pastAppointment);
            CancelAppt = (Button) a_itemView.findViewById(R.id.b_cancelApptPatient);
        }
    }
}
