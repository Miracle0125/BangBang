package com.yzx.bangbang.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.yzx.bangbang.adapter.AssignmentDetailAdapter;
import com.yzx.bangbang.model.Comment;
import com.yzx.bangbang.R;
import com.yzx.bangbang.model.User;
import com.yzx.bangbang.presenter.AssignmentDetailPresenter;
import com.yzx.bangbang.utils.FrMetro;
import com.yzx.bangbang.utils.NetWork.NetworkFunctions;
import com.yzx.bangbang.utils.NetWork.Retro;
import com.yzx.bangbang.utils.sql.DAO;
import com.yzx.bangbang.utils.util;
import com.yzx.bangbang.widget.EvaluateView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.Assignment;


public class AssignmentDetail extends RxAppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asm_detail);
        listener = presenter.getListener();
        check_data(assignment);
    }

    //rxjava 写这里就是爽
    private void check_data(Assignment assignment) {

        if (assignment == null)
            this.assignment = (Assignment) getIntent().getSerializableExtra("assignment");
        if (this.assignment == null) {
            int asm_id = getIntent().getIntExtra("asm_id", -1);
            if (asm_id == -1) return;
            listener.get_assignment_by_id(asm_id, this::check_data);
            return;
        }
        init();
    }

    private void init() {
        initView();
        refresh(REFRESH_WITH_OUT_ASSIGNMENT);
    }

    private void initView() {
        ButterKnife.bind(this);

        adapter.setAssignment(assignment);
        adapter.setClickListener(this::onClick);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        sr.setOnRefreshListener(() -> refresh(REFRESH_ALL));

        host_name.setText(assignment.getEmployer_name());
        host_portrait.setImageURI(Retro.get_portrait_uri(assignment.getEmployer_id()));
        NetworkFunctions.get_user_record(this, assignment.getEmployer_id(),
                r -> evaluate_view.setEvaluate(r.evaluate));

//        edit_bar.getViewTreeObserver().addOnPreDrawListener(() -> {
//            LayoutParamUtil.wh(holder, -1, edit_bar.getHeight());
//            return true;
//        });
//        edit.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus) edit.setHint("回复 @" + assignment.getEmployer_name());
//            else edit.setHint("不说点什么吗");
//        });
//        decor_view = getWindow().getDecorView();
//        decor_view.getViewTreeObserver().addOnGlobalLayoutListener(() ->
//                LayoutParamUtil.trans_y(edit_bar, util.get_visible_bottom(decor_view) - Params.screenHeight));
    }

    //首次调用只获取评论
    private void refresh(int mode) {
        if (mode == REFRESH_ALL)
            listener.get_assignment_by_id(assignment.getId(), r -> {
                adapter.setAssignment(r);
                adapter.notifyDataSetChanged();
                finish_refresh(mode);
            });
        listener.get_comment(assignment.getId(), r -> {
            adapter.setComments(r);
            adapter.notifyDataSetChanged();
            finish_refresh(mode);
        });
        listener.get_bids(assignment.getId(), r -> {
            adapter.setBids(r);
            adapter.notifyDataSetChanged();
            finish_refresh(mode);
        });
    }

    private void finish_refresh(int mode) {
        if (++refresh_flag == mode) {
            sr.setRefreshing(false);
            refresh_flag = 0;
        }
    }

    @OnClick({R.id.button_bid})
    public void onClick(View v) {
        String[] res = {"未知错误", "成功"};
        switch (v.getId()) {
            case R.id.button_send_comment:
                User user = (User) DAO.query(DAO.TYPE_USER);
                EditText edit = adapter.getEditText();
                listener.post_comment(new Comment(0, assignment.getId(),
                        user.getId(), user.getName(),
                        assignment.getEmployer_id(), assignment.getEmployer_name(),
                        edit.getText().toString(), util.getDate(),
                        0, adapter.getComments().size(), 0), r -> toast(res[r]));
                edit.getText().clear();
                edit.clearFocus();
                util.toggle_keyboard(this);
                break;
        }
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detach();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fm != null) {
                if (fm.getCurrent() != null) {
                    fm.removeCurrent();
                    return false;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private int refresh_flag = 0;
    private static final int REFRESH_WITH_OUT_ASSIGNMENT = 2;
    private static final int REFRESH_ALL = 3;
    FrMetro fm;
    public Assignment assignment;
    AssignmentDetailPresenter presenter = new AssignmentDetailPresenter(this);
    AssignmentDetailPresenter.Listener listener;
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
    SimpleDraweeView host_portrait;
    @BindView(R.id.host_name)
    TextView host_name;
    @BindView(R.id.evaluate_view)
    EvaluateView evaluate_view;
    View decor_view;
}
