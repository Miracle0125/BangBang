package com.yzx.bangbang.view.mainView;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import com.facebook.drawee.view.SimpleDraweeView;
import java.lang.ref.WeakReference;

public class Portrait extends SimpleDraweeView {
    GestureDetectorCompat detector;
    Object obj;
    WeakReference<Handler> handlerRef;
    int what,arg1,arg2;
    public Portrait(Context context) {
        this(context, null, 0);
    }
    public Portrait(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private void onSingleTab(){
        Message message = Message.obtain();
/*        Bundle data = new Bundle();
        data.putInt("asm_id",asm_id);
        message.obj = data;*/
        message.what = what;
        message.obj = obj;
        message.arg1 =arg1;
        message.arg2 = arg2;
        if (handlerRef.get()!=null)
            handlerRef.get().sendMessage(message);
    }
public void setMessageObj(Object obj){
    this.obj = obj;
}
    public void setMessageArg(int what,int arg1,int arg2){
        this.what = what;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }
    public void setMessageArg(int what){
        this.setMessageArg(what,0,0);
    }
public void setHandler(Handler handler){
    handlerRef = new WeakReference<>(handler);
}
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
        //return super.onTouchEvent(event);
    }
    public Portrait(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

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
                onSingleTab();
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
}
