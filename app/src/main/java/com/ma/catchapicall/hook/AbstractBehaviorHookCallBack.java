package com.ma.catchapicall.hook;

public abstract class AbstractBehaviorHookCallBack extends MethodHookCallBack {
    @Override
    public void beforeHookedMethod(HookParam param) {
        // TODO Auto-generated method stub
//		Logger.log_behavior("Invoke "+ param.method.getDeclaringClass().getName()+"->"+param.method.getName());
        this.paramInfo(param);
        //this.printStackInfo();
        param.setResult(null);
    }

    @Override
    public void afterHookedMethod(HookParam param) {
        // TODO Auto-generated method stub
        //Logger.log_behavior("End Invoke "+ param.method.toString());
    }

    public abstract void paramInfo(HookParam param);

}
