package com.example.seniorproject_hospitalapp;

import android.content.Context;
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

/*
 *  This is a recycler view adapter class for SearchDoctorsAdmin activity's recycler view.
 *  This class provides an interface to display and control the Doctor Profile Data in the recycler view
 */
public class DoctorRecyclerViewAdapter extends FirestoreRecyclerAdapter<DoctorDataModel, DoctorRecyclerViewAdapter.DoctorViewHolder>
{
    String m_userID =FirebaseAuth.getInstance().getUid();
    FirebaseFirestore m_fStore = FirebaseFirestore.getInstance();
    DocumentReference m_df = m_fStore.collection("Admin").document(m_userID);

    /**/
    /*

    NAME

            DoctorRecyclerViewAdapter - constructor for DoctorRecyclerViewAdapter class

    SYNOPSIS

            public DoctorRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<DoctorDataModel> options)
                options    --> instance of FirestoreRecyclerOptions class

    DESCRIPTION

            This function is constructor for DoctorRecyclerViewAdapter class

    RETURNS

            Nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021

    */
    /**/
    public DoctorRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<DoctorDataModel> options) {
        super(options);
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

            This function is called to create a new RecyclerView.ViewHolder. The created view is
            inflated using the single row resource file doctor_single_row.xml. This inflated
            view is passed to the DoctorViewHolder class's constructor which initializes
            the views contained inside it, and is returned or passed to the onBindViewHolder

    RETURNS

            DoctorViewHolder

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021

    */
    /**/
    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup a_parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(a_parent.getContext());
        View view = inflater.inflate(R.layout.doctor_single_row, a_parent, false);
        return new DoctorRecyclerViewAdapter.DoctorViewHolder(view);
    }

    /**/
    /*

    NAME

            onBindViewHolder - Binds the new item's view with its respective data / content

    SYNOPSIS

            protected void onBindViewHolder(@NonNull UserViewHolder a_holder, int a_position, @NonNull UserDataModel a_model)
                a_holder   --> Holder object for the given item
                a_position --> position of the data array to be populated in the recycler view
                a_model    --> an instance of DoctorDataModel class

    DESCRIPTION

            This function binds the view passed by onCreateViewHolder to its actual contents
            that is received from the object of DoctorDataModel and methods of the class.
            It also sets an onClickListener for single itemViews which will then take the
            user to the particular Doctor's profile page depending on whether the user is
            a Patient or an Admin.

    RETURNS

            Nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021

    */
    /**/
    @Override
    protected void onBindViewHolder(@NonNull DoctorViewHolder a_holder, int a_position, @NonNull DoctorDataModel a_model) {

        a_holder.DocName.setText(a_model.getDocName());
        CheckAndSetInfo(a_holder.DocEmail,a_model.getDocEmail(),"Doctor's Email");
        CheckAndSetInfo(a_holder.DocPhone,a_model.getDocPhone(),"Doctor's Phone");
        if(a_model.getProfileURL()!=null && a_model.getProfileURL()!=""){
            Picasso.get().load(a_model.getProfileURL()).into(a_holder.DocImg);
        }else{
            a_holder.DocImg.setImageResource(R.drawable.doctor_avatar);;
        }

        //on click for each card view that takes user to the Doctor's profile page
        a_holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                m_df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        //identify if user is patient or admin
                        if(documentSnapshot.getString("isAdmin")!=null){

                            //user is admin, send to DoctorProfileAdmin activity
                            GoToDoctorProfile(DoctorProfileAdmin.class, a_model, a_holder);

                        }else{

                            //user is normal user
                            GoToDoctorProfile(DoctorProfileUser.class, a_model, a_holder);
                        }
                    }
                });

            }
        });
    }

    /**/
    /*

    NAME

            GoToDoctorProfile - takes user to the respective Doctor's profile page

    SYNOPSIS

            private void GoToDoctorProfile(Class a_DoctorProfileClass, DoctorDataModel a_model, DoctorViewHolder a_holder)
                a_DoctorProfileClass   --> Doctor's Profile class passed from the onBindViewHolder
                a_model                --> an instance of DoctorDataModel class
                a_holder               --> an instance of DoctorViewHolder class

    DESCRIPTION

            This function takes user to the respective Doctor's profile page depending on
            whether the user is an Admin or Patient. If the user is Admin, they are taken to
            DoctorProfileAdmin and if the user is a Patient, they are taken to DoctorProfileUser

    RETURNS

            Nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021

    */
    /**/
    private void GoToDoctorProfile(Class a_DoctorProfileClass, DoctorDataModel a_model, DoctorViewHolder a_holder){
        Intent intent = new Intent(a_holder.itemView.getContext(), a_DoctorProfileClass);
        intent.putExtra("DocId", a_model.getDocID());
        intent.putExtra("DocName", a_model.getDocName());
        intent.putExtra("DocEmail", a_model.getDocEmail());
        intent.putExtra("DocPhone", a_model.getDocPhone());
        intent.putExtra("DocBio", a_model.getDocBio());
        intent.putExtra("DocProfileUrl", a_model.getProfileURL());

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        a_holder.itemView.getContext().startActivity(intent);
    }

    /**/
    /*

    NAME

            GoToDoctorProfile - checks and sets information in the recycler view's single items

    SYNOPSIS

            private void CheckAndSetInfo(TextView a_view, String a_data, String a_defaultData)
                a_view          --> View to set the info on
                a_data          --> actual data which can also be null
                a_defaultData   --> string to set if a_data is null

    DESCRIPTION

            This function checks and sets information in the recycler view's single items.
            It accepts the actual data, and a default data. If the actual data is null then
            it sets the text for the view as the default data else sets the text as the
            actual data.

    RETURNS

            Nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021

    */
    /**/
    public void CheckAndSetInfo(TextView a_view, String a_data, String a_defaultData){
        if(a_data!=null){
            a_view.setText(a_data);
        }else{
            a_view.setText(a_defaultData);
        }
    }
    /*
     * This class is a View Holder class for SearchDoctorsAdmin's recycler view adapter.
     * This class pulls the reference of single row layout associated with the DoctorRecyclerViewAdapter.
     *
     * */
    class DoctorViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView DocImg;
        TextView DocName, DocPhone, DocEmail;

        /**/
        /*

        NAME

                DoctorViewHolder - constructor for UserViewHolder class

        SYNOPSIS

                public DoctorViewHolder(@NonNull View itemView)
                    itemView --> Single item in the recycler view

        DESCRIPTION

                The constructor of DoctorViewHolder class initializes all the member
                variables and views to their respective ids in the layout resource file

        RETURNS

                Nothing

        AUTHOR

                Sibika Silwal

        DATE

                7:30pm 03/10/2021

        */
        /**/
        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            DocImg = (CircleImageView)itemView.findViewById(R.id.imgplaceholderDoc);
            DocName = (TextView)itemView.findViewById(R.id.t_DocName);
            DocPhone= (TextView)itemView.findViewById(R.id.t_DocPhone);
            DocEmail = (TextView)itemView.findViewById(R.id.t_DocEmail);
        }
    }
}
