package com.yzx.bangbang.presenter;

import android.annotation.SuppressLint;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yzx.bangbang.activity.ChatActivity;
import com.yzx.bangbang.adapter.chat.ChatAdapter;
import com.yzx.bangbang.interfaces.network.IChat;
import com.yzx.bangbang.utils.netWork.Retro;
import com.yzx.bangbang.utils.netWork.WebSocketManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import model.ChatRecord;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

@SuppressLint("CheckResult")
public class ChatPresenter {
    private ChatActivity context;
    private int user_id, other_id;
    private ChatAdapter adapter;

    public void get_chat_record(int user_id, int other_id, Consumer<List<ChatRecord>> consumer) {
        Retro.list().create(IChat.class)
                .get_chat_record(user_id, other_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void send(String text) {

    }

    public void refresh() {
        if (other_id != -1)
            get_chat_record(user_id, other_id, r -> {
                adapter.messages = r;
                adapter.notifyDataSetChanged();
            });
    }

    public void onResume() {
      //  WebSocketManager.connect_socket(user_id, WebSocketManager.CHAT_SOCKET, webSocketListener);

    }

    public void onStop() {
        WebSocketManager.close_socket(WebSocketManager.CHAT_SOCKET);
    }

    private WebSocketListener webSocketListener = new WebSocketListener() {
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
        }
    };


    public void post_message() {

    }

    public ChatPresenter(ChatActivity chatActivity) {
        this.context = chatActivity;
        adapter = context.adapter;
        user_id = context.user_id;
        other_id = context.other_id;
    }

    public void detach() {
        context = null;
        adapter = null;
    }

}
