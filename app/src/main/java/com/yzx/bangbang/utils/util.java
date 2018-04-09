package com.yzx.bangbang.utils;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;

import com.balysv.materialripple.MaterialRippleLayout;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class util {
    public static Interpolator interpolator;
    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;
    public static final String DATA_OBSERVER = "DataObserver";
    public static final int DATA_CHANGED = 0;
    public static final int DATA_NOT_CHANGED = 1;

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

    public static String get_random_id() {
        return UUID.randomUUID().toString();
    }

    public static boolean is_keyboard_rose(View decorView) {
        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        return ((double) (rect.bottom - rect.top) / Params.screenHeight) < 0.8;
    }

    public static float get_visible_bottom(View decorView) {
        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        return rect.bottom;
    }

    public static void toggle_keyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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


    public static int price_color(float p) {
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


    private static SimpleDateFormat format0, format1;

    //将日期变成几天前或者几小时前
    public static String transform_date(String raw_date) {
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

    public static void exit_app() {
        try {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isForeground(Context context, String className) {
        if (context == null || className == null)
            return false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null)
            return false;
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName name = list.get(0).topActivity;
            if (className.equals(name.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    public static int count_file_lines(File file) {
        if (file == null) {
            return 0;
        }
        int count = 0;
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                count++;
                scanner.next();
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return count;
    }

    public static String pack_json(String s) {
        return "[" + s + "]";
    }

    public static String unpack_json(String s) {
        return s.substring(1, s.length() - 2);
    }
}
