package com.yzx.bangbang.view.mainView;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.fragment.Main.FrMain;
import com.yzx.bangbang.fragment.Main.FrMessage;
import com.yzx.bangbang.fragment.Main.FrMyProject;
import com.yzx.bangbang.fragment.Main.FrNotify;
import com.yzx.bangbang.fragment.Main.FrUser;
import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.FrMetro;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainLayout extends RelativeLayout {
    Main context;

    public MainLayout(Context context) {
        this(context, null, 0);
    }

    public MainLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = (Main) context;
    }

    private int current_index = 0;
    private final static List<Integer> button_id = Arrays.asList(
            R.id.main_select_home
            , R.id.main_select_notify
            , R.id.main_select_my_project
            , R.id.main_select_message
            , R.id.main_select_user
    );
    private final static String[] fragment_name = new String[]{"主页", "通知", "我的项目", "私信", "用户"};
    private final static Class[] target_class = new Class[]{FrMain.class, FrNotify.class, FrMyProject.class, FrMessage.class, FrUser.class};
    ImageView[] selector = new ImageView[button_id.size()];
    @BindView(R.id.toolbar)
    TextView toolbar;
    public FrMetro metro;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    void init() {
        ButterKnife.bind(this);
        initSelector();
        metro = context.fm;
        metro.goToFragment(FrMain.class);
    }

    @OnClick({R.id.main_select_home
            , R.id.main_select_notify
            , R.id.main_select_message
            , R.id.main_select_user
            , R.id.main_select_my_project})
    public void onClick(View v) {
//        if (v.getId() == R.id.main_select_my_project) {
//            Flowable.just(util.obtain_message(Main.ACTION_NEW_ASSIGNMENT))
//                    .compose(context.<Message>bindUntilEvent(ActivityEvent.DESTROY))
//                    .subscribe(context.consumer);
//        } else {
        int idx = button_id.indexOf(v.getId());
        toolbar.setText(fragment_name[idx]);
        changeFocus(idx);
        metro.goToFragment(target_class[idx]);
    }

    private void initSelector() {
        for (int i = 0; i < button_id.size(); i++)
            selector[i] = (findViewById(button_id.get(i)));
//        for (ImageView imageView : selector)
//            imageView.setClickable(true);
        selector[0].setColorFilter(getResources().getColor(R.color.blue_button));
        // changeFocus(current_index);
    }

    //改变颜色
    private void changeFocus(int i) {
        if (i == current_index) return;
        int old = current_index;
        current_index = i;
        selector[current_index].setColorFilter(getResources().getColor(R.color.blue_button));
        selector[old].setColorFilter(Color.parseColor("#bdc3c7"));
    }
}
