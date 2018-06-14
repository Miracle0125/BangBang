package com.yzx.bangbang.adapter.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yzx.bangbang.R;
import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.utils.util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FrMainAdapter extends RecyclerView.Adapter {

    public FrMainAdapter(Main main) {
        context = main;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = context.getLayoutInflater().inflate(layout[viewType], parent, false);
        if (viewType == TYPE_HEADER) return new HeaderHolder(v);
        return new ClassificationItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) return;
        int new_pos = position - 1;
        ClassificationItemHolder h = (ClassificationItemHolder) holder;
        h.text.setText(classification[new_pos]);
        h.v.setTag(new_pos);
        if (onClickListener != null)
            h.v.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return classification.length + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_HEADER;
        return TYPE_ITEM;
    }

    public static class ClassificationItemHolder extends RecyclerView.ViewHolder {

        public ClassificationItemHolder(View itemView) {
            super(itemView);
            v = itemView;
            ButterKnife.bind(this, v);
        }

        View v;
        @BindView(R.id.text)
        TextView text;
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {

        public HeaderHolder(View itemView) {
            super(itemView);
        }
    }

    public View.OnClickListener onClickListener;
    private Main context;
    private final String[] classification = util.get_str_array(R.array.assignment_types);
    private static final int[] layout = new int[]{R.layout.main_fr_main_header, R.layout.main_fr_main_classify_item};
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
//    private static final String[] classification = new String[]{
//            "网页,IT & 软件",
//            "写作 & 文案",
//            "设计 & 架构",
//            "数据录入 & 管理",
//            "工程 & 科学",
//            "销售",
//            "商业,会计,HR & 法律",
//            "产品采购 & 制造",
//            "语言 & 翻译",
//            "其它"};
}
