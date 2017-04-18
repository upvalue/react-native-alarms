A react-native library for interacting with Android alarms.

## Warning

This library is in alpha condition. I am currently only using an RTC_WAKEUP repeating alarm in production. Make sure to
test that alarms fire properly in the desired manner before depending on them.

## Documentation

Read up on the [Android Alarm](https://developer.android.com/training/scheduling/alarms.html) documentation, as
everything applies to this library as well.

Specifically: Many applications will be better served by using GCM, and all repeating alarms are inexact.

## Installation

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

If you want your application to launch at boot, so alarms can be restored, add this with your permissions:

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

## Usage

### setAlarm(name: string, type: int, opts: object)

NAME is the name of the event that will be fired. Should not be `boot`.

TYPE should be one of the AlarmManager type constants: `RTC`, `RTC_WAKEUP`, `ELAPSED_REALTIME` or `ELAPSED_REALTIME_WAKEUP`.

OPTS contains information on when to fire, different for `RTC` and `ELAPSED_REALTIME` alarms. If an interval is provided,
react-native-alarms will make the alarm repeating.

### clearAlarm(name: string)

### Constants

#### `type`

`RTC`, `RTC_WAKEUP`, `ELAPSED_REALTIME`, `ELAPSED_REALTIME_WAKEUP`

#### `interval`

`INTERVAL_FIFTEEN_MINUTES`, `INTERVAL_HALF_HOUR`, `INTERVAL_HOUR`, `INTERVAL_DAY`, `INTERVAL_HALF_DAY`

Intervals may also be a number in milliseconds, minimum `60000`.

## Example code

```javascript
import AlarmAndroid from 'react-native-alarms';

AlarmAndroid.setAlarm('test', AlarmAndroid.ELAPSED_REALTIME, {
  trigger: 20000, /* required, milliseconds, for elapsed realtime clocks */
});

/* Alarms are cancelled by name */
AlarmAndroid.clearAlarm("test");

AlarmAndroid.AlarmEmitter.addListener('test', (e) => {
  console.log('Received alarm-test');
  AlarmAndroid.clearAlarm("test");
});

/* Remove listeners (this will not clear alarms on the Android side, but is recommended when an alarm is removed) */
AlarmAndroid.AlarmEmitter.removeAllListeners("test");

/* 8AM wakeup alarm */
AlarmAndroid.setAlarm('test2', AlarmAndroid.RTC_WAKEUP, {
  // Time fields -- passed to Java's Calendar class. These will default to the current time if not provided
  // you should most likely set them all.
  date: 5 // Date of the month
  hour: 8, minute: 0, second: 0,
  // If interval is set, alarm will be repeating
  interval: AlarmAndroid.INTERVAL_DAY 
});

```

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
