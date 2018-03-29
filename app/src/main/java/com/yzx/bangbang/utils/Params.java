package com.yzx.bangbang.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewConfiguration;

import com.yzx.bangbang.activity.SignIn;
import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.sql.SpUtil;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * 初始化一些参数
 */
public class Params {
    //public static String ip = "192.168.23.1";
    public static String ip = "222.210.1.47";
    public static boolean use_default_account = true;
    public static final String DATABASE = "DateBase";
    public static int screenHeight, screenWidth, statusHeight;
    public static float scale;
    public static int COLOR_BLUE_MAIN;
    public static int COLOR_GRAY;
    public static int COLOR_DEEP_BLUE;
    public static int COLOR_GREEN;
    public static int COLOR_RED;
    public static int COLOR_WHITE;
    public static int COLOR_LIGHT_BLUE;
    private static WeakReference<Context> context;
    public static int touchSlop;
    public static final String TEMP_DIR = "/sdcard/BangBang/ListReceiver/";
    public static final String ROOT_DIR = "/sdcard/BangBang/";
    public static final int EVENT_TYPE_ACCEPT_ASSIGNMENT = 1;
    public static final int EVENT_TYPE_CONCERN_PERSON = 2;
    public static final int EVENT_TYPE_COLLECT_ASSIGNMENT = 3;

    public static void initParams(Context context) {
        AsyncTask.execute(()->{
            Params.context = new WeakReference<>(context);
            scale = Params.context.get().getResources().getDisplayMetrics().density;
            touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
            setScreenSize();
            setStatusHeight();
            initColor();
            createDir();
        });
    }

    private static void createDir() {
        File file = new File("/sdcard/BangBang");
        if (!file.exists())
            file.mkdir();
        file = new File("/sdcard/BangBang/ListReceiver");
        if (!file.exists())
            file.mkdir();
    }

    private static void setStatusHeight() {
        Rect rect = new Rect();
        if (context.get() instanceof Activity) {
            View decor = ((Activity) context.get()).getWindow().getDecorView();
            decor.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                decor.getWindowVisibleDisplayFrame(rect);
                statusHeight = rect.top;
            });
        }
    }

    private static void setScreenSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        if (context.get() instanceof AppCompatActivity)
            ((AppCompatActivity) context.get()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        else if (context.get() instanceof Activity)
            ((Activity) context.get()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;
    }


    private static void initColor() {
        Resources resources = context.get().getResources();
        COLOR_BLUE_MAIN = resources.getColor(R.color.blue_mainTheme);
        COLOR_GRAY = resources.getColor(R.color.gray);
        COLOR_DEEP_BLUE = resources.getColor(R.color.deep_blue);
        COLOR_GREEN = Color.parseColor("#006000");
        COLOR_RED = Color.parseColor("#CC0000");
        COLOR_WHITE = Color.parseColor("#ffffff");
        COLOR_LIGHT_BLUE = resources.getColor(R.color.light_blue);
    }
}
