package com.yzx.bangbang.view.indvInfo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2016/12/5.
 */
public class AssignListItem extends RelativeLayout {

    Assignment data;
    public AssignListItem(Context context) {
        this(context, null, 0);
    }

    public AssignListItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AssignListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void setData(Assignment data){
        this.data = data;
    }
}
