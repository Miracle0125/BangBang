package com.yzx.bangbang.view.ad;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2018/3/29.
 */

public class Diagonal extends View {
    Paint p = new Paint();
    public Diagonal(Context context) {
        this(context, null, 0);
    }

    public Diagonal(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Diagonal(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        p.setColor(Color.parseColor("#1EB5BD"));
        p.setStrokeWidth(4);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        canvas.drawLine(canvas.getWidth(),0,0,canvas.getHeight(),p);
    }
}
