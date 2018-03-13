package com.yzx.bangbang.Utils;

import android.app.Activity;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ActivityManager {
    private static int currentLayer = 0;
    public static Stack<Class<?>> activityStack;
    public static ActivityManager inst;
    public static void init(){
        inst = new ActivityManager();
    }
    public static ActivityManager getManager() {
        if (inst == null) {
            inst = new ActivityManager();
        }
        return inst;
    }

    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        currentLayer++;
        //WeakReference<Activity> ref = new WeakReference<>(activity);
        activityStack.add(activity.getClass());
        Log.d("ss", "size " + activityStack.size());
        //map.put(activity.getClass(),ref);
    }

    public static int getLayer(){
        return currentLayer;
    }

    public static Class<?> getTop(){
        if (!activityStack.isEmpty()){
            Class<?> cls = activityStack.lastElement();
        return cls;}
        else return null;
    }

    public void onFinish() {
        currentLayer--;
        activityStack.pop();
        Log.d("ss","size "+activityStack.size());
    }
}
