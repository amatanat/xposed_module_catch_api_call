package com.ma.catchapicall.hook;

import java.lang.reflect.Member;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class XposeHookHelperImpl implements HookHelperInterface {

    @Override
    public void hookMethod(Member method, MethodHookCallBack callback) {
        // TODO Auto-generated method stub
        if(method !=null)
            XposedBridge.hookMethod(method, callback);
    }

    @Override
    public void findAndHookMethod(String className, ClassLoader classLoader, String methodName,
                              Object... parameterTypesAndCallback) {
            XposedHelpers.findAndHookMethod(className, classLoader, methodName,
                    parameterTypesAndCallback);
    }
}
