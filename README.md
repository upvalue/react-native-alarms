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

In your AndroidManifest.xml, within your `<application>` tag (alarms will fail silently if you don't add this!)

```xml
<receiver android:name="com.ioddly.alarms.AlarmRun" android:enabled="true"></receiver> 
```

## Usage

```javascript
import AndroidAlarm from 'react-native-alarms';

AndroidAlarm.setAlarm({
  name: "test", /* required, will be used to name the event fired */
  type: AndroidAlarm.ELAPSED_REALTIME, /* required */,
  trigger: 20000, /* milliseconds, for elapsed realtime clocks */
});

AndroidAlarm.AlarmEmitter.addListener('test', (e) => {
  console.log('Received alarm-test');
  AndroidAlarm.clearAlarm("test");
});

/* Alarms are cancelled by name */
AndroidAlarm.clearAlarm("test");

/* Or you can remove listeners */
AndroidAlarm.AlarmEmitter.removeAllListeners("test");

/* 8AM wakeup alarm */
AndroidAlarm.setAlarm({
  name: "test2",
  type: AndroidAlarm.RTC_WAKEUP,
  // Time fields -- passed to Java's Calendar class. These will default to the current time if not provided
  // you should most likely set them all.
  /* date: 5 -- Date of the month */
  hour: 8, minute: 0, second: 0,
  // If interval is set, alarm will be repeating
  interval: AndroidAlarm.INTERVAL_DAY 
});

```

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
