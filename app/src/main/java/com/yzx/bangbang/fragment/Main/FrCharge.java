package com.yzx.bangbang.fragment.Main;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yzx.bangbang.R;
import com.yzx.bangbang.interfaces.network.IMain;
import com.yzx.bangbang.utils.netWork.Retro;
import com.yzx.bangbang.utils.util;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import model.Assignment;


public class FrCharge extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.main_fr_charge, container, false);
        init();
        return v;
    }

    private void init() {
        ButterKnife.bind(this, v);
    }

    @OnClick(R.id.button_main)
    void click() {
        String in = editText.getText().toString();
        if (!util.isNumeric(in)) {
            Toast.makeText(context(), "输入不合法", Toast.LENGTH_SHORT).show();
            return;
        }
        int amount = Integer.parseInt(in);
        charge(util.get_user_id(), amount, r -> util.toast_binary(context(), r));
    }


    @SuppressLint("CheckResult")
    private void charge(int user_id, int amount, Consumer<Integer> consumer) {
        Retro.single().create(IMain.class)
                .charge(user_id, amount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context().bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    private RxAppCompatActivity context() {
        return (RxAppCompatActivity) getActivity();
    }

    View v;
    @BindView(R.id.edit)
    EditText editText;
}
