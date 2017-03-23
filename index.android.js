import {NativeEventEmitter, NativeModules} from 'react-native';

const BatchedBridge = require('react-native/Libraries/BatchedBridge/BatchedBridge');
const AlarmAndroid = NativeModules.AlarmAndroid;

class AlarmEmitter extends NativeEventEmitter {

};

AlarmEmitter = new AlarmEmitter();

BatchedBridge.registerCallableModule('AlarmEmitter', AlarmEmitter);

let RTC, RTC_WAKEUP, ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, setAlarm, clearAlarm, INTERVAL_FIFTEEN_MINUTES,
  INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY, INTERVAL_HALF_DAY;

({RTC, RTC_WAKEUP, ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, setAlarm, clearAlarm,
  INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY, INTERVAL_HALF_DAY} = AlarmAndroid);

export default {RTC, RTC_WAKEUP, ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, setAlarm, clearAlarm, AlarmEmitter};
