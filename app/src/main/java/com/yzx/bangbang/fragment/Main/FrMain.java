package com.yzx.bangbang.fragment.Main;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yzx.bangbang.activity.BrowseAssignment;
import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.R;
import com.yzx.bangbang.adapter.main.FrMainAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FrMain extends Fragment {

    public void init() {
        ButterKnife.bind(this, v);
        adapter = new FrMainAdapter(context());
        adapter.onClickListener = onClickListener;
        list.setLayoutManager(new LinearLayoutManager(context()));
        list.setAdapter(adapter);
    }

//    private void initScrollView() {
//        for (int i = 0; i < classification.length; i++) {
//            ViewGroup v = (ViewGroup) context().getLayoutInflater().inflate(R.layout.main_fr_main_classify_item, scroll_content, false);
//            ((TextView) v.findViewById(R.id.text)).setText(classification[i]);
//            v.setTag(i);
//            v.setOnClickListener(onClickListener);
//            scroll_content.addView(v);
//        }
//    }

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
        v = inflater.inflate(R.layout.list_layout0, container, false);
        init();
        return v;
    }

    @BindView(R.id.list)
    RecyclerView list;
    private View v;
    FrMainAdapter adapter;

}
