package com.ma.catchapicall;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class ReceiveApiCalls implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("com.android.systemui")){
            XposedBridge.log("%%%%%%%%%%%%%%%% Loaded app: " + lpparam.packageName);
            findAndHookMethod("com.android.systemui.statusbar.policy.Clock", lpparam.classLoader, "updateClock", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    TextView tv = (TextView) param.thisObject;
                    String text = tv.getText().toString();
                    XposedBridge.log("time: " + text);
                    tv.setText(text + " :)");
                    tv.setTextColor(Color.RED);
                }
            });
//            // TODO before a method call display a new UI.

        } else if(lpparam.packageName.equals("com.android.contacts")) {
            XposedBridge.log("******Loaded app: " + lpparam.packageName);
            XposedBridge.log("@@@@@@@@@ found class: " + findClass
                    ("com.android.contacts.activities.ContactSelectionActivity",
                    lpparam.classLoader));

            findAndHookMethod("com.android.contacts.activities.ContactSelectionActivity", lpparam.classLoader,
                    "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) {
                    Context context = (Context) (param.thisObject);
                    PackageManager manager = context.getPackageManager();
                    Intent intent = manager.getLaunchIntentForPackage("com.ma.bakingrecipes");
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    context.startActivity(intent);
                    XposedBridge.log("@@@@@@@@@ " + "intent is started");
                }
            });

        }else{
            XposedBridge.log("----------------------- Loaded app: " + lpparam.packageName);
            return;
        }
    }
}
