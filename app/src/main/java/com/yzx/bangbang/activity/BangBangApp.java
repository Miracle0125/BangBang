package com.yzx.bangbang.activity;

import android.app.Application;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.leakcanary.LeakCanary;
import com.yzx.bangbang.utils.netWork.WebSocketManager;
import com.yzx.bangbang.utils.sql.DAO;
import com.yzx.bangbang.utils.sql.SpUtil;

public class BangBangApp extends Application {
    public static Resources r;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        //initLeakCanary();
        AsyncTask.execute(this::init);
    }

    private void init() {
        r = getResources();
        SpUtil.init(this);
        DAO.init();
        //SqlUtil.connect();
    }

    private void initLeakCanary() {
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not setParams your app in this process.
            LeakCanary.install(this);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.e("ss", "ws closed in app");
        WebSocketManager.close();
    }

}
