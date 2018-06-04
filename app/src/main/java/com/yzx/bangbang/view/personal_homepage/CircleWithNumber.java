package com.yzx.bangbang.view.personal_homepage;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yzx.bangbang.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/13.
 */

public class CircleWithNumber extends LinearLayout {
    public CircleWithNumber(Context context) {
        this(context, null, 0);
    }

    public CircleWithNumber(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public CircleWithNumber(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this, this);
    }

    public void set_number(int i) {
        if (i == -1) return;
        text_number.setText(i + "");
    }

    public void set_number(float i) {
        if (i == -1) return;
        text_number.setText(i + "");
    }

    public void set_spec(String s) {
        text_spec.setText(s);
    }

    @BindView(R.id.text_number)
    TextView text_number;
    @BindView(R.id.text_special)
    TextView text_spec;
}
