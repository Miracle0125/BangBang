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

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 我既讨厌写注释，又讨厌看别人不写注释的代码。好了我现在开始看一年前的代码了。
 */
public class FrMain extends Fragment {
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
    //Map<Integer, View> Id_View;
    MainAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.main_fr_main, container, false);
        // v.getViewTreeObserver().addOnGlobalLayoutListener(() -> ((Main) getActivity()).setScrollView(scrollView));
        init();
        return v;
    }

    public void init() {
        adapter = new MainAdapter(getActivity());
        ButterKnife.bind(this, v);
        initSpinner();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("test", "refresh");
                context().listener.getAssignment((r) -> {
                    adapter.setData(r);
                    if (recyclerView.getLayoutManager() == null)
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    if (recyclerView.getAdapter() == null)
                        recyclerView.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);
                }, sort_type);
            }
        });
//        swipeRefreshLayout.setOnRefreshListener(() -> {
//
//        });
        recyclerView.setOnClickListener(v -> {
            Log.d("test", "item click");
        });
        //refresh();
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

    public void refresh() {
        swipeRefreshLayout.setRefreshing(true);
    }

//    private void LoadImage(Bundle data) {
//        int[] imageId = {R.id.image0, R.id.image1, R.id.image2};
//        SimpleDraweeView draweeView;
//        View parent = Id_View.get(data.getInt("asm_id"));
//        if (parent == null)
//            return;
//        draweeView = parent.findViewById(imageId[data.getInt("pos")]);
//        if (draweeView == null)
//            return;
//        draweeView.setImageURI(Uri.fromFile(new File(data.getString("path"))));
//    }

    public Main context() {
        return (Main) super.getActivity();
    }
}
