package com.bhaskar.corvasnotes.SharedPref;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    final Context context;
    final SharedPreferences appSettingsPrefs;
    final SharedPreferences.Editor sharedPrefsEdit;

    public SharedPref(Context context) {
        this.context = context;
        appSettingsPrefs = context.getSharedPreferences("App Settings",0);
        sharedPrefsEdit = appSettingsPrefs.edit();
    }

    public final Boolean isNightModeOn() {
        return appSettingsPrefs.getBoolean("Night Mode",true);
    }

    public void setNightMode(Boolean state) {
        sharedPrefsEdit.putBoolean("Night Mode", state);
        sharedPrefsEdit.apply();
    }

    public final Boolean isSwitchOn() {
        return appSettingsPrefs.getBoolean("Switch Mode",true);
    }

    public void setSwitchMode(Boolean state) {
        sharedPrefsEdit.putBoolean("Switch Mode", state);
        sharedPrefsEdit.apply();
    }

}
