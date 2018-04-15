package com.yzx.bangbang.fragment.Main;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.FrMetro;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FrMessage extends Fragment {
    View v;
    FrMetro fm;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.main_fr_message, container, false);
        init();
        return v;
    }

    private void init() {
        ButterKnife.bind(this, v);
        fm = new FrMetro(getFragmentManager(), R.id.fragment_container);
        fm.goToFragment(fragments[0]);
        toggle_background = new FrameLayout[]{toggle_message, toggle_contacts};
        toggle_tv = new TextView[]{text_message, text_contacts};
    }


    @OnClick({R.id.toggle_message, R.id.toggle_contacts})
    void toggle(View v) {
        if (v.getId() == R.id.toggle_message) {
            if (toggle_flag == 0) return;
            high_light(0);
            fm.goToFragment(fragments[0]);
        } else {
            if (toggle_flag == 1) return;
            high_light(1);
            fm.goToFragment(fragments[1]);
        }
    }

    private void high_light(int pos) {
        toggle_flag = pos;
        toggle_background[pos].setBackgroundColor(getResources().getColor(R.color.blue_button));
        toggle_tv[pos].setTextColor(getResources().getColor(R.color.white));
        if (++pos > 1) pos = 0;
        toggle_background[pos].setBackgroundColor(getResources().getColor(R.color.white));
        toggle_tv[pos].setTextColor(getResources().getColor(R.color.blue_button));
    }

    private void go_to_fragment(int pos) {

    }

    private Main context() {
        return ((Main) getActivity());
    }

    @BindView(R.id.toggle_message)
    FrameLayout toggle_message;
    @BindView(R.id.toggle_contacts)
    FrameLayout toggle_contacts;
    @BindView(R.id.text_message)
    TextView text_message;
    @BindView(R.id.text_contacts)
    TextView text_contacts;
    FrameLayout[] toggle_background;
    TextView[] toggle_tv;
    private static final Class[] fragments = new Class[]{FrChatRecords.class,FrContacts.class};
    private int toggle_flag = 0;

}
