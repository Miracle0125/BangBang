package com.yzx.bangbang.utils;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.gson.reflect.TypeToken;
import com.yzx.bangbang.Activity.BangBangApp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import model.Assignment;


public class util {
    public static Interpolator interpolator;
    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;
    public static final String DATA_OBSERVER = "DataObserver";
    public static final int DATA_CHANGED = 0;
    public static final int DATA_NOT_CHANGED = 1;

    public static void setMaterialRipple(View view) {
        setMaterialRipple(view, 0);
    }

    //第三方库的动画效果
    public static void setMaterialRipple(View view, int color) {
        if (color == 0) color = Params.COLOR_WHITE;
        MaterialRippleLayout.on(view)
                .rippleColor(color)
                .rippleAlpha(0.2f)
                .rippleHover(true)
                .create();
    }

    //移动view
    public static void Animate(View v, float translation, int mode, int duration) {
        if (interpolator == null) interpolator = new DecelerateInterpolator();
        switch (mode) {
            case VERTICAL:
                ViewCompat.animate(v)
                        .translationY(translation)
                        .setDuration(duration)
                        .setInterpolator(interpolator)
                        .start();
                break;
            case HORIZONTAL:
                ViewCompat.animate(v)
                        .translationX(translation)
                        .setDuration(duration)
                        .setInterpolator(interpolator)
                        .start();
                break;
        }
    }

    /**
     * 用于分析query_data_common查询单个字段时返回的json，key 为该字段，数目限制为1
     */
    public static int parseString(String s, String key) {
        if (s.charAt(0) != '{') return -1;
        int start;
        int end = start = s.indexOf(key) + key.length() + 3;
        while (Character.isDigit(s.charAt(end)))
            end++;
        return Integer.valueOf(s.substring(start, end));
    }

    /**
     * 用于分析query_data_common查询单个字段时返回的json，key 为该字段，数目不限
     */
    public static List<Integer> parseSingleColumnJsonInteger(String s, String key) {
        if (s.charAt(0) != '{') return null;
        List<Integer> list = new ArrayList<>();
        int start, end = 0, index;
        while (end < s.length()) {
            index = s.indexOf(key);
            if (index == -1) break;
            end = start = index + key.length() + 3;
            while (Character.isDigit(s.charAt(end)))
                end++;
            list.add(Integer.valueOf(s.substring(start, end)));
            s = s.substring(end, s.length());
        }
        return list;
    }

    public static int px(int dp) {
        return (int) (dp * Params.scale + 0.5f);
    }

    public static String getId() {
        return UUID.randomUUID().toString();
    }

    public static class KeyBoardDetector {
        private Rect rect;

        public KeyBoardDetector(View decorView) {
            rect = new Rect();
            decorView.getWindowVisibleDisplayFrame(rect);
        }

        public int get_key_board_top() {
            return rect.bottom;
        }

        public boolean isKeyBoardRose() {
            return ((double) (rect.bottom - rect.top) / Params.screenHeight) < 0.8;
        }
    }

