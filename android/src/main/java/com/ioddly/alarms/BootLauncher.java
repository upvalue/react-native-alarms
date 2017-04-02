package com.ioddly.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.ReactContext;

public class BootLauncher extends BroadcastReceiver {
    final static String alarmName = "boot";

    public static void fire(ReactContext reactContext) {
        Log.i("RNAlarms", "firing alarm '" + alarmName + "'");
        reactContext.getJSModule(AlarmEmitter.class).emit(alarmName, null);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!(intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))) {
            return;
        }

        Log.i("RNAlarms", "received boot event");
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
