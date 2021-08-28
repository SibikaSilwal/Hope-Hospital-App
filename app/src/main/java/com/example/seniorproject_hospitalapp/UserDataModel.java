package com.example.seniorproject_hospitalapp;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * This is UserDataModel class which represents each user (Patients) and their properties. It has all
 * the necessary setter and getter functions for User's properties and other functions associated
 * with Users. This class also works as Firebase recycler view's model class for recycler view
 * adapter like SearchViewAdapter.
 *
 * */
public class UserDataModel
{
    private String fName, phone, uID, email, gender, address, bloodGroup;
    private String profileURL, insuranceFront, insuranceBack, token;
    private String newReminderTime;
    private Boolean newTestResult;
    private ArrayList<Object> WardName = new ArrayList<>();
    private ArrayList<Map<String, Object>> AppointmentsInfo;

    private FirebaseFirestore m_fStore = FirebaseFirestore.getInstance();

    //Empty constructor for UserDataModel which is required for Firestore's automatic data mapping
    UserDataModel(){}


    //Getter and Setter for the class's member variables

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getfName() {
        return CheckNull(fName);
    }

    public void setfName(String a_fName) { this.fName = a_fName; }

    public String getPhone() {
        return CheckNull(phone);
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String a_UserID, String a_profileURL) {
        DocumentReference m_docRef = m_fStore.collection("users").document(a_UserID);
        m_docRef.update("profileURL", a_profileURL);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Object> getWardName() {
        if(WardName==null){
            WardName.add("You are not added to any wards yet to make an appointment.");
        }
        return WardName;
    }

    public void setwardName(ArrayList<Object> wardName) {
        WardName = wardName;
    }

    public String getWardNameAsString(String a_header){
        String userWards = a_header;
        for(Object ward : getWardName()){
            userWards = userWards.concat("\n"+ward.toString()) ;
        }
        return userWards;
    }
    public ArrayList<Map<String, Object>> getAppointmentsInfo() {
        return AppointmentsInfo;
    }

    public void setAppointmentsInfo(ArrayList<Map<String, Object>> appointmentsInfo) {
        AppointmentsInfo = appointmentsInfo;
    }

    public String getGender() {
        return CheckNull(gender);
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return CheckNull(address);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBloodGroup() {
        return CheckNull(bloodGroup);
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getInsuranceFront() {
        return CheckNull(insuranceFront);
    }

    public void setInsuranceFrontURL(String insuranceFrontURL) {
        this.insuranceFront = insuranceFrontURL;
    }

    public String getInsuranceBack() {
        return CheckNull(insuranceBack);
    }

    public void setInsuranceBackURL(String insuranceBackURL) {
        this.insuranceBack = insuranceBackURL;
    }

    /*public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewReminderTime() {
        return CheckNull(newReminderTime);
    }

    public void setNewReminderTime(String newReminderTime) {
        this.newReminderTime = newReminderTime;
    }

    public Boolean getNewTestResult() {
        return newTestResult;
    }

    public void setNewTestResult(Boolean newTestResult) {
        this.newTestResult = newTestResult;
    }
    */

    /**/
    /*

    NAME

            UpdateGenericInfo - updates user's generic information in the database

    SYNOPSIS

            public void UpdateGenericInfo(String a_UserID, String a_email, String a_Name, String a_Phone, String a_Gender
                                ,String a_BloodGroup, String a_Address, Context a_context)
                a_UserID      --> User's id whose information is being updates
                a_email       --> User's email to be updates
                a_Name        --> User's name to be updates
                a_Phone       --> User's phone to be updates
                a_Gender      --> User's gender to be updates
                a_BloodGroup  --> User's blood group to be updates
                a_Address     --> User's address to be updates
                a_context     --> Context to show the toast message after successful update

    DESCRIPTION

            This function updates user's generic information in the database. The function
            takes the informations to be updates as the arguments of the function, and updates
            the user's information in the user's document in firestore database.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            2:30pm 08/05/2021

    */
    /**/
    public void UpdateGenericInfo(String a_UserID, String a_email, String a_Name, String a_Phone, String a_Gender
                                ,String a_BloodGroup, String a_Address, Context a_context){
        DocumentReference m_docRef = m_fStore.collection("users").document(a_UserID);
        Map<String, Object> edited = new HashMap<>();
        edited.put("email",a_email);
        edited.put("fName", a_Name);
        edited.put("phone", a_Phone);
        edited.put("gender", a_Gender);
        edited.put("bloodGroup", a_BloodGroup);
        edited.put("address", a_Address);
        m_docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(a_context, "Your information was updated successfully.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(a_context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**/
    /*

    NAME

            CheckNull - covers Doctor's ward list to a string

    SYNOPSIS

            private String CheckNull(Object a_data)
                a_data  --> data object to be checked if is null or not

    DESCRIPTION

            This function checks if the provided data is null. If not null
            returns the same object after converting it to string. If null
            returns an empty string

    RETURNS

            String data

    AUTHOR

            Sibika Silwal

    DATE

            2:30pm 08/05/2021

    */
    /**/
    private String CheckNull(Object a_data){
        if(a_data!=null){
            return a_data.toString();
        }
        return "";
    }
}
