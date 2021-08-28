package com.example.seniorproject_hospitalapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 *  This is a recycler view adapter class for Appointment activity's recycler view.
 *  This class provides an interface to display doctor's availabilities and allows
 * users and admin to schedule appointments for doctor's with patients
 */
public class DocAvailabilityAdapter extends RecyclerView.Adapter<DocAvailabilityAdapter.AvailabilityViewHolder> {

    private Context m_context;
    private ArrayList<Map<String, Object>> m_AvailDataArray = new ArrayList<>();
    private FirebaseFirestore m_fstore = FirebaseFirestore.getInstance();
    private DocumentReference m_DocumentRefDoctor, m_DocumentRefUser;
    private String m_userID, m_doctorID, m_doctorName;

    /**/
    /*

    NAME

            DocAvailabilityAdapter - constructor for UserDocumentAdapter class

    SYNOPSIS

            public DocAvailabilityAdapter(ArrayList<Map<String, Object>> a_data, Context a_context)
                a_data     --> Doctor's availability data array passed from Appointment class
                a_context  --> application context

    DESCRIPTION

            This function initializes the global array m_AvailDataArray with the Doctor availability Array,
            m_context to the context passed. The constructor also stores the userID, doctorID, and doctorName
            in their respective strings, and initializes user and doctor DocumentReference object.

    RETURNS

            Nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/25/2021

    */
    /**/
    public DocAvailabilityAdapter(ArrayList<Map<String, Object>> a_data, Context a_context) {
        this.m_AvailDataArray = a_data;
        this.m_context = a_context;
        m_userID = a_data.get(a_data.size() -1).get("userID").toString();
        m_doctorID = a_data.get(a_data.size() -1).get("docID").toString();
        m_doctorName = a_data.get(a_data.size() -1).get("Doctor").toString();
        m_DocumentRefDoctor = m_fstore.collection("Doctors").document(m_doctorID);
        m_DocumentRefUser = m_fstore.collection("users").document(m_userID);
    }

    /**/
    /*

    NAME

            onCreateViewHolder - creates empty view for the Single Row layout

    SYNOPSIS

            public AvailabilityViewHolder onCreateViewHolder(@NonNull ViewGroup a_parent, int a_viewType)
                a_parent --> parent ViewGroup
                a_viewType --> view position in the recycler view

    DESCRIPTION

            This function is called to create a new RecyclerView.ViewHolder. The created view
            is inflated using the single row resource file available_time_single_row.xml. This
            inflated view is passed to the Holder class's constructor which initializes the views
            contained inside it, and is returned or passed to the onBindViewHolder

    RETURNS

            AvailabilityViewHolder

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/25/2021

    */
    /**/
    @NonNull
    @Override
    public AvailabilityViewHolder onCreateViewHolder(@NonNull ViewGroup a_parent, int a_viewType) {
        LayoutInflater inflater = LayoutInflater.from(a_parent.getContext());
        View view = inflater.inflate(R.layout.available_time_single_row, a_parent, false);
        return new AvailabilityViewHolder(view);
    }

    /**/
    /*

    NAME

            onBindViewHolder - Binds the new item's view with its respective data / content

    SYNOPSIS

            public void onBindViewHolder(@NonNull AvailabilityViewHolder a_holder, int a_position)
                a_holder --> Holder object for the given item
                a_position --> position of the data array to be populated in the recycler view

    DESCRIPTION

            This function binds the view passed by onCreateViewHolder to its actual contents
            that is received from the m_AvailDataArray array. It also sets an onClickListener
            for single itemView which schedules user's appointment with the doctor for the given
            date and time. After clicking on the itemView, the database for both user and doctor
            is updated to reflect the newly scheduled appointment

    RETURNS

            Nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/25/2021

    */
    /**/
    @Override
    public void onBindViewHolder(@NonNull AvailabilityViewHolder a_holder, int a_position) {
        a_holder.m_day.setText(m_AvailDataArray.get(a_position).get("day").toString());
        a_holder.m_time.setText(m_AvailDataArray.get(a_position).get("time").toString());
        a_holder.m_reminderTime.setText(m_AvailDataArray.get(a_position).get("reminderTime").toString());

        //on clicking a view, database is updated to schedule the appointment
        a_holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_DocumentRefDoctor.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        //retrieving and updating the day's availability in Doctor's document in the database
                        ArrayList<Map<String, Object>> dayArr = (ArrayList<Map<String, Object>>) task.getResult().get(m_AvailDataArray.get(a_position).get("day").toString());

                        dayArr.get((int)m_AvailDataArray.get(a_position).get("index")).put("isAvailable", false);
                        dayArr.get((int)m_AvailDataArray.get(a_position).get("index")).put("AppointmentID", m_userID);
                        m_DocumentRefDoctor.update(m_AvailDataArray.get(a_position).get("day").toString(), dayArr)

                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                //Appointment information map to add to the users document in database
                                Map<String, Object> apptinfo = new HashMap<>();
                                apptinfo.put("Time", a_holder.m_time.getText());
                                apptinfo.put("Day", a_holder.m_day.getText());
                                apptinfo.put("reminderTime", a_holder.m_reminderTime.getText());
                                apptinfo.put("Doctor", m_doctorName);
                                apptinfo.put("DoctorID", m_doctorID);

                                m_DocumentRefUser.update("AppointmentsInfo", FieldValue.arrayUnion(apptinfo))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //reflects the appointment availability changes in the page
                                        m_AvailDataArray.remove(a_position);
                                        notifyItemRemoved(a_position);
                                        notifyItemRangeChanged(a_position, m_AvailDataArray.size()-1);
                                    }
                                });
                                //updates newReminderTime in user's document which is used to send notification to user
                                m_DocumentRefUser.update("newReminderTime", a_holder.m_reminderTime.getText());
                                Toast.makeText(m_context, "Appointment set for "+a_holder.m_day.getText()+" "+a_holder.m_time.getText(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        dayArr.clear();

                    }
                });
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

                This function gives the size of the m_AvailDataArray array minus 1
                because the last element in the m_AvailDataArray contains a map of
                Docotor's id, Doctor's name, and User's ID. Thus, the function
                returns the number of availabilities for the doctor in the given day

        RETURNS

                data Array Count

        AUTHOR

                Sibika Silwal

        DATE

                7:30pm 03/25/2021

        */
    /**/
    @Override
    public int getItemCount() {
        return m_AvailDataArray.size()-1;
    }

    /*
     * This class is a View Holder class for Appointment Class's recycler view adapter.
     * This class pulls the reference of single row layout associated with the DocAvailabilityAdapter.
     *
     * */
    class AvailabilityViewHolder extends RecyclerView.ViewHolder{
        TextView m_day;
        TextView m_time;
        TextView m_reminderTime;

        /**/
        /*

        NAME

                AvailabilityViewHolder - constructor for UserViewHolder class

        SYNOPSIS

                public AvailabilityViewHolder(@NonNull View itemView)
                    itemView --> Single item in the recycler view

        DESCRIPTION

                The constructor of AvailabilityViewHolder class initializes all the member
                variables and views to their respective ids in the layout resource file

        RETURNS

                Nothing

        AUTHOR

                Sibika Silwal

        DATE

                7:30pm 03/25/2021

        */
        /**/
        public AvailabilityViewHolder(@NonNull View itemView) {
            super(itemView);
            m_day = itemView.findViewById(R.id.t_AvailDay);
            m_time = itemView.findViewById(R.id.t_availTime);
            m_reminderTime = itemView.findViewById(R.id.t_reminderTime);
        }
    }

}
