package com.yzx.bangbang.presenter;

import android.annotation.SuppressLint;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yzx.bangbang.interfaces.network.IMain;
import com.yzx.bangbang.utils.netWork.Retro;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@SuppressLint({"Registered","CheckResult"})
public class HomepageProxy extends RxAppCompatActivity {






    public void add_to_contact(int owner, int person_id, Consumer<Integer> consumer) {
        Retro.list().create(IMain.class)
                .add_to_contact(owner,person_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }
}
