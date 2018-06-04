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

    public static WebSocket connect_socket(int user_id, int what, WebSocketListener listener) {
        Request request = buildRequest(user_id,what);
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

    private static Request buildRequest(int user_id, int what) {
        String ws_servlet[] = {"notify", "chat"};
        return new Request.Builder()
                .url("ws://" + Params.ip + ":8080/BangBang/" + ws_servlet[what] + "/" + user_id)
                .build();
    }
}
