package com.yzx.bangbang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yzx.bangbang.Service.NetworkService;
import com.yzx.bangbang.fragment.sign_in.FrSignIn;
import com.yzx.bangbang.fragment.sign_in.TestUtils;
import com.yzx.bangbang.interfaces.network.ISignIn;
import com.yzx.bangbang.model.User;
import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.sql.SA;
import com.yzx.bangbang.utils.FrMetro;
import com.yzx.bangbang.utils.netWork.Retro;
import com.yzx.bangbang.utils.Params;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.schedulers.Schedulers;

public class SignIn extends RxAppCompatActivity {
    public static final int UNKNOWN_WRONG = 0;
    public static final int SIGN_IN_SUCCESS = 1;
    public static final int SIGN_UP_SUCCESS = 2;
    public static final int NULL_ACCOUNT = 3;
    public static final int NULL_PASSWORD = 4;
    public static final int NULL_NAME = 5;
    public static final int PASS_WRONG = 6;
    public static final int PASS_NOT_MATCH = 7;
    public static final int ACCOUNT_EXIST = 8;
    public static final int ACCOUNT_NOT_EXIST = 9;
    public static final int NAME_EXIST = 10;


    public static final int ACTION_SIGN_IN = 20;
    public static final int ACTION_SIGN_UP = 21;


    private static final int STATE_NORMAL = -1;
    private static final String TAG_SIGN_IN = "0";
    private static final String TAG_SIGN_UP = "1";
    private static final int layout[] = {R.layout.sign_in_fr_sign_in, R.layout.sign_in_fr_sign_up};
    private static final String[] hints = new String[]{"Don't you have an account?", "I have an account already."};
    private static final String[] code = new String[]{"未知错误", "登陆成功", "注册成功", "账号为空", "密码为空", "用户名为空", "密码错误", "密码不一致", "账号已存在", "账号不存在", "用户名已存在"};
    private String[] inputs;
    private int current_fragment;
    private AlertDialog dialog;
    private FrMetro fm;
    @BindView(R.id.text_hint)
    TextView text_hint;
    @BindView(R.id.button_change_fragment)
    TextView button_change_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_layout);
        init();
        Params.initParams(this);
    }

    void init() {
        ButterKnife.bind(this);
        fm = new FrMetro(getFragmentManager(), R.id.fragment_container);
        go_to_fragment(0);
        startService(new Intent(this, NetworkService.class));
    }

    @OnClick({R.id.button_change_fragment})
    void click() {
        if (++current_fragment > 1) current_fragment = 0;
        text_hint.setText(hints[current_fragment]);
        button_change_fragment.setText(current_fragment == 1 ? "Login" : "Sign Up");
        go_to_fragment(current_fragment);
    }

    @OnClick(R.id.button_test_account)
    void show_test_account() {
        dialog = TestUtils.show_test_account(this, v -> post(new String[]{(String) v.getTag(), "0"}, ACTION_SIGN_IN));
    }

    private void go_to_fragment(int type) {
        fm.setCallBack(fragment -> {
            ((FrSignIn) fragment).res = layout[type];
            ((FrSignIn) fragment).consumer = o -> handle_response(judge(o, type + ACTION_SIGN_IN));
        });
        fm.goToFragment(FrSignIn.class, type == 0 ? TAG_SIGN_IN : TAG_SIGN_UP);
    }

    public int judge(String[] s, int action) {
        if (s[0].equals("") && Params.use_default_account && action == ACTION_SIGN_IN) {
            post(new String[]{"n", "0"}, action);
            return STATE_NORMAL;
        }
        if (action == ACTION_SIGN_IN) {
            if (s[0].equals(""))
                return NULL_ACCOUNT;
            if (s[1].equals(""))
                return NULL_PASSWORD;
        } else if (action == ACTION_SIGN_UP) {
            if (s[0].equals(""))
                return NULL_NAME;
            if (s[1].equals(""))
                return NULL_ACCOUNT;
            if (s[2].equals("") || s[3].equals(""))
                return NULL_PASSWORD;
            if (!s[2].equals(s[3]))
                return PASS_NOT_MATCH;
        }
        inputs = s;
        post(s, action);
        return STATE_NORMAL;
    }

    private void post(String[] src, int action) {
        if (action == ACTION_SIGN_IN) {
            Retro.single().create(ISignIn.class)
                    .sign_in(src[0], src[1])
                    .subscribeOn(Schedulers.io())
                    //.observeOn(AndroidSchedulers.mainThread())
                    .compose(bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(r -> handle_response(r.state, r.user));
        } else if (action == ACTION_SIGN_UP) {
            Retro.single().create(ISignIn.class)
                    .sign_up(src[0], src[1], src[2])
                    .subscribeOn(Schedulers.io())
                    //.observeOn(AndroidSchedulers.mainThread())
                    .compose(bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(this::handle_response);
        }
    }

    private void handle_response(int state) {
        handle_response(state, null);
    }

    private void handle_response(int state, User user) {
        if (state == -1) return;
        runOnUiThread(() -> Toast.makeText(this, code[state], Toast.LENGTH_SHORT).show());
        if (state == SIGN_UP_SUCCESS) {
            post(new String[]{inputs[1], inputs[2]}, ACTION_SIGN_IN);
        } else if (state == SIGN_IN_SUCCESS) {
            SA.insert(user, SA.TYPE_USER);
            startActivity(new Intent(this, Main.class));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.cancel();
        }
        finish();
    }


}
