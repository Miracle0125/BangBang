package com.yzx.bangbang.view.mainView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.Params;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsLayout extends RelativeLayout {

    public ContactsLayout(Context context) {
        this(context, null, 0);
    }

    public ContactsLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContactsLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this, this);
    }

    @BindView(R.id.alphabet_view)
    LinearLayout alphabet_view;

    private static final String[] log_str = {"down", "up", "move"};

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (Params.screenWidth - ev.getRawX() < alphabet_view.getWidth()) {
            return alphabet_view.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }
}
