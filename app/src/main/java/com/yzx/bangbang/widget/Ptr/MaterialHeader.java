package com.yzx.bangbang.widget.Ptr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.yzx.bangbang.utils.util;
import com.yzx.bangbang.widget.PtrUIListener;

import in.srain.cube.views.ptr.PtrFrameLayout;

public class MaterialHeader extends View implements PtrUIListener {

    private MaterialProgressDrawable mDrawable;
    private float mScale = 1f;
    private PtrFrameLayout mPtrFrameLayout;
    public int state;
    public static final int STATE_PREPARED = 0;
    public static final int STATE_ON_REFRESH = 1;
    public static final int STATE_ON_CANCEL = 2;


    private Animation EndAnimation = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            mScale = 1f - interpolatedTime;
            mDrawable.setAlpha((int) (255 * mScale));
            invalidate();
        }
    };

    public MaterialHeader(Context context) {
        this(context, null, 0);
    }

    public MaterialHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }
    private void initView() {
        state = STATE_PREPARED;
        mDrawable = new MaterialProgressDrawable(getContext(), this);
        mDrawable.setBackgroundColor(Color.WHITE);
        //??
        mDrawable.setCallback(this);
        EndAnimation.setDuration(200);
        EndAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onUIReset();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void invalidateDrawable(Drawable dr) {
        if (dr == mDrawable) {
            invalidate();
        } else {
            super.invalidateDrawable(dr);
        }
    }

    public void setColorSchemeColors(int[] colors) {
        mDrawable.setColorSchemeColors(colors);
        invalidate();
    }

    public static int height;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        height = mDrawable.getIntrinsicHeight() + getPaddingTop() + getPaddingBottom();
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int size = mDrawable.getIntrinsicHeight();
        mDrawable.setBounds(0, 0, size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int saveCount = canvas.save();
        //Rect rect = mDrawable.getBounds();
        int l = getPaddingLeft() + (getMeasuredWidth() - mDrawable.getIntrinsicWidth()) / 2;
        canvas.translate(l, getPaddingTop());
        //canvas.scale(mScale, mScale, rect.exactCenterX(), rect.exactCenterY());
        mDrawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }



    @Override
    public void onUIRefreshBegin() {
        mDrawable.setAlpha(255);
        mDrawable.start();
        state = STATE_ON_REFRESH;
/*        postDelayed(new Runnable() {
            @Override
            public void run() {
                startAnimation(EndAnimation);
            }
        }, 3000);*/
    }

    @Override
    public void onUIRefreshCanceled() {
        state = STATE_ON_CANCEL;
        startAnimation(EndAnimation);
    }

    @Override
    public void onUIPositionChange(int translation) {
        util.Animate(this, translation,util.VERTICAL,0);
        //scrollBy(0, -translation);

        float percent = Math.min(1f, (float)1*translation/height);
        mDrawable.setAlpha((int) (255 * percent));
        mDrawable.showArrow(true);

        float strokeStart = ((percent) * .8f);
        mDrawable.setStartEndTrim(0f, Math.min(0.8f, strokeStart));
        mDrawable.setArrowScale(Math.min(1f, percent));

        // magic
        float rotation = (-0.25f + .4f * percent + percent * 2) * .5f;
        mDrawable.setProgressRotation(rotation);
        invalidate();
    }
    @Override
    public void onUIReset() {
        mScale = 1f;
        //50毫秒的误差，有可能导致BUG
        util.Animate(this, 0, util.VERTICAL, 300);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawable.stop();
                mDrawable.setAlpha(255);
                mDrawable.invalidateSelf();
                invalidate();
                state = STATE_PREPARED;
            }
        },350);
    }
    public PtrUIListener getUIListener(){
        return this;
    }
}

