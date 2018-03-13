package com.yzx.bangbang.view.mainView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yzx.bangbang.activity.NewAssignment;
import com.yzx.bangbang.Fragment.Main.FrMain;
import com.yzx.bangbang.Fragment.Main.FrMessage;
import com.yzx.bangbang.Fragment.Main.FrNews;
import com.yzx.bangbang.Fragment.Main.FrUser;
import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.FrMetro;
import com.yzx.bangbang.utils.Params;
import java.util.Arrays;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;


public class MainLayout extends RelativeLayout {
    Activity context;

    public MainLayout(Context context) {
        this(context, null, 0);
    }

    public MainLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = (Activity) context;
        init();
    }

    private static int current_index = 0;
    List<Integer> button_id = Arrays.asList(
            R.id.main_select_main
            , R.id.main_select_news
            , R.id.main_select_message
            , R.id.main_select_client
            , R.id.main_new_assign);
    String[] fragment_name = {"主页", "消息", "私信", "用户", ""};
    Class[] target_class = {FrMain.class, FrNews.class, FrMessage.class, FrUser.class, NewAssignment.class};
    ImageView[] selector = new ImageView[button_id.size()];
    @BindView(R.id.toolbar)
    TextView toolbar;
    public FrMetro metro;

    void init() {
        initSelector();
        metro= new FrMetro(context.getFragmentManager(), R.id.main_fr_container);
        metro.goToFragment(FrMain.class);
    }

    @OnClick({R.id.main_select_main
            , R.id.main_select_news
            , R.id.main_select_message
            , R.id.main_select_client
            , R.id.main_new_assign})
    public void onClick(View v) {
        if (v.getId() == R.id.main_new_assign) {
            Intent intent = new Intent(context, NewAssignment.class);
            //intent.putExtra("user", user);!!
            context.startActivity(intent);
        } else {
            int idx = button_id.indexOf(v.getId());
            toolbar.setText(fragment_name[idx]);
            changeFocus(idx);
            metro.goToFragment(target_class[idx]);
        }
    }

    private void initSelector() {
        for (int i = 0; i < button_id.size(); i++)
            selector[i] = ((ImageView) findViewById(button_id.get(i)));
        for (ImageView imageView : selector)
            imageView.setClickable(true);
//            ntf_news = (TextView) findViewById(R.id.main_ntf_circle0);
//            ntf_message = (TextView) findViewById(R.id.main_ntf_circle1);
        selector[0].setColorFilter(Params.COLOR_BLUE_MAIN);
        changeFocus(current_index);
    }

    //改变颜色
    private void changeFocus(int i) {
        if (i == current_index) return;
        int old = current_index;
        current_index = i;
        selector[current_index].setColorFilter(Params.COLOR_BLUE_MAIN);
        selector[old].setColorFilter(Params.COLOR_GRAY);
    }
}
