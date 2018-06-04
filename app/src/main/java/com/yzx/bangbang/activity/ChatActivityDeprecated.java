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

import com.google.gson.Gson;
import com.yzx.bangbang.model.Mysql.ChatRecordDeprecated;
import com.yzx.bangbang.model.SimpleIndividualInfo;
import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.netWork.UniversalImageDownloader;
import com.yzx.bangbang.utils.Params;
import com.yzx.bangbang.utils.util;

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


public class ChatActivityDeprecated extends AppCompatActivity implements View.OnClickListener {
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
        setContentView(R.layout.chat_layout1);
        downloader = new UniversalImageDownloader(this);
        gson = new Gson();
        initView();
        initEdit();
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
        btn_back = findViewById(R.id.button_back);
        btn_back.setOnClickListener(this);
    }

    RelativeLayout.LayoutParams params_edit, params_bar, params_holder;
    int editHeight, wordsOfEdit, diff_editHeight, currentLines;
    RelativeLayout bar;
    TextView bar_tv;

    private void initEdit() {
        diff_editHeight = util.dp2px(45);
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
                    diff_editHeight = (edit.getLineCount() - 1) * util.dp2px(12) + editHeight;
                    params_edit.height = diff_editHeight;
                    edit.setLayoutParams(params_edit);
                    params_holder.height = params_bar.height = util.dp2px(75) + (edit.getLineCount() - 1) * util.dp2px(12);
                    bar.setLayoutParams(params_bar);
                    placeHolder.setLayoutParams(params_holder);
                    currentLines = edit.getLineCount();
                }
            }
        });
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


    private void addSoundRecordView(String path, int length) {

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
                        view.setOnClickListener(ChatActivityDeprecated.this);
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
            case R.id.button_back:
                finish();
                isActivityAlive = false;
                break;
            case R.id.btn_send:
                //sendMessage();
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
        private WeakReference<ChatActivityDeprecated> ref;

        public ChatActivityHandler(ChatActivityDeprecated activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ChatActivityDeprecated inst = ref.get();
            if (inst != null)
                inst.handleMsg(msg);
        }
    }

    private void handleMsg(Message msg) {
        switch (msg.what) {
            case ACTION_SEND_SOUND_RECORD:
                //sendSoundRecordInfo((String) msg.obj, msg.arg1);
                break;
        }
    }

    private class TempList {
        List<ChatRecordDeprecated> list;
    }
}
