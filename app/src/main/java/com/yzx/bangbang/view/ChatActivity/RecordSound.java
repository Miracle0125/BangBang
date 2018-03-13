package com.yzx.bangbang.view.ChatActivity;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.yzx.bangbang.Activity.ChatActivity;
import com.yzx.bangbang.Utils.Params;
import com.yzx.bangbang.Utils.util;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Administrator on 2016/12/12.
 */
public class RecordSound extends FrameLayout {
    ChatActivity chatActivity;


    public RecordSound(Context context) {
        this(context, null, 0);
    }

    public RecordSound(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordSound(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        chatActivity = (ChatActivity) context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startRecord();
                break;
            case MotionEvent.ACTION_UP:
                stopRecord();
                break;
            case MotionEvent.ACTION_CANCEL:
                stopRecord();
                break;
        }
        //return super.onTouchEvent(event);
        return true;
    }

    String path;
    long startTime, endTime, length;
    MediaRecorder recorder;

    private void startRecord() {
        Toast.makeText(chatActivity, "录音开始", Toast.LENGTH_SHORT).show();
        Date date0 = new Date();
        startTime = date0.getTime();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        path = Params.TEMP_DIR + util.getRandomString(8) + ".3gp";
        recorder.setOutputFile(path);
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();   // Recording is now started// You can reuse the object by going back to setAudioSource() step
/*        recorder.stop();
        recorder.release();*/
    }


    private void stopRecord() {
        if (recorder == null) return;
        recorder.stop();
        recorder.release();
        Date date = new Date();
        endTime = date.getTime();
        length = endTime - startTime;
        if (length < 400) {
            Toast.makeText(chatActivity, "录音时间太短", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(chatActivity, "录音结束", Toast.LENGTH_SHORT).show();
        postSendMessage();
    }

    private void postSendMessage() {
        Message message = Message.obtain();
        message.what = ChatActivity.ACTION_SEND_SOUND_RECORD;
        message.obj = path;
        message.arg1 = (int) length / 1000;
        chatActivity.handler.sendMessage(message);
    }
}
