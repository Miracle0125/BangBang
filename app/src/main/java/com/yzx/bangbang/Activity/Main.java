package com.yzx.bangbang.Activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.yzx.bangbang.Activity.Common.PtrActivity;
import com.yzx.bangbang.Fragment.Main.FrMain;
import com.yzx.bangbang.module.SimpleIndividualInfo;
import com.yzx.bangbang.module.User;
import com.yzx.bangbang.R;
import com.yzx.bangbang.Service.NetworkService;
import com.yzx.bangbang.Utils.FrMetro;
import com.yzx.bangbang.Utils.util;
import com.yzx.bangbang.Widget.Ptr.MaterialHeader;
import com.yzx.bangbang.module.receiver.LAssignment;
import com.yzx.bangbang.presenter.MainPresenter;
import java.lang.ref.WeakReference;
import java.util.Set;
import io.reactivex.functions.Consumer;

/**
 * 此Activity与各个子界面交互
 * 后期为了提高(xiang)开(yao)发(tou)速(lan)度，许多与数据库的交互不通过特定的Servlet，而是直接通过通用的Servlet直接查询SQL语句，返回类拥有数据库所有的字段，在module/mysql
 * 中的类全是和数据库字段契合的类
 */
public class Main extends PtrActivity implements NetworkService.Listener {
    //public MaterialHeader header;
    public static WeakReference<Main> ref;
    //public static User user;
    public static final int ACTION_SHOW_DETAIL = 0;
    public static final int ACTION_EXIT_LOG_IN = 3;
    public static final int STATE_REFRESH_FINISH = 4;
    public static final int ACTION_CLICK_PORTRAIT = 5;
    public static NetworkService networkService;
    public FrMetro fm;
    MainServiceConn conn;
    private MainPresenter presenter=new MainPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        prepare(View.inflate(this, R.layout.main_layout, null));
        init();
    }

    public void init() {
        ref = new WeakReference<>(this);
        updateNtfCircle = () -> {
            int num = 0;
            Set<Integer> set = NetworkService.id_diff.keySet();
            for (int i : set) {
                if (NetworkService.id_diff.get(i) > 0) num++;
            }
            if (num == 0) {
                ntf_news.setVisibility(View.GONE);
            } else {
                ntf_news.setText(String.valueOf(num));
                ntf_news.setVisibility(View.VISIBLE);
            }
        };
        user = (User) getIntent().getSerializableExtra("user");
        if (NetworkService.inst == null) {
            conn = new MainServiceConn();
            bindService(new Intent(this, NetworkService.class), conn, BIND_AUTO_CREATE);
        } else {
            NetworkService.inst.getAssignments();
        }
    }



    private void initView() {

    }

    class MainServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            networkService = ((NetworkService.LocalBinder) service).getService();
            networkService.registerListener(Main.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            networkService = null;
        }
    }

    //主界面选择栏,ntf_news为显示新消息数量的小圆
    public TextView ntf_news, ntf_message;
    Runnable updateNtfCircle;

    @Override
    public void onNetWorkUpdate(int tag) {
        switch (tag) {
            case NetworkService.RECEIVE_MESSAGE:
                if (ntf_news != null) {
                    runOnUiThread(updateNtfCircle);
                }
                break;
        }
    }

//    FrNews frNews = (FrNews) getFragmentManager().findFragmentByTag((FrNews.class).toString());
//                if (frNews != null)
//            frNews.Resume();


    public Consumer<Message> consumer = (msg) -> {
        Intent intent;
        switch (msg.what) {
            case ACTION_SHOW_DETAIL:
                intent = new Intent(Main.this, AssignDetail.class);
                intent.putExtra("assignment", msg.obj);
                intent.putExtra("user", user);
                startActivity(intent);
                break;
            case ACTION_EXIT_LOG_IN:
                NetworkService.clearData();
                intent = new Intent(Main.this, SignIn.class);
                intent.putExtra("flag", "exit_log_in");
                startActivity(intent);
                break;
            case STATE_REFRESH_FINISH:
                if (super.header.state == MaterialHeader.STATE_ON_REFRESH)
                    onFinishRefresh();
                break;
            case ACTION_CLICK_PORTRAIT:
                intent = new Intent(Main.this, IndvInfo.class);
                intent.putExtra("info", (SimpleIndividualInfo) msg.obj);
                startActivity(intent);
                break;
            case 6:
                if (ntf_news != null) {
                    runOnUiThread(updateNtfCircle);
                }
                break;
            default:
                break;
        }
    };

    @Override
    protected void onDestroy() {
        unbindService(conn);
        presenter.detach();
//        clear();
        super.onDestroy();
    }

    long exit_time_record = System.currentTimeMillis();

    @Override
    public void onRefresh() {
        if (fm.getCurrent() instanceof FrMain) {
            ((FrMain) fm.findFragmentByClass(FrMain.class)).Refresh();
        } else onFinishRefresh();
    }

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

    public Listener getListener(){
        return listener;
    }
    Listener listener;
    public interface Listener{
        void getAssignment(Consumer<LAssignment> consumer);
    }
}
