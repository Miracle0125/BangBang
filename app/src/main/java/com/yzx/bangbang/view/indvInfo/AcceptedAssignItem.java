package com.yzx.bangbang.view.indvInfo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.yzx.bangbang.module.EventModule;

/**
 * Created by Administrator on 2016/12/5.
 */
public class AcceptedAssignItem extends RelativeLayout {
    public EventModule data;
    public AcceptedAssignItem(Context context) {
        this(context, null, 0);
    }

    public AcceptedAssignItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AcceptedAssignItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setData(EventModule data){
        this.data = data;
    }
}
