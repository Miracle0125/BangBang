package com.yzx.bangbang.fragment.assignment_detail;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yzx.bangbang.R;
import com.yzx.bangbang.activity.AssignmentDetail;
import com.yzx.bangbang.model.Bid;
import com.yzx.bangbang.model.Evaluate;
import com.yzx.bangbang.model.User;
import com.yzx.bangbang.presenter.AssignmentDetailPresenter;
import com.yzx.bangbang.utils.util;
import com.yzx.bangbang.widget.EvaluateSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.Assignment;

public class FrEvaluate extends Fragment {
    View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.asm_detail_evaluate_layout, container, false);
        ButterKnife.bind(this, v);
        init();
        return v;
    }

    private void init() {
        assignment = context().assignment;
        presenter = context().presenter;
        presenter.get_on_going_bid(assignment.getId(), r -> on_going_bid = r);
    }

    private Evaluate build_evaluate_entity(Bid bid, Assignment assignment) {
        User user = context().user;
        Evaluate evaluate = new Evaluate(
                assignment.getId(),
                bid.host_id,
                user.getId(),
                evaluate_seek_bar.get_evaluate(),
                user.getName(),
                content.getText().toString(),
                util.getDate());
        if (user.getId() == bid.host_id) {
            evaluate.relate_user = assignment.getEmployer_id();
            evaluate.host_id = bid.host_id;
            evaluate.host_name = bid.host_name;
        }
        return evaluate;
    }

    @OnClick(R.id.button_main)
    void commit() {
        if (on_going_bid == null)
            return;
        Evaluate evaluate = build_evaluate_entity(on_going_bid, assignment);
        presenter.evaluate(evaluate, this::on_finish_evaluate);
    }

    private void on_finish_evaluate(int r) {
        util.toast_binary(context(), r);
        if (r == 0) return;
        context().FINISH_WHEN_STOP = true;
        Intent intent = new Intent(context(), AssignmentDetail.class);
        intent.putExtra("assignment", assignment);
        context().startActivity(intent);
    }

    private AssignmentDetail context() {
        return (AssignmentDetail) getActivity();
    }

    Assignment assignment;
    Bid on_going_bid;
    AssignmentDetailPresenter presenter;
    @BindView(R.id.content)
    EditText content;
    @BindView(R.id.target_name)
    TextView target_name;
    @BindView(R.id.target_portrait)
    SimpleDraweeView target_portrait;
    @BindView(R.id.evaluate_seek_bar)
    EvaluateSeekBar evaluate_seek_bar;

}
