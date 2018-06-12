package com.yzx.bangbang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yzx.bangbang.model.SimpleIndividualInfo;
import com.yzx.bangbang.R;
import com.yzx.bangbang.model.User;
import com.yzx.bangbang.utils.sql.DAO;
import com.yzx.bangbang.presenter.MainPresenter;
import com.yzx.bangbang.view.mainView.MainLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import model.Assignment;


public class Main extends RxAppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_layout);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // presenter.check_notify();
    }

    public void init() {
        ButterKnife.bind(this);
        presenter.init(user.getId());
    }

    public Consumer<Message> consumer = (msg) -> {
        Intent intent = null;
        switch (msg.what) {
            case ACTION_NEW_ASSIGNMENT:
                intent = new Intent(this, NewAssignment.class);
                break;
            case ACTION_CLICK_PORTRAIT:
                intent = new Intent(this, IndvInfo.class);
                intent.putExtra("info", (SimpleIndividualInfo) msg.obj);
                break;
            default:
                break;
        }
        if (intent != null)
            startActivityForResult(intent, msg.what);
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exit_time_record < 2000)
                exit();
            else {
                Toast.makeText(Main.this, "再点一次退出", Toast.LENGTH_SHORT).show();
                exit_time_record = System.currentTimeMillis();
            }
        }
        return false;
    }

    private void exit() {
        presenter.detach();
        finish();
    }

    @Override
    protected void onStop() {
        if (exit_sign_in_flag)
            exit();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        presenter.detach();
        super.onDestroy();
    }

    @BindView(R.id.main_layout)
    MainLayout mainLayout;
    public boolean exit_sign_in_flag;
    public User user = (User) DAO.query(DAO.TYPE_USER);
    public int user_id = user.getId();
    public static final int ACTION_SHOW_DETAIL = 1;
    public static final int ACTION_CLICK_PORTRAIT = 2;
    public static final int ACTION_NEW_ASSIGNMENT = 3;
    public static final int RESULT_UPLOAD_SUCCESS = 4;
    public MainPresenter presenter = new MainPresenter(this);
    private long exit_time_record = System.currentTimeMillis();
}
