package com.yzx.bangbang.presenter;


import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.model.LatLng;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yzx.bangbang.Service.INetworkObserver;
import com.yzx.bangbang.Service.INetworkService;
import com.yzx.bangbang.Service.NetworkService;
import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.interfaces.network.IMain;
import com.yzx.bangbang.model.Contact;
import com.yzx.bangbang.utils.netWork.Retro;
import com.yzx.bangbang.utils.sql.SpUtil;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import model.Assignment;
import model.ChatRecord;

@SuppressLint("CheckResult")
public class MainPresenter {
    private Main main;
    private int user_id;
    private boolean is_service_bind;

    public void init(int user_id) {
        this.user_id = user_id;
        AsyncTask.execute(() -> {
            online_user();
            getLocation();
            //check_notify();
        });
        //testPush();
    }

//    public void check_notify() {
//        AsyncTask.execute(() -> begin_check_notify(r -> {
//            SA.insert(r, SA.TYPE_NOTIFIES);
//            show_notify(r);
//        }));
//    }
//
//    private void begin_check_notify(Consumer<List<Notify>> consumer) {
//        Retro.list().create(IMain.class)
//                .get_notify(user_id)
////                .subscribeOn(Schedulers.io())
////                .observeOn(AndroidSchedulers.mainThread())
//                .compose(main.bindUntilEvent(ActivityEvent.DESTROY))
//                .subscribe(consumer);
//    }
//
//    private void show_notify(List<Notify> notifies) {
//        int not_read = 0;
//        for (int i = 0; i < notifies.size(); i++)
//            if (notifies.get(i).read == 0) not_read++;
//        if (not_read == 0) return;
//        Notification notification = new Notification.Builder(main)
//                .setSmallIcon(R.drawable.main_icon_portrait)
//                .setContentTitle(not_read + "条新消息")
//                .build();
//        NotificationManager manager = (NotificationManager) main.getSystemService(Context.NOTIFICATION_SERVICE);
//        if (manager != null)
//            manager.notify(1, notification);
//    }

    private void online_user() {
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                iNetworkService = INetworkService.Stub.asInterface(service);
                try {
                    iNetworkService.connect(user_id);
                    iNetworkService.register(iNetworkObserver);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                try {
                    iNetworkService.disconnect();
                    iNetworkService.unregister(iNetworkObserver);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
        is_service_bind = true;
        main.bindService(new Intent(main, NetworkService.class), connection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection connection;
    private INetworkService iNetworkService;
    private INetworkObserver iNetworkObserver = new INetworkObserver.Stub() {
        @Override
        public void on_message(String s) throws RemoteException {
//            if (util.isNumeric(s)) {
//                if (s.equals(Params.NOTIFY_ARRIVE)) {
//                    check_notify();
//                }
//            }
        }
    };


    public void get_user_assignment(int user_id,int filter, Consumer<List<Assignment>> consumer) {
        Retro.list().create(IMain.class)
                .get_user_assignment(user_id,filter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(main.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void get_contacts(int user_id, Consumer<List<Contact>> consumer) {
        Retro.list().create(IMain.class)
                .get_contacts(user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(main.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void get_recent_conversations(int user_id, Consumer<List<ChatRecord>> consumer){
        Retro.list().create(IMain.class)
                .get_recent_conversations(user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(main.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void get_balance(int user_id, Consumer<Integer> consumer){
        Retro.single().create(IMain.class)
                .get_balance(user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(main.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }




    public MainPresenter(Main main) {
        this.main = main;
    }

    private void getLocation() {
        AMapLocationClient locationClient = new AMapLocationClient(main);
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        locationClient.setLocationOption(mLocationOption);
        locationClient.setLocationListener((amapLocation) -> {
            if (amapLocation != null)
                if (amapLocation.getErrorCode() == 0) {
                    SpUtil.putObject(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()), SpUtil.LATLNG);
                } else {
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
        });
        locationClient.startLocation();
    }

    private void testPush() {
        XGPushConfig.enableDebug(main, true);
        XGPushManager.registerPush(main, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                //token在设备卸载重装的时候有可能会变
                Log.d("TPush", "注册成功，设备token为：" + data);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
            }
        });
        XGPushManager.bindAccount(main.getApplicationContext(), "XINGE");
        XGPushManager.setTag(main, "XINGE");
    }

    public void detach() {
        if (is_service_bind && main != null) {
            main.unbindService(connection);
            is_service_bind = false;
        }
        main = null;
    }
}
