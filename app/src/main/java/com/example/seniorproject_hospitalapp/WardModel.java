package com.example.seniorproject_hospitalapp;

public class WardModel {
    String IconURI, WardName;
    public WardModel(){}
    public WardModel(String iconURI, String wardName) {
        IconURI = iconURI;
        WardName = wardName;
    }

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
