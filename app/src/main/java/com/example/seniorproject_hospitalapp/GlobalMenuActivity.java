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

public class GlobalMenuActivity extends AppCompatActivity
{
    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
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
            case R.id.mitem_homeIcon:
                startActivity(new Intent(getApplicationContext(), HomePage.class));
                return true;
            case R.id.mitem_settings:
                startActivity(new Intent(getApplicationContext(), SettingsPage.class));
                return true;
            case R.id.mitem_profile:
                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
