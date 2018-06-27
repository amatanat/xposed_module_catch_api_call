package com.ma.catchapicall.api;

public class ApiMonitorHookManager {

    private static ApiMonitorHookManager hookmger;
    private ContentResolverHook contentResolverHook;

    private ApiMonitorHookManager(){
        this.contentResolverHook = new ContentResolverHook();
    }

    public static ApiMonitorHookManager getInstance(){
        if(hookmger == null)
            hookmger = new ApiMonitorHookManager();
        return hookmger;
    }

    public void startMonitor(){
        this.contentResolverHook.startHook();
    }

    public void replaceMethod(){
        this.contentResolverHook.replaceMethod();
    }

}
