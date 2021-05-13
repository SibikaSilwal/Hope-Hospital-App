package com.example.seniorproject_hospitalapp;

public class GlobalDocModel
{
    String file, fileName;

    GlobalDocModel(){}

    public GlobalDocModel(String file, String fileName) {
        this.file = file;
        this.fileName = fileName;
    }

    public String getFile() {
        return file;
    }

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
