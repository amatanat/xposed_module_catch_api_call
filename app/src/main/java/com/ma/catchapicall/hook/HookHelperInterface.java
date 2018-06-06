package com.ma.catchapicall.hook;

import java.lang.reflect.Member;

import de.robv.android.xposed.XC_MethodReplacement;

public interface HookHelperInterface {
    void hookMethod(Member method, MethodHookCallBack callback);
    void findAndHookMethod(String className, ClassLoader classLoader, String methodName,
                       Object... parameterTypesAndCallback);
}
