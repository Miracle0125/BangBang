package com.yzx.bangbang.presenter;

import com.google.gson.Gson;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yzx.bangbang.interfaces.network.IAssignmentDetail;
import com.yzx.bangbang.activity.AssignmentDetail;
import com.yzx.bangbang.model.Bid;
import com.yzx.bangbang.model.Comment;
import com.yzx.bangbang.model.Evaluate;
import com.yzx.bangbang.utils.netWork.Retro;


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

    public void get_assignment_by_id(int id, Consumer<Assignment> consumer) {
        Retro.single().create(IAssignmentDetail.class)
                .get_assignment_by_id(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void get_comment(int id, Consumer<List<Comment>> consumer) {
        Retro.list().create(IAssignmentDetail.class)
                .get_comment(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void get_sub_comment(int id, Consumer<List<Comment>> consumer) {
        Retro.list().create(IAssignmentDetail.class)
                .get_sub_comment(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void post_comment(Comment comment, Consumer<Integer> consumer) {
        Retro.single().create(IAssignmentDetail.class)
                .post_comment(new Gson().toJson(comment))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void get_bids(int id, Consumer<List<Bid>> consumer) {
        Retro.list().create(IAssignmentDetail.class)
                .get_bids(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void post_bid(Bid bid, Consumer<Integer> consumer) {
        Retro.single().create(IAssignmentDetail.class)
                .post_bid(new Gson().toJson(bid))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void get_on_going_bid(int asm_id, Consumer<Bid> consumer) {
        Retro.list().create(IAssignmentDetail.class)
                .get_on_going_bid(asm_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void get_on_going_remain_time(int asm_id, Consumer<String> consumer) {
        Retro.str().create(IAssignmentDetail.class)
                .get_on_going_remain_time(asm_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void apply_for_checking(int asm_id, Consumer<Integer> consumer) {
        Retro.single().create(IAssignmentDetail.class)
                .apply_for_checking(asm_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void check_qualified(int asm_id, Consumer<Integer> consumer) {
        Retro.single().create(IAssignmentDetail.class)
                .check_qualified(asm_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void check_unqualified(int asm_id, Consumer<Integer> consumer) {
        Retro.single().create(IAssignmentDetail.class)
                .check_unqualified(asm_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void evaluate(Evaluate evaluate, Consumer<Integer> consumer) {
        Retro.single().create(IAssignmentDetail.class)
                .evaluate(new Gson().toJson(evaluate))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void check_evaluate(int asm_id, int user_id, Consumer<Integer> consumer) {
        Retro.single().create(IAssignmentDetail.class)
                .check_evaluate(asm_id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }


    public void detach() {
        context = null;
    }
}
