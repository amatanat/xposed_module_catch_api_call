package com.ma.catchapicall.api;

import android.net.Uri;
import android.text.TextUtils;

import com.ma.catchapicall.hook.AbstractBehaviorHookCallBack;
import com.ma.catchapicall.hook.ApiMonitorHook;
import com.ma.catchapicall.hook.HookParam;
import com.ma.catchapicall.util.RefInvoke;

import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;

public class ContentResolverHook extends ApiMonitorHook {

    private static final String[] privacyUris = {
            "content://com.android.contacts",
            "content://contacts/",
            "content://call_log",
            "content://com.android.calendar" };

    private boolean isSensitiveUri(Uri uri) {
        String url = uri.toString().toLowerCase();
        for (String privacyUri : privacyUris) {
            if (url.startsWith(privacyUri)) {
                return true;
            }
        }
        return false;
    }

    private String concatenateStringArray(String[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i == array.length - 1)
                sb.append(array[i]);
            else
                sb.append(array[i] + ",");
        }
        return sb.toString();
    }

    private String concatenateQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        StringBuilder sb = new StringBuilder("select ");
        if (projection == null) {
            sb.append("* ");
        } else {
            sb.append(concatenateStringArray(projection));
        }
        sb.append(" from [" + uri.toString() + "] ");
        if (!TextUtils.isEmpty(selection)) {
            sb.append(" where ");
            if (selectionArgs == null) {
                sb.append(selection);
            } else {
                String selectstr = selection;
                for (int i = 0; i < selectionArgs.length; i++) {
                    selectstr = selectstr.replaceFirst("/?", selectionArgs[i]);
                }
                sb.append(selectstr);
            }
        }
        if (!TextUtils.isEmpty(sortOrder))
            sb.append(" order by " + sortOrder);
        return sb.toString();
    }


    @Override
    public void startHook() {
        Method querymethod = RefInvoke.findMethodExact(
                "android.content.ContentResolver",
                ClassLoader.getSystemClassLoader(), "query",
                Uri.class, String[].class, String.class, String[].class, String.class);
        hookhelper.hookMethod(querymethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void paramInfo(HookParam param) {
                Uri uri = (Uri) param.args[0];
                if (isSensitiveUri(uri)) {
                    XposedBridge.log("android.content.ContentResolver.query");
                    XposedBridge.log("Read ContentProvider -> Uri = " + uri.toString());
                    String queryStr = concatenateQuery(uri, (String[]) param.args[1], (String) param.args[2], (String[]) param.args[3],
                            (String) param.args[4]);
                    XposedBridge.log("Query SQL = " + queryStr);
                }
            }
        });

    }
}
