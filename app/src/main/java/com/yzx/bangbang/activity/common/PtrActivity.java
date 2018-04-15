package com.yzx.bangbang.activity.common;


import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.util;
import com.yzx.bangbang.widget.Ptr.MaterialHeader;
import com.yzx.bangbang.widget.PtrUIListener;
/**
 * Created by Administrator on 2018/3/9.
 */

public abstract class PtrActivity extends RxAppCompatActivity {
    public MaterialHeader header;
    //PtrUIListener ptrUIListener;
    public View scroll;

    public void prepare(View v) {
        prepare(v, new RelativeLayout.LayoutParams(-1, -1));
    }

    public void prepare(View v, RelativeLayout.LayoutParams rl) {
        ViewGroup ptrLayout = (ViewGroup) View.inflate(this, R.layout.common_ptr, null);
        header = ptrLayout.findViewById(R.id.ptr_header);
        header.setColorSchemeColors(getResources().getIntArray(R.array.ptr_colors));
        header.setPadding(0, util.dp2px(15), 0, util.dp2px(10));
        ptrUIListener = header.getUIListener();
        //ptrLayout.registerPtrUIListener(ptrUIListener);
        ptrLayout.addView(v, rl);
        setContentView(ptrLayout);
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
                    if(scroll==null) return false;
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
                        onRefresh();
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

    void reset() {
        lastY = 0;
        translation = 0;
        startPtr = false;
    }

    private boolean isScrollToTop(float dy){
        return scroll.getScrollY()==0&&dy>0;
    }

    private int compute(float d) {
        startPtr=true;
        translation += d / 2;
        translation = Math.max(0, translation);
        translation = Math.min(translation, (int) (MaterialHeader.height * 1.2));
        return translation;
    }

    public void setPtrMargin(int margin) {
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(-1, -1);
        header.setLayoutParams(rl);
    }

    public void onRefresh() {

    }

    public void onFinishRefresh() {
        ptrUIListener.onUIRefreshCanceled();
    }

}



