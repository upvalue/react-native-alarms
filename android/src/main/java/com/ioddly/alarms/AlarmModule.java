package com.ioddly.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.module.annotations.ReactModule;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@ReactModule(name = "AlarmAndroid")
public class AlarmModule extends ReactContextBaseJavaModule {
  public AlarmModule(ReactApplicationContext reactContext) {
    super(reactContext);
    Log.i("RNAlarms", "AlarmModule initialized");
  }

  @Override
  public String getName() {
    return "AlarmAndroid";
  }

  @Override
  /**
   * Return constants for use in JS code
   */
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

  /**
   * Creates a PendingIntent for an alarm
   * @param name Name of the alarm
   * @param flags PendingIntent flags
   * @return
   */
  private PendingIntent createPending(final String name, final int flags) {
    Context context = getReactApplicationContext();
    Intent intent = new Intent(context, AlarmRun.class);
    intent.putExtra("name", name);
    // This is so alarms may be cancelled
    intent.setAction(name);
    intent.setData(Uri.parse("http://"+name));
    return PendingIntent.getBroadcast(context, 0, intent, flags);
  }

  /**
   * Check if an alarm exists
   * @param name Alarm name
   * @return true if alarm exists
   */
  private boolean jAlarmExists(final String name) {
    return createPending(name, PendingIntent.FLAG_NO_CREATE) != null;
  }

  /** Clear an alarm by name */
  private void jClearAlarm(final String name) {
    PendingIntent pending = createPending(name, PendingIntent.FLAG_NO_CREATE);
    if(pending != null) {
      pending.cancel();
      ((AlarmManager) getReactApplicationContext().getSystemService(Context.ALARM_SERVICE)).cancel(pending);
    } else {
      Log.i("RNAlarms", "No PendingIntent found for alarm '"+name+"'");
    }
  }

  @ReactMethod
  /**
   * React method: Check if an alarm exists.
   * @param name Alarm name
   * @param promise JS promise
   */
  public void alarmExists(final String name, final Promise promise) {
      WritableArray args =  Arguments.createArray();
      args.pushBoolean(jAlarmExists(name));
      promise.resolve(args);
  }

  /**
   * Set a new alarm
   * @param name Alarm name
   * @param type Android alarm type
   * @param opts Options
   */
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
        calendar.set(Calendar.SECOND, opts.getInt("second"));
      }
      ms = calendar.getTimeInMillis();
      Log.i("RNAlarms", "RTC" + wakeup + repeating_s + " " +name + " at date: " + calendar.get(Calendar.DATE) + " - h:m:s "+ calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
    }

    PendingIntent pending;
    if(jAlarmExists(name)) {
      Log.i("RNAlarms", "PendingIntent already exists for alarm '"+name+"'; updating!");
      jClearAlarm(name);
      pending = createPending(name, PendingIntent.FLAG_UPDATE_CURRENT);
    } else {
      pending = createPending(name, 0);
    }

    if(repeating) {
      int interval = opts.getInt("interval");
      manager.setInexactRepeating(type, ms, interval, pending);
    } else {
      manager.set(type, ms, pending);
    }
  }

  /** Find and launch the main activity */
  @ReactMethod
  public void launchMainActivity() {
    AlarmHelper.launchMainActivity(getReactApplicationContext());
  }


  /**
   * Clear an existing alarm
   * @param name Alarm name
   */
  @ReactMethod
  public void clearAlarm(String name) {
    Log.i("RNAlarms", "Clearing alarm '"+name+"'");
    jClearAlarm(name);
  }
}

