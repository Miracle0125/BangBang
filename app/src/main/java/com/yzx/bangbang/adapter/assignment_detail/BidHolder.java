package com.yzx.bangbang.adapter.assignment_detail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yzx.bangbang.R;
import com.yzx.bangbang.model.Bid;
import com.yzx.bangbang.utils.netWork.Retro;
import com.yzx.bangbang.widget.EvaluateView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/8.
 */
@SuppressWarnings("all")
public class BidHolder extends RecyclerView.ViewHolder {

    public BidHolder(View itemView) {
        super(itemView);
        v = itemView;
        ButterKnife.bind(this, itemView);
    }

    public void bind(Bid bid, boolean IS_USER_SAME_WITH_HOST, int status) {
        host_name.setText(bid.host_name);
        evaluate_view.setEvaluate(bid.evaluate);
        day_time.setText("在" + bid.day_time + "天内");
        price.setText("¥" + bid.price);
        host_portrait.setImageURI(Retro.get_portrait_uri(bid.host_id));
        if (!IS_USER_SAME_WITH_HOST || status > 0)
            button_choose.setVisibility(View.GONE);
        else if (onClickListener != null)
            button_choose.setOnClickListener(v -> {
                onClickListener.onClick(v);
            });
    }


    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    View.OnClickListener onClickListener;
    public View v;
    @BindView(R.id.host_name)
    TextView host_name;
    @BindView(R.id.host_portrait)
    SimpleDraweeView host_portrait;
    @BindView(R.id.day_time)
    TextView day_time;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.evaluate_view)
    EvaluateView evaluate_view;
    @BindView(R.id.button_choose)
    public Button button_choose;
}
