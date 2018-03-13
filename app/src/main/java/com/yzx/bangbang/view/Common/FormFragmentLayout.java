package com.yzx.bangbang.view.Common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.yzx.bangbang.utils.util;



/**
 * Created by Administrator on 2018/3/10.
 */

public class FormFragmentLayout extends LinearLayout {
    private static final float MINIMUM_DISTANCE = 220f;
    Context context;

    public FormFragmentLayout(Context context) {
        this(context, null, 0);
    }

    public FormFragmentLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FormFragmentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initDetector();
    }

    private float translation = 0;
    private float rawY = 0;

    private void initDetector() {
        View v = ((Activity) context).getWindow().getDecorView();
        v.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (!util.IsKeyBoardRose(v)) {
                translation = 0;
                util.Animate(this, translation, util.VERTICAL, 150);
            } else {
                float d = getVisibleRect(v).bottom - rawY;
                if (d < MINIMUM_DISTANCE) {
                    translation += -(MINIMUM_DISTANCE - d);
                    util.Animate(this, translation, util.VERTICAL, 150);
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            rawY = ev.getRawY();
        }
        return super.dispatchTouchEvent(ev);
    }

    private Rect getVisibleRect(View decorView) {
        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        return rect;
    }
}

