package com.yzx.bangbang.utils.ui;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.yzx.bangbang.utils.util;

/**
 * Created by Administrator on 2018/3/16.
 */

public class LayoutParamUtil {
    public static void wh(ViewGroup v, int w, int h) {
        ViewGroup.LayoutParams p = v.getLayoutParams();
        if (w != -1)
            p.width = w;
        if (h != -1)
            p.height = h;
        v.setLayoutParams(p);
    }

    public static void trans_y(ViewGroup v, float y) {
        util.Animate(v, y, util.VERTICAL, 100);
    }
}
