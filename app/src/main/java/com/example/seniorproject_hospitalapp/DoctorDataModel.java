package com.example.seniorproject_hospitalapp;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
* This is DoctorDataModel class which represents each Doctor and their properties. It has all
* the necessary setter and getter functions for Doctor's properties and other functions associated
* with Doctors. This class also works as Firebase recycler view's model class for recycler view
* adapter like SearchDoctorsAdmin.
*
* */
public class DoctorDataModel {

    private String DocName, DocID, profileURL, DocEmail, DocPhone, DocBio;
    private ArrayList<String> WardName = new ArrayList<>();

    //Empty constructor for DoctorDataModel which is required for Firestore's automatic data mapping
    public  DoctorDataModel(){}


    //Getter and Setter for the class's member variables

    public String getDocEmail() { return DocEmail; }

    public void setDocEmail(String docEmail) {
        DocEmail = docEmail;
    }

    public String getDocPhone() {
        return DocPhone;
    }

    public void setDocPhone(String docPhone) {
        DocPhone = docPhone;
    }

    public String getDocBio() {
        return  DocBio;
    }

    public void setDocBio(String docBio) {
        DocBio = docBio;
    }

    public String getDocID() {
        return DocID;
    }

    public void setDocID(String docID) {
        DocID = docID;
    }

    public String getDocName() { return DocName;}

    public void setDocName(String docName) {
        DocName = docName;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public ArrayList<String> getWardName() {
        if(WardName==null){
            WardName.add("Doctor do not belong to any ward at the moment.");
        }
        return WardName;
    }

    public void setWardName(ArrayList<String> wardName) {
        WardName = wardName;
    }

    /**/
    /*

    NAME

            GetDocWardAsString - covers Doctor's ward list to a string

    SYNOPSIS

            public String GetDocWardAsString()

    DESCRIPTION

            This function covers Doctor's ward list to a properly formatted string
            and returns the string

    RETURNS

            String: Doctor's wards

    AUTHOR

            Sibika Silwal

    DATE

            2:30pm 08/05/2021

    */
    /**/
    public String GetDocWardAsString(){
        String DoctorWards = "Wards Doctor is in.";
        for(Object ward : getWardName()){
            DoctorWards = DoctorWards.concat("\n"+ward.toString()) ;
        }
        return DoctorWards;
    }

}
