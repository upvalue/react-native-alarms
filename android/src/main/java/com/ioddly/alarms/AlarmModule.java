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
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AlarmModule extends ReactContextBaseJavaModule {
  public AlarmModule(ReactApplicationContext reactContext) {
    super(reactContext);
    Log.i("RN_ALARMS", "AlarmModule initialized");
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
    constants.put("INTERVAL_HALF_HOUR", AlarmManager.INTERVAL_FIFTEEN_MINUTES);
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
  public void setAlarm(ReadableMap opts) {
    int type = opts.getInt("type");
    boolean repeating = opts.hasKey("interval");
    Context context = getReactApplicationContext();

    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    String name = opts.getString("name");
    PendingIntent pending = createPending(name);

    long ms = 0;

    String wakeup = (type == AlarmManager.ELAPSED_REALTIME_WAKEUP || type == AlarmManager.RTC_WAKEUP) ? "_WAKEUP" : "";
    String repeating_s = repeating ? " (repeating) " : "";

    if(type == AlarmManager.ELAPSED_REALTIME || type == AlarmManager.ELAPSED_REALTIME_WAKEUP) {
      ms = SystemClock.elapsedRealtime() + opts.getInt("trigger");
      Log.i("RNAlarms", "ELAPSED_REALTIME"+ wakeup + " " + repeating_s + "ALARM '"+name+"' in " + opts.getInt("trigger") + "ms");
    } else {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(System.currentTimeMillis());
      calendar.set(Calendar.HOUR_OF_DAY, opts.getInt("hour"));
      calendar.set(Calendar.MINUTE, opts.hasKey("minute") ? opts.getInt("minute") : 0);
      calendar.set(Calendar.SECOND, opts.hasKey("second") ? opts.getInt("second") : 0);
      ms = calendar.getTimeInMillis();
      Log.i("RNAlarms", "RTC" + wakeup + repeating_s + " " +name + " at " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
    }

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

