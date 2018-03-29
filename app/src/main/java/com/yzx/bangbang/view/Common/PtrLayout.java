package com.yzx.bangbang.view.Common;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import com.yzx.bangbang.activity.common.PtrActivity;
import com.yzx.bangbang.widget.Ptr.MaterialHeader;
import com.yzx.bangbang.widget.PtrUIListener;

public class PtrLayout extends RelativeLayout {
    PtrActivity ptrActivity;
    public PtrLayout(Context context) {
        this(context, null, 0);
    }

    public PtrLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PtrLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ptrActivity= (PtrActivity) context;
    }

    float lastY;
    int translation;
    boolean startPtr;
    PtrUIListener ptrUIListener;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //ViewGroup fragment = (ViewGroup) findViewById(R.id.main_fr_container);
        if (ptrUIListener != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastY = ev.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(ptrActivity.scroll==null) return false;
                    float d=(ev.getY()-lastY);
                    lastY=ev.getY();
                    if(d>0&& isScrollToTop(d)||d<0&&translation>0||startPtr){
                        ptrUIListener.onUIPositionChange(compute(d));
                        return false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (translation > MaterialHeader.height) {
                        ptrUIListener.onUIRefreshBegin();
                        ptrActivity.onRefresh();
                    } else {
                        ptrUIListener.onUIReset();
                    }
                    boolean ret = translation > 0;
                    reset();
                    if(ret) return false;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void registerPtrUIListener(PtrUIListener listener) {
        ptrUIListener = listener;
    }

    void reset() {
        lastY = 0;
        translation = 0;
        startPtr = false;
    }

    private boolean isScrollToTop(float dy){
        return ptrActivity.scroll.getScrollY()==0&&dy>0;
    }

    private int compute(float d) {
        startPtr=true;
        translation += d / 2;
        translation = Math.max(0, translation);
        translation = Math.min(translation, (int) (MaterialHeader.height * 1.2));
        return translation;
    }
}
