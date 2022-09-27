package com.bhaskar.corvasnotes;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.bhaskar.corvasnotes.activities.SettingsActivity;
//import freecode.notes.app.Activity.Setting.SettingActivity;

public class NavigationUtil {

    public static void SettingActivity(@NonNull Activity activity) {
        ActivityCompat.startActivity(activity, new Intent(activity, SettingsActivity.class), null);
        activity.finish();
    }

//    public static void DeleteActivity(@NonNull Activity activity) {
//        ActivityCompat.startActivity(activity, new Intent(activity, DeleteActivity.class), null);
//        activity.finish();
//    }
}
