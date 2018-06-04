package com.yzx.bangbang.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.yzx.bangbang.fragment.IndividualInfo.FrAcceptedAssign;
import com.yzx.bangbang.fragment.IndividualInfo.FrAssignList;
import com.yzx.bangbang.fragment.IndividualInfo.FrCollection;
import com.yzx.bangbang.fragment.IndividualInfo.FrConcern;
import com.yzx.bangbang.model.SimpleIndividualInfo;
import com.yzx.bangbang.model.User;
import com.yzx.bangbang.model.UserRecord;
import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.FrMetro;
import com.yzx.bangbang.utils.netWork.OkHttpUtil;
import com.yzx.bangbang.utils.netWork.UniversalImageDownloader;
import com.yzx.bangbang.utils.sql.SpUtil;


public class IndvInfo extends AppCompatActivity implements View.OnClickListener {
    int indv_id = -1;
    String indv_name;
    TextView name, signature, bb_contact, num_his_asm, num_concern, num_follower, num_accepted_asm, num_clct;
    SimpleDraweeView portrait;
    View btn_back, his_asm_bar, accepted_asm_bar, btn_message, btn_concern,indv_clct_bar,indv_block_follower,indv_block_concern,btn_user_setting;
    public SimpleIndividualInfo info;
    private boolean isUser;
    public UniversalImageDownloader downloader;
    public FrMetro fm;
    boolean hasConcernUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        fm = new FrMetro(getFragmentManager(), R.id.fragment_container);
        downloader = new UniversalImageDownloader(this);
        Gson gson = new Gson();
        User user= (User) SpUtil.getObject(SpUtil.USER);
        Bundle data = getIntent().getExtras();
        if (user == null) return;
        if (data != null) {
            info = (SimpleIndividualInfo) data.getSerializable("info");
            if (info == null)
                //info = gson.fromJson(SpUtil.getString(SpUtil.SAVED_INFO, String.valueOf(ActivityManager.getLayer())), SimpleIndividualInfo.class);
            if (info == null) return;
        }
        indv_id = info.id;
        indv_name = info.name;
        if (user.getId() != indv_id)
            setContentView(R.layout.individual_info_layout);
        else {
            setContentView(R.layout.individual_user_info_layout);
            isUser = true;
        }
        initView();
        new Thread(this::getIndvInfo).start();
        //if (!isUser)
           // checkIfHasConcerned();
        //SpUtil.putString(SpUtil.SAVED_INFO, String.valueOf(ActivityManager.getLayer()), gson.toJson(info));
    }

    private void initView() {
        name = (TextView) findViewById(R.id.indv_name);
        signature = (TextView) findViewById(R.id.indv_signature);
        bb_contact = (TextView) findViewById(R.id.indv_bb_contact);
        num_his_asm = (TextView) findViewById(R.id.indv_num_his_asm);
        num_concern = (TextView) findViewById(R.id.indv_num_concern);
        num_follower = (TextView) findViewById(R.id.indv_num_follower);
        portrait = (SimpleDraweeView) findViewById(R.id.indv_portrait);
        btn_back = findViewById(R.id.indv_btn_back);
        his_asm_bar = findViewById(R.id.indv_his_asm_bar);
        indv_block_follower = findViewById(R.id.indv_block_follower);
        indv_block_follower.setOnClickListener(this);
        indv_block_concern = findViewById(R.id.indv_block_concern);
        indv_block_concern.setOnClickListener(this);
        if (isUser) {
            accepted_asm_bar = findViewById(R.id.indv_accepted_asm_bar);
            accepted_asm_bar.setOnClickListener(this);
            num_accepted_asm = (TextView) findViewById(R.id.indv_num_accepted_asm);
            num_clct = (TextView) findViewById(R.id.indv_num_clct);
            indv_clct_bar = findViewById(R.id.indv_clct_bar);
            indv_clct_bar.setOnClickListener(this);
            btn_user_setting = findViewById(R.id.btn_user_setting);
            btn_user_setting.setOnClickListener(this);
        } else {
            btn_concern = findViewById(R.id.indv_btn_concern);
            btn_concern.setOnClickListener(this);
            btn_message = findViewById(R.id.btn_message);
            btn_message.setOnClickListener(this);
        }
        btn_back.setOnClickListener(this);
        his_asm_bar.setOnClickListener(this);

    }

    UserRecord userRecord;
    User user;

    private void getIndvInfo() {
        if (indv_id == -1) return;
        OkHttpUtil okHttpUtil = OkHttpUtil.inst(s -> {
            Gson gson = new Gson();
            Receive receive = gson.fromJson(s, Receive.class);
            if (receive != null) {
                user = receive.user;
                userRecord = receive.userRecord;
                downloader.downLoadPortrait(user.getId(), portrait);
                runOnUiThread(IndvInfo.this::loadData);
            }
        });
        okHttpUtil.addPart("indv_id", String.valueOf(indv_id));
        okHttpUtil.post("query_indv_info");
    }

    private void loadData() {
        if (isUser) {
            num_accepted_asm.setText(String.valueOf(userRecord.num_accept));
            num_clct.setText(String.valueOf(userRecord.num_collect));
        }
        num_concern.setText(String.valueOf(userRecord.num_concerns));
        num_follower.setText(String.valueOf(userRecord.num_be_concerned));
        num_his_asm.setText(String.valueOf(userRecord.num_assignment));
        bb_contact.setText(user.getBbContact());
        signature.setText(user.getSignature());
        name.setText(user.getName());
    }

    private void showAssignmentList() {
        if (fm == null) {
            fm = new FrMetro(getFragmentManager(), R.id.fragment_container);
        }
        fm.goToFragment(FrAssignList.class);
        updateFragmentBackground();
    }

    private void showAcceptedAssign() {
        if (fm == null) {
            fm = new FrMetro(getFragmentManager(), R.id.fragment_container);
        }
        fm.goToFragment(FrAcceptedAssign.class);
        updateFragmentBackground();
        //(fm.getCurrent()).setAlpha(1);
    }

    private void  showCollectedAssignment(){
        if (fm == null) {
            fm = new FrMetro(getFragmentManager(), R.id.fragment_container);
        }
        fm.goToFragment(FrCollection.class);
        updateFragmentBackground();
    }

    public boolean isConcernList;
    private void showConcernFragment(boolean isConcernList){
        if (fm == null) {
            fm = new FrMetro(getFragmentManager(), R.id.fragment_container);
        }

        fm.goToFragment(FrConcern.class);
        updateFragmentBackground();
        this.isConcernList = isConcernList;
    }

    public void updateFragmentBackground() {
        if (fm == null) return;
        if (fm.getCurrent() == null)
            findViewById(R.id.fragment_container).setBackgroundColor(Color.parseColor("#00FAFAFA"));
        else
            findViewById(R.id.fragment_container).setBackgroundColor(getResources().getColor(R.color.opaque));
    }

