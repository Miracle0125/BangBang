package com.yzx.bangbang.utils.NetWork;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.yzx.bangbang.Service.NetworkService;
import com.yzx.bangbang.utils.Params;
import com.yzx.bangbang.utils.SpUtil;
import com.yzx.bangbang.utils.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UniversalImageDownloader {
    private Map<Integer, String> portraitCache = new HashMap<>();
    Gson gson = new Gson();
    Context context;


    public UniversalImageDownloader(Context context) {
        this.context = context;
    }

    public void downLoadPortrait(final int user_id, final SimpleDraweeView view) {
        if (portraitCache.get(user_id) == null)
            NetworkService.inst.DownloadImage("select image from portrait where user_id = " + user_id, user_id, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String s = response.body().string();
                    //if there is no portrait , server will return "failed",which length <10
                    if (s.length() < 10) {
                        //((Activity) context).runOnUiThread(() -> view.setImageURI(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.drawable.main_icon_portrait)));
                        return;
                    }
                    NetworkService.CommonImageReceiver receiver = gson.fromJson(s, NetworkService.CommonImageReceiver.class);
                    if (view != null) {
                        final String path = Params.TEMP_DIR + util.getRandomString(8) + ".png";
                        util.writeFile(Base64.decode(receiver.image_base64, Base64.DEFAULT), path, () -> loadPortrait(view, path, context));
                        portraitCache.put(user_id, path);
                        //SpUtil.putString(SpUtil.PORTRAIT,user_id,path);
                    }
                }
            });
        else {
            loadPortrait(view, portraitCache.get(user_id), context);
        }
    }

    public void setPortraitUsingCache(int user_id, final SimpleDraweeView view) {
        String path = null;
        //String path = SpUtil.getString(SpUtil.PORTRAIT,user_id,context);
        if (path != null)
            ((Activity) context).runOnUiThread(() -> view.setImageURI(Uri.fromFile(new File(path))));
    }

    private void loadPortrait(final SimpleDraweeView view, final String path, Context context) {
        ((Activity) context).runOnUiThread(() -> view.setImageURI(Uri.fromFile(new File(path))));
    }

    private boolean isTimerRunning;
    TimerTask task;

    public void downloadAssignmentImages(int asm_id, int pos, final SimpleDraweeView draweeView) {
        startDownload(asm_id, pos, draweeView, false);
    }

    public void onRefresh() {
        if (task != null) task.cancel();
        isTimerRunning = false;
        draweeViews.clear();
    }

    Timer timer = new Timer(true);
    List<SimpleDraweeView> draweeViews = new ArrayList<>();

    private void startDownload(int asm_id, int pos, final SimpleDraweeView draweeView, boolean isReload) {
        //this.startDownload(asm_id, pos, draweeView, false);
        if (draweeViews.size() == 0 && !isTimerRunning) {
            task = getTask();
            timer.schedule(task, 3000, 3000);
            isTimerRunning = true;
        }
        if (!isReload) draweeViews.add(draweeView);
        ImageInfo info = new ImageInfo(asm_id, pos);
        draweeView.setTag(info);
        OkHttpUtil okHttp = OkHttpUtil.inst((s) -> {
            if (s.length() == 0 || s.charAt(0) == '<') return;
            String path = Params.TEMP_DIR + util.getRandomString(8) + ".png";
            ImageEntity entity = gson.fromJson(s, ImageEntity.class);
            //Log.d("ss","image download complete");
            util.writeFile(Base64.decode(entity.imgBase64, Base64.DEFAULT), path, () -> ((Activity) context).runOnUiThread(() -> {
                //Log.d("ss","image loaded");
                draweeView.setImageURI(Uri.fromFile(new File(path)));
            }));
        });
        okHttp.addPart("info", gson.toJson(info));
        okHttp.post("query_asm_image");
    }

    List<Integer> cache = new ArrayList<>();

    private TimerTask getTask() {
        return new TimerTask() {
            @Override
            public void run() {
                //draweeViews.stream().filter(draweeView -> draweeView.getDrawable() != null).forEach(draweeViews::remove);
                if (draweeViews.size() == 0) {
                    //timer.cancel();
                    cancel();
                    isTimerRunning = false;
                    return;
                }
                for (int i = 0; i < draweeViews.size(); i++) {
                    SimpleDraweeView draweeView = draweeViews.get(i);
                    if (draweeView.getDrawable() != null) draweeViews.remove(i);
                    else {
                        ImageInfo info = (ImageInfo) draweeView.getTag();
                        if (info != null) {
                            Log.d("ss", "download again");
                            startDownload(info.asm_id, info.pos, draweeView, true);
                        }
                    }
                }
                for (SimpleDraweeView draweeView : draweeViews) {

                }
            }
        };
    }


    private class ImageEntity {
        String imgBase64;
        int pos;
        int asm_id;
    }

    private class ImageInfo {
        int pos;
        int asm_id;

        ImageInfo(int asm_id, int pos) {
            this.pos = pos;
            this.asm_id = asm_id;
        }
    }
}
