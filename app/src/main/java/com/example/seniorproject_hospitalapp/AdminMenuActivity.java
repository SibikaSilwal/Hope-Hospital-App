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

public class AdminMenuActivity extends AppCompatActivity {
    //menu
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
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
                return super.onOptionsItemSelected(item);
        }
    }
}
