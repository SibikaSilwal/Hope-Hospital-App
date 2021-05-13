package com.example.seniorproject_hospitalapp;

public class DoctorDataModel {
    String DocName, DocID, profileURL;
    public  DoctorDataModel(){}

    public DoctorDataModel(String docName, String docID, String profileurl) {
        DocName = docName;
        DocID = docID;
        profileURL = profileurl;
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
}
