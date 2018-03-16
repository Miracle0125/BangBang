package com.yzx.bangbang.activity;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.leakcanary.LeakCanary;
import com.yzx.bangbang.utils.sql.DAO;
import com.yzx.bangbang.utils.sql.SqlUtil;
import com.yzx.bangbang.utils.Params;
import com.yzx.bangbang.utils.sql.SpUtil;

import java.io.File;

public class BangBangApp extends Application {
    public static Resources r;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        //initLeakCanary();
        AsyncTask.execute(()->{
            clearDir(Params.TEMP_DIR);
            clearSp();
            init();
        });
    }

    private void init() {
        r = getResources();
        SpUtil.init(this);
        DAO.init();
        //SqlUtil.init();

    }

    private void initLeakCanary() {
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not setParams your app in this process.
            LeakCanary.install(this);
        }
    }

    private void clearDir(String path) {
        File file = new File(path);
        if (!(file.exists() && file.isDirectory())) {
            return;
        }
        String[] tempList = file.list();
        if (tempList == null) return;
        for (int i = 0; i < tempList.length; i++) {
            File f = new File(path + tempList[i]);
            if (f.exists())
                f.delete();
        }

    }

    private void clearSp() {
        SharedPreferences db = getSharedPreferences(Params.DATABASE, 0);
        if (db != null)
            db.edit().clear().apply();
        db = getSharedPreferences(SpUtil.PORTRAIT, 0);
        if (db != null)
            db.edit().clear().apply();
        db = getSharedPreferences(SpUtil.SAVED_INFO, 0);
        if (db != null)
            db.edit().clear().apply();
        db = getSharedPreferences(SpUtil.SAVED_INFO, 0);
        if (db != null)
            db.edit().clear().apply();
    }
}
