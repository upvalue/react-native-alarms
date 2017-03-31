package com.ioddly.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.SupportsWebWorkers;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.JavaScriptModule;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

@ReactModule(name = "AlarmAndroid")
public class AlarmModule extends ReactContextBaseJavaModule {
    /*
  public static interface AlarmEmitter extends JavaScriptModule {
    void emit(String eventName, @Nullable Object data);
  }
  */

  public AlarmModule(ReactApplicationContext reactContext) {
    super(reactContext);
    Log.i("RNAlarms", "AlarmModule initialized");
  }

  @Override
  public String getName() {
    return "AlarmAndroid";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();

    constants.put("RTC", AlarmManager.RTC);
    constants.put("RTC_WAKEUP", AlarmManager.RTC_WAKEUP);
    constants.put("ELAPSED_REALTIME", AlarmManager.ELAPSED_REALTIME);
    constants.put("ELAPSED_REALTIME_WAKEUP", AlarmManager.ELAPSED_REALTIME_WAKEUP);

    constants.put("INTERVAL_FIFTEEN_MINUTES", AlarmManager.INTERVAL_FIFTEEN_MINUTES);
    constants.put("INTERVAL_HALF_HOUR", AlarmManager.INTERVAL_HALF_HOUR);
    constants.put("INTERVAL_HOUR", AlarmManager.INTERVAL_HOUR);
    constants.put("INTERVAL_DAY", AlarmManager.INTERVAL_DAY);
    constants.put("INTERVAL_HALF_DAY", AlarmManager.INTERVAL_HALF_DAY);

    return constants;
  }

  private PendingIntent createPending(String name) {
    Context context = getReactApplicationContext();
    Intent intent = new Intent(context, AlarmRun.class);
    intent.putExtra("name", name);
    // This is so alarms may be cancelled
    intent.setAction(name);
    intent.setData(Uri.parse("http://"+name));
    return PendingIntent.getBroadcast(context, 0, intent, 0);
  }

  @ReactMethod
  public void setAlarm(final String name, final int type, final ReadableMap opts) {
    boolean repeating = opts.hasKey("interval");
    Context context = getReactApplicationContext();

    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    long ms = 0;

    String wakeup = (type == AlarmManager.ELAPSED_REALTIME_WAKEUP || type == AlarmManager.RTC_WAKEUP) ? "_WAKEUP" : "";
    String repeating_s = repeating ? " (repeating every "+opts.getInt("interval")+"ms) " : "";

    if(type == AlarmManager.ELAPSED_REALTIME || type == AlarmManager.ELAPSED_REALTIME_WAKEUP) {
      ms = SystemClock.elapsedRealtime() + opts.getInt("trigger");
      Log.i("RNAlarms", "ELAPSED_REALTIME"+ wakeup + " " + repeating_s + "ALARM '"+name+"' in " + opts.getInt("trigger") + "ms");
    } else {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(System.currentTimeMillis());
      if(opts.hasKey("date")) {
        calendar.set(Calendar.DATE, opts.getInt("date"));
      }
      if(opts.hasKey("hour")) {
        calendar.set(Calendar.HOUR_OF_DAY, opts.getInt("hour"));
      }
      if(opts.hasKey("minute")) {
        calendar.set(Calendar.MINUTE, opts.getInt("minute"));
      }
      if(opts.hasKey("second")) {
        calendar.set(Calendar.MINUTE, opts.getInt("second"));
      }
      ms = calendar.getTimeInMillis();
      Log.i("RNAlarms", "RTC" + wakeup + repeating_s + " " +name + " at " + calendar.get(Calendar.DATE) + " - "+ calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
    }

    PendingIntent pending = createPending(name);

    if(repeating) {
      int interval = opts.getInt("interval");
      manager.setInexactRepeating(type, ms, interval, pending);
    } else {
      manager.set(type, ms, pending);
    }
  }

  @ReactMethod
  public void clearAlarm(String name) {
    Log.i("RNAlarms", "Clearing alarm '"+name+"'");
    ((AlarmManager)getReactApplicationContext().getSystemService(Context.ALARM_SERVICE)).cancel(createPending(name));
  }
}

