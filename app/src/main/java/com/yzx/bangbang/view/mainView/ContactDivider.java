package com.yzx.bangbang.view.mainView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yzx.bangbang.R;

public class ContactDivider extends LinearLayout {


    public ContactDivider(Context context) {
        this(context, null, 0);
    }

    public ContactDivider(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContactDivider(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setText(int c) {
        if (c < 0 || c > 26) return;
        ((TextView) findViewById(R.id.letter)).setText(c == 26 ? '#' : (char) ('A' + c));
    }
}
