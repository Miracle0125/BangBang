package com.yzx.bangbang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/7/12.
 */
public abstract class ClassifyViewHelper extends ViewGroup {
    Context context;
    public abstract void addData(String[]... params);
    public abstract void insertView();
    //public abstract void setContext(Context context);

    public ClassifyViewHelper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        addData();
        insertView();
    }
}
