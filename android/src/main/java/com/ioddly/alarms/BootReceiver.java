package com.ioddly.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;

import static com.facebook.react.common.ApplicationHolder.getApplication;

public class BootReceiver extends BroadcastReceiver {
    final static String alarmName = "boot";

    private static void fire(ReactContext reactContext) {
        Log.i("RNAlarms", "firing alarm '" + alarmName + "'");
        reactContext.getJSModule(AlarmEmitter.class).emit(alarmName, null);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("RNAlarms", "BOOT EVENT RECEIVED");
        // TODO code duplication with AlarmRun
        ReactInstanceManager manager = ((ReactApplication) getApplication()).getReactNativeHost().getReactInstanceManager();
        ReactContext reactContext = manager.getCurrentReactContext();

        if(reactContext != null) {
            BootReceiver.fire(reactContext);
        } else {
            manager.addReactInstanceEventListener(new ReactInstanceManager.ReactInstanceEventListener() {
                public void onReactContextInitialized(ReactContext context) {
                    BootReceiver.fire(context);
                }
            });
            if(!manager.hasStartedCreatingInitialContext()) {
                manager.createReactContextInBackground();
            }
        }
    }
}
