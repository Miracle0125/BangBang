package com.yzx.bangbang.presenter;


import android.util.Log;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yzx.bangbang.Activity.Main;
import com.yzx.bangbang.Interface.Network.Main.IMain;
import com.yzx.bangbang.Utils.NetWork.Retro;
import com.yzx.bangbang.module.receiver.LAssignment;
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
    public void getAssignment(Consumer<LAssignment> consumer) {
        Retro.inst().create(IMain.class)
                .get_assignment()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(main.<LAssignment>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
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

    public void detach(){
        main=null;
    }
}
