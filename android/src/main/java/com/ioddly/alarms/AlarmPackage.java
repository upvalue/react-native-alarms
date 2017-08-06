package com.ioddly.alarms;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AlarmPackage implements ReactPackage {
  @Override
  public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
    return Collections.emptyList();
  }

  public List<Class<? extends JavaScriptModule>> createJSModules() {
    List<Class<? extends JavaScriptModule>> jsModules = new ArrayList<Class<? extends JavaScriptModule>>(Arrays.asList(AlarmEmitter.class));
    return jsModules;
  }

  @Override
  public List<NativeModule> createNativeModules(
                              ReactApplicationContext reactContext) {
    List<NativeModule> modules = new ArrayList<>();

    modules.add(new AlarmModule(reactContext));

    return modules;
  }

}
