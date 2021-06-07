package com.example.seniorproject_hospitalapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DoctorDataModel {
    String DocName, DocID, profileURL;
    ArrayList<Map<String, Object>> Sunday, Monday;
    ArrayList<Map<String, String>> DoctorAppointments = new ArrayList<Map<String, String>>();
    public  DoctorDataModel(){}

    public DoctorDataModel(String docName, String docID, String profileurl, ArrayList<Map<String, Object>> sunday, ArrayList<Map<String, Object>> monday) {
        DocName = docName;
        DocID = docID;
        profileURL = profileurl;
        Sunday = sunday;
        Monday = monday;
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

    public ArrayList<Map<String, Object>> getSunday() {
        return Sunday;
    }

    public void setSunday(ArrayList<Map<String, Object>> sunday) {
        Sunday = sunday;
    }

    public ArrayList<Map<String, Object>> getMonday() {
        return Monday;
    }

    public void setMonday(ArrayList<Map<String, Object>> monday) {
        Monday = monday;
    }

    public  ArrayList<Map<String, String>> getDocAppts(){
        System.out.println("Calling this?");
        if(getSunday()!= null){
            for (Map<String, Object> appointment : getSunday()) {
                if(!(boolean)appointment.get("isAvailable")){
                    Map<String, String> appt = new HashMap<>();
                    appt.put("patientID", appointment.get("AppointmentID").toString());
                    appt.put("time", appointment.get("Time").toString());
                    DoctorAppointments.add(appt);
                }
            }
        }
        System.out.println("Anything?? "+DoctorAppointments);
        return DoctorAppointments;
    }
}
