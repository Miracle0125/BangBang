package com.yzx.bangbang.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yzx.bangbang.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EvaluateView extends LinearLayout {
    public EvaluateView(Context context) {
        this(context, null, 0);
    }

    public EvaluateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EvaluateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @BindView(R.id.evaluate_container)
    ViewGroup evaluate_container;
    @BindView(R.id.evaluate)
    TextView text_evaluate;
    @BindView(R.id.evaluate_star)
    SimpleDraweeView evaluate_star;
    @BindView(R.id.text_no_evaluate)
    TextView text_no_evaluate;


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        inflate_finished = true;
        if (evaluate != -2f)
            initView();
    }

    public void setEvaluate(float evaluate) {
        this.evaluate = evaluate;
        if (inflate_finished)
            initView();
    }

    private void initView() {
        if (evaluate == -1) {
            evaluate_container.setVisibility(GONE);
            text_no_evaluate.setVisibility(VISIBLE);
        } else {
            text_evaluate.setText(evaluate + "");
            if (evaluate > 4.8) {

            } else if (evaluate > 4f) {
                evaluate_star.setImageResource(R.drawable.stars_4);
            } else if (evaluate > 3f) {
                evaluate_star.setImageResource(R.drawable.stars_3);
            } else if (evaluate > 2f) {
                evaluate_star.setImageResource(R.drawable.stars_2);
            } else if (evaluate > 1f) {
                evaluate_star.setImageResource(R.drawable.stars_1);
            }
        }
    }

    private float evaluate = -2f;
    private boolean inflate_finished = false;

}
