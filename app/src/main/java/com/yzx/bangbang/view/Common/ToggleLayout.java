package com.yzx.bangbang.view.Common;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yzx.bangbang.R;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ToggleLayout extends LinearLayout {

    public ToggleLayout(Context context) {
        this(context, null, 0);
    }

    public ToggleLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this, this);
        toggles = new FrameLayout[]{toggle0, toggle1};
        tvs = new TextView[]{text0, text1};
//        toggle0 = findViewById(R.id.toggle0);
//        toggle1 = findViewById(R.id.toggle1);
    }

    public void setText(String text0, String text1) {
        this.text0.setText(text0);
        this.text1.setText(text1);
    }

    @OnClick({R.id.toggle0, R.id.toggle1})
    void toggle(View v) {
        int pos = toggle_id.indexOf(v.getId());
        if (pos == current_toggle) return;
        high_light(pos);
        if (onToggleListener != null) {
            onToggleListener.onToggle(pos);
        }
    }

    private void high_light(int pos) {
        current_toggle = pos;
        toggles[pos].setBackgroundColor(getResources().getColor(R.color.blue_button));
        tvs[pos].setTextColor(getResources().getColor(R.color.white));
        if (++pos > 1) pos = 0;
        toggles[pos].setBackgroundColor(getResources().getColor(R.color.white));
        tvs[pos].setTextColor(getResources().getColor(R.color.blue_button));
    }

    public OnToggleListener onToggleListener;

    public interface OnToggleListener {
        void onToggle(int pos);
    }

    private FrameLayout[] toggles;
    private List<Integer> toggle_id = Arrays.asList(R.id.toggle0, R.id.toggle1);
    private TextView[] tvs;
    int current_toggle = 0;
    @BindView(R.id.toggle0)
    FrameLayout toggle0;
    @BindView(R.id.toggle1)
    FrameLayout toggle1;
    @BindView(R.id.text0)
    TextView text0;
    @BindView(R.id.text1)
    TextView text1;
}
