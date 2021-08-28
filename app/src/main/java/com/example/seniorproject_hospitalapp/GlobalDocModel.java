package com.example.seniorproject_hospitalapp;

/*
* This class works as Firebase recycler view's model class for Global Document recycler view
* adapter i.e. GlobalDocRecViewAdapter.
*
* */
public class GlobalDocModel
{
    String file, fileName;

    //Empty constructor for GlobalDocModel which is required for Firestore's automatic data mapping
    GlobalDocModel(){}

    //Getter and Setter for the class's member variables

    public String getFile() { return file; }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
