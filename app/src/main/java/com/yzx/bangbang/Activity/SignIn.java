package com.yzx.bangbang.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yzx.bangbang.Fragment.Common.FormFragment;
import com.yzx.bangbang.Interface.Network.SignIn.ISignIn;
import com.yzx.bangbang.Interface.Network.SignIn.ISignUp;
import com.yzx.bangbang.module.receiver.RSignIn;
import com.yzx.bangbang.module.User;
import com.yzx.bangbang.R;
import com.yzx.bangbang.Utils.FrMetro;
import com.yzx.bangbang.Utils.NetWork.Retro;
import com.yzx.bangbang.Utils.Params;
import com.yzx.bangbang.Utils.SpUtil;
import com.yzx.bangbang.Utils.util;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class SignIn extends RxAppCompatActivity {
    public static final int SIGN_IN_SUCCESS = 1;
    public static final int PASS_WRONG = 2;
    public static final int PASS_NOT_MATCH = 3;
    public static final int ACCOUNT_NOT_EXIST = 4;
    public static final int NULL_PASSWORD = 5;
    public static final int SIGN_UP_SUCCESS = 6;
    public static final int NULL_ACCOUNT = 7;
    public static final int ACCOUNT_EXIST = 8;
    public static final int NULL_NAME = 9;
    public static final int NAME_EXIST = 10;
    public static final int ACTION_SIGN_IN = 20;
    public static final int ACTION_SIGN_UP = 21;

    Intent mIntent;
    FrMetro metro;
    String[]inputs, code = new String[]{"", "登陆成功", "注册成功", "密码错误", "密码不一致", "密码为空", "账号为空", "用户名为空", "账号已存在", "账号不存在", "用户名已存在"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_content);
        init();
        Params.initParams(this);
    }

    void init() {
        ButterKnife.bind(this);
        metro = new FrMetro(getFragmentManager(), R.id.sign_in_frag_container);
        mIntent = new Intent(this, Main.class);
    }

    @OnClick({R.id.button_sign_in, R.id.button_sign_up})
    public void onclick(View v) {
        metro.setCallBack((fragment) -> {
            switch (v.getId()) {
                case R.id.button_sign_up:
                    ((FormFragment) fragment).setRes(R.layout.sign_in_fragment_sign_up);
                    ((FormFragment) fragment).setConsumer(o -> handleState(judge((String[]) o, ACTION_SIGN_UP)));
                    break;
                case R.id.button_sign_in:
                    ((FormFragment) fragment).setRes(R.layout.sign_in_fragment_sign_in);
                    ((FormFragment) fragment).setConsumer(o -> handleState(judge((String[]) o, ACTION_SIGN_IN)));
                    break;
            }
        });
        metro.goToFragment(FormFragment.class);
    }

    public int judge(String[] s, int type) {
        if (type == 0) {
            if (s[0].equals(""))
                return SignIn.NULL_ACCOUNT;
            if (s[1].equals(""))
                return SignIn.NULL_PASSWORD;
        } else if (type == 1) {
            if (s[0].equals(""))
                return NULL_NAME;
            if (s[1].equals(""))
                return NULL_ACCOUNT;
            if (s[2].equals("") || s[3].equals(""))
                return NULL_PASSWORD;
            if (!s[2].equals(s[3]))
                return PASS_NOT_MATCH;
        }
        inputs=s;
        post(s, type);
        return -1;
    }

    private void post(String[] src, int type) {
        if (type == ACTION_SIGN_IN) {
            Retro.inst().create(ISignIn.class)
                    .impl(src[0], src[1])
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(this.<RSignIn>bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe ( r -> handleState(r.state, r.user));
        } else {
            Retro.inst().create(ISignUp.class)
                    .impl(src[0], src[1], src[2])
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(this.<Integer>bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(this::handleState);
        }
    }

    private void handleState(int state){
        handleState(state,null);
    }
    private void handleState(int state, User user) {
        if (state == -1) return;
        Toast.makeText(this, code[state], Toast.LENGTH_SHORT).show();
        if (state == SIGN_UP_SUCCESS) {
            post(inputs, ACTION_SIGN_IN);
        } else if (state == SIGN_IN_SUCCESS) {
            Gson gson =new Gson();
            SpUtil.putString(Params.DATABASE, "user", gson.toJson(user), this);
            mIntent.putExtra("user", user);
            util.putString(R.string.key_user, gson.toJson(user), this);
            startActivity(mIntent);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (metro.getCurrent() != null) {
                metro.removeCurrent();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void change(View view) {
        View v = getLayoutInflater().inflate(R.layout.edit_layout, null);
        EditText changeEdit = (EditText) v.findViewById(R.id.sign_in_edit);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("改变设置？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", null);
        builder.setView(v);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(view1 -> {
            Params.ip = changeEdit.getText().toString();
            Toast.makeText(this, "IP改变成功", Toast.LENGTH_SHORT).show();
            dialog.cancel();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

}
