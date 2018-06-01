package com.ma.catchapicall;

import android.os.Bundle;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by amatanat
 */

public class ReceiveApiCalls implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
            // check the package name
            if (lpparam.packageName.equals("com.android.contacts")) {
            XposedBridge.log("******Loaded app: " + lpparam.packageName);
            XposedBridge.log("@@@@@@@@@ found class: " + findClass
                    ("com.android.contacts.activities.ContactSelectionActivity",
                            lpparam.classLoader));

            // find 'onCreate' in ContactSelectionActivity and hook it.
            findAndHookMethod("com.android.contacts.activities.ContactSelectionActivity", lpparam.classLoader,
                    "onCreate", Bundle.class,

                    // replace method in ContactSelectionActivity
//                    new XC_MethodReplacement() {
//                        @Override
//                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//                            Context context = (Context) (param.thisObject);
//                            PackageManager manager = context.getPackageManager();
//                            // open third-party app
//                            Intent intent = manager.getLaunchIntentForPackage("com.ma.bakingrecipes");
//                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                            // TODO startActivityForResult
//                            context.startActivity(intent);
//                            // TODO return required value
//                            return null;
//                        }
//                    }

                    // before onCreate call in ContactSelectionActivity.
                    new XC_MethodHook() {
                @Override
                // before 'onCreate' call in ContactSelectionActivity, log the text
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) {
                    XposedBridge.log("*** Xposed log before onCreate call in Contact.");
                }
            } );
        } else if(lpparam.packageName.equals("com.android.calendar")){
                XposedBridge.log("******Loaded app: " + lpparam.packageName);

                findAndHookMethod("com.android.calendar.AllInOneActivity", lpparam.classLoader,
                        "onCreate", Bundle.class,

                        // before onCreate call in ContactSelectionActivity.
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) {
                                XposedBridge.log("*** Xposed log before onCreate call in Calendar.");
                            }
                        } );
            }
    }
}
