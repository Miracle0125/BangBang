package com.yzx.bangbang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yzx.bangbang.fragment.Main.FrMain;
import com.yzx.bangbang.model.SimpleIndividualInfo;
import com.yzx.bangbang.R;
import com.yzx.bangbang.model.User;
import com.yzx.bangbang.utils.FrMetro;
import com.yzx.bangbang.utils.util;
import com.yzx.bangbang.presenter.MainPresenter;
import java.lang.ref.WeakReference;
import io.reactivex.functions.Consumer;


/**
 * 此Activity与各个子界面交互
 * 后期为了提高(xiang)开(yao)发(tou)速(lan)度，许多与数据库的交互不通过特定的Servlet，而是直接通过通用的Servlet直接查询SQL语句，返回类拥有数据库所有的字段，在module/mysql
 * 中的类全是和数据库字段契合的类
 */
public class Main extends RxAppCompatActivity {
    public static User user;
    public static WeakReference<Main> ref;
    public static final int ACTION_SHOW_DETAIL = 1;
    public static final int ACTION_EXIT_LOG_IN = 2;
    public static final int ACTION_CLICK_PORTRAIT = 3;
    public static final int ACTION_NEW_ASSIGNMENT = 4;
    public static final int RESULT_UPLOAD_SUCCESS = 5;
    public FrMetro metro;
    public MainPresenter.Listener listener;
    private MainPresenter presenter = new MainPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //prepare(View.inflate(this, R.layout.main_layout, null));
        setContentView(R.layout.main_layout);
        init();
    }

    public void init() {
        listener = presenter.getListener();
        listener.init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_UPLOAD_SUCCESS)
            if (metro.getCurrent() instanceof FrMain)
                ((FrMain) metro.getCurrent()).refresh();
        super.onActivityResult(requestCode, resultCode, data);
    }

    public Consumer<Message> consumer = (msg) -> {
        Intent intent = null;
        switch (msg.what) {
//            case ACTION_SHOW_DETAIL:
//                intent = new Intent(Main.this, AssignmentDetail.class);
//                Bundle bundle = new Bundle();
//                intent.putExtra("assignment", (Assignment) msg.obj);
//                startActivity(intent);
//                break;
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

    @Override
    protected void onDestroy() {
        presenter.detach();
        super.onDestroy();
    }

    long exit_time_record = System.currentTimeMillis();

//    @Override
//    public void onRefresh() {
//        if (fm.getCurrent() instanceof FrMain) {
//            ((FrMain) fm.findFragmentByClass(FrMain.class)).refresh();
//        } else onFinishRefresh();
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exit_time_record < 2000)
                util.AppExit();
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
}
