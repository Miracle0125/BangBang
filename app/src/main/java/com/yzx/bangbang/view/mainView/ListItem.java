package com.yzx.bangbang.view.mainView;
import android.content.Context;

import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;


public class ListItem extends LinearLayout {
    Context context;
    Listener listener;
    int position;
    public boolean layoutExpanded;
    GestureDetectorCompat detector;
    int asm_id;
    public ListItem(Context context) {
        this(context, null, 0);
    }

    public ListItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        detector = new GestureDetectorCompat(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (listener != null) listener.onItemTouched(asm_id);
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    public interface Listener {
        void onItemTouched(int position);
    }

    public void registerListener(Listener listener) {
        this.listener = listener;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    public void setAssignmentId(int asm_id){
        this.asm_id = asm_id;
    }
}
