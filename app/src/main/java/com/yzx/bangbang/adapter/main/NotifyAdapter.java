package com.yzx.bangbang.adapter.main;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.yzx.bangbang.R;
import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.model.Notify;
import com.yzx.bangbang.utils.Params;
import com.yzx.bangbang.utils.netWork.Retro;
import com.yzx.bangbang.utils.util;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/4/2.
 */

public class NotifyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<Notify> notifies = new ArrayList<>();
    private View.OnClickListener onClickListener;
    private Main context;


    public NotifyAdapter(Main context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = context.getLayoutInflater().inflate(R.layout.main_notify, viewGroup, false);
        return new NotifyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        NotifyViewHolder h = (NotifyViewHolder) holder;
        Notify notify = notifies.get(i);
        h.date.setText(util.transform_date(notify.date));
        h.content.setText(Html.fromHtml(notify.person_name + " 投标了 " + bold_text(notify.relate_str)));
        if (notify.read == 1) {
            h.root_layout.setBackgroundColor(Params.COLOR_WHITE);
        }
        h.host_portrait.setImageURI(Retro.get_portrait_uri(notify.person_id));
        if (onClickListener != null)
            h.root_layout.setOnClickListener(onClickListener);
        h.root_layout.setTag(i);
    }

    private String bold_text(String s) {
        return "<b><tt>" + s + "</tt></b>";
    }

    @Override
    public int getItemCount() {
        return notifies.size();
    }

    public static class NotifyViewHolder extends RecyclerView.ViewHolder {

        public NotifyViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        @BindView(R.id.content)
        TextView content;

        @BindView(R.id.date)
        TextView date;

        @BindView(R.id.host_portrait)
        SimpleDraweeView host_portrait;

        @BindView(R.id.root_layout)
        ViewGroup root_layout;

        @OnClick(R.id.root_layout)
        void click(View v) {

        }

    }


    public List<Notify> getNotifies() {
        return notifies;
    }

    public void setNotifies(List<Notify> notifies) {
        this.notifies = notifies;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
