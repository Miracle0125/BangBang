package com.yzx.bangbang.view.mainView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;


public class AlphabetView extends LinearLayout {
    private Context context;
    private int h;
    public OnTouchListener listener;
    public static final int LETTER_NUMBER = 28;


    public AlphabetView(Context context) {
        this(context, null, 0);
    }

    public AlphabetView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlphabetView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        addView(build_text("↑"));

        for (char i = 'A'; i <= 'Z'; i++) {
            final String character = i + "";
            TextView tv = build_text(character);

            addView(tv);
        }

        addView(build_text("#"));
    }

    private LayoutParams layoutParams = new LayoutParams(-1, -1, 1);

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        h = getHeight() / 28;
    }

    private TextView build_text(final String character) {
        TextView tv = new TextView(context);
        tv.setLayoutParams(layoutParams);
        tv.setGravity(Gravity.CENTER);
        tv.setClickable(true);
        tv.setText(character);
        return tv;
    }

    // private static final String[] log_str = {"down", "up", "move"};

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //Log.e("contact", "receive ev" + log_str[ev.getAction()]);
        if (ev.getAction() != MotionEvent.ACTION_UP) {
            if (listener != null && h != 0) {
                int pos = (int) (ev.getY() / h);
                if (pos < 0) pos = 0;
                if (pos > 27) pos = 27;
//                pos = Math.max(0, pos);
//                pos = Math.min(27, pos);
                listener.onTouch(pos);
                //Log.e("contact", "y  " + ev.getY() + "h   " + h);
            }
        } else if (listener != null) {
            //Log.e("contact", "on leave");
            listener.onLeave();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public interface OnTouchListener {
        void onTouch(int pos);

        void onLeave();
    }
}