package com.yzx.bangbang.presenter;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yzx.bangbang.activity.BrowseAssignment;
import com.yzx.bangbang.interfaces.network.IMain;
import com.yzx.bangbang.utils.netWork.Retro;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import model.Assignment;

/**
 * Created by Administrator on 2018/4/15.
 */

public class BrowsePresenter {
    private BrowseAssignment context;

    public BrowsePresenter(BrowseAssignment c) {
        context = c;
    }

    public void getAssignment(int mode, int what, Consumer<List<Assignment>> consumer) {
        Retro.withList().create(IMain.class)
                .get_assignment(mode, what)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void detach() {
        context = null;
    }

}
