import {NativeModules} from 'react-native';

let RTC, RTC_WAKEUP, ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, setAlarm, clearAlarm;

({RTC, RTC_WAKEUP, ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, setAlarm, clearAlarm} = NativeModules.AlarmAndroid);

export default {RTC, RTC_WAKEUP, ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, setAlarm, clearAlarm};
