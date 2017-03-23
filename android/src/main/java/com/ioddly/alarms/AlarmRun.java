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
//import com.facebook.react.modules.core.DeviceEventManagerModule;

import static com.facebook.react.common.ApplicationHolder.getApplication;
// FIXME: getApplication is deprecated. I am not clear on what the proper way to do this is.

public class AlarmRun extends BroadcastReceiver {

    private static void fire(ReactContext reactContext, String alarmName) {
        Log.i("RNAlarms", "firing alarm '" + alarmName + "'");
        reactContext.getJSModule(AlarmEmitter.class).emit(alarmName, null);

        //WritableMap params = Arguments.createMap();
        //params.putString("name", alarmName);
        //reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("alarm-"+alarmName, params);
    }

    public void onReceive(Context context, Intent intent) {
        final String alarmName = intent.getStringExtra("name");
        ReactInstanceManager manager = ((ReactApplication) getApplication()).getReactNativeHost().getReactInstanceManager();
        ReactContext reactContext = manager.getCurrentReactContext();

        if(reactContext != null) {
            fire(reactContext, alarmName);
        } else {
            manager.addReactInstanceEventListener(new ReactInstanceManager.ReactInstanceEventListener() {
                public void onReactContextInitialized(ReactContext context) {
                    AlarmRun.fire(context, alarmName);
                }
            });
            if(!manager.hasStartedCreatingInitialContext()) {
                manager.createReactContextInBackground();
            }
        }

    }
}
