package com.yzx.bangbang.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.yzx.bangbang.R;
import com.yzx.bangbang.activity.AssignmentDetail;
import com.yzx.bangbang.model.Comment;

import java.util.List;

/**
 * Created by Administrator on 2018/3/14.
 */

public class AssignmentDetailAdapter extends RecyclerView.Adapter<AssignmentDetailAdapter.ViewHolder> {

    private List<Comment> data;
    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_HEADER = 1;
    private AssignmentDetail context;

    public AssignmentDetailAdapter(AssignmentDetail context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 0) {
            View v = context.getLayoutInflater().inflate(R.layout.asm_detail_header, viewGroup, false);
            return new ViewHolder(v, i);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_DEFAULT;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView, int type) {
            super(itemView);
        }
    }
}
