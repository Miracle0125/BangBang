package com.yzx.bangbang.utils;

import android.app.Activity;
import android.content.Context;

import com.amap.api.maps.model.LatLng;
import com.google.gson.Gson;
import com.yzx.bangbang.model.User;

import model.Assignment;

public class SpUtil {
    private static Context app_context;
    public static final String EXPLORE_RECORD = "explore_record";
    public static final String PORTRAIT = "portrait";
    public static final String SAVED_INFO = "saved_info";
    public static final String DEFAULT = "default";
    public static final int USER = 0;
    public static final int ASSIGNMENT = 1;
    public static final int LATLNG = 2;
    private static final Class cls[] = {User.class, Assignment.class, LatLng.class};

    public static void init(Context context) {
        app_context = context.getApplicationContext();
    }

    public static void putRefreshFlag(Context context) {
        util.putInt(util.DATA_OBSERVER, util.DATA_CHANGED, context);
    }

    public static void putString(String db, String key, String value) {
        app_context.getSharedPreferences(db, Activity.MODE_PRIVATE).edit().putString(key, value).apply();
    }

    public static String getString(String db, String key) {
        return app_context.getSharedPreferences(db, Activity.MODE_PRIVATE).getString(key, null);
    }


    //user  integer
    public static void putString(String db, int key, String value) {
        app_context.getSharedPreferences(db, Activity.MODE_PRIVATE).edit().putString(String.valueOf(key), value).apply();
    }

    public static String getString(String db, int key) {
        return app_context.getSharedPreferences(db, Activity.MODE_PRIVATE).getString(String.valueOf(key), null);
    }

    public static String getString(int key) {
        return app_context.getSharedPreferences(DEFAULT, Activity.MODE_PRIVATE).getString(String.valueOf(key), null);
    }

    public static Object getObject(int cls1) {
        return new Gson().fromJson(getString(cls1), cls[cls1]);
    }

    public static void putInt(String db, String key, int value, Context context) {
        app_context.getSharedPreferences(db, Activity.MODE_PRIVATE).edit().putInt(key, value).apply();
    }

    public static int getInt(String db, String key, Context context) {
        return app_context.getSharedPreferences(db, Activity.MODE_PRIVATE).getInt(key, -1);
    }


}
