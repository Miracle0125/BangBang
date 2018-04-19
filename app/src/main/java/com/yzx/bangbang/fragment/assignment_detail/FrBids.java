package com.yzx.bangbang.fragment.assignment_detail;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yzx.bangbang.R;
import com.yzx.bangbang.activity.AssignmentDetail;
import com.yzx.bangbang.adapter.assignment_detail.BidHolder;
import com.yzx.bangbang.interfaces.network.IAssignmentDetail;
import com.yzx.bangbang.model.Bid;
import com.yzx.bangbang.utils.netWork.Retro;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FrBids extends Fragment {
    View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.list_layout1, container, false);
        init();
        return v;
    }

    private void init() {
        ButterKnife.bind(this, v);
        toolbar_title.setText("全部投标");
        button_back.setOnClickListener(onClickListener);
        context = (AssignmentDetail) getActivity();
        adapter.setBids(context.bids);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }


    View.OnClickListener onClickListener = v -> {
        switch (v.getId()) {
            case R.id.button_back:
                exit();
                break;
            case R.id.button_choose:
                choose_bid((Bid) v.getTag());
                break;
        }
    };

    private void choose_bid(Bid bid) {
        Retro.single().create(IAssignmentDetail.class)
                .choose_bid(new Gson().toJson(bid))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.<Integer>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(r -> {
                    context.refresh();
                    Toast.makeText(context, r == 1 ? "成功" : "失败", Toast.LENGTH_SHORT).show();
                    exit();
                });
    }

    private void exit() {
        ((AssignmentDetail) getActivity()).fm.removeCurrent();
    }

    private class FrBidsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Bid> bids = new ArrayList<>();

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = context.getLayoutInflater().inflate(R.layout.asm_detail_bid, viewGroup, false);
            return new BidHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
            BidHolder h = (BidHolder) holder;
            Bid bid = bids.get(i);
            h.setOnClickListener(onClickListener);
            h.button_choose.setTag(bid);
            h.bind(bid, context.IS_USER_SAME_WITH_HOST, context.assignment.getStatus());
        }

        @Override
        public int getItemCount() {
            return bids.size();
        }

        public void setBids(List<Bid> bids) {
            this.bids = bids;
        }
    }

    @BindView(R.id.list)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.button_back)
    SimpleDraweeView button_back;

    public AssignmentDetail context;
    FrBidsAdapter adapter = new FrBidsAdapter();
}
