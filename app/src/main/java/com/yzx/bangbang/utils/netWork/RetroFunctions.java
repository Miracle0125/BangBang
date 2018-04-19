package com.yzx.bangbang.utils.netWork;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yzx.bangbang.interfaces.network.ICommon;
import com.yzx.bangbang.model.UserRecord;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Administrator on 2018/3/29.
 */

public class RetroFunctions {

    public static void get_user_record(RxAppCompatActivity context, int id, Consumer<UserRecord> consumer) {
        Retro.single().create(ICommon.class)
                .get_user_record(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.<UserRecord>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }
}
