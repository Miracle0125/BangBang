package com.yzx.bangbang.presenter;


import android.util.Log;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.model.LatLng;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yzx.bangbang.Activity.Main;
import com.yzx.bangbang.Interface.Network.Main.IMain;
import com.yzx.bangbang.utils.NetWork.Retro;
import java.util.List;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import model.Assignment;

public class MainPresenter implements Main.Listener {
    private Main main;

    public MainPresenter(Main main) {
        this.main = main;
    }

    @Override
    public void getAssignment(Consumer<List<Assignment>> consumer,int mode) {
        Retro.withList().create(IMain.class)
                .get_assignment(mode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(main.<List<Assignment>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void getLocation(Consumer<LatLng> c) {
        AMapLocationClient locationClient = new AMapLocationClient(main);
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        locationClient.setLocationOption(mLocationOption);
        locationClient.setLocationListener((amapLocation) -> {
            if (amapLocation != null)
                if (amapLocation.getErrorCode() == 0) {
                    Flowable.just(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()))
                            .compose(main.<LatLng>bindUntilEvent(ActivityEvent.DESTROY))
                            .subscribe(c);
                } else {
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
        });
        locationClient.startLocation();
    }

    void testPush() {
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
        main = null;
    }
}
