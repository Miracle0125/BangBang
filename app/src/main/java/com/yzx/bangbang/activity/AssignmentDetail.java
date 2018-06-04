package com.yzx.bangbang.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yzx.bangbang.adapter.assignment_detail.AssignmentDetailAdapter;
import com.yzx.bangbang.fragment.assignment_detail.FrBids;
import com.yzx.bangbang.fragment.assignment_detail.FrEvaluate;
import com.yzx.bangbang.fragment.assignment_detail.FrOnGoing;
import com.yzx.bangbang.model.Bid;
import com.yzx.bangbang.model.Comment;
import com.yzx.bangbang.R;
import com.yzx.bangbang.model.User;
import com.yzx.bangbang.presenter.AssignmentDetailPresenter;
import com.yzx.bangbang.utils.FrMetro;
import com.yzx.bangbang.utils.Params;
import com.yzx.bangbang.utils.netWork.RetroFunctions;
import com.yzx.bangbang.utils.netWork.Retro;
import com.yzx.bangbang.utils.sql.DAO;
import com.yzx.bangbang.utils.util;
import com.yzx.bangbang.widget.EvaluateView;
import com.yzx.bangbang.widget.Portrait;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import model.Assignment;


public class AssignmentDetail extends RxAppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        check_assignment();
    }

    //rxjava 写这里就是爽
    private void check_assignment() {
        if (assignment == null)
            assignment = (Assignment) getIntent().getSerializableExtra("assignment");
        if (assignment == null) {
            int asm_id = getIntent().getIntExtra("asm_id", -1);
            if (asm_id == -1) return;
            presenter.get_assignment_by_id(asm_id, r -> {
                assignment = r;
                check_assignment();
            });
            return;
        }
        init();
    }


    //if status==going||checking,go to FrOnGoing
    //if status==finished  check if had evaluate
    private void init() {
        fm = new FrMetro(getFragmentManager(), R.id.fragment_container);
        if (assignment.getStatus() != Assignment.STATUS_OPEN
                && assignment.getStatus() != Assignment.STATUS_CLOSED) {
            setContentView(R.layout.fragment_layout);
            if (assignment.getStatus() == Assignment.STATUS_FINISHED) {
                presenter.check_evaluate(assignment.getId(), user.getId(), r -> {
                    if (r == 0) {
                        fm.goToFragment(FrEvaluate.class);
                    } else init_assignment_detail();
                });
            } else {
                fm.goToFragment(FrOnGoing.class);
            }
            return;
        }
        init_assignment_detail();
    }

    private void init_assignment_detail() {
        setContentView(R.layout.asm_detail);
        IS_USER_SAME_WITH_HOST = user.getId() == assignment.getEmployer_id();
        initView();
        refresh();
    }

    private void initView() {
        ButterKnife.bind(this);

        adapter.setAssignment(assignment);
        adapter.setOnClickListener(this::onClick);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        sr.setOnRefreshListener(this::refresh);

        host_name.setText(assignment.getEmployer_name());
        host_portrait.setData(assignment.getEmployer_id());
        //host_portrait.setImageURI(Retro.get_portrait_uri(assignment.getEmployer_id()));
        RetroFunctions.get_user_record(this, assignment.getEmployer_id(),
                r -> evaluate_view.setEvaluate(r.evaluate));

    }

    private void update_button_bid() {
        if (assignment.getStatus() > 0) {
            button_bid.setVisibility(View.GONE);
            return;
        }
        if (IS_USER_SAME_WITH_HOST) {
            button_bid.setText("选标");
            if (assignment.getServants() == 0)
                button_bid.setEnabled(false);
            else button_bid.setEnabled(true);
        }
    }

    //首次调用只获取评论
    public void refresh() {
        presenter.get_assignment_by_id(assignment.getId(), r -> {
            adapter.setAssignment(r);
            assignment = r;
            finish_refresh();
            update_button_bid();
            refresh_bid();
        });
        presenter.get_comment(assignment.getId(), r -> {
            comments = r;
            adapter.setComments(r);
            finish_refresh();
        });
    }

    private void refresh_bid() {
        Consumer<List<Bid>> c = r -> {
            bids = r;
            adapter.setBids(r);
            finish_refresh();
        };
        if (assignment.getStatus() == 0)
            presenter.get_bids(assignment.getId(), c);
        else if (assignment.getStatus() != 3) {
            presenter.get_on_going_bid(assignment.getId(), r -> {
                bids = Collections.singletonList(r);
                adapter.setBids(bids);
                finish_refresh();
            });
        }
    }

    private void finish_refresh() {
        if (++refresh_flag == REFRESH_ALL) {
            sr.setRefreshing(false);
            refresh_flag = 0;
            adapter.notifyDataSetChanged();
            update_button_bid();
        }
    }

    private void show_bid_dialog() {
        View v = getLayoutInflater().inflate(R.layout.asm_detail_dialog_bid, null);
        EditText edit_time = v.findViewById(R.id.edit_time);
        EditText edit_price = v.findViewById(R.id.edit_price);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("竞标");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", null);
        builder.setView(v);
        bid_dialog = builder.create();
        bid_dialog.show();
        bid_dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(view -> {
            String s_price = edit_price.getText().toString();
            String s_time = edit_time.getText().toString();
            if (!util.isNumeric(s_price) || !util.isNumeric(s_time)) {
                toast("输入必须为数字");
                return;
            }
            int time = Integer.valueOf(s_time);
            float price = Float.valueOf(s_price);
            final int MAX_PRICE = 10000000;
            if (time <= 0 || time > 365 || price <= 0 || price > MAX_PRICE) {
                toast("输入不合法");
                return;
            }
            post_bid(time, price);
        });
    }

    private void post_bid(int time, float price) {
        RetroFunctions.get_user_record(this, user.getId(), r ->
                presenter.post_bid(new Bid(assignment.getId(), user.getId(), user.getName(),
                        0, r.evaluate, time, price), r1 -> {
                    toast(Params.request_result[r1]);
                    bid_dialog.cancel();
                }));
    }

    @OnClick({R.id.button_bid})
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_send_comment:

                EditText edit = adapter.getEditText();
                presenter.post_comment(new Comment(0, assignment.getId(),
                        user.getId(), user.getName(),
                        assignment.getEmployer_id(), assignment.getEmployer_name(),
                        edit.getText().toString(), util.getDate(),
                        0, adapter.getComments().size(), 0), r -> toast(Params.request_result[r]));
                edit.getText().clear();
                edit.clearFocus();
                util.toggle_keyboard(this);
                break;
            case R.id.button_bid:
                if (IS_USER_SAME_WITH_HOST)
                    fm.goToFragment(FrBids.class);
                else
                    show_bid_dialog();
                break;
        }
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (FINISH_WHEN_STOP)
            finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detach();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (assignment.getStatus() != 1 && fm != null) {
                if (fm.getCurrent() != null) {
                    fm.removeCurrent();
                    return false;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean FINISH_WHEN_STOP = false;
    private int refresh_flag = 0;
    public static final int REFRESH_WITH_OUT_ASSIGNMENT = 2;
    public static final int REFRESH_ALL = 3;

    public boolean IS_USER_SAME_WITH_HOST;

    public FrMetro fm;
    public Assignment assignment;
    public List<Bid> bids = new ArrayList<>();
    public List<Comment> comments = new ArrayList<>();
    public User user = (User) DAO.query(DAO.TYPE_USER);
    private AlertDialog bid_dialog;
    public AssignmentDetailPresenter presenter = new AssignmentDetailPresenter(this);
    AssignmentDetailAdapter adapter = new AssignmentDetailAdapter(this);
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout sr;
    @BindView(R.id.list)
    RecyclerView list;
    //    @BindView(R.id.place_holder)
//    FrameLayout holder;
//    @BindView(R.id.edit_bar)
//    RelativeLayout edit_bar;
//    @BindView(R.id.edit)
//    EditText edit;
//    @BindView(R.id.button_send_comment)
//    SimpleDraweeView button_send;
    @BindView(R.id.button_bid)
    Button button_bid;
    @BindView(R.id.host_portrait)
    Portrait host_portrait;
    @BindView(R.id.host_name)
    TextView host_name;
    @BindView(R.id.evaluate_view)
    EvaluateView evaluate_view;
}
