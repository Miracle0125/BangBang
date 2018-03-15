package com.yzx.bangbang.activity;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yzx.bangbang.model.Mysql.ChatRecord;
import com.yzx.bangbang.model.SimpleIndividualInfo;
import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.NetWork.OkHttpUtil;
import com.yzx.bangbang.utils.NetWork.UniversalImageDownloader;
import com.yzx.bangbang.utils.Params;
import com.yzx.bangbang.utils.util;
import com.yzx.bangbang.view.mainView.Portrait;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int ACTION_SEND_SOUND_RECORD = 1;
    View btn_back;
    TextView toolbar_title;
    LinearLayout scroll_view_container;
    View placeHolder, btn_send;
    EditText edit;
    SimpleIndividualInfo obj_info;
    UniversalImageDownloader downloader;
    private int current_floors;
    Gson gson;
    String convr_id;
    private AlertDialog dialog;
    boolean isActivityAlive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        if ((obj_info = getExtras()) == null) return;
        setContentView(R.layout.chat_layout);
        downloader = new UniversalImageDownloader(this);
        gson = new Gson();
        initView();
        initEdit();
        sync();
        getConvrId();
    }

    private SimpleIndividualInfo getExtras() {
        Bundle data = getIntent().getExtras();
        if (data == null) return null;
        return (SimpleIndividualInfo) data.getSerializable("info");
    }

    private void initView() {
        btn_send = findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        placeHolder = findViewById(R.id.bottom_bar_place_holder);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText("与" + obj_info.name + "的私信");
        scroll_view_container = (LinearLayout) findViewById(R.id.scroll_view_container);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
    }

    RelativeLayout.LayoutParams params_edit, params_bar, params_holder;
    int editHeight, wordsOfEdit, diff_editHeight, currentLines;
    RelativeLayout bar;
    TextView bar_tv;

    private void initEdit() {
        diff_editHeight = util.px(45);
        bar = (RelativeLayout) findViewById(R.id.bottom_bar);
        edit = (EditText) findViewById(R.id.edit);
        bar_tv = (TextView) findViewById(R.id.edit_tv);
        params_edit = (RelativeLayout.LayoutParams) edit.getLayoutParams();
        params_bar = (RelativeLayout.LayoutParams) bar.getLayoutParams();
        params_holder = (RelativeLayout.LayoutParams) placeHolder.getLayoutParams();
        edit.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    edit.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                editHeight = edit.getHeight();
            }
        });
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                wordsOfEdit = s.toString().trim().length();
                bar_tv.setText("还能输入" + (120 - wordsOfEdit) + "字");
                if (currentLines != edit.getLineCount()) {
                    diff_editHeight = (edit.getLineCount() - 1) * util.px(12) + editHeight;
                    params_edit.height = diff_editHeight;
                    edit.setLayoutParams(params_edit);
                    params_holder.height = params_bar.height = util.px(75) + (edit.getLineCount() - 1) * util.px(12);
                    bar.setLayoutParams(params_bar);
                    placeHolder.setLayoutParams(params_holder);
                    currentLines = edit.getLineCount();
                }
            }
        });
    }

    //每隔5秒同步服务器信息
    private void sync() {
        new Thread(() -> {
            while (isActivityAlive) {
                downloadData();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void downloadData() {
        OkHttpUtil okhttp = OkHttpUtil.inst((s) -> {
            if (s.length() == 0 || s.charAt(0) == '<') return;
            TempList tempList = gson.fromJson(s, TempList.class);
            if (tempList != null)
                processData(tempList.list);
        });
        okhttp.addPart("sql", "select * from chat_record where user_id = " + Main.user.getId() + " and obj_user_id = " + obj_info.id + " or user_id = " + obj_info.id + " and obj_user_id = " + Main.user.getId() + " order by date asc");
        okhttp.post("query_data_common");
    }

    private void getConvrId() {
        OkHttpUtil okhttp = OkHttpUtil.inst((s) -> {
            if (s.equals("")) {
                convr_id = Main.user.getId() + "," + obj_info.id;
                return;
            }

            convr_id = parseString(s, "convr_id");
/*            if (convr_id == null)
                convr_id = Main.user.get_random_id() + "," + obj_info.id;*/
        });
        okhttp.addPart("sql", "select distinct(convr_id) from `chat_record` where user_id = '" + Main.user.getId() + "' and obj_user_id = '" + obj_info.id + "' or user_id = '" + obj_info.id + "' and obj_user_id = '" + Main.user.getId() + "'");
        okhttp.post("query_data_common");
    }

    private String parseString(String s, String key) {
        if (s.charAt(0) != '{') return null;
        int start;
        int end = start = s.indexOf(key) + key.length() + 3;
        while (end < s.length()) {
            char c = s.charAt(end);
            if (c != '"')
                end++;
            else return s.substring(start, end);
        }
        return null;
    }

    private void processData(List<ChatRecord> list) {
        if (list == null || list.size() == current_floors) return;
        int i = current_floors;
        do {
            ChatRecord cr = list.get(i++);
            runOnUiThread(() -> {
                View v = inflateView(cr);
                scroll_view_container.addView(v);
                current_floors++;
            });
        } while (i < list.size());
    }

    private View inflateView(ChatRecord cr) {
        View v;
        int id;
        if (cr.user_id == Main.user.getId()) {
            v = getLayoutInflater().inflate(R.layout.chat_layout_right, scroll_view_container, false);
            id = Main.user.getId();
        } else {
            v = getLayoutInflater().inflate(R.layout.chat_layout_left, scroll_view_container, false);
            id = obj_info.id;
        }
        if (cr.type == 0) {
            TextView tv = (TextView) v.findViewById(R.id.message);
            tv.setText(cr.message);
            tv = (TextView) v.findViewById(R.id.date);
            tv.setText(util.transform_date(util.getDate(cr.date)));
        } else if (cr.type == 1) {
            v.findViewById(R.id.message).setVisibility(View.GONE);
            v.findViewById(R.id.chat_sound_record_container).setVisibility(View.VISIBLE);
            TextView tv = (TextView) v.findViewById(R.id.chat_sound_record_time);
            tv.setText("" + cr.record_time);
            downloadFile(cr.message, v);
        }
        Portrait portrait = (Portrait) v.findViewById(R.id.host_portrait);
        downloader.downLoadPortrait(id, portrait);
        return v;
    }

    private void addSoundRecordView(String path, int length) {

    }

    private void sendMessage() {
        OkHttpUtil okhttp = OkHttpUtil.inst((s) -> {
            if (s.equals("success")) {
                downloadData();
            }
        });
        okhttp.addPart("sql", "", "insert into chat_record (`user_id`, `user_name`, `obj_user_id`, `obj_user_name`, `message`, `date`,`convr_id`) values ('" + Main.user.getId() + "','" + Main.user.getName() + "','" + obj_info.id + "','" + obj_info.name + "','" + edit.getText().toString() + "','" + util.getDateLong() + "','" + convr_id + "')", OkHttpUtil.MEDIA_TYPE_JSON);
        okhttp.post("update_data_common");
    }

    private void sendSoundRecordInfo(String path, int length) {
        String server_path = getServerPath();
        OkHttpUtil okHttp = OkHttpUtil.inst((s) -> {
            if (s.length() == 0 || s.charAt(0) == '<') return;
            if (s.equals("success")) {
                //downloadData();
                uploadFile(path, server_path);
            }
        });
        okHttp.addPart("sql", "", "insert into chat_record (`user_id`, `user_name`, `obj_user_id`, `obj_user_name`, `message`, `date`,`convr_id`,`type`,`record_time`) values ('" + Main.user.getId() + "','" + Main.user.getName() + "','" + obj_info.id + "','" + obj_info.name + "','" + server_path + "','" + util.getDateLong() + "','" + convr_id + "','" + 1 + "','" + length + "')", OkHttpUtil.MEDIA_TYPE_JSON);
        okHttp.post("update_data_common_with_slashes");
    }

    private void uploadFile(String path, String server_path) {
        OkHttpUtil okhttp = OkHttpUtil.inst((s) -> {
            if (s.length() == 0 || s.charAt(0) == '<') return;
            if (s.equals("success")) {
                runOnUiThread(() -> Toast.makeText(ChatActivity.this, "发送成功", Toast.LENGTH_SHORT).show());
                downloadData();
            }
        });
        okhttp.addPart("file", server_path, new File(path));
        okhttp.post("upload_file_common");
    }

    private String getUrl(String servlet) {
        return "http://" + Params.ip + ":8080/BangBang/" + servlet;
    }

    private String getServerPath() {
        return "D:\\Server\\sound\\" + util.getRandomString(8) + ".3gp";
    }

    private void downloadFile(String path, final View v) {
        //path = addSlashes(path);
        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart("path", path);
        Request request = new Request.Builder().url(getUrl("download_file_common")).post(builder.build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call arg0, IOException arg1) {
            }

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len;
                String path = Params.TEMP_DIR + util.getRandomString(8) + ".3gp";
                FileOutputStream fos = null;
                try {
                    is = arg1.body().byteStream();
                    File file = new File(path);
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    runOnUiThread(() -> {
                        addSoundRecordView(path, 0);
                        View view = v.findViewById(R.id.chat_sound_record_container);
                        view.setOnClickListener(ChatActivity.this);
                        view.setTag(path);
                    });
                } catch (Exception e) {
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    private String addSlashes(String s) {
        String r = "";
        StringBuilder sb = new StringBuilder(r);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\')
                sb.append('\\');
            sb.append(c);
        }
        return sb.toString();
    }

    MediaPlayer player;

    public void playerSound(String path) {
        if (player != null) {
            if (player.isPlaying())
                player.stop();
        }
        player = new MediaPlayer();
        try {
            player.setDataSource(path);
            player.prepare();
            player.start();
        } catch (IOException e) {
        }
    }


    private void showrecordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("准备开始录音");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", null);
        dialog = builder.create();
        dialog.show();
        //dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener((view) -> fulfillAssignment());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                isActivityAlive = false;
                break;
            case R.id.btn_send:
                sendMessage();
                break;
            case R.id.chat_sound_record_container:
                playerSound((String) view.getTag());
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            isActivityAlive = false;
        return super.onKeyDown(keyCode, event);
    }

    public ChatActivityHandler handler = new ChatActivityHandler(this);

    public static class ChatActivityHandler extends Handler {
        private WeakReference<ChatActivity> ref;

        public ChatActivityHandler(ChatActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ChatActivity inst = ref.get();
            if (inst != null)
                inst.handleMsg(msg);
        }
    }

    private void handleMsg(Message msg) {
        switch (msg.what) {
            case ACTION_SEND_SOUND_RECORD:
                sendSoundRecordInfo((String) msg.obj, msg.arg1);
                break;
        }
    }

    private class TempList {
        List<ChatRecord> list;
    }
}
