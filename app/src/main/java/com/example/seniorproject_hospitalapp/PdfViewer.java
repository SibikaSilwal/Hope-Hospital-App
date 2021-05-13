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

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class PdfViewer extends AdminMenuActivity {
    Toolbar m_mainToolBar;
    PDFView m_pdfview;
    WebView m_webView;
    ProgressBar m_progBar;
    String m_fileURL, m_finalURL, m_fileName;
    StorageReference storageReference, m_fileRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        //setting up the toolbar
        m_mainToolBar= findViewById(R.id.mtoolbar);
        m_mainToolBar.setTitle("Hope Hospital App");
        setSupportActionBar(m_mainToolBar);


        SetupUI();


    }
    private void SetupUI(){
        //getting file_url from the previous intent
        Intent data = getIntent();
        m_fileURL = data.getStringExtra("fileURL");

        m_webView= findViewById(R.id.webview);
        m_progBar= findViewById(R.id.pdf_progressbar);

        String url="";
        try {
            url= URLEncoder.encode(m_fileURL,"UTF-8"); //Add this to convert URL to UTF-8 , learn more about it
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        m_webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url="+url);

        //m_pdfview = findViewById(R.id.pdfviewer);
        /*storageReference = FirebaseStorage.getInstance().getReference();
        m_fileRef = storageReference.child("GlobalDoc/"+"PrivacyPolicyDocs"+m_fileName);
        m_fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //Picasso.get().load(uri).into(m_profileImg);
                Toast.makeText(PdfViewer.this, "Successful", Toast.LENGTH_SHORT).show();
                //System.out.println("url: "+uri);
                //m_pdfview.fromUri(uri)
                  //      .load();
               m_fileURL = uri.toString();
               //System.out.println("fileurl: "+m_fileURL);
                //m_progBar.setVisibility(View.VISIBLE);
                m_finalURL = "http://drive.google.com/viewerng/viewer?embedded=true&url=" + m_fileURL;
                /*m_webView.getSettings().setJavaScriptEnabled(true);
                m_webView.getSettings().setBuiltInZoomControls(true);

                m_webView.setWebChromeClient(new WebChromeClient(){

                    @Override
                    public void onProgressChanged(WebView view, int newProgress) { //track the progress of url
                        super.onProgressChanged(view, newProgress);

                        if(newProgress==100) {
                            //indicates that url progress is complete
                            m_progBar.setVisibility(View.GONE);
                        }
                    }
                });
                 */
                /*System.out.println("here fileurl: "+m_fileURL);
                System.out.println("here fileurl: "+m_finalURL);
                String url="";
                try {
                    url= URLEncoder.encode(m_fileURL,"UTF-8"); //Add this to convert URL to UTF-8 , learn more about it
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                m_webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url="+url);
                //m_webView.loadUrl(m_finalURL);
            }
        }); */
    }
}