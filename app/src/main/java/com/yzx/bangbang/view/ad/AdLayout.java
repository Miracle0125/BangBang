package com.yzx.bangbang.view.ad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class AdLayout extends RelativeLayout {
    public AdLayout(Context context) {
        super(context);
    }

    public AdLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean customDraw;
    Bitmap src;

    @Override
    protected void onDraw(Canvas canvas) {
        if (!customDraw)
            super.onDraw(canvas);
        else {
            drawBitmap(canvas);
        }
    }

    public void drawBitmap(Canvas canvas) {
        if (src == null)
            return;
        Paint p = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix(new float[]{
                2, 0, 0, 0, 0,
                0, 2, 0, 0, 0,
                0, 0, 2, 0, 0,
                0, 0, 0, 1, 0,
        });
        p.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(src, 0, 0, p);
    }
}
