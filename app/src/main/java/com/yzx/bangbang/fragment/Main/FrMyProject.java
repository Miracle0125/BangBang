package com.yzx.bangbang.fragment.Main;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yzx.bangbang.R;
import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.utils.util;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;

/**
 * Created by Administrator on 2018/4/10.
 */

public class FrMyProject extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_fr_my_project, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @OnClick(R.id.button_new_asm)
    void click(View v) {
        if (v.getId() == R.id.button_new_asm)
            Flowable.just(util.obtain_message(Main.ACTION_NEW_ASSIGNMENT)).compose(context().<Message>bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(context().consumer);
    }


    private Main context() {
        return (Main) getActivity();
    }

}
