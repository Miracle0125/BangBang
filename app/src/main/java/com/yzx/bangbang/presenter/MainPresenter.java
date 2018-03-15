package com.yzx.bangbang.presenter;


import android.util.Log;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.model.LatLng;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.Interface.network.IMain;
import com.yzx.bangbang.utils.NetWork.Retro;
import com.yzx.bangbang.utils.sql.SpUtil;

import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import model.Assignment;

public class MainPresenter {
    private Main main;

    public Listener getListener() {
        return new Listener() {
            @Override
            public void getAssignment(Consumer<List<Assignment>> consumer, int mode) {
                Retro.withList().create(IMain.class)
                        .get_assignment(mode)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(main.<List<Assignment>>bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe(consumer);
            }

            @Override
            public void init() {
                getLocation();
                //testPush();
            }
        };
    }


    public MainPresenter(Main main) {
        this.main = main;
        //this.listener=listener;
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
        main = null;
    }

    public interface Listener {
        void getAssignment(Consumer<List<Assignment>> consumer, int mode);

        void init();
    }
}
