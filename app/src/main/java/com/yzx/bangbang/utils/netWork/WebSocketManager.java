package com.yzx.bangbang.utils.netWork;


import com.yzx.bangbang.utils.Params;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketManager {
    private static WebSocket webSocket;

    public static void connect(int user_id, WebSocketListener listener) {
        Request request = new Request.Builder()
                .url("ws://" + Params.ip + ":8080/BangBang/ws/" + user_id)
                .build();
        webSocket = new OkHttpClient().newWebSocket(request, listener);
    }

    public static void close() {
        if (webSocket != null) {
            webSocket.close(1000, null);
        }
    }


}
