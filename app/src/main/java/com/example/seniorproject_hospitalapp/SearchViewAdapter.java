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

/*
 *  This is a recycler view adapter class for SearchUsersAdmin activity's recycler view.
 *  This class provides an interface to display and control the User Profile Data in the recycler view
 */
public class SearchViewAdapter extends FirestoreRecyclerAdapter<UserDataModel, SearchViewAdapter.UserViewHolder>
{
    Context m_context;

    /**/
    /*

    NAME

            SearchViewAdapter - constructor for UserDocumentAdapter class

    SYNOPSIS

            public SearchViewAdapter(@NonNull FirestoreRecyclerOptions<UserDataModel> options, Context a_context)
                a_context  --> application context
                options    --> instance of FirestoreRecyclerOptions class

    DESCRIPTION

            This function initializes the global context, m_context to the context passed

    RETURNS

            Nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021

    */
    /**/
    public SearchViewAdapter(@NonNull FirestoreRecyclerOptions<UserDataModel> options, Context a_context) {
        super(options);
        this.m_context = a_context;
    }

    /**/
    /*

    NAME

            onCreateViewHolder - creates empty view for the Single Row layout

    SYNOPSIS

            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup a_parent, int a_viewType)
                a_parent --> parent ViewGroup
                a_viewType --> view position in the recycler view

    DESCRIPTION

            This function is called to create a new RecyclerView.ViewHolder. The created view
            is inflated using the single row resource file usersinglerow.xml. This
            inflated view is passed to the UserViewHolder class's constructor which initializes the views
            contained inside it, and is returned or passed to the onBindViewHolder

    RETURNS

            UserViewHolder

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021

    */
    /**/
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup a_parent, int a_viewType) {
        LayoutInflater inflater = LayoutInflater.from(a_parent.getContext());
        View view = inflater.inflate(R.layout.usersinglerow, a_parent, false);
        return new UserViewHolder(view);
    }

    /**/
    /*

    NAME

            onBindViewHolder - Binds the new item's view with its respective data / content

    SYNOPSIS

            protected void onBindViewHolder(@NonNull UserViewHolder a_holder, int a_position, @NonNull UserDataModel a_model)
                a_holder   --> Holder object for the given item
                a_position --> position of the data array to be populated in the recycler view
                a_model    --> an instance of UserDataModel class

    DESCRIPTION

            This function binds the view passed by onCreateViewHolder to its actual contents
            that is received from the object of UserDataModel and methods of the class.
            It also sets an onClickListener for single itemViews which will then take the
            user to the particular user's profile page

    RETURNS

            Nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021

    */
    /**/
    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder a_holder, int a_position, @NonNull UserDataModel a_model) {

        a_holder.UserName.setText(a_model.getfName());
        a_holder.UserPhone.setText(a_model.getPhone());
        a_holder.UserEmail.setText(a_model.getEmail());
        String userID = a_model.getuID();
        if(a_model.getProfileURL()!=null && a_model.getProfileURL()!=""){
            Picasso.get().load(a_model.getProfileURL()).into(a_holder.UserImg);
        }else{
            a_holder.UserImg.setImageResource(R.drawable.patient_avatar);;
        }

        a_holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(a_holder.itemView.getContext(), UserProfileAdminView.class);
                intent.putExtra("userId", userID);
                intent.putExtra("uName", a_model.getfName());
                intent.putExtra("uEmail", a_model.getEmail());
                intent.putExtra("uPhone", a_model.getPhone());
                intent.putExtra("imgURL", a_model.getProfileURL());
                System.out.println("imgURL: "+a_model.getProfileURL());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                a_holder.UserImg.getContext().startActivity(intent);
            }
        });
    }

    /*
     * This class is a View Holder class for SearchUsersAdmin's recycler view adapter.
     * This class pulls the reference of single row layout associated with the SearchViewAdapter.
     *
     * */
    class UserViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView UserImg;
        TextView UserName, UserPhone, UserEmail;

        /**/
        /*

        NAME

                UserViewHolder - constructor for UserViewHolder class

        SYNOPSIS

                public UserViewHolder(@NonNull View itemView)
                    itemView --> Single item in the recycler view

        DESCRIPTION

                The constructor of UserViewHolder class initializes all the member
                variables and views to their respective ids in the layout resource file

        RETURNS

                Nothing

        AUTHOR

                Sibika Silwal

        DATE

                7:30pm 03/10/2021

        */
        /**/
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            UserImg = (CircleImageView)itemView.findViewById(R.id.imgplaceholder);
            UserName = (TextView)itemView.findViewById(R.id.t_usersName);
            UserPhone= (TextView)itemView.findViewById(R.id.t_usersPhone);
            UserEmail = (TextView)itemView.findViewById(R.id.t_usersEmail);
        }
    }
}
