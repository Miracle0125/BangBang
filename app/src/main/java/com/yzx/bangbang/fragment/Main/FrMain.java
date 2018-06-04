package com.yzx.bangbang.fragment.Main;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yzx.bangbang.activity.BrowseAssignment;
import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FrMain extends Fragment {

    public void init() {
        ButterKnife.bind(this, v);
        initScrollView();
    }

    private void initScrollView() {
        for (int i = 0; i < classification.length; i++) {
            ViewGroup v = (ViewGroup) context().getLayoutInflater().inflate(R.layout.main_fr_main_classify_item, scroll_content, false);
            ((TextView) v.findViewById(R.id.text)).setText(classification[i]);
            v.setTag(i);
            v.setOnClickListener(onClickListener);
            scroll_content.addView(v);
        }
    }

    View.OnClickListener onClickListener = v -> {
        Intent intent = new Intent(context(), BrowseAssignment.class);
        intent.putExtra("what", (int) v.getTag());
        startActivity(intent);
    };

    public Main context() {
        return (Main) super.getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.main_fr_main, container, false);
        init();
        return v;
    }

    @BindView(R.id.scroll_content)
    LinearLayout scroll_content;
    private View v;
    public static final String[] classification = new String[]{
            "网页,IT & 软件",
            "写作 & 文案",
            "设计 & 架构",
            "数据录入 & 管理",
            "工程 & 科学",
            "销售",
            "商业,会计,HR & 法律",
            "产品采购 & 制造",
            "语言 & 翻译",
            "其它"};
}
