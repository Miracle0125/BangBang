package com.yzx.bangbang.fragment.Main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yzx.bangbang.R;

/**
 * Created by Administrator on 2018/4/10.
 */

public class FrChatRecords extends Fragment {
    View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.main_fr_message_list, container, false);
        return v;
    }
}
