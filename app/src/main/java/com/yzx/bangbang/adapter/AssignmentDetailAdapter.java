package com.yzx.bangbang.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yzx.bangbang.R;
import com.yzx.bangbang.activity.AssignmentDetail;
import com.yzx.bangbang.model.Comment;
import com.yzx.bangbang.utils.NetWork.Retro;
import com.yzx.bangbang.utils.util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import model.Assignment;

/**
 * Created by Administrator on 2018/3/14.
 */

public class AssignmentDetailAdapter extends RecyclerView.Adapter<AssignmentDetailAdapter.ViewHolder> {

    private List<Comment> comments = new ArrayList<>();
    public Assignment assignment;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_COMMENT = 1;
    private AssignmentDetail context;
    public AssignmentDetailAdapter adapter = this;

    public AssignmentDetailAdapter(AssignmentDetail context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        int[] layout = {R.layout.asm_detail_header, R.layout.asm_detail_comment};
        View v = context.getLayoutInflater().inflate(layout[i], viewGroup, false);
        return new ViewHolder(v);
    }

    @SuppressWarnings("all")
    @Override
    public void onBindViewHolder(ViewHolder h, int i) {
        if (i == TYPE_HEADER) {
            h.title.setText(assignment.getTitle());
            h.content.setText(assignment.getContent());
            h.host_name.setText(assignment.getEmployer_name());
            h.date.setText(util.transform_date(assignment.getDate()));
            h.price.setText(util.s(assignment.getPrice()));
            h.price.setTextColor(util.price_color(assignment.getPrice()));
            h.num_comments.setText(assignment.getFloors() + "");
            h.num_servants.setText(assignment.getRepliers() + "");
            h.host_portrait.setImageURI(Retro.get_portrait_uri(assignment.getEmployer_id()));
        } else {
            h.content.setText(comments.get(i).content);
            h.date.setText(util.transform_date(comments.get(i).date));
            h.host_name.setText(comments.get(i).poster_name);
            h.num_comments.setText(comments.get(i).floors);
            h.host_portrait.setImageURI(Retro.get_portrait_uri(comments.get(i).getPosterId()));
        }
    }

    @Override
    public int getItemCount() {
        return comments.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_COMMENT;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        //有些是通用的: host name/portrait content date
        //TextView title, content, host_name, date, price, num_servants, num_comments;
        @Nullable
        @BindView(R.id.title)
        TextView title;
        @Nullable
        @BindView(R.id.content)
        TextView content;
        @Nullable
        @BindView(R.id.host_name)
        TextView host_name;
        @Nullable
        @BindView(R.id.date)
        TextView date;
        @Nullable
        @BindView(R.id.num_servants)
        TextView num_servants;
        @Nullable
        @BindView(R.id.num_comments)
        TextView num_comments;
        @Nullable
        @BindView(R.id.price)
        TextView price;
        @Nullable
        @BindView(R.id.subscribe)
        Button subscribe;
        @Nullable
        @BindView(R.id.collect)
        Button collect;
        @Nullable
        @BindView(R.id.button_servants)
        View button_servants;
        @Nullable
        @BindView(R.id.host_portrait)
        SimpleDraweeView host_portrait;

        @Optional
        @OnClick({R.id.collect, R.id.subscribe, R.id.button_servants, R.id.host_portrait})
        void click(View v) {
            if (listener != null)
                listener.click(v);
        }
    }


    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }


    public static ClickListener listener;

    public void setClickListener(ClickListener listener) {
        this.listener = listener;
    }

    public interface ClickListener {
        void click(View v);
    }

}
