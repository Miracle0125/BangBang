package com.yzx.bangbang.Fragment.Common;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.yzx.bangbang.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

public class FormFragment extends Fragment {
    int style = 1;
    int res;
    View view;
    List<EditText> inputs = new ArrayList<>();
    Consumer<String[]> consumer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(res, container, false);
        ButterKnife.bind(this, view);
        adapt((ViewGroup) view);
        return view;
    }

    private void adapt(ViewGroup root) {
        if (style == STYLE_EDIT) {
            for (int i = 0; i < root.getChildCount(); i++) {
                View v = root.getChildAt(i);
                if (v instanceof EditText) {
                    inputs.add((EditText) root.getChildAt(i));
                } else if (v instanceof ViewGroup) {
                    adapt((ViewGroup) v);
                }
            }
        } else if (style == STYLE_LIST) {

        }
    }

    @OnClick(R.id.form_fr_btn)
    void onclick() {
        String[] ret = new String[inputs.size()];
        for (int i = 0; i < inputs.size(); i++) {
            ret[i] = inputs.get(i).getText().toString();
        }
        if (consumer != null)
            Flowable.just(ret).subscribe(consumer);
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public void setConsumer(Consumer<String[]> consumer) {
        this.consumer = consumer;
    }

    public static final int MaxColumn = 6;
    public static final int STYLE_EDIT = 1;
    public static final int STYLE_LIST = 2;

}
