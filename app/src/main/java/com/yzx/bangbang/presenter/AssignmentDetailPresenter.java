package com.yzx.bangbang.presenter;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yzx.bangbang.Interface.network.IAssignmentDetail;
import com.yzx.bangbang.activity.AssignmentDetail;
import com.yzx.bangbang.model.Comment;
import com.yzx.bangbang.utils.NetWork.Retro;


import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
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
            public void get_assignment_by_id(int id, Consumer<Assignment> consumer) {
                Retro.inst().create(IAssignmentDetail.class)
                        .get_assignment_by_id(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(context.<Assignment>bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe(consumer);
            }

            @Override
            public void get_comment(int id, Consumer<List<Comment>> consumer) {
                Retro.withList().create(IAssignmentDetail.class)
                        .get_comment(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(context.<List<Comment>>bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe(consumer);
            }

            @Override
            public void get_sub_comment(int id, Consumer<List<Comment>> consumer) {
                Retro.withList().create(IAssignmentDetail.class)
                        .get_sub_comment(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(context.<List<Comment>>bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe(consumer);
            }

            @Override
            public void post_comment(Comment comment,Consumer<Integer> consumer) {
                Retro.inst().create(IAssignmentDetail.class)
                        .post_comment(comment)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(context.<Integer>bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe(consumer);
            }
        };
    }

    public void detach() {
        context = null;
    }

    public interface Listener {
        void get_assignment_by_id(int id, Consumer<Assignment> consumer);

        void get_comment(int id, Consumer<List<Comment>> consumer);

        void get_sub_comment(int id, Consumer<List<Comment>> consumer);

        void post_comment(Comment comment,Consumer<Integer> consumer);
    }
}
