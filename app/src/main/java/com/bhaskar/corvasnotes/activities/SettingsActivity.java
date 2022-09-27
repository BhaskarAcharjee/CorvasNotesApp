package com.bhaskar.corvasnotes.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.bhaskar.corvasnotes.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


//        SharedPrefs for day night
        final SharedPreferences appSettingsPrefs = getSharedPreferences("App Settings",0);
        final SharedPreferences.Editor sharedPrefsEdit = appSettingsPrefs.edit();
        final boolean isNightModeOn = appSettingsPrefs.getBoolean("Night Mode",true);

        if (isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        //        Day Night Mode switch
        final LottieAnimationView lottieSwitchButton = findViewById(R.id.lottieSwitchButton);
        lottieSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (isSwitchOn) {
//                    lottieSwitchButton.setMinAndMaxProgress(0.5f, 1.0f); // Light Mode To Dark Mode animation
//                    lottieSwitchButton.playAnimation();
//                    isSwitchOn = false;
//                } else {
//                    lottieSwitchButton.setMinAndMaxProgress(0.0f, 0.4f); // Dark Mode To Light Mode animation
//                    lottieSwitchButton.playAnimation();
//                    isSwitchOn = true;
//                }
                if (isNightModeOn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    sharedPrefsEdit.putBoolean("Night Mode",false);
                    sharedPrefsEdit.apply();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    sharedPrefsEdit.putBoolean("Night Mode",true);
                    sharedPrefsEdit.apply();
                }

            }
        });

//      Go to About Page
        LinearLayout about = findViewById(R.id.nav_about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent player = new Intent(SettingsActivity.this, AboutActivity.class);
                startActivity(player);
            }
        });


    }

//    Home Selecting > Back to MAin Activity Layout
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                overridePendingTransition(0, 0);
                overridePendingTransition(0, 0);
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                finish();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

// On Backpress > Settings to Main Activity
    @Override
    public void onBackPressed() {
        overridePendingTransition(0, 0);
        overridePendingTransition(0, 0);
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
        finish();
    }

}