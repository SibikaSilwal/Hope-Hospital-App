package com.example.seniorproject_hospitalapp;

/*
 * This class works as Firebase recycler view's model class for Ward groups recycler view
 * adapter i.e. WardGroupAdapter.
 *
 * */
public class WardModel {

    String IconURI, WardName;

    //Empty constructor for WardModel which is required for Firestore's automatic data mapping
    public WardModel(){}

    //Getter and Setter for the class's member variables

    public String getIconURI() {
        return IconURI;
    }

    public void setIconURI(String iconURI) {
        IconURI = iconURI;
    }

    public String getWardName() {
        return WardName;
    }

    public void setWardName(String wardName) {
        WardName = wardName;
    }
}
