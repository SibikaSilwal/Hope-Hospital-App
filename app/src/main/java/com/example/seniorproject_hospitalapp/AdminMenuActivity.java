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
 * This is a menu class that is used only for admin specific activities.
 * This class utilizes and overrides the onCreateOptionsMenu and
 * onOptionsItemSelected functions to create the menu interface
 * and add the event listeners.
 *
 * */
public class AdminMenuActivity extends AppCompatActivity {

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

            7:30pm 03/20/2021

    */
    /**/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_menu, menu);
        for(int i =0; i<menu.size(); i++){
            MenuItem mItem = menu.getItem(i);
            SpannableString spannable = new SpannableString(
                    menu.getItem(i).getTitle().toString()
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
            the onclick or onItemSelected event for each menu item. It takes the admin
            to the specific pages on clicking to the respective menu items.


    RETURNS

            True or False

    AUTHOR

            Sibika Silwal

    DATE

            7:30pm 03/20/2021

    */
    /**/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem a_item) {
        switch (a_item.getItemId()){
            case R.id.mitemAdmin_homeIcon:
                startActivity(new Intent(getApplicationContext(), AdminHome.class));
                return true;
            case R.id.mitemAdmin_globalDocs:
                startActivity(new Intent(getApplicationContext(), GlobalDocAdmin.class));
                return true;
            case R.id.mitemAdmin_SearchPatients:
                startActivity(new Intent(getApplicationContext(), SearchUsersAdmin.class));
                return true;
            case R.id.mitemAdmin_ManageGroup:
                startActivity(new Intent(getApplicationContext(), ManageGroupAdmin.class));
                return true;
            case R.id.mitemAdmin_LogOut:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), LoginPage.class));
                return true;
            default:
                return super.onOptionsItemSelected(a_item);
        }
    }
}
