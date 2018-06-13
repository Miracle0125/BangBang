package com.yzx.bangbang.fragment.assignment_detail;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yzx.bangbang.R;
import com.yzx.bangbang.activity.AssignmentDetail;
import com.yzx.bangbang.model.Bid;
import com.yzx.bangbang.presenter.AssignmentDetailPresenter;
import com.yzx.bangbang.utils.netWork.Retro;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.Assignment;


public class FrOnGoing extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.asm_detail_on_going_layout, container, false);
        init();
        return v;
    }

    private void init() {
        ButterKnife.bind(this, v);
        assignment = context().assignment;
        presenter = context().presenter;
        title.setText(assignment.getTitle());
        content.setText(assignment.getContent());
        refresh();
    }

    private void init_buttons() {
        if (bid.host_id == context().user.getId()) {
            if (assignment.getStatus() == Assignment.STATUS_GOING) {
                status = 2;
                text_button_main.setText("雇主正期待您的优秀工作成果");
                button_main.setText("工作完成,申请验收");
                vice_button_container.setVisibility(View.VISIBLE);
            } else if (assignment.getStatus() == Assignment.STATUS_CHECKING) {
                status = 3;
                text_button_main.setText("请等待雇主验收...");
                button_main.setVisibility(View.INVISIBLE);
            }
        } else {
            if (assignment.getStatus() == Assignment.STATUS_CHECKING) {
                status = 0;
                text_button_main.setText("即将付款给对方，是否满意成果？");
                button_main.setText("验收");
                vice_button_container.setVisibility(View.VISIBLE);
                text_button_vice.setText("我不满意这个成果");
                button_vice.setText("驳回");
            } else if (assignment.getStatus() == Assignment.STATUS_GOING) {
                status = 1;
                button_main.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void refresh() {
        presenter.get_on_going_bid(assignment.getId(), r -> {
            bid = r;
            freelancer_name.setText(bid.host_name);
            freelancer_portrait.setImageURI(Retro.get_portrait_uri(bid.host_id));
            init_buttons();
        });
        presenter.get_on_going_remain_time(assignment.getId(), r -> {
            if (r.substring(0, 2).equals("超出"))
                text_remain_time.setText("超出时间");
            remain_time.setText(r.substring(2, r.length()));
        });
    }

    private void refresh_activity() {
        context().FINISH_WHEN_STOP = true;
        Intent intent = new Intent(context(), AssignmentDetail.class);
        intent.putExtra("asm_id", context().assignment.getId());
        startActivity(intent);
    }

    private AssignmentDetail context() {
        return (AssignmentDetail) getActivity();
    }


    @OnClick({R.id.button_main, R.id.button_vice})
    void click(View v) {
        if (v.getId() == R.id.button_main) {
            if (status == 0) {
                presenter.check_qualified(assignment.getId(), r -> {
                    if (r == 0) {
                        toast("您的余额不足，请充值！");
                    } else refresh_activity();
                });
            } else if (status == 2) {
                presenter.apply_for_checking(assignment.getId(), this::handle_result);
            }
        } else {
            if (status == 0) {
                presenter.check_unqualified(assignment.getId(), this::handle_result);
            } else if (status == 2) {
                presenter.apply_for_checking(assignment.getId(), this::handle_result);
            }
        }
    }

    private void handle_result(int res) {
        Toast.makeText(context(), res == 1 ? "操作成功" : "未知错误", Toast.LENGTH_SHORT).show();
        if (res == 1) refresh();
    }

    private void toast(String s) {
        Toast.makeText(context(), s, Toast.LENGTH_SHORT).show();
    }


    @OnClick(R.id.toolbar_back)
    void back() {
        context().finish();
    }


    View v;
    Assignment assignment;
    Bid bid;
    AssignmentDetailPresenter presenter;
    private int status = 0;
    @BindView(R.id.freelancer_portrait)
    SimpleDraweeView freelancer_portrait;
    @BindView(R.id.freelancer_name)
    TextView freelancer_name;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.remain_time)
    TextView remain_time;
    @BindView(R.id.text_remain_time)
    TextView text_remain_time;
    @BindView(R.id.text_button_main)
    TextView text_button_main;
    @BindView(R.id.button_main)
    Button button_main;
    @BindView(R.id.vice_button_container)
    LinearLayout vice_button_container;
    @BindView(R.id.button_vice)
    TextView button_vice;
    @BindView(R.id.text_button_vice)
    TextView text_button_vice;
}
