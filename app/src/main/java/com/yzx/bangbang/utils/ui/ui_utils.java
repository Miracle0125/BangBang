package com.yzx.bangbang.utils.ui;

import android.graphics.Rect;
import android.view.View;

import com.yzx.bangbang.utils.Params;

public class ui_utils {

    public static void fluctuating_bottom(View decorView, View bottom) {
        Rect rect = new Rect();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            decorView.getWindowVisibleDisplayFrame(rect);
            bottom.setTranslationY(-(Params.screenHeight - rect.height()));
        });
    }
}
