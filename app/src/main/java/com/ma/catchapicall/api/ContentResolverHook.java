package com.ma.catchapicall.api;

import android.app.Activity;
import android.app.AndroidAppHelper;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.ma.catchapicall.hook.AbstractBehaviorHookCallBack;
import com.ma.catchapicall.hook.ApiMonitorHook;
import com.ma.catchapicall.hook.HookParam;
import com.ma.catchapicall.util.RefInvoke;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static de.robv.android.xposed.XC_MethodReplacement.DO_NOTHING;

public class ContentResolverHook extends ApiMonitorHook {

    private static final String[] privacyUris = {
            "content://com.android.contacts/contacts",
            // "content://contacts/",
            //  "content://com.android.contacts",
            "content://com.android.contacts/raw_contacts",
            "content://call_log/calls",
            "content://com.android.contacts/data/emails",
            "content://com.andorid.contacts/data/phones",
            "content://com.android.calendar/events",
            "content://com.android.calendar/attendees",
            "content://com.android.calendar/reminders",
            "content://com.android.calendar/instances",
            "content://com.android.calendar/instances/when",
            "content://com.android.calendar/calendars",
            "content://com.android.calendar/calendar_alerts"
    };

    /***
     * This method is used to determine if the
     * @param uri - passed API uri
     * @return
     */
    private boolean isSensitiveUri(Uri uri) {
        String url = uri.toString().toLowerCase();
        for (String privacyUri : privacyUris) {
            if (url.startsWith(privacyUri)) {
                return true;
            }
        }
        return false;
    }

    private int whichUri(Uri uri) {
        String url = uri.toString().toLowerCase();
        for (int i = 0; i < privacyUris.length; i++) {
            if (url.startsWith(privacyUris[i])) {
                XposedBridge.log("uri: " + privacyUris[i]);
                return i;
            }

        }
        return -1;
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
                    param.setResult(DO_NOTHING);
                }
            }
        });

    }

    @Override
    public void replaceMethod() {

        hookhelper.findAndHookMethod("android.content.ContentResolver", ClassLoader.getSystemClassLoader(),
                "query", Uri.class, String[].class, String.class, String[].class, String.class,

                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        final Uri uri = (Uri) param.args[0];
                        // if sensitive uri then replace query method
                        if (isSensitiveUri(uri)) {

                            hookhelper.findAndHookMethod("android.content.ContentResolver", ClassLoader.getSystemClassLoader(),
                                    "query", Uri.class, String[].class, String.class, String[].class, String.class,

                                    new XC_MethodReplacement() {
                                        @Override
                                        protected Object replaceHookedMethod(MethodHookParam param) {
                                            Context context = AndroidAppHelper.currentApplication();
                                            XposedBridge.log("android.content.ContentResolver.query");
                                            XposedBridge.log("Read ContentProvider -> Uri = " + uri.toString());
                                            String queryStr = concatenateQuery(uri, (String[]) param.args[1], (String) param.args[2], (String[]) param.args[3],
                                                    (String) param.args[4]);
                                            XposedBridge.log("Query SQL = " + queryStr);

                                            int whichUri = whichUri(uri);
                                            MatrixCursor matrixCursor = new MatrixCursor(new String[0]);
                                            switch (whichUri) {
                                                case 1:
                                                    // RAW_CONTACTS TABLE
                                                    Intent intent = new Intent();
                                                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                                    intent.setClassName(
                                                            // app's package name
                                                            "com.ma.trustedcomponent",
                                                            // The full class name of the activity you want to start
                                                            "com.ma.trustedcomponent.MainActivity");
                                                    context.startActivity(intent);
                                                    param.setResult(matrixCursor);
                                                    break;
                                                case 2:
                                                    //CALL_LOG
                                                    Intent intent2 = new Intent();
                                                    intent2.addCategory(Intent.CATEGORY_LAUNCHER);
                                                    intent2.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                                    intent2.setClassName(
                                                            // app's package name
                                                            "com.ma.trustedcomponent",
                                                            // The full class name of the activity you want to start
                                                            "com.ma.trustedcomponent.MainActivity");
                                                    context.startActivity(intent2);
                                                    break;
                                                default:
                                                    break;
                                            }
                                            return matrixCursor;
                                        }
                                    });
                        }
                    }
                });


    }

    private class singletonActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle bunle) {
            super.onCreate(bunle);
        }

        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == 100) {
                // Make sure the request was successful
                if (resultCode == RESULT_OK) {
                    XposedBridge.log("RESULT CODE IS 100 @@@@@@@@@");
                }
            }
        }
    }
}
