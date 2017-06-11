package com.ioddly.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.ReactContext;

/**
 * Boot launcher. Completely optional; starts the application on boot so that alarms can be restored.
 * May be irritating to users, use sparingly.
 */
public class BootLauncher extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        if(!(intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))) {
            return;
        }

        Log.i("RNAlarms", "received BOOT_COMPLETED event");

        final String alarmName = "@boot";
        ReactApplication reapp = ((ReactApplication) context.getApplicationContext());
        ReactInstanceManager manager = reapp.getReactNativeHost().getReactInstanceManager();
        ReactContext recontext = manager.getCurrentReactContext();
        // Probably not necessary
        if(recontext != null) {
            Log.i("RNAlarms", "Attempting to fire boot event @boot");
            AlarmRun.fire(recontext, alarmName);
        } else {
            Log.i("RNAlarms", "Application is closed; attempting to launch and fire boot event @boot'");
            manager.addReactInstanceEventListener(new ReactInstanceManager.ReactInstanceEventListener() {
                public void onReactContextInitialized(ReactContext context) {
                    Log.i("RNAlarms", "Attempting to fire boot event @boot");
                    AlarmRun.fire(context, alarmName);
                }
            });
            if (!manager.hasStartedCreatingInitialContext()) {
                manager.createReactContextInBackground();
            }
        }
    }
}
