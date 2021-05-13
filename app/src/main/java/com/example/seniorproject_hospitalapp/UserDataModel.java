package com.example.seniorproject_hospitalapp;

public class UserDataModel
{
    String fName, phone, profileURL, uID, email;

    UserDataModel(){} //blank constructor for firebase api to pull the user data

    public UserDataModel(String fName, String phone, String profileURL, String uID, String email) {
        this.fName = fName;
        this.phone = phone;
        this.profileURL = profileURL;
        this.uID = uID;
        this.email = email;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileURL() {
        if(profileURL == null){ profileURL = "";}
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
