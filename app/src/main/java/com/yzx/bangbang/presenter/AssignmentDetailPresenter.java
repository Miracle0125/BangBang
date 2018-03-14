package com.yzx.bangbang.presenter;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yzx.bangbang.Interface.network.IAssignmentDetail;
import com.yzx.bangbang.activity.AssignmentDetail;
import com.yzx.bangbang.utils.NetWork.Retro;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import model.Assignment;

/**
 * Created by Administrator on 2018/3/14.
 */

public class AssignmentDetailPresenter {

    private AssignmentDetail context;

    public AssignmentDetailPresenter(AssignmentDetail context) {
        this.context = context;
    }

    public Listener getListener() {
        return new Listener() {
            @Override
            public void get_assignment_by_id(int id) {
                Retro.inst().create(IAssignmentDetail.class)
                        .get_assignment_by_id(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(context.<Assignment>bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe();
            }
        };
    }

    public void detach() {
        context = null;
    }

    public interface Listener {
        void get_assignment_by_id(int id);
    }
}
