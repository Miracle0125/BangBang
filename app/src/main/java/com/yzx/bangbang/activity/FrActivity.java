package com.yzx.bangbang.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.FrMetro;
import com.yzx.bangbang.utils.sql.SA;

public class FrActivity extends RxAppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        Class fragment = (Class) SA.query(SA.TYPE_FRACTIVITY_FRAGMENT);
        if (fragment == null) finish();
        setContentView(R.layout.fragment_activity_layout);
        fm = new FrMetro(getFragmentManager(), R.id.fragment_container);
        fm.goToFragment(fragment);
    }

    FrMetro fm;
}
