package com.example.seniorproject_hospitalapp;

import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class UserClass  extends Thread{
    private String m_UserId, m_FullName, m_Email, m_Phone, m_Address, m_Gender, m_BloodGroup;
    private String m_ProfileURL, m_InsuranceFrontURL, m_InsuranceBackURL, m_DeviceToken;
    private String m_ApptReminderTime;
    private Boolean m_NewTestResult;
    private ArrayList<String> m_WardNames = new ArrayList();
    private ArrayList<Map<String, String>> m_ApptInfo = new ArrayList<Map<String, String>>();

    private FirebaseAuth m_fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore m_fStore = FirebaseFirestore.getInstance();
    DocumentReference m_docRef;

    public UserClass(String m_UserId){
        this.m_UserId = m_UserId;
        m_docRef = m_fStore.collection("users").document(m_UserId);
        m_docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                m_FullName = GetUserInfo(task.getResult().get("fName"));
                m_Email = GetUserInfo(task.getResult().get("email"));
                m_Phone = GetUserInfo(task.getResult().get("phone"));
                m_Address = GetUserInfo(task.getResult().get("address"));
                m_Gender = GetUserInfo(task.getResult().get("gender"));
                m_BloodGroup = GetUserInfo(task.getResult().get("bloodGroup"));
                m_ProfileURL = GetUserInfo(task.getResult().get("profileURL"));
                m_InsuranceFrontURL = GetUserInfo(task.getResult().get("insuranceFront"));
                m_InsuranceBackURL = GetUserInfo(task.getResult().get("insuranceBack"));
                m_DeviceToken = GetUserInfo(task.getResult().get("token"));
                if(task.getResult().getData().get("WardName")!=null){
                    m_WardNames = (ArrayList<String>)task.getResult().getData().get("WardName");
                }else{
                    m_WardNames.add("You are not added to any wards yet to make an appointment.");
                }
                System.out.println("name"+m_FullName);
            }
        });
    }

    public String getM_FullName() {
        System.out.println("From get: name"+m_FullName);
        return m_FullName;
    }

    public void setM_FullName(String m_FullName) {
        this.m_FullName = m_FullName;
    }

    public String getM_Email() {
        return m_Email;
    }

    public void setM_Email(String m_Email) {
        this.m_Email = m_Email;
    }

    public String getM_Phone() {
        return m_Phone;
    }

    public void setM_Phone(String m_Phone) {
        this.m_Phone = m_Phone;
    }

    public String getM_Address() {
        return m_Address;
    }

    public void setM_Address(String m_Address) {
        this.m_Address = m_Address;
    }

    public String getM_Gender() {
        return m_Gender;
    }

    public void setM_Gender(String m_Gender) {
        this.m_Gender = m_Gender;
    }

    public String getM_BloodGroup() {
        return m_BloodGroup;
    }

    public void setM_BloodGroup(String m_BloodGroup) {
        this.m_BloodGroup = m_BloodGroup;
    }

    public String getM_ProfileURL() {
        return m_ProfileURL;
    }

    public void setM_ProfileURL(String m_ProfileURL) {
        this.m_ProfileURL = m_ProfileURL;
    }

    public String getM_InsuranceFrontURL() {
        return m_InsuranceFrontURL;
    }

    public void setM_InsuranceFrontURL(String m_InsuranceFrontURL) {
        this.m_InsuranceFrontURL = m_InsuranceFrontURL;
    }

    public String getM_InsuranceBackURL() {
        return m_InsuranceBackURL;
    }

    public void setM_InsuranceBackURL(String m_InsuranceBackURL) {
        this.m_InsuranceBackURL = m_InsuranceBackURL;
    }

    public String getM_DeviceToken() {
        return m_DeviceToken;
    }

    public void setM_DeviceToken(String m_DeviceToken) {
        this.m_DeviceToken = m_DeviceToken;
    }

    public String getM_ApptReminderTime() {
        return m_ApptReminderTime;
    }

    public void setM_ApptReminderTime(String m_ApptReminderTime) {
        this.m_ApptReminderTime = m_ApptReminderTime;
    }

    public Boolean getM_NewTestResult() {
        return m_NewTestResult;
    }

    public void setM_NewTestResult(Boolean m_NewTestResult) {
        this.m_NewTestResult = m_NewTestResult;
    }

    public ArrayList<String> getM_WardNames() {
        return m_WardNames;
    }

    public void setM_WardNames(ArrayList<String> m_WardNames) {
        this.m_WardNames = m_WardNames;
    }

    public ArrayList<Map<String, String>> getM_ApptInfo() {
        return m_ApptInfo;
    }

    public void setM_ApptInfo(ArrayList<Map<String, String>> m_ApptInfo) {
        this.m_ApptInfo = m_ApptInfo;
    }

    private String GetUserInfo(Object a_data){
        if(a_data!=null){
            return a_data.toString();
        }
        return "";
    }
}