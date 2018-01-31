A react-native library for interacting with Android alarms. Requires RN 0.44 or greater. Includes TypeScript bindings.

## Warning

This library is in hyper-alpha condition. Make sure to test that alarms fire properly in the desired manner before
depending on them. APIs are subject to break, so pin by git commit. And you should probably use remote notifications
anyway!

## Documentation

Read up on the [Android Alarm](https://developer.android.com/training/scheduling/alarms.html) documentation, as
everything applies to this library as well.

Specifically: Many applications will be better served by using GCM, and all repeating alarms are inexact.

## Installation (ALARMS WILL FAIL SILENTLY IF YOU DON'T EDIT AndroidManifest.xml)

In terminal

```shell
yarn add git+https://github.com/ioddly/react-native-alarms.git
react-native link
```
In your AndroidManifest.xml  add this within within your `<application ...>` tag (alarms will fail silently if you
don't add this!)

```xml
<receiver android:name="com.ioddly.alarms.AlarmRun" android:enabled="true"></receiver> 
```

Optional: If you want your application to start on boot and fire a special @boot event, so alarms can be restored, add
this with your permissions:

```xml
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

And this within your `<application ...>` 

```xml
<receiver android:name="com.ioddly.alarms.BootLauncher" android:enabled="true">
  <intent-filter>
    <action android:name="android.intent.action.BOOT_COMPLETED" />
    <category android:name="android.intent.category.DEFAULT" />
  </intent-filter>
</receiver>
```

## PendingIntents and Alarms

react-native-alarm creates a PendingIntent for each named alarm. The name is used to check whether alarms already
exist. It is your responsibility to manage alarms by name. If you re-use an alarm's name, it will be updated
to the new parameters.

## Usage

An Error will be thrown if arguments are provided with the incorrect type.

Note that alarms can result in a new instance of react-native being instantiated if the device has gone idle; be aware
of this if you're doing expensive things on startup.

### alarmSetElapsedRealtime(name: string, trigger: number, interval?: number)

Creates an ELAPSED_REALTIME alarm.

NAME is the name of the event that will be fired when the alarm goes off.

TRIGGER is when the alarm will be triggered, in milliseconds.

INTERVAL determines whether the alarm will be a repeating alarm, defaults to non-repeating. Can be either one of the
provided interval constants, or a number in milliseconds above 60000 (alarms have a minimum interval of one minute,
this is Android behavior). All repeating alarms are inexact.

### alarmSetElapsedRealtimeWakeup

Same arguments as above, but creates an ELAPSED_REALTIME_WAKEUP alarm.

### alarmSetRTC(name: string, date: Date, interval?: number)
### alarmSetRTCWakeup(name: string, date: Date, interval?: number)

Same as above, but results in an RTC alarm with the given Date object used to initialize a Java Calendar.

### alarmExists(name: string): Promise

Returns a promise which will be called with an array containing a single boolean indicating whether a PendingIntent
exists with the given name.

Usage: 
```js
alarmExists('alarm1').then(([exists]) => console.log(`alarm1 ${exists ? 'exists' : 'does not exist'}`));
```

### setAlarm(name: string, type: number, opts: Object)

This is the Java alarm-setting method. See the Java code for the fields of the OPTS object. You shouldn't have to use
this.

### clearAlarm(name: string)

Clears an alarm with the given name. Has no effect if called with an alarm that does not exist.

### launchMainActivity()

A convenience method which can be used to launch the main activity when an alarm goes off. For e.g. an alarm clock
application. May be irritating to users if an app launches when they do not expect it, use sparingly. 

### AlarmEmitter

AlarmEmitter is an instance of NativeEventEmitter used to listen specifically to alarm events. It has the same
interface.

#### AlarmEmitter.addListener(event, callback)

Listen for an alarm (alarm fire events have the exact same name as alarms) or the @boot event.

#### AlarmEmitter.removeAllListeners(event)

Note this does not clear alarms on the Android side, but is recommended when an alarm is removed or no longer
necessary.

### Constants

#### `type`

`RTC`, `RTC_WAKEUP`, `ELAPSED_REALTIME`, `ELAPSED_REALTIME_WAKEUP`

#### `interval`

`INTERVAL_FIFTEEN_MINUTES`, `INTERVAL_HALF_HOUR`, `INTERVAL_HOUR`, `INTERVAL_DAY`, `INTERVAL_HALF_DAY`

Intervals may also be a number in milliseconds, minimum `60000`.

## Credits

Some of the code in this library is derived from react-native-push-notifications and should be considered to be under
the license of that library.

## Handy ADB commands

Show alarms: `adb shell dumpsys alarm`

Alarms should appear with your package name next to them.

## Manual linking

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.ioddly.alarms.AlarmPackage;` to the imports at the top of the file
  - Add `new AlarmPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-alarms'
  	project(':react-native-alarms').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-alarms/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-alarms')
  	```

4. Edit AndroidManifest.xml as described above