    public static boolean IsKeyBoardRose(View decorView) {
        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        return ((double) (rect.bottom - rect.top) / Params.screenHeight) < 0.8;
    }

    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrst";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(base.charAt(random.nextInt(base.length())));
        }
        return sb.toString();
    }

    public static int getRandomInt(int length) {
        String base = "1234567890";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(base.charAt(random.nextInt(base.length())));
        }
        return Integer.valueOf(sb.toString());
    }


    public static int CustomColor(float p) {
        if (p >= 100) return Params.COLOR_RED;
        if (p >= 10) return Params.COLOR_GREEN;
        return Params.COLOR_DEEP_BLUE;
    }

    public static String s(int i) {
        return String.format("%d", i);
    }

    public static String s(float f) {
        return String.valueOf(f);
    }

    private static SimpleDateFormat format0, format1;

    public static ByteArrayOutputStream compress(Bitmap image, int size) {
        int times = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, out);
        if (out.toByteArray().length < size * 1024)
            return out;
        float zoom = (float) Math.sqrt(size * 1024 / (float) out.toByteArray().length);
        Matrix matrix = new Matrix();
        matrix.setScale(zoom, zoom);
        Bitmap result = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
        out.reset();
        result.compress(Bitmap.CompressFormat.JPEG, 90, out);
        while (out.toByteArray().length > size * 1024) {
            matrix.setScale(0.9f, 0.9f);
            result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
            out.reset();
            result.compress(Bitmap.CompressFormat.JPEG, 90, out);
            times++;
            if (times > 28)
                break;
        }
        return out;
    }

    //有时通过VIEW自带的方法获取Bitmap会返回空值，所以创造了这个方法
    public static Bitmap loadBitmapFromView(View v) {
        if (v == null) {
            return null;
        }
        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas c = new Canvas(screenshot);
        v.draw(c);
        return screenshot;
    }

    public static String getDate() {
        Calendar c = new GregorianCalendar();
        return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
    }

    //通过long 形势的date，获取date的string
    public static String getDate(long date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date);
        return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
    }

    public static long getDateLong() {
        Calendar c = new GregorianCalendar();
        return c.getTime().getTime();
    }

    //将数据流写入临时文件，可以添加回调接口
    public static void writeFile(byte[] b, String path, @Nullable writeFileCallback callback) {
        try {
            InputStream in = new ByteArrayInputStream(b);
            File file = new File(path);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            if (callback != null)
                callback.onFinish();
            fos.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface writeFileCallback {
        void onFinish();
    }

    public static void putInt(String key, int value, Context context) {
        context.getApplicationContext().getSharedPreferences(Params.DATABASE, Activity.MODE_PRIVATE).edit().putInt(key, value).commit();
    }

    public static int getInt(String key, Context context) {
        return context.getApplicationContext().getSharedPreferences(Params.DATABASE, Activity.MODE_PRIVATE).getInt(key, -1);
    }

    public static void putString(int key, String value, Context context) {
        context.getApplicationContext().getSharedPreferences(Params.DATABASE, Activity.MODE_PRIVATE).edit().putString(context.getResources().getString(key), value).commit();
    }

    public static String getString(int key, Context context) {
        return context.getApplicationContext().getSharedPreferences(Params.DATABASE, Activity.MODE_PRIVATE).getString(context.getResources().getString(key), null);
    }

    public static void putBoolean(String key, boolean value, Context context) {
        context.getApplicationContext().getSharedPreferences(Params.DATABASE, Activity.MODE_PRIVATE).edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(String key, Context context) {
        return context.getApplicationContext().getSharedPreferences(Params.DATABASE, Activity.MODE_PRIVATE).getBoolean(key, false);
    }

    public static void deleteSpData(String db, String key, Context context) {
        context.getApplicationContext().getSharedPreferences(db, Activity.MODE_PRIVATE).edit().remove(key).commit();
    }

    public static String resStr(int id) {
        return BangBangApp.r.getString(id);
    }

    //将日期变成几天前或者几小时前
    public static String CustomDate(String raw_date) {
        if (format0 == null || format1 == null) {
            format0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format1 = new SimpleDateFormat("yyyy-MM-dd");
        }
        try {
            Date date = format0.parse(raw_date);
            Date now = new Date();
            long t = now.getTime() - date.getTime();

            long day = t / (24 * 60 * 60 * 1000);
            if (day > 7) return format1.format(date);
            else if (day >= 1)
                return String.valueOf((int) day) + "天前";

            long hour = t / (60 * 60 * 1000);
            if (hour >= 1)
                return String.valueOf((int) hour) + "小时前";

            long minute = t / (60 * 1000);
            return String.valueOf((int) minute) + "分钟前";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Message obtain_message(int what) {
        return obtain_message(what, null);
    }

    public static Message obtain_message(int what, Object o) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = o;
        return msg;
    }

    public static void AppExit() {
        try {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
