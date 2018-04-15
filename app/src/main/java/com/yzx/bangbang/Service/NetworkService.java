package com.yzx.bangbang.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import com.yzx.bangbang.utils.netWork.WebSocketManager;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class NetworkService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        WebSocketManager.close();
        return super.onUnbind(intent);
    }

    private WebSocketListener webSocketListener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            int n = listeners.beginBroadcast();
            for (int i = 0; i < n; i++) {
                try {
                    listeners.getBroadcastItem(i).on_message(text);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            listeners.finishBroadcast();
        }
    };

    RemoteCallbackList<INetworkObserver> listeners = new RemoteCallbackList<INetworkObserver>();
    private Binder binder = new INetworkService.Stub() {

        @Override
        public void connect(int user_id) throws RemoteException {
            WebSocketManager.connect(user_id, webSocketListener);
        }

        @Override
        public void disconnect() throws RemoteException {
            WebSocketManager.close();
        }

        @Override
        public void register(INetworkObserver l) throws RemoteException {
            listeners.register(l);
        }

        @Override
        public void unregister(INetworkObserver l) throws RemoteException {
            listeners.unregister(l);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        WebSocketManager.close();
    }
    //Messenger messenger = new Messenger(binder);

}
