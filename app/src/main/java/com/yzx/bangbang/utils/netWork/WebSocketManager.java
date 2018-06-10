package com.yzx.bangbang.utils.netWork;


import android.util.SparseArray;

import com.yzx.bangbang.utils.Params;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketManager {
    public static final int NOTIFY_SOCKET = 0;
    public static final int CHAT_SOCKET = 1;
    private static SparseArray<WebSocket> socket_map = new SparseArray<>();
    private static final int CODE_NORMAL_CLOSE = 1000;

    public static WebSocket connect_socket(WebSocketListener listener,int what, int... p) {
        Request request = buildRequest(what,p);
        WebSocket webSocket = new OkHttpClient().newWebSocket(request, listener);
        socket_map.put(what, webSocket);
        return webSocket;
    }

    public static void close_socket(int what) {
        WebSocket socket = socket_map.get(what);
        if (socket != null) {
            socket.close(CODE_NORMAL_CLOSE, null);
            socket_map.remove(what);
        }
    }

    private static Request buildRequest(int what, int... p) {
        String ws_servlet[] = {"notify", "chat"};
        StringBuilder sb = new StringBuilder("ws://" + Params.ip + ":8080/BangBang/" + ws_servlet[what]);
        for (int i : p)
            sb.append("/").append(i);
        return new Request.Builder()
                .url(sb.toString())
                .build();
    }
}
