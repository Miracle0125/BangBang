package com.yzx.bangbang.adapter.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yzx.bangbang.activity.BrowseAssignment;
import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.util;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import model.Assignment;


public class AssignmentsAdapter extends RecyclerView.Adapter<AssignmentsAdapter.ViewHolder> {

    private List<Assignment> data;
    private Activity context;
    private static int pos = 0;
    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_WITH_IMAGE = 1;

    public AssignmentsAdapter(Context context) {
        this.context = (Activity) context;
    }

    int layout[] = {R.layout.main_list_item, R.layout.main_list_item_with_images};

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = context.getLayoutInflater().inflate(R.layout.browse_assignment_item, viewGroup, false);
        v.setOnClickListener(listener);
        return new ViewHolder(v);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder h, int i) {
        Assignment assignment = data.get(i);
        h.v.setTag(assignment);
        h.date.setText(util.transform_date(assignment.getDate()));
        h.title.setText(assignment.getTitle());
        h.num_servants.setText(assignment.getServants() + " 竞标");
        h.price.setText("¥" + assignment.getPrice());
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View v;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            this.v = v;
        }

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.num_servants)
        TextView num_servants;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.price)
        TextView price;

    }


    View.OnClickListener listener;


    @Override
    public int getItemViewType(int pos) {
        return data.get(pos).getImages() > 0 ? TYPE_WITH_IMAGE : TYPE_DEFAULT;
    }

    public void setData(List<Assignment> data) {
        this.data = data;
    }

    public void appendData(List<Assignment> new_data) {
        if (new_data == null) {
            return;
        }
        data.addAll(new_data);
    }

    public int get_last_id() {
        if (data.isEmpty()) return -1;
        return data.get(data.size() - 1).getId();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

}
