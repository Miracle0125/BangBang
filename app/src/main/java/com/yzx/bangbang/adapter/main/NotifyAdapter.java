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

    private static final String[] model = {
            "%s 投标了 %s",
            "您赢得了 %s 的竞标",
            "您的需求 %s 已被完成",
            "您为 %s 提交的成果已通过",
            "很遗憾，您为 %s 提交的成果未通过",
            "很遗憾，您的威客不能完成需求 %s "};
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
        h.content.setText(Html.fromHtml(build_notify_text(notify)));
        if (notify.read == 1) {
            h.root_layout.setBackgroundColor(Params.COLOR_WHITE);
        }
        h.host_portrait.setImageURI(Retro.get_portrait_uri(notify.person_id));
        if (onClickListener != null)
            h.root_layout.setOnClickListener(onClickListener);
        h.root_layout.setTag(i);
    }

    private String build_notify_text(Notify notify) {
        if (notify.what == 0)
            return String.format(model[0], notify.person_name, bold_text(notify.relate_str));
        return String.format(model[notify.what], bold_text(notify.relate_str));
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
