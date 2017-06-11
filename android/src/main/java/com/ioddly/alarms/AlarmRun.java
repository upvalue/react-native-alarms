package com.ioddly.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.ReactContext;

public class AlarmRun extends BroadcastReceiver {
    /**
     * Fires alarm after ReactContext has been obtained
     * @param reactContext
     * @param alarmName
     */
    public static void fire(ReactContext reactContext, String alarmName) {
        if(reactContext.hasActiveCatalystInstance()) {
            Log.i("RNAlarms", "Firing alarm '" + alarmName + "'");
            reactContext.getJSModule(AlarmEmitter.class).emit(alarmName, null);
        } else {
            Log.i("RNAlarms", "no active catalyst instance; not firing alarm '" + alarmName + "'");
        }
    }

    public void onReceive(final Context context, Intent intent) {
        Handler handler = new Handler(Looper.getMainLooper());

        final String alarmName = intent.getStringExtra("name");
        handler.post(new Runnable() {
            public void run() {
                ReactApplication reapp = ((ReactApplication) context.getApplicationContext());
                ReactInstanceManager manager = reapp.getReactNativeHost().getReactInstanceManager();
                ReactContext recontext = manager.getCurrentReactContext();
                if(recontext != null) {
                    Log.i("RNAlarms", "Attempting to fire alarm '" + alarmName + "'");
                    fire(recontext, alarmName);
                } else {
                    Log.i("RNAlarms", "Application is closed; attempting to launch and fire alarm '" + alarmName + "'");
                    manager.addReactInstanceEventListener(new ReactInstanceManager.ReactInstanceEventListener() {
                        public void onReactContextInitialized(ReactContext context) {
                            Log.i("RNAlarms", "Attempting to fire alarm '" + alarmName + "'");
                           fire(context, alarmName);
                        }
                    });
                    if(!manager.hasStartedCreatingInitialContext()) {
                        manager.createReactContextInBackground();
                    }
                }
            }
        });
    }
}
