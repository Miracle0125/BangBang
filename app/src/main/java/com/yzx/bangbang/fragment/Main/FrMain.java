package com.yzx.bangbang.fragment.Main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.adapter.main.MainAdapter;
import com.yzx.bangbang.adapter.main.MainDistanceSpinnerAdapter;
import com.yzx.bangbang.adapter.main.MainSortSpinnerAdapter;
import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.util;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;


/**
 * 我既讨厌写注释，又讨厌看别人不写注释的代码。好了我现在开始看一年前的代码了。
 */
public class FrMain extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.main_fr_main, container, false);
        init();
        return v;
    }

    public void init() {
        adapter = new MainAdapter(getActivity());
        adapter.setOnClickListener(v ->
                Flowable.just(util.obtain_message(Main.ACTION_SHOW_DETAIL, v.getTag()))
                        .subscribe(context().consumer));
        ButterKnife.bind(this, v);
        initSpinner();
        swipeRefreshLayout.setOnRefreshListener(this::refresh);
        refresh();
    }

    private void refresh() {
        if (!swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(true);
        context().presenter.getAssignment((r) -> {
            adapter.setData(r);
            adapter.notifyDataSetChanged();
            if (recyclerView.getLayoutManager() == null)
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            if (recyclerView.getAdapter() == null)
                recyclerView.setAdapter(adapter);

            swipeRefreshLayout.setRefreshing(false);
        }, sort_type);
    }

    private void initSpinner() {
        spinner_distance.setAdapter(new MainDistanceSpinnerAdapter(getActivity(), getResources().getStringArray(R.array.distances)));
        spinner_distance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (distance == i) return;
                distance = i;
                refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner_sort.setAdapter(new MainSortSpinnerAdapter(getActivity(), getResources().getStringArray(R.array.sort)));
        spinner_sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (sort_type == i) return;
                sort_type = i;
                refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    float[][] dis_scope = {{0, 1000f}, {1000f, 2000f}, {2000f, 5000f}, {5000f, 200000f}};

    public Main context() {
        return (Main) super.getActivity();
    }

    public static final int IMAGE_DOWNLOAD_COMPLETE = 10;
    public static final int MODE_DEFAULT = 0;
    public static final int MODE_PRICE_ASC = 1;
    public static final int MODE_PRICE_DESC = 2;

    int sort_type = MODE_DEFAULT;
    public static int distance = 0;

    @BindView(R.id.main_list)
    RecyclerView recyclerView;
    @BindView(R.id.fr_main_spinner_distance)
    Spinner spinner_distance;
    @BindView(R.id.fr_main_spinner_sort)
    Spinner spinner_sort;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    private View v;
    MainAdapter adapter;
}
