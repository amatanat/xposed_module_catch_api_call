package com.ma.catchapicall.module;

import android.content.pm.ApplicationInfo;

import com.ma.catchapicall.api.ApiMonitorHookManager;
import com.ma.catchapicall.hook.ApiMonitorHook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

// source https://github.com/cyruliu/Sensitive_API_Monitor

public class MonitorXposedModule implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.appInfo == null ||
                (lpparam.appInfo.flags &
                        (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) {
            return;
        }



        if (!lpparam.packageName.equals("com.ma.trustedcomponent"))
            ApiMonitorHookManager.getInstance().replaceMethod();


       // ApiMonitorHookManager.getInstance().startMonitor();

    }
}
