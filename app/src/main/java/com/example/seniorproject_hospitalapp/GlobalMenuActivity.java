package com.example.seniorproject_hospitalapp;

import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

/*
* This is a menu class that is used only for user specific activities.
* This class utilizes and overrides the onCreateOptionsMenu and
* onOptionsItemSelected functions to create the menu interface
* and add the event listeners.
*
* */
public class GlobalMenuActivity extends AppCompatActivity
{
    /**/
    /*

    NAME

            onCreateOptionsMenu - Initialize the contents of the Activity's standard options menu.

    SYNOPSIS

            public boolean onCreateOptionsMenu(Menu a_menu)
                a_menu  --> Menu class object that provides an interface for managing the items in the menu.

    DESCRIPTION

            This function specifies the options menu for Users / Patient only activities. It overrides
            onCreateOptionsMenu(), and inflates the menu resource file main_menu.xml into the Menu
            passed in the argument.


    RETURNS

            True or False

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021

    */
    /**/
    @Override
    public boolean onCreateOptionsMenu(Menu a_menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, a_menu);
        for(int i =0; i<a_menu.size(); i++){
            MenuItem mItem = a_menu.getItem(i);
            SpannableString spannable = new SpannableString(
                    a_menu.getItem(i).getTitle().toString()
            );
            spannable.setSpan(new ForegroundColorSpan(Color.BLACK),
                    0, spannable.length(), 0);
            mItem.setTitle(spannable);
        }

        return true;
    }

    /**/
    /*

    NAME

            onOptionsItemSelected - takes users to the respective page on clicking on each items

    SYNOPSIS

            public boolean onOptionsItemSelected(@NonNull MenuItem a_item)
                a_item  --> The menu item that was selected.

    DESCRIPTION

            This function overrides the onOptionsItemSelected function which defines
            the onclick or onItemSelected event for each menu item. It takes the user
            to the specfic pages on clicking to the respective menu items.


    RETURNS

            True or False

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/10/2021

    */
    /**/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem a_item) {
        switch (a_item.getItemId()){
            case R.id.mitem_homeIcon:
                startActivity(new Intent(getApplicationContext(), HomePage.class));
                return true;
            case R.id.mitem_Doctors:
                startActivity(new Intent(getApplicationContext(), SearchDoctorsAdmin.class));
                return true;
            case R.id.mitem_documents:
                startActivity(new Intent(getApplicationContext(), ViewUserDocuments.class));
                return true;
            case R.id.mitem_UploadDocuments:
                Intent intent = new Intent(getApplicationContext(), UserInsuranceUpload.class);
                intent.putExtra("patientID", FirebaseAuth.getInstance().getUid());
                startActivity(intent);
                return true;
            case R.id.mitem_profile:
                startActivity(new Intent(getApplicationContext(), UpdateProfile.class));
                return true;
            case R.id.mitem_journal:
                startActivity(new Intent(getApplicationContext(), UserJournalActivity.class));
                return true;
            case R.id.mitem_logOut:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), LoginPage.class));
                return true;
            default:
                return super.onOptionsItemSelected(a_item);
        }
    }
}
