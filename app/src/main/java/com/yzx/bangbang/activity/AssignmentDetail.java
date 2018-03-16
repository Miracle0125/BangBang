package com.yzx.bangbang.activity;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yzx.bangbang.adapter.AssignmentDetailAdapter;
import com.yzx.bangbang.fragment.AD.FrReplierInfo;
import com.yzx.bangbang.model.Comment;
import com.yzx.bangbang.model.Msg;
import com.yzx.bangbang.model.ReplierInfo;
import com.yzx.bangbang.R;
import com.yzx.bangbang.model.User;
import com.yzx.bangbang.presenter.AssignmentDetailPresenter;
import com.yzx.bangbang.utils.FrMetro;
import com.yzx.bangbang.utils.NetWork.UniversalImageDownloader;
import com.yzx.bangbang.utils.NetWork.OkHttpUtil;
import com.yzx.bangbang.utils.Params;
import com.yzx.bangbang.utils.sql.DAO;
import com.yzx.bangbang.utils.ui.LayoutParamUtil;
import com.yzx.bangbang.utils.util;
import com.yzx.bangbang.view.ad.AdLayout;
import com.yzx.bangbang.view.ad.CommentView;
import com.yzx.bangbang.view.ad.SubCommentView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.Assignment;


public class AssignmentDetail extends RxAppCompatActivity implements CommentView.Listener, SubCommentView.Listener {
    public Assignment assignment;
    TextView replier_info_text, btn_clct_tv;
    View decorView, bottomBar, placeHolder, replier_info_bar, btn_clct, btn_back;
    SimpleDraweeView portrait;
    private static final int ACCEPT_ASSIGN = 0;
    private static final int POST_COMMENT = 1;
    public static final int ACTION_REMOVE_FRAGMENT = 2;
    private static int state;
    //private static int userState;
    //private static final int ALREADY_ACCEPTED = 10;
    //private static final int IS_ASSIGN_OWNER = 11;
    private boolean isOwner;
    private boolean already_accepted, already_collected;
    Gson gson = new Gson();
    public UniversalImageDownloader downloader;
    public boolean chosen;
    public boolean fulfill;
    public List<ReplierInfo> replierInfoList;
    AssignmentDetailPresenter presenter = new AssignmentDetailPresenter(this);
    AssignmentDetailPresenter.Listener listener;
    AssignmentDetailAdapter adapter = new AssignmentDetailAdapter(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asm_detail);
        check_data(assignment);
    }

    AdLayout adLayout;

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
        listener = presenter.getListener();
        initView();
        refresh(REFRESH_ONLY_COMMENT);
    }

    Button btn_subscribe;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout sr;
    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.place_holder)
    FrameLayout holder;
    @BindView(R.id.edit_bar)
    RelativeLayout edit_bar;
    @BindView(R.id.edit)
    EditText edit;
    @BindView(R.id.button_send)
    SimpleDraweeView button_send;

    View decor_view;

    private void initView() {
        ButterKnife.bind(this);
        adapter.setAssignment(assignment);
        adapter.setClickListener(this::onClick);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        edit_bar.getViewTreeObserver().addOnPreDrawListener(() -> {
            LayoutParamUtil.wh(holder, -1, edit_bar.getHeight());
            return true;
        });
        decor_view = getWindow().getDecorView();
        decor_view.getViewTreeObserver().addOnGlobalLayoutListener(() ->
                LayoutParamUtil.trans_y(edit_bar, util.get_visible_bottom(decor_view) - Params.screenHeight));
        sr.setOnRefreshListener(() -> refresh(REFRESH_ALL));
    }


    private int refresh_flag = 0;
    private static final int REFRESH_ONLY_COMMENT = 1;
    private static final int REFRESH_ALL = 2;

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
    }

    private void finish_refresh(int mode) {
        if (++refresh_flag == mode) {
            sr.setRefreshing(false);
            refresh_flag = 0;
        }
    }

    Map<Integer, ViewGroup> commentMap;
    Map<Integer, ViewGroup> commentViewTreeMap;
    LinearLayout container;

    //List<ViewGroup> comments;
    private void processInputStream(List<Comment> list, int floors) {
        if (commentMap != null) {
            commentMap.clear();
        } else
            commentMap = new HashMap<>();
        commentViewTreeMap = new HashMap<>();

        if (container == null)
            container = (LinearLayout) findViewById(R.id.ad_comment_container);
        else if (container.getChildCount() != 0)
            container.removeAllViews();
        CommentInflater inflater = new CommentInflater(this, (ViewGroup) findViewById(R.id.ad_comment_container));
        int floorsCache = floors;
        for (int i = 0; i < floorsCache; i++) {
            Comment comment = list.get(i);
            CommentView commentView = null;
            if (comment.parent == 0) {
                commentView = (CommentView) inflater.inflate(comment);
                if (comment.floors != 0)
                    floorsCache += comment.floors;
                commentMap.put(comment.id, commentView);
                container.addView(commentView);
            } else {
                //SubCommentView subCommentView = (SubCommentView) inflater.initView(comment,commentMap.get(comment.parent));
                commentView = (CommentView) inflater.inflate(comment, commentMap.get(comment.parent));
                commentMap.get(comment.parent).addView(commentView);
            }
            if (commentView == null)
                return;
            commentView.registerListener(this);
            commentView.setParams(comment);
            downloader.downLoadPortrait(comment.poster_id, (SimpleDraweeView) commentView.findViewById(R.id.ad_comment_portrait));
        }
    }

    RelativeLayout.LayoutParams params_edit, params_bar, params_holder;
    int editHeight, wordsOfEdit, diff_editHeight, currentLines;
    RelativeLayout bar;
    TextView bar_tv;

    private void initEdit() {

        diff_editHeight = util.px(45);
        bar = (RelativeLayout) findViewById(R.id.ad_bottom_bar);
        edit = (EditText) findViewById(R.id.edit);
        bar_tv = (TextView) findViewById(R.id.edit_tv);
        edit.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    edit.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                editHeight = edit.getHeight();
            }
        });
        params_edit = (RelativeLayout.LayoutParams) edit.getLayoutParams();
        params_bar = (RelativeLayout.LayoutParams) bar.getLayoutParams();
        params_holder = (RelativeLayout.LayoutParams) placeHolder.getLayoutParams();
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                wordsOfEdit = s.toString().trim().length();
                bar_tv.setText("还能输入" + (120 - wordsOfEdit) + "字");
                if (currentLines != edit.getLineCount()) {
                    diff_editHeight = (edit.getLineCount() - 1) * util.px(12) + editHeight;
                    params_edit.height = diff_editHeight;
                    edit.setLayoutParams(params_edit);
                    params_holder.height = params_bar.height = util.px(75) + (edit.getLineCount() - 1) * util.px(12);
                    bar.setLayoutParams(params_bar);
                    placeHolder.setLayoutParams(params_holder);
                    currentLines = edit.getLineCount();
                }
            }
        });
    }

    private void OnStartInput() {
        params_edit.bottomMargin = util.px(30);
        params_holder.height = params_bar.height = diff_editHeight + util.px(30);
        edit.setLayoutParams(params_edit);
        bar.setLayoutParams(params_bar);
        placeHolder.setLayoutParams(params_holder);
        bar_tv.setVisibility(View.VISIBLE);
        if (already_accepted) {
            btn_subscribe.setTextColor(getResources().getColor(R.color.white));
            btn_subscribe.setBackgroundColor(Color.parseColor("#2196f3"));
        }
        btn_subscribe.setText("发送");
        state = POST_COMMENT;
    }

    private void OnCancelInput() {
        params_edit.bottomMargin = 0;
        params_holder.height = params_bar.height = diff_editHeight;
        edit.setLayoutParams(params_edit);
        bar.setLayoutParams(params_bar);
        placeHolder.setLayoutParams(params_holder);
        bar_tv.setVisibility(View.INVISIBLE);
        if (already_accepted) {
            btn_subscribe.setBackgroundColor(getResources().getColor(R.color.cloud_gray));
            btn_subscribe.setTextColor(getResources().getColor(R.color.gray));
            btn_subscribe.setText("已接受");
        } else {
            btn_subscribe.setText("接受需求");
        }
        state = ACCEPT_ASSIGN;
    }

    private void updateButtonView() {
        if (btn_subscribe != null)
            if (state == ACCEPT_ASSIGN && already_accepted) {
                btn_subscribe.setBackgroundColor(getResources().getColor(R.color.cloud_gray));
                btn_subscribe.setTextColor(getResources().getColor(R.color.gray));
                btn_subscribe.setText("已接受");
            }
    }

    private void updateReplierInfoBar(List<ReplierInfo> list) {
        if (replier_info_bar == null || replier_info_text == null) return;
        int index = 0;
        for (ReplierInfo info : list) {
            SimpleDraweeView view;
            switch (index) {
                case 0:
                    view = (SimpleDraweeView) findViewById(R.id.replier_info_bar_image0);
                    view.setVisibility(View.VISIBLE);
                    downloader.downLoadPortrait(info.user_id, view);
                    break;
                case 1:
                    view = (SimpleDraweeView) findViewById(R.id.replier_info_bar_image1);
                    view.setVisibility(View.VISIBLE);
                    downloader.downLoadPortrait(info.user_id, view);
                    //downloader.downLoadPortrait(info.user_id, (SimpleDraweeView) findViewById(R.id.replier_info_bar_image1));
                    break;
                case 2:
                    view = (SimpleDraweeView) findViewById(R.id.replier_info_bar_image2);
                    view.setVisibility(View.VISIBLE);
                    downloader.downLoadPortrait(info.user_id, view);
                    //downloader.downLoadPortrait(info.user_id, (SimpleDraweeView) findViewById(R.id.replier_info_bar_image2));
                    break;
            }
            index++;
        }
        if (index == 0) return;
        replier_info_text.setText("已有" + index + "个帮众");
    }

    @OnClick({R.id.button_send})
    public void onClick(View v) {
        String[] res = {"发送失败", "发送成功"};
        switch (v.getId()) {
            case R.id.button_send:
                User user = (User) DAO.query(DAO.TYPE_USER);
                listener.post_comment(new Comment(0, assignment.getId(),
                        user.getId(), user.getName(),
                        assignment.getEmployer_id(), assignment.getEmployer_name(),
                        edit.getText().toString(), util.getDate(),
                        0, adapter.getComments().size(), 0), r -> toast(res[r]));
                break;
        }
    }


    private int parseString(String s) {
        if (s.charAt(0) != '{') return -1;
        int end = 22;
        while (Character.isDigit(s.charAt(end)))
            end++;
        return Integer.valueOf(s.substring(22, end));
    }

    //@SuppressLint("HandlerLeak")
    Dialog dialog;
    String poster_name;
    int poster_id, parent_id;
    boolean saveFlag;
    boolean isSubComment;

    @Override
    public void onItemTouched(String poster_name, int poster_id, int id, boolean isSubComment) {
        if (dialog == null)
            return;
        saveFlag = false;
        inst().poster_name = poster_name;
        inst().poster_id = poster_id;
        inst().parent_id = id;
        inst().isSubComment = isSubComment;
        dialog.show();
    }

    public AssignmentDetail inst() {
        return AssignmentDetail.this;
    }


    private Bitmap getCache() {
        if (adLayout == null)
            return null;
        adLayout.buildDrawingCache();
        adLayout.setDrawingCacheEnabled(true);
        Bitmap bmp = Bitmap.createBitmap(adLayout.getDrawingCache(), 0, 0, adLayout.getWidth(), adLayout.getHeight());
        adLayout.destroyDrawingCache();
        return bmp;
    }

    @Override
    public void onSubCommentTouched(String poster_name, int poster_id, int id) {

    }

    private void startPost(String json) {
        OkHttpUtil okHttp = OkHttpUtil.inst(s -> {
            if (s.equals("success")) {
                runOnUiThread(() -> Toast.makeText(AssignmentDetail.this, "评论成功", Toast.LENGTH_SHORT).show());
                //getComment();
            } else if (s.equals("failed")) {
                runOnUiThread(() -> Toast.makeText(AssignmentDetail.this, "发送失败", Toast.LENGTH_SHORT).show());
            }
        });
        okHttp.addPart("comment", null, json, OkHttpUtil.MEDIA_TYPE_JSON);
        okHttp.post("post_comment");
    }

    //接收需求后更新数据库消息表，增加需求的回复者数量,更新UserRecord,更新Replier_info
    private void requestAcceptAsm() {
        if (already_accepted) return;
        if (isOwner) {
            Toast.makeText(AssignmentDetail.this, "你是发布人啊(。?`ω′?)", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(() -> {
            Msg msg = new Msg(assignment.getEmployer_name(), Main.user.getName(), assignment.getTitle(), util.getDate(), assignment.getEmployer_id(), assignment.getEmployer_id(), Main.user.getId(), assignment.getId(), 0);
            ReplierInfo info = new ReplierInfo(Main.user.getName(), assignment.getTitle(), null, util.getDate(), Main.user.getId(), assignment.getId(), assignment.getPrice());
            OkHttpUtil okhttp = OkHttpUtil.inst(s -> {
                if (s.equals("update_success")) ;
                //inst().handler.sendEmptyMessage(NetworkService.KEY_INSERT_MSG);
            });
            okhttp.addPart("accept_assignment", "message", gson.toJson(msg), OkHttpUtil.MEDIA_TYPE_JSON);
            okhttp.addPart("accept_assignment", "replier_info", gson.toJson(info), OkHttpUtil.MEDIA_TYPE_JSON);
            okhttp.post("accept_assignment");
        }).start();
    }

    FrMetro fm;

    private void showReplierInfo() {
        if (fm == null) {
            fm = new FrMetro(getFragmentManager(), R.id.fragment_container);
        }
        fm.goToFragment(FrReplierInfo.class);
        updateFragmentBackground();
        //(fm.getCurrent()).setAlpha(1);
    }

    private void updateFragmentBackground() {
        if (fm == null) return;
        if (fm.getCurrent() == null)
            findViewById(R.id.fragment_container).setBackgroundColor(Color.parseColor("#00FAFAFA"));
        else
            findViewById(R.id.fragment_container).setBackgroundColor(getResources().getColor(R.color.opaque));
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fm != null) {
                if (fm.getCurrent() != null) {
                    fm.removeCurrent();
                    updateFragmentBackground();
                    return false;
                }
            }
            //ActivityManager.getManager().onFinish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /*    public static Class Comments{
            List<Comment> list;
        }*/
    public static class Comments {
        List<Comment> list;
        int floors;
    }

    private class ReplierInfoList {
        List<ReplierInfo> list;
    }

    private class TempList {
        List<TempAssignment> list;

        private class TempAssignment {
            public String employer_name, title, content, date;
            public int id, repliers, employer_id, images, floors;
            public float price;
            public double latitude, longitude;

            public Assignment toAssignment() {
                // return new Assignment(employer_name, title, content, date, id, repliers, employer_id, images, price);
                return null;
            }
        }
    }

    private static class CommentInflater {
        WeakReference<LayoutInflater> inflaterRef;
        WeakReference<ViewGroup> viewRef;

        public CommentInflater(AssignmentDetail context, ViewGroup parent) {
            inflaterRef = new WeakReference<>(context.getLayoutInflater());
            viewRef = new WeakReference<ViewGroup>(parent);
        }

        public View inflate(Comment comment) {
            LayoutInflater inflater = inflaterRef.get();
            if (inflater == null)
                return null;
            ViewGroup parent = viewRef.get();
            if (parent == null)
                return null;
            View v = inflater.inflate(R.layout.ad_commet, parent, false);
            TextView tv = (TextView) v.findViewById(R.id.ad_comment_content);
            tv.setText(comment.content);
            tv = (TextView) v.findViewById(R.id.ad_comment_date);
            tv.setText(util.transform_date(comment.date));
            tv = (TextView) v.findViewById(R.id.ad_comment_name);
            tv.setText(comment.poster_name);
            tv = (TextView) v.findViewById(R.id.ad_comment_flour);
            tv.setText((comment.pos) + "楼");
            return v;
        }

        public View inflate(Comment comment, ViewGroup parent) {
            LayoutInflater inflater = inflaterRef.get();
            if (inflater == null)
                return null;
            if (parent == null)
                return null;
            View v = inflater.inflate(R.layout.ad_sub_comment, parent, false);
            TextView tv;
            tv = (TextView) v.findViewById(R.id.ad_sub_comment_poster_name);
            tv.setText(comment.poster_name);
            tv = (TextView) v.findViewById(R.id.ad_sub_comment_date);
            tv.setText(util.transform_date(comment.date));
            tv = (TextView) v.findViewById(R.id.ad_sub_comment_content);
            tv.setText(comment.content);
            return v;
        }
    }

}
