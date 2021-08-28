package com.example.seniorproject_hospitalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/*
* This class is used to open pdf files within the context of the application in a web view.
* This class receives the url of pdfs to be viewed and adds it to the google's base url provided
* to open pdf in web views.
* */
public class PdfViewer extends AppCompatActivity {
    private Toolbar m_mainToolBar;
    private WebView m_webView;
    private String m_fileURL;

    /**/
    /*

    NAME

            onCreate - initializes PdfViewer activity..

    SYNOPSIS

            protected void onCreate(Bundle a_savedInstanceState);
                a_savedInstanceState     --> the activity's previously frozen state, if there was one

    DESCRIPTION

            This function initialized the PdfViewer activity and links
            it to its respective layout resource file i.e. activity_pdf_viewer
            which allows retrieving the Widgets and Views used in the layout to
            perform actions and handle events as required, by setting the events
            listeners.

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:24pm 01/19/2021

    */
    /**/
    @Override
    protected void onCreate(Bundle a_savedInstanceState) {
        super.onCreate(a_savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        //setting up the toolbar
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("");
        setSupportActionBar(m_mainToolBar);


        SetupUI();

        //brings the user back to the same activity that had fired up the pdfViewer activity after viewing the pdf
        finish();

    }

    /**/
    /*

    NAME

            SetupUI - initializes all UI components

    SYNOPSIS

            private void SetupUI()

    DESCRIPTION

            This function initializes all UI components to their respective Views
            from the layout :activity_pdf_viewer.xml. Uses android method
            findViewById that, "finds a view that was identified by the android:id
           XML attribute that was processed in onCreate(Bundle)." Src: Android Documentation
           (https://developer.android.com/reference/android/app/Activity#findViewById(int))

    RETURNS

            nothing

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 02/19/2021

    */
    /**/
    private void SetupUI(){

        //getting file_url from the previous intent
        Intent data = getIntent();
        m_fileURL = data.getStringExtra("fileURL");

        m_webView= findViewById(R.id.webview);

        String url="";
        try {
            //convert URL to UTF-8
            url= URLEncoder.encode(m_fileURL,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //opens the pdf in google drive or chrome
        m_webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url="+url);

    }
}