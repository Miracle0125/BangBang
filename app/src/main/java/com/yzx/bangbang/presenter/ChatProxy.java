package com.yzx.bangbang.presenter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.google.gson.Gson;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yzx.bangbang.activity.ChatActivity;
import com.yzx.bangbang.adapter.chat.ChatAdapter;
import com.yzx.bangbang.interfaces.network.IChat;
import com.yzx.bangbang.utils.netWork.Retro;
import com.yzx.bangbang.utils.netWork.WebSocketManager;
import com.yzx.bangbang.utils.util;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import model.ChatRecord;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


/**
 * 尝试用继承的形式使用presenter
 */
@SuppressLint({"Registered", "CheckResult"})
public class ChatProxy extends RxAppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        webSocket = WebSocketManager.connect_socket(webSocketListener, WebSocketManager.CHAT_SOCKET, user_id, other_id);
        refresh();
    }

    @Override
    protected void onStop() {
        super.onStop();
        WebSocketManager.close_socket(WebSocketManager.CHAT_SOCKET);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void refresh() {
        get_chat_record(user_id, other_id, r -> {
            adapter.messages = r;
            adapter.notifyDataSetChanged();
        });
    }

    private WebSocketListener webSocketListener = new WebSocketListener() {
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            if (check_if_result_code(text)) return;
            ChatRecord chatRecord = new Gson().fromJson(text, ChatRecord.class);
            adapter.messages.add(chatRecord);
            adapter.notifyDataSetChanged();
        }
    };

    private boolean check_if_result_code(String s) {
        if (s.charAt(0) != '/') return false;
        if (s.equals(code_200)) {
            refresh();
            chatActivity.clear_edit();
            return true;
        }
        return false;
    }

    public void send(String text) {
        webSocket.send(text);
    }

    public void get_chat_record(int user_id, int other_id, Consumer<List<ChatRecord>> consumer) {
        Retro.list().create(IChat.class)
                .get_chat_record(user_id, other_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    private static final String code_200 = "///200";
    public ChatActivity chatActivity;
    public int user_id = util.get_user_id(), other_id;
    public ChatAdapter adapter;
    WebSocket webSocket;
}
