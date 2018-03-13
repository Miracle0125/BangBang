package com.yzx.bangbang.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.model.Mysql.ExploreRecord;
import com.yzx.bangbang.utils.NetWork.OkHttpUtil;
import com.yzx.bangbang.utils.Params;
import com.yzx.bangbang.utils.util;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Flowable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkService extends Service {
    public static final int RECEIVE_MESSAGE = 0;
    public static final int KEY_INSERT_MSG = 1;
    public static NetworkService inst;
    static List<Listener> listeners;
    public static Map<Integer, Integer> id_diff;
    //public static SparseIntArray id_diff;
    public static Map<Integer, Integer> id_count;
    Gson gson = new Gson();
    boolean bind = false;

    @Override
    public void onCreate() {
        super.onCreate();
        listeners = new ArrayList<>();
        inst = this;
        //id_diff =  new HashMap<>();
        id_diff=new HashMap<>();
        id_count = new HashMap<>();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        getAssignments();
        bind = true;
        return new LocalBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        clear();
        return super.onUnbind(intent);
    }

    public List<Integer> assignments;

    /**
     * 先获取用户所有的需求，再从explore_record表获取该需求当前的帮众数量，存入id_count中，系统轮询该用户需求的帮众数量，与id_count相减获得差值
     * 存入id_diff,然后id_diff  value大于0的，消息提醒数量+1
     */
    public void getAssignments() {
        OkHttpUtil okHttpUtil = OkHttpUtil.inst(s -> {
            if (s.length() == 0 || s.charAt(0) == '<') return;
            assignments = util.parseSingleColumnJsonInteger(s, "id");
            for (int id : assignments) {
                id_count.put(id, 0);
            }
            initCountMap();
        });
        okHttpUtil.addPart("sql", "", "select id from assignment where employer_id = " + Main.user.getId(), OkHttpUtil.MEDIA_TYPE_JSON);
        okHttpUtil.post("query_data_common");
    }

    Timer timer;

    private void initCountMap() {
        OkHttpUtil okHttpUtil = OkHttpUtil.inst(s -> {
            if (!(s.length() == 0 || s.charAt(0) == '<')) {
                ReceiveRecordList receiveRecordList = gson.fromJson(s, ReceiveRecordList.class);
                if (receiveRecordList != null && receiveRecordList.list.size() != 0) {
                    for (ExploreRecord er : receiveRecordList.list) {
                        id_count.put(er.asm_id, er.repliers);
                    }
                }
            }
            if (timer == null)
                timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    queryMessage(assignments);
                }
            };
            timer.schedule(task, 0, 3500);
        });
        okHttpUtil.addPart("sql", "", "select * from explore_record where user_id = " + Main.user.getId(), OkHttpUtil.MEDIA_TYPE_JSON);
        okHttpUtil.post("query_data_common");
    }

    //浏览消息后会更新Map的数据
    public void queryMessage(List<Integer> assignments) {
        if (assignments == null || assignments.size() == 0 || Main.ref.get() == null || id_count.size() == 0)
            return;
        OkHttpClient client = new OkHttpClient();
        for (int id : assignments) {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.addFormDataPart("sql", "select count(*) from event where asm_id = " + id + " and type = 1");
            Request request = new Request.Builder().url(getUrl("query_data_common")).post(builder.build()).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call arg0, IOException arg1) {
                }

                @Override
                public void onResponse(Call arg0, Response arg1) throws IOException {
                    String s = arg1.body().string();
                    if (s.length() == 0 || s.charAt(0) == '<') return;
                    int count = util.parseString(s, "count(*)");
                    int diff = count - id_count.get(id);
                    id_diff.put(id, diff);
                    if (Main.ref.get() != null)
                        Flowable.just(util.obtain_message(6)).subscribe(Main.ref.get().consumer);
                        //Main.ref.get().getHandler().sendEmptyMessage(6);

                }
            });
        }
    }

    public static void updateCount(int asm_id, int count) {
        OkHttpUtil okHttpUtil = OkHttpUtil.inst(s -> {
        });
        okHttpUtil.addPart("sql", "", "update explore_record set repliers = " + count + " where user_id = " + Main.user.getId() + " and asm_id = " + asm_id, OkHttpUtil.MEDIA_TYPE_JSON);
        okHttpUtil.post("update_data_common");
    }

    //查询是否有记录
    public static void checkIfHasRecord(int asm_id, int count) {
        OkHttpUtil okHttpUtil = OkHttpUtil.inst(s -> {
            if (s.length() == 0 || s.charAt(0) == '<') return;
            int c = util.parseString(s, "count(*)");
            if (c > 0) {
                updateCount(asm_id, count);
            } else {
                insertRecord(asm_id, count);
            }
            id_count.put(asm_id, count);
        });
        okHttpUtil.addPart("sql", "select count(*) from explore_record where user_id = " + Main.user.getId() + " and asm_id = " + asm_id);
        okHttpUtil.post("query_data_common");
    }

    //当浏览过消息时，记录当时消息的帮众数量，如果以后帮众数量变得更多，消息为高亮
    private static void insertRecord(int asm_id, int count) {
        OkHttpUtil okHttpUtil = OkHttpUtil.inst(s -> {
        });
        okHttpUtil.addPart("sql", "insert into explore_record (`user_id`,`asm_id`,`repliers`) values ('" + Main.user.getId() + "','" + asm_id + "','" + count + "')");
        okHttpUtil.post("update_data_common");
    }

    private String getUrl(String servlet) {
        return "http://" + Params.ip + ":8080/BangBang/" + servlet;
    }

    public NetworkService getInst() {
        return NetworkService.this;
    }

    public class LocalBinder extends Binder {
        public NetworkService getService() {
            return NetworkService.this;
        }
    }

    //在上层universalImageDownloader还会再包装一次
    private static OkHttpClient imageDownloader;

    public void DownloadImage(String sql, int code, Callback callback) {
        if (imageDownloader == null)
            imageDownloader = new OkHttpClient();
        MultipartBody body = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart(String.valueOf(code), sql)
                .build();
        Request request = new Request.Builder().url("http://" + Params.ip + ":8080/BangBang/query_image").post(body)
                .build();
        Call call = imageDownloader.newCall(request);
        call.enqueue(callback);
    }

    private void clear() {
        inst = null;
        listeners.clear();
        listeners = null;
        bind = false;
        id_count.clear();
        id_diff = null;
        id_count.clear();
        id_count = null;
    }

    public interface Listener {
        void onNetWorkUpdate(int tag);
    }

    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    public static void clearData() {
        id_count.clear();
        id_count.clear();
    }

    private class ReceiveRecordList {
        List<ExploreRecord> list;
    }

    public class CommonImageReceiver {
        public String image_base64;
        public int code;
    }
}