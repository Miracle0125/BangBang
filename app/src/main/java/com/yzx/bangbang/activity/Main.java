package com.yzx.bangbang.activity;

import android.app.Fragment;
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
import com.yzx.bangbang.utils.util;
import com.yzx.bangbang.presenter.MainPresenter;
import com.yzx.bangbang.view.mainView.MainLayout;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import model.Assignment;


public class Main extends RxAppCompatActivity {
    //解耦
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_layout);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.check_notify();
    }

    public void init() {
        presenter.init(user.getId());
    }

    public Consumer<Message> consumer = (msg) -> {
        Intent intent = null;
        switch (msg.what) {
            case ACTION_SHOW_DETAIL:
                intent = new Intent(this, AssignmentDetail.class);
                intent.putExtra("assignment", (Assignment) msg.obj);
                break;
            case ACTION_NEW_ASSIGNMENT:
                intent = new Intent(this, NewAssignment.class);
                break;
            case ACTION_EXIT_LOG_IN:
                intent = new Intent(this, SignIn.class);
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

    private long exit_time_record = System.currentTimeMillis();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exit_time_record < 2000)
                finish();
                //util.exit_app();
            else {
                Toast.makeText(Main.this, "再点一次退出", Toast.LENGTH_SHORT).show();

                exit_time_record = System.currentTimeMillis();
            }
        }
        return false;
    }

    public static Main get() {
        return ref.get();
    }

    public Fragment get_current_fragment() {
        return mainLayout.metro.getCurrent();
    }

    @Override
    protected void onDestroy() {
        presenter.detach();
        super.onDestroy();
    }

    @BindView(R.id.main_layout)
    MainLayout mainLayout;

    public static User user = (User) DAO.query(DAO.TYPE_USER);
    public static WeakReference<Main> ref;
    public static final int ACTION_SHOW_DETAIL = 1;
    public static final int ACTION_EXIT_LOG_IN = 2;
    public static final int ACTION_CLICK_PORTRAIT = 3;
    public static final int ACTION_NEW_ASSIGNMENT = 4;
    public static final int RESULT_UPLOAD_SUCCESS = 5;
    // public static boolean
    //public MainPresenter.Listener listener;
    public MainPresenter presenter = new MainPresenter(this);
}
