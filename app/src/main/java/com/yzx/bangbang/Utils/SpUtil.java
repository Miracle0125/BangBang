package com.yzx.bangbang.Utils;

import android.app.Activity;
import android.content.Context;

public class SpUtil {
    public static final String EXPLORE_RECORD= "explore_record";
    public static final String PORTRAIT= "portrait";
    public static final String SAVED_INFO = "saved_info";
    public static final String DATABASE = "database";

    //1 not changed ,
    public static int readIntAndChange(Context context){
        int i =  util.getInt(util.DATA_OBSERVER,context);
        util.putInt(util.DATA_OBSERVER,util.DATA_NOT_CHANGED,context);
        return i;
    }
    public static void putRefreshFlag(Context context){
        util.putInt(util.DATA_OBSERVER,util.DATA_CHANGED,context);
    }

    public static int readIntAndDelete(int key,Context context){
        String k  = String.valueOf(key);
        int i  =util.getInt(k,context);
        util.deleteSpData(Params.DATABASE, k, context);
        return i;
    }

    public static void putString(String db,String key, String value, Context context) {
        context.getApplicationContext().getSharedPreferences(db, Activity.MODE_PRIVATE).edit().putString(key, value).commit();
    }

    public static String getString(String db,String key, Context context) {
        return context.getApplicationContext().getSharedPreferences(db, Activity.MODE_PRIVATE).getString(key, null);
    }


    //user  integer
    public static void putString(String db,int key, String value, Context context) {
        context.getApplicationContext().getSharedPreferences(db, Activity.MODE_PRIVATE).edit().putString(String.valueOf(key), value).commit();
    }

    public static String getString(String db,int key, Context context) {
        return context.getApplicationContext().getSharedPreferences(db, Activity.MODE_PRIVATE).getString(String.valueOf(key), null);
    }

    public static void putInt(String db,String key, int value, Context context){
        context.getApplicationContext().getSharedPreferences(db, Activity.MODE_PRIVATE).edit().putInt(key, value).commit();
    }

    public static int getInt(String db,String key, Context context) {
        return context.getApplicationContext().getSharedPreferences(db, Activity.MODE_PRIVATE).getInt(key, -1);
    }


}
