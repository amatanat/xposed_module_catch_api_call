package com.ma.catchapicall.hook;

public abstract class ApiMonitorHook {
    protected HookHelperInterface hookhelper = HookHelperFactory.getHookHelper();
    public abstract void startHook();
}
