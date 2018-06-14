package com.yzx.bangbang.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yzx.bangbang.R;
import com.yzx.bangbang.adapter.main.AssignmentsAdapter;
import com.yzx.bangbang.fragment.Main.FrMain;
import com.yzx.bangbang.presenter.BrowsePresenter;
import com.yzx.bangbang.utils.util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.Assignment;

public class BrowseAssignment extends RxAppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_layout);
        init();
    }

    public void init() {
        ButterKnife.bind(this);
        assignment_what = getIntent().getIntExtra("what", -1);
        if (assignment_what != -1)
            toolbar_title.setText(classification[assignment_what]);
        adapter = new AssignmentsAdapter(this);
        adapter.setOnClickListener(onClickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1)) {
                    load_more();
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(this::refresh);
        refresh();
    }

    View.OnClickListener onClickListener = v -> {
        Intent intent = new Intent(this, AssignmentDetail.class);
        intent.putExtra("assignment", (Assignment) v.getTag());
        startActivity(intent);
    };

    private void refresh() {
        if (assignment_what == -1) return;
        if (!swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(true);
        presenter.getAssignment(sort_type, assignment_what, (r) -> {
            adapter.setData(r);
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void load_more() {
        Toast.makeText(BrowseAssignment.this, "正在加载更多", Toast.LENGTH_SHORT).show();
        presenter.getAssignment(DEFAULT_MORE, assignment_what, adapter.get_last_id(), r -> {
            if (r.isEmpty()) {
                Toast.makeText(this, "没有更多了", Toast.LENGTH_SHORT).show();
                return;
            }
            adapter.appendData(r);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onDestroy() {
        clear();
        super.onDestroy();
    }

    private void clear() {
        presenter.detach();
    }

    public static final int MODE_DEFAULT = 0;
    public static final int MODE_PRICE_ASC = 1;
    public static final int MODE_PRICE_DESC = 2;
    public static final int DEFAULT_MORE = 3;

    int sort_type = MODE_DEFAULT;
    public static int distance = 0;
    private int assignment_what = -1;
    @BindView(R.id.list)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;

    @OnClick(R.id.toolbar_back)
    void back() {
        clear();
        finish();
    }

    String[] classification = util.get_str_array(R.array.assignment_types);
    AssignmentsAdapter adapter;
    BrowsePresenter presenter = new BrowsePresenter(this);
}