//    private void concernUser() {
//        OkHttpUtil okhttp = OkHttpUtil.single((s) -> {
//            if (s.equals("success"))
//                updateNumConcern(true);
//        });
//        String sql1 = "insert into `event` (`user_id`, `user_name`, `date`, `type`, `obj_user_id`, `obj_user_name`) values ('" + Main.user.getId() + "','" + Main.user.getName() + "','" + util.getDate() + "','" + String.valueOf(Params.EVENT_TYPE_CONCERN_PERSON) + "','" + indv_id + "','" + indv_name + "');";
//        okhttp.addPart("sql", "", sql1, OkHttpUtil.MEDIA_TYPE_JSON);
//        okhttp.post("update_data_common");
//    }

//    private void cancelConcern() {
//        OkHttpUtil okhttp = OkHttpUtil.single((s) -> {
//            if (s.equals("success"))
//                updateNumConcern(false);
//        });
//        String sql1 = "delete from `event` where user_id = '" + Main.user.getId() + "' and obj_user_id = '" + indv_id + "' and type = 2";
//        okhttp.addPart("sql", "", sql1, OkHttpUtil.MEDIA_TYPE_JSON);
//        okhttp.post("update_data_common");
//    }

//    private void updateNumConcern(boolean isIncrease) {
//        String symbol = isIncrease ? "+" : "-";
//        String sql2 = "update `user_record` set `num_concern`  = num_concern " + symbol + " '1' where user_id = '" + Main.user.getId() + "';";
//        OkHttpUtil okhttp = OkHttpUtil.single((s) -> {
//            if (s.equals("success"))
//                updateNumFollower(isIncrease);
//        });
//        okhttp.addPart("sql", "", sql2, OkHttpUtil.MEDIA_TYPE_JSON);
//        okhttp.post("update_data_common");
//    }

    //the last method of 3
    private void updateNumFollower(boolean isIncrease) {
        String symbol = isIncrease ? "+" : "-";
        String sql3 = "update `user_record` set `num_follower`  = num_follower " + symbol + " '1' where user_id = '" + indv_id + "';";
        OkHttpUtil okhttp = OkHttpUtil.inst((s) -> {
            if (s.equals("success")) {
                runOnUiThread(() -> {
                    if (!hasConcernUser)
                        Toast.makeText(IndvInfo.this, "关注成功", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(IndvInfo.this, "已取消关注", Toast.LENGTH_SHORT).show();
                });
               // checkIfHasConcerned();
            }
        });
        okhttp.addPart("sql", "", sql3, OkHttpUtil.MEDIA_TYPE_JSON);
        okhttp.post("update_data_common");
    }

//    private void checkIfHasConcerned() {
//        OkHttpUtil okhttp = OkHttpUtil.single((s) -> {
//            if (s.length() == 0 || s.charAt(0) == '<') return;
//            int i = util.parseString(s, "count(*)");
//            if (i == 1) {
//                hasConcernUser = true;
//                runOnUiThread(() -> updateConcernButton(true));
//            } else if (i == 0) {
//                hasConcernUser = false;
//                runOnUiThread(() -> updateConcernButton(false));
//            }
//        });
//        okhttp.addPart("sql", "select count(*) from event where user_id = '" + Main.user.getId() + "' and type = 2 and obj_user_id = '" + indv_id + "'");
//        okhttp.post("query_data_common");
//    }

    private void updateConcernButton(boolean concern) {
        if (concern) {
            btn_concern.setBackgroundColor(getResources().getColor(R.color.cloud_gray));
            TextView tv = ((TextView) findViewById(R.id.indv_btn_concern_textview));
            tv.setText("已关注");
            tv.setTextColor(getResources().getColor(R.color.gray));
        } else {
            btn_concern.setBackgroundColor(Color.parseColor("#99cc33"));
            TextView tv = ((TextView) findViewById(R.id.indv_btn_concern_textview));
            tv.setText("关注");
            tv.setTextColor(getResources().getColor(R.color.main));
        }
    }

    private void startChatActivity() {
        Intent i = new Intent(this, ChatActivityDeprecated.class);
        i.putExtra("info", info);
        startActivity(i);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.indv_btn_back:
                break;
            case R.id.indv_his_asm_bar:
                showAssignmentList();
                break;
            case R.id.indv_accepted_asm_bar:
                showAcceptedAssign();
                break;
            case R.id.indv_clct_bar:
                showCollectedAssignment();
                break;
            case R.id.btn_message:
                startChatActivity();
                break;
            case R.id.indv_btn_concern:
               // if (hasConcernUser) cancelConcern();
               // else
                //    concernUser();
                break;
            case R.id.indv_block_concern:
                showConcernFragment(true);
                break;
            case R.id.indv_block_follower:
                showConcernFragment(false);
                break;
            case R.id.btn_user_setting:
                Intent i = new Intent(this,UserSetting.class);
                i.putExtra("info",info);
                startActivity(i);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fm.getCurrent() != null) {
                fm.removeCurrent();
                updateFragmentBackground();
                return false;
            }
            //onFinish();
            //return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (finishFlag)
            finish();
    }

    private boolean finishFlag;

    private void onFinish() {
        //ActivityManager.single.onFinish();
        finishFlag = true;
        //Class<?> cls = ActivityManager.getTop();
//        if (cls != null)
//            startActivity(new Intent(this, cls));
//        else finish();
    }

    private class Receive {
        User user;
        UserRecord userRecord;
    }
}
