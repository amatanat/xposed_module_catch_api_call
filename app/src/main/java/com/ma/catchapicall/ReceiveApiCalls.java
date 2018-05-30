package com.ma.catchapicall;

import android.graphics.Color;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class ReceiveApiCalls implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // XposedBridge.log("Loaded app: " + lpparam.packageName);
        if (lpparam.packageName.equals("com.android.systemui")){
            XposedBridge.log("%%%%%%%%%%%%%%%% Loaded app: " + lpparam.packageName);
            findAndHookMethod("com.android.systemui.statusbar.policy.Clock", lpparam.classLoader, "updateClock", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    TextView tv = (TextView) param.thisObject;
                    String text = tv.getText().toString();
                    tv.setText(text + " :)");
                    tv.setTextColor(Color.RED);
                }
            });
        } else if (lpparam.packageName.equals("com.android.contacts")){
            XposedBridge.log("??????? Loaded app: " + lpparam.packageName);
            // TODO find and hook method
            // TODO before a method call display a new UI.

        } else {
            XposedBridge.log("----------------------- Loaded app: " + lpparam.packageName);
            return;
        }

    }
}
