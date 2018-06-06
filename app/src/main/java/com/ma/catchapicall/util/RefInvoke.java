package com.ma.catchapicall.util;

import java.lang.reflect.Method;

public class RefInvoke {
    public static Method findMethodExact(String className, ClassLoader classLoader,
                                         String methodName, Class<?>... parameterTypes) {
        try {
            Class clazz = classLoader.loadClass(className);
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
