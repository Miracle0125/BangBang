package com.yzx.bangbang.fragment.Main;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.FrMetro;
import com.yzx.bangbang.view.Common.ToggleLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        toggleLayout.setText("消息", "联系人");
        toggleLayout.onToggleListener = pos -> fm.goToFragment(fragments[pos]);
    }

    @BindView(R.id.toggle_layout)
    ToggleLayout toggleLayout;
    private static final Class[] fragments = new Class[]{FrChatRecords.class, FrContacts.class};

}
