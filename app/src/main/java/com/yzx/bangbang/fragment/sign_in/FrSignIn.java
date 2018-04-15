package com.yzx.bangbang.fragment.sign_in;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yzx.bangbang.R;
import com.yzx.bangbang.activity.SignIn;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

/**
 * Created by Administrator on 2018/4/10.
 */

public class FrSignIn extends Fragment {


    public int res;
    public Consumer<String[]> consumer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(res, container, false);
        ButterKnife.bind(this, v);
        ets = new EditText[]{form_fr_input0, form_fr_input1, form_fr_input2, form_fr_input3};
        return v;
    }


    private void commit_form_data() {
        if (consumer == null) {
            return;
        }
        String s[] = new String[4];
        int idx = 0;
        for (EditText t : ets)
            if (t != null)
                s[idx++] = t.getText().toString();
        Flowable.just(s).compose(context().<String[]>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    private SignIn context(){
        return (SignIn) getActivity();
    }

    @BindView(R.id.form_fr_input0)
    EditText form_fr_input0;
    @BindView(R.id.form_fr_input1)
    EditText form_fr_input1;
    @Nullable
    @BindView(R.id.form_fr_input2)
    EditText form_fr_input2;
    @Nullable
    @BindView(R.id.form_fr_input3)
    EditText form_fr_input3;
    EditText[] ets;

    @OnClick({R.id.button_main})
    void click(View v) {
        commit_form_data();
    }
}
