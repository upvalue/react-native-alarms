interface rnAlarmEmitter {
  addListener(ev: string, callback: () => void);
  removeAllListeners(ev: string);
}

interface rnAlarmStatic {
  alarmSetElapsedRealtime(name: string, trigger: number, interval?: number);
  alarmSetElapsedRealtimeWakeup(name: string, trigger: number, interval?: number);
  alarmSetRTC(name: string, date: Date, interval?: number);
  alarmSetRTCWakeup(name: string, date: Date, interval?: number);
  alarmExists(name: string): Promise<boolean>;

  launchMainActivity();

  AlarmEmitter: rnAlarmEmitter;
}

declare var rnAlarmModule: rnAlarmStatic;

declare module "react-native-alarms" {
  export default rnAlarmModule;
}
