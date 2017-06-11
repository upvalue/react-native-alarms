package com.ioddly.alarms;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/** Shared methods */
public class AlarmHelper {
    /** Find and launch the main activity of the application */
    static void launchMainActivity(final Context context) {

        Class klass = null;
        try {
            Intent launch = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            klass = Class.forName(launch.getComponent().getClassName());
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("RNAlarms", "failed to find main activity class");
            return;
        }

        if(klass == null) {
            Log.e("RNAlarms", "failed to find main activity class");
        }

        Log.e("RNAlarms", "starting activity " + klass.getCanonicalName());
        Intent mainintent = new Intent(context, klass);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mainintent);
    }
}
