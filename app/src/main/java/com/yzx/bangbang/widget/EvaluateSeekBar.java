package com.yzx.bangbang.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yzx.bangbang.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/19.
 */

public class EvaluateSeekBar extends LinearLayout {
    public EvaluateSeekBar(Context context) {
        this(context, null, 0);
    }

    public EvaluateSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EvaluateSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    public float get_evaluate() {
        return Float.parseFloat(text_evaluate.getText().toString());
    }

    private void init() {
        ButterKnife.bind(this, this);
        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                text_evaluate.setText(String.valueOf(progress * UNIT));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @BindView(R.id.seek_bar)
    SeekBar seek_bar;
    @BindView(R.id.text_evaluate)
    TextView text_evaluate;

    private static final float UNIT = 0.1f;
}
