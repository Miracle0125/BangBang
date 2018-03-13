package com.yzx.bangbang.activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.yzx.bangbang.Fragment.AD.FrReplierInfo;
import com.yzx.bangbang.model.Comment;
import com.yzx.bangbang.model.Msg;
import com.yzx.bangbang.model.ReplierInfo;
import com.yzx.bangbang.model.Reply;
import com.yzx.bangbang.model.User;
import com.yzx.bangbang.R;
import com.yzx.bangbang.Service.NetworkService;
import com.yzx.bangbang.utils.FrMetro;
import com.yzx.bangbang.utils.NetWork.UniversalImageDownloader;
import com.yzx.bangbang.utils.SpUtil;
import com.yzx.bangbang.utils.NetWork.OkHttpUtil;
import com.yzx.bangbang.utils.Params;
import com.yzx.bangbang.utils.util;
import com.yzx.bangbang.view.ad.AdLayout;
import com.yzx.bangbang.view.ad.CommentView;
import com.yzx.bangbang.view.ad.SubCommentView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Assignment;


public class AssignmentDetail extends Activity implements View.OnClickListener, CommentView.Listener, SubCommentView.Listener {
    public Assignment assignment;
    TextView button, replier_info_text, btn_clct_tv;
    View decorView, bottomBar, placeHolder, replier_info_bar, btn_clct, btn_back;
    SimpleDraweeView portrait;
    EditText edit;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_layout);
        //ActivityManager.getManager().addActivity(this);
        int asm_id;
        assignment = (Assignment) getIntent().getSerializableExtra("assignment");
        if (assignment == null) {
            asm_id = getIntent().getExtras().getInt("asm_id");
            downloadAssignmentById(asm_id);
        } else
            init();
    }

    AdLayout adLayout;

    private void init() {
        downloader = new UniversalImageDownloader(this);
        fm = new FrMetro(getFragmentManager(), R.id.ad_fragment_container);
        if (assignment.employer_id == Main.user.getId()) isOwner = true;
        initView();
        initEdit();
        initDialog();
        getComment();
        getReplierInfo();
        downloadImages();
        setKBDetector();
        checkIfHasChosen();
        checkIfHasFulfill();
        currentLines = 1;
    }

    @Override
    protected void onDestroy() {
        clear();
        super.onDestroy();
    }

    private void clear() {
    }

    private void initView() {
        if (assignment == null) return;
        adLayout = (AdLayout) findViewById(R.id.ad_layout);
        button = (TextView) findViewById(R.id.btn_send);
        button.setOnClickListener(this);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        placeHolder = findViewById(R.id.ad_bottom_bar_place_holder);
        bottomBar = findViewById(R.id.ad_bottom_bar);
        replier_info_bar = findViewById(R.id.replier_info_bar);
        replier_info_bar.setOnClickListener(this);
        replier_info_text = (TextView) findViewById(R.id.replier_info_text);
        ((TextView) findViewById(R.id.ad_title)).setText(assignment.title);
        ((TextView) findViewById(R.id.ad_content)).setText(assignment.content);
        ((TextView) findViewById(R.id.ad_posterName)).setText(assignment.employer_name);
        ((TextView) findViewById(R.id.ad_price)).setText(util.s(assignment.price));
        ((TextView) findViewById(R.id.ad_price)).setTextColor(util.CustomColor(assignment.price));
        downloader.downLoadPortrait(assignment.employer_id, (SimpleDraweeView) findViewById(R.id.ad_portrait));
        if (assignment.employer_id == Main.user.getId()) {
            View v = findViewById(R.id.ad_btn_delete);
            v.setVisibility(View.VISIBLE);
            v.setOnClickListener(this);
            btn_clct = findViewById(R.id.ad_btn_clct);
            btn_clct.setVisibility(View.GONE);
            adLayout.invalidate();
        } else {
            btn_clct_tv = (TextView) findViewById(R.id.ad_btn_clct_tv);
            btn_clct = findViewById(R.id.ad_btn_clct);
            btn_clct.setOnClickListener(this);
            checkIfHasCollected();
        }
    }

    int preLength;

    private void initDialog() {
        dialog = new Dialog(AssignmentDetail.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ad_comment_option);
        View v1 = dialog.findViewById(R.id.ad_reply_comment);
        v1.setOnClickListener(view -> {
            saveFlag = true;
            if (isSubComment) {
                String s = "回复 " + inst().poster_name + " 的评论:";
                preLength = s.length();
                edit.setText(s);
                edit.setSelection(edit.getText().length());
                edit.setFocusableInTouchMode(true);
                edit.requestFocus();
            }
            dialog.hide();
            //postSubComment(inst.poster_name,inst.poster_id,inst.parent_id);
        });
        //dialog.setContentView(dialogView);
        dialog.setOnCancelListener(dialogInterface -> {
            if (!saveFlag) {
                inst().poster_name = "";
                inst().poster_id = -1;
                inst().parent_id = -1;
                isSubComment = false;
            }
        });
    }

    private void downloadAssignmentById(int asm_id) {
        OkHttpUtil okhttp = OkHttpUtil.inst((s) -> {
            if (s.equals("")) {
                runOnUiThread(()->{
                    Toast.makeText(AssignmentDetail.this, "需求不存在", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
            TempList tempList = gson.fromJson(s, TempList.class);
            if (tempList != null && tempList.list.size() != 0) {
                assignment = tempList.list.get(0).toAssignment();
                runOnUiThread(this::init);
            }
        });
        okhttp.addPart("sql", "select * from assignment where `id` = " + asm_id);
        okhttp.post("query_data_common");
    }

    Comments comments;

    private void getComment() {
        OkHttpUtil okHttp = OkHttpUtil.inst(s -> {
            if (s.length() == 0 || s.charAt(0) == '<')
                return;
            comments = gson.fromJson(s, Comments.class);
            runOnUiThread(() -> processInputStream(comments.list, comments.floors));
        });
        okHttp.addPart("comment", String.valueOf(assignment.id));
        okHttp.post("query_comment");
    }

    private void downloadImages() {
        for (int i = 0; i < assignment.images; i++) {
            SimpleDraweeView view;
            switch (i) {
                case 0:
                    view = (SimpleDraweeView) findViewById(R.id.ad_image0);
                    view.setVisibility(View.VISIBLE);
                    downloader.downloadAssignmentImages(assignment.id, i, view);
                    break;
                case 1:
                    view = (SimpleDraweeView) findViewById(R.id.ad_image1);
                    view.setVisibility(View.VISIBLE);
                    downloader.downloadAssignmentImages(assignment.id, i, view);
                    break;
                case 2:
                    view = (SimpleDraweeView) findViewById(R.id.ad_image2);
                    view.setVisibility(View.VISIBLE);
                    downloader.downloadAssignmentImages(assignment.id, i, view);
                    break;
            }
        }
    }

    private void getReplierInfo() {
        OkHttpUtil okhttp = OkHttpUtil.inst(s -> {
            final ReplierInfoList temp = gson.fromJson(s, ReplierInfoList.class);
            if (temp != null) {
                for (ReplierInfo info : temp.list) {
                    if (Main.user.getId() == info.user_id)
                        already_accepted = true;
                }
                runOnUiThread(() -> {
                    if (!isOwner)
                        updateButtonView();
                    updateReplierInfoBar(temp.list);
                });
                inst().replierInfoList = temp.list;
            }
        });
        okhttp.addPart("asm_id", String.valueOf(assignment.id));
        okhttp.post("query_replier_info");
    }

    public void checkIfHasChosen() {
        OkHttpUtil okhttp = OkHttpUtil.inst(s -> {
            if (s.length() == 0 || s.charAt(0) == '<') return;
            int i = parseString(s);
            if (i == 1) {
                chosen = true;
                runOnUiThread(() -> findViewById(R.id.ad_icon_success_block).setVisibility(View.VISIBLE));
            }
        });
        okhttp.addPart("sql", "select count(*) from `event` where `asm_id` = '" + assignment.id + "' and `success` = '1'");
        okhttp.post("query_data_common");
    }

    public void checkIfHasFulfill() {
        OkHttpUtil okhttp = OkHttpUtil.inst(s -> {
            if (s.length() == 0 || s.charAt(0) == '<') return;
            int i = parseString(s);
            if (i == 1) {
                fulfill = true;
                runOnUiThread(() -> ((TextView) findViewById(R.id.ad_state_text)).setText("需求完结"));
            }
        });
        okhttp.addPart("sql", "select count(*) from `event` where `asm_id` = '" + assignment.id + "' and `fulfill` = '1'");
        okhttp.post("query_data_common");
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
            button.setTextColor(getResources().getColor(R.color.white));
            button.setBackgroundColor(Color.parseColor("#2196f3"));
        }
        button.setText("发送");
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
            button.setBackgroundColor(getResources().getColor(R.color.cloud_gray));
            button.setTextColor(getResources().getColor(R.color.gray));
            button.setText("已接受");
        } else {
            button.setText("接受需求");
        }
        state = ACCEPT_ASSIGN;
    }

    private void updateButtonView() {
        if (button != null)
            if (state == ACCEPT_ASSIGN && already_accepted) {
                button.setBackgroundColor(getResources().getColor(R.color.cloud_gray));
                button.setTextColor(getResources().getColor(R.color.gray));
                button.setText("已接受");
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

    util.KeyBoardDetector detector;

    private void setKBDetector() {
        //detector = new util.KeyBoardDetector();
        decorView = getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            detector = new util.KeyBoardDetector(decorView);
            util.Animate(bottomBar, -(Params.screenHeight - detector.get_key_board_top()), util.VERTICAL, 0);
            if (detector.isKeyBoardRose()) OnStartInput();
            else OnCancelInput();
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                if (state == ACCEPT_ASSIGN) {
                    requestAcceptAsm();
                } else if (state == POST_COMMENT) {
                    sendComment();
                }
                break;
            case R.id.ad_btn_delete:
                deleteAssignment();
                break;
            case R.id.ad_btn_clct:
                if (!already_collected)
                    collectAssignment();
                else cancelCollection();
                break;
            case R.id.replier_info_bar:
                if (!isOwner) return;
                showReplierInfo();
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    //3  define in params.EVENT_TYPE_COLLECT_ASSIGNMENT
    private void checkIfHasCollected() {
        OkHttpUtil okHttp = OkHttpUtil.inst(s -> {
            //while result >0 ,has collected
            if (util.parseString(s, "count(*)") == 0) {
                already_collected = false;
                runOnUiThread(() -> updateButtonCollectView(false));
            } else {
                runOnUiThread(() -> updateButtonCollectView(true));
                already_collected = true;
            }
        });
        okHttp.addPart("sql", null, "select count(*) from event where user_id = " + Main.user.getId() + " and type = 3 and asm_id = " + assignment.id, OkHttpUtil.MEDIA_TYPE_JSON);
        okHttp.post("query_data_common");
    }

    private void updateButtonCollectView(boolean hasCollect) {
        if (hasCollect) {
            btn_clct_tv.setText("已收藏");
            btn_clct_tv.setTextColor(getResources().getColor(R.color.gray));
            btn_clct.setBackgroundColor(getResources().getColor(R.color.cloud_gray));
            already_collected = true;
        } else {
            btn_clct_tv.setText("收藏");
            btn_clct_tv.setTextColor(getResources().getColor(R.color.main));
            btn_clct.setBackgroundColor(Color.parseColor("#99cc33"));
            already_collected = false;
        }
    }

    private int parseString(String s) {
        if (s.charAt(0) != '{') return -1;
        int end = 22;
        while (Character.isDigit(s.charAt(end)))
            end++;
        return Integer.valueOf(s.substring(22, end));
    }

    private void collectAssignment() {
        OkHttpUtil okHttp = OkHttpUtil.inst(s -> {
            if (s.equals("success"))
                updateNumCollection(true);
        });
        okHttp.addPart("sql", null, "insert into event (`user_id`, `user_name`, `date`, `type`, `asm_id`, `asm_title`, `price`) values ('" + Main.user.getId() + "','" + Main.user.getName() + "','" + util.getDate() + "','" + String.valueOf(Params.EVENT_TYPE_COLLECT_ASSIGNMENT) + "','" + assignment.id + "','" + assignment.title + "','" + assignment.price + "')", OkHttpUtil.MEDIA_TYPE_JSON);
        okHttp.post("update_data_common");
    }

    private void cancelCollection() {
        OkHttpUtil okHttp = OkHttpUtil.inst(s -> {
            if (s.equals("success"))
                updateNumCollection(false);
        });
        okHttp.addPart("sql", "delete from event where user_id = '" + Main.user.getId() + "' and asm_id = '" + assignment.id + "' and type = '" + Params.EVENT_TYPE_COLLECT_ASSIGNMENT + "'");
        okHttp.post("update_data_common");
    }

    private void updateNumCollection(boolean isIncrease) {
        String symbol = isIncrease ? "+" : "-";
        OkHttpUtil okHttp = OkHttpUtil.inst(s -> {
            if (s.equals("success"))
                runOnUiThread(() -> {
                    if (already_collected)
                        Toast.makeText(AssignmentDetail.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(AssignmentDetail.this, "已收藏", Toast.LENGTH_SHORT).show();
                    checkIfHasCollected();
                });
        });
        okHttp.addPart("sql", "update user_record set `num_coll` =  num_coll " + symbol + " 1 where user_id = " + Main.user.getId());
        okHttp.post("update_data_common");
    }

    private void deleteAssignment() {
        OkHttpUtil okHttp = OkHttpUtil.inst(s -> {
            if (s.equals("success")) {
                runOnUiThread(() -> Toast.makeText(AssignmentDetail.this, "删除成功", Toast.LENGTH_SHORT).show());
                finish();
                SpUtil.putRefreshFlag(AssignmentDetail.this);
            } else if (s.equals("failed")) {
                runOnUiThread(() -> Toast.makeText(AssignmentDetail.this, "删除失败", Toast.LENGTH_SHORT).show());
            }
        });
        okHttp.addPart("delete_assignment", null, String.valueOf(assignment.id), OkHttpUtil.MEDIA_TYPE_JSON);
        okHttp.post("delete_asm");
    }


    //@SuppressLint("HandlerLeak")
    public Handler handler = new AdHandler(this);
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

    public static class AdHandler extends Handler {
        private WeakReference<AssignmentDetail> ref;

        public AdHandler(AssignmentDetail ref) {
            this.ref = new WeakReference<>(ref);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AssignmentDetail inst = ref.get();
            if (inst != null)
                inst.handleMsg(msg);
        }
    }

    public void handleMsg(Message msg) {
        switch (msg.what) {
            case NetworkService.KEY_INSERT_MSG:
                Toast.makeText(AssignmentDetail.this, "请求发送成功", Toast.LENGTH_SHORT).show();
                break;
            case ACTION_REMOVE_FRAGMENT:
                if (fm != null) {
                    if (fm.getCurrent() != null) {
                        fm.removeCurrent();
                        updateFragmentBackground();
                    }
                }
                break;
        }
    }

    public static class Temp {
        List<Reply> list;

        public List<Reply> getList() {
            return list;
        }
    }

    static int MajorComments;

    private void sendComment() {
        User user = Main.user;
        Gson gson = new Gson();
        String s = edit.getText().toString();
        Comment comment;
        if (saveFlag) {
            comment = new Comment(user.getName(), poster_name, util.getDate(), -1, assignment.id, user.getId(), poster_id, inst().parent_id, -1, -1, edit.getText().toString());
        } else {
            comment = new Comment(user.getName(), assignment.getEmployer_name(), util.getDate(), -1, assignment.id, user.getId(), assignment.employer_id, 0, -1, -1, edit.getText().toString());
        }
        startPost(gson.toJson(comment));
    }

/*    private boolean isSubComment(String s) {
        if (s.charAt(2) != ' ')
            return false;
        for (int i = 3; i < s.length(); i++) {
            if (s.charAt(i) == ' ') {
                if (s.charAt(i + 4) == ':')
                    return true;
            }
        }
        return false;
    }*/

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
        //NetworkService.inst.send(gson.toJson(msg), NetworkService.KEY_INSERT_MSG);
/*        NetworkService.inst.sendJson("accept_assignment", "", gson.toJson(msg), "message_manager", new OkHttpUtil.simpleOkHttpCallback() {
            @Override
            public void onResponse(String s) {
                if (s.equals("update_success"))
                    inst().handler.sendEmptyMessage(NetworkService.KEY_INSERT_MSG);
            }
        });*/
        if (already_accepted) return;
        if (isOwner) {
            Toast.makeText(AssignmentDetail.this, "你是发布人啊(。・`ω´・)", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(() -> {
            Msg msg = new Msg(assignment.employer_name, Main.user.getName(), assignment.title, util.getDate(), assignment.employer_id, assignment.employer_id, Main.user.getId(), assignment.id, 0);
            ReplierInfo info = new ReplierInfo(Main.user.getName(), assignment.title, null, util.getDate(), Main.user.getId(), assignment.id, assignment.price);
            OkHttpUtil okhttp = OkHttpUtil.inst(s -> {
                if (s.equals("update_success"))
                    inst().handler.sendEmptyMessage(NetworkService.KEY_INSERT_MSG);
            });
            okhttp.addPart("accept_assignment", "message", gson.toJson(msg), OkHttpUtil.MEDIA_TYPE_JSON);
            okhttp.addPart("accept_assignment", "replier_info", gson.toJson(info), OkHttpUtil.MEDIA_TYPE_JSON);
            okhttp.post("accept_assignment");
        }).start();
    }

    FrMetro fm;

    private void showReplierInfo() {
        if (fm == null) {
            fm = new FrMetro(getFragmentManager(), R.id.ad_fragment_container);
        }
        fm.goToFragment(FrReplierInfo.class);
        updateFragmentBackground();
        //(fm.getCurrent()).setAlpha(1);
    }

    private void updateFragmentBackground() {
        if (fm == null) return;
        if (fm.getCurrent() == null)
            findViewById(R.id.ad_fragment_container).setBackgroundColor(Color.parseColor("#00FAFAFA"));
        else
            findViewById(R.id.ad_fragment_container).setBackgroundColor(getResources().getColor(R.color.opaque));
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
                return new Assignment(employer_name, title, content, date, id, repliers, employer_id, images, price);
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
            tv.setText(util.CustomDate(comment.date));
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
            tv.setText(util.CustomDate(comment.date));
            tv = (TextView) v.findViewById(R.id.ad_sub_comment_content);
            tv.setText(comment.content);
            return v;
        }
    }

}
