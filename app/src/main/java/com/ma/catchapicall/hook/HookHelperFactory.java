package com.ma.catchapicall.hook;

public class HookHelperFactory {
    private static HookHelperInterface hookHelper;

    public static HookHelperInterface getHookHelper(){
        if(hookHelper == null)
            hookHelper = new XposeHookHelperImpl();
        return hookHelper;
    }
}
