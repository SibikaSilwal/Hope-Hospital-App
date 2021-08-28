package com.example.seniorproject_hospitalapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

/*
 *  This is a recycler view adapter class for ManageGroupAdmin activity's recycler view.
 *  This class provides an interface to display and control the Wards Data in the recycler view
 */
public class WardGroupAdapter extends FirestoreRecyclerAdapter<WardModel, WardGroupAdapter.WardViewHolder> {

    /**/
    /*

    NAME

            WardGroupAdapter - constructor for WardGroupAdapter class

    SYNOPSIS

            public WardGroupAdapter(@NonNull FirestoreRecyclerOptions<WardModel> options)
                options    --> instance of FirestoreRecyclerOptions class

    DESCRIPTION

            This is the constructor for WardGroupAdapter class

    RETURNS

            Nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/25/2021

    */
    /**/
    public WardGroupAdapter(@NonNull FirestoreRecyclerOptions<WardModel> options) {
        super(options);
    }

    /**/
    /*

    NAME

            onCreateViewHolder - creates empty view for the Single Row layout

    SYNOPSIS

            public WardViewHolder onCreateViewHolder(@NonNull ViewGroup a_parent, int a_viewType)
                a_parent --> parent ViewGroup
                a_viewType --> view position in the recycler view

    DESCRIPTION

            This function is called to create a new RecyclerView.ViewHolder. The created view
            is inflated using the single row resource file ward_singlerow.xml. This
            inflated view is passed to the WardViewHolder class's constructor which initializes the views
            contained inside it, and is returned or passed to the onBindViewHolder

    RETURNS

            WardViewHolder

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/25/2021

    */
    /**/
    @NonNull
    @Override
    public WardViewHolder onCreateViewHolder(@NonNull ViewGroup a_parent, int a_viewType) {
        View view = LayoutInflater.from(a_parent.getContext()).inflate(R.layout.ward_singlerow, a_parent, false);
        return new WardGroupAdapter.WardViewHolder(view);
    }

    /**/
    /*

    NAME

            onBindViewHolder - Binds the new item's view with its respective data / content

    SYNOPSIS

            protected void onBindViewHolder(@NonNull WardViewHolder a_holder, int a_position, @NonNull WardModel a_model)
                a_holder   --> Holder object for the given item
                a_position --> position of the data array to be populated in the recycler view
                a_model    --> an instance of WardModel class

    DESCRIPTION

            This function binds the view passed by onCreateViewHolder to its actual contents
            that is received from the object of WardModel and methods of the class.

    RETURNS

            Nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/25/2021

    */
    /**/
    @Override
    protected void onBindViewHolder(@NonNull WardViewHolder a_holder, int a_position, @NonNull WardModel a_model) {
        a_holder.m_WardName.setText(a_model.getWardName());
        if(a_model.getIconURI()!=null && a_model.getIconURI()!=""){
            Picasso.get().load(a_model.getIconURI()).into(a_holder.m_WardIcon);
        }
    }


    /*
     * This class is a View Holder class for ManageGroupAdmin's recycler view adapter.
     * This class pulls the reference of single row layout associated with the WardGroupAdapter.
     *
     * */
    class WardViewHolder extends RecyclerView.ViewHolder
    {
        ImageView m_WardIcon;
        TextView m_WardName;

        /**/
        /*

        NAME

                WardViewHolder - constructor for WardViewHolder class

        SYNOPSIS

                public WardViewHolder(@NonNull View itemView)
                    itemView --> Single item in the recycler view

        DESCRIPTION

                The constructor of WardViewHolder class initializes all the member
                variables and views to their respective ids in the layout resource file

        RETURNS

                Nothing

        AUTHOR

                Sibika Silwal

        DATE

                7:30pm 03/25/2021

        */
        /**/
        public WardViewHolder(@NonNull View itemView) {
            super(itemView);
            m_WardIcon = itemView.findViewById(R.id.wardIcon);
            m_WardName = itemView.findViewById(R.id.t_wardName);
        }
    }
}
