import {NativeEventEmitter, NativeModules} from 'react-native';

const BatchedBridge = require('react-native/Libraries/BatchedBridge/BatchedBridge');
const AlarmAndroid = NativeModules.AlarmAndroid;

class _AlarmEmitter extends NativeEventEmitter {};

const AlarmEmitter = new _AlarmEmitter();

BatchedBridge.registerCallableModule('AlarmEmitter', AlarmEmitter);

let {RTC, RTC_WAKEUP, ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, launchMainActivity, setAlarm,
  INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY, INTERVAL_HALF_DAY} = AlarmAndroid;

// Most of these wrappers are mainly to check arguments before calling Java code. This results in stack traces with JS
// source information.

// Sane wrappers around setAlarm, which takes a big and untyped bundle of opts for most arguments
const alarmSetElapsedRealtime = (name, trigger, interval, wakeup) => {
  let prefix = `RNAlarms: alarmSetElapsedRealtime${wakeup ? 'Wakeup' : ''}: `;

  if(!(typeof name === 'string' || name instanceof String))
    throw new Error(`${prefix} first argument name must be a string`);
  if(typeof trigger !== 'number')
    throw new Error(`${prefix} second argument date must be a Date`);
  if(interval && typeof interval !== 'number')
    throw new Error(`${prefix} third argument interval, if provided, must be a number`);

  let opts = { trigger: trigger };
  if(interval) {
    opts.interval = interval;
  }

  return setAlarm(name, wakeup ? ELAPSED_REALTIME_WAKEUP : ELAPSED_REALTIME, opts);
};

const alarmSetElapsedRealtimeWakeup = (name, trigger, interval) => {
  return alarmSetElapsedRealtime(name, trigger, interval, true);
}

const alarmSetRTC = (name, date, interval, wakeup) => {
  let prefix = `RNAlarms: alarmSetRTC${wakeup ? 'Wakeup' : ''}: `;

  // Check argument types
  if(!(typeof name === 'string' || name instanceof String))
    throw new Error(`${prefix} first argument name must be a string`);
  if(!(date instanceof Date))
    throw new Error(`${prefix} second argument date must be a Date`);
  if(interval && typeof interval != 'number')
    throw new Error(`${prefix} third argument interval, if provided, must be a number or one of the provided constants`);

  let opts = { date: date.getDate(), minute: date.getMinutes(), hour: date.getHours(), second: date.getSeconds() };
  if(interval) {
    opts.interval = interval;
  }

  return setAlarm(name, wakeup ? RTC_WAKEUP : RTC, opts);
};

const alarmSetRTCWakeup = (name, date, interval) => {
  return alarmSetRTC(name, date, interval, true);
}

const clearAlarm = (name) => {
  if(!(typeof name === 'string' || name instanceof String)) {
    throw new Error('RNAlarms: clearAlarm first argument must be a string');
  }

  AlarmAndroid.clearAlarm(name);
}

const alarmExists = (name) => {
  if(!(typeof name === 'string' || name instanceof String)) {
    throw new Error('RNAlarms: alarmExists first argument must be a string');
  }

  return AlarmAndroid.alarmExists(name);
}

export default {RTC, RTC_WAKEUP, ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, alarmExists, setAlarm, clearAlarm, 
  AlarmEmitter, setAlarm,
  alarmSetElapsedRealtime, alarmSetElapsedRealtimeWakeup, alarmSetRTC, alarmSetRTCWakeup,
  INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY, INTERVAL_HALF_DAY, launchMainActivity};
