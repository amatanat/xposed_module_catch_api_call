package com.ma.catchapicall.hook;

import java.lang.reflect.Member;

public interface HookHelperInterface {
    void hookMethod(Member method, MethodHookCallBack callback);
}
