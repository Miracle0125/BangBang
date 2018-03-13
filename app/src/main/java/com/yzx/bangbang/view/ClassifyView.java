package com.yzx.bangbang.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.util;

import java.util.ArrayList;
import java.util.List;

public class ClassifyView extends ClassifyViewHelper {
    //List<View> ChildViews = new ArrayList<>();
    List<String[]> paramList;
    int RowMargin;
    int TextMargin;
    int RowHeight, rows;
    Context context;
    float scale;
    Listener listener;
    int ViewHeight;
    boolean initialized;

    @Override
    public void addData(String[]... params) {
    }

    @Override
    public void insertView() {
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        List<String[]> tempList = getParamList();


        for (int i = 0; i < tempList.size(); i++) {
            //count += tempList.get(i).length;
            for (int j = 0; j < tempList.get(i).length; j++) {
                //String[] tempAsb = tempList.get(i);
                TextView textView = new TextView(super.context);
                textView.setLayoutParams(lp);
                //textView.setTextColor(Color.parseColor("#FFFFFF"));
                textView.setTextAppearance(super.context, R.style.ClassifyText);
                textView.setText(tempList.get(i)[j]);
                addView(textView);
            }
        }
    }

    public ClassifyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        scale = context.getResources().getDisplayMetrics().density;
    }

    public ClassifyView(Context context) {
        this(context, null, 0);

    }

    public ClassifyView(Context context, Listener listener) {

        this(context, null, 0);
        this.listener = listener;
    }

    public ClassifyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    boolean mark;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        List<String[]> tempList = getParamList();
        int index = 0;
        int top = RowMargin;


        for (int i = 0; i < rows; i++) {

            int xCur = 0;

            for (int j = 0; j < tempList.get(i).length; j++) {
                View v = getChildAt(index);
                int width = v.getMeasuredWidth();
                int height = v.getMeasuredHeight();
                v.layout(xCur, top, xCur + width, top + height);
                xCur = xCur + width + TextMargin;
                index++;
            }
            top = top + RowHeight + RowMargin * 2;
        }

        if (!mark) {
            ///listener.onLayoutFinish(ViewHeight);
            //Main.HeaderHeight = ViewHeight;
            mark = true;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        rows = getParamList().size();
        RowHeight = getChildAt(0).getMeasuredHeight();
        RowMargin = util.px(8);
        TextMargin = util.px(3);
        int calcHeight = (RowMargin * 2 + RowHeight) * rows;


        int width = (widthMode == MeasureSpec.EXACTLY) ? sizeWidth : util.px(150);
        int height = (heightMode == MeasureSpec.EXACTLY) ? sizeHeight : calcHeight;

        ViewHeight = height;

        setMeasuredDimension(width, height);
    }

/*    private int px(int dipValue) {
        return (int) (dipValue * scale + 0.5f);
    }*/

    public void inputData(String[]... params) {
        for (String[] param : params) {
            if (paramList == null) paramList = new ArrayList<>();
            paramList.add(param);
        }
    }

    public List<String[]> getParamList() {
        return paramList;
    }

    public interface Listener {
        void onLayoutFinish(int height);

        boolean onInterceptTouchEvent_rl(MotionEvent ev);

        boolean onTouchEvent_rl(MotionEvent ev);
    }
}
