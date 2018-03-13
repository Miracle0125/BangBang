package com.yzx.bangbang.Fragment.Main;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.yzx.bangbang.Activity.Main;
import com.yzx.bangbang.Adapter.Main.MainAssignmentsListAdapter;
import com.yzx.bangbang.Adapter.Main.MainDistanceSpinnerAdapter;
import com.yzx.bangbang.Adapter.Main.MainSortSpinnerAdapter;
import com.yzx.bangbang.module.Mysql.AssignmentModule;
import com.yzx.bangbang.R;
import com.yzx.bangbang.Utils.NetWork.OkHttpUtil;
import com.yzx.bangbang.Utils.NetWork.UniversalImageDownloader;
import com.yzx.bangbang.Utils.SpUtil;
import com.yzx.bangbang.Utils.util;
import com.yzx.bangbang.view.mainView.ListItem;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import model.Assignment;


/**
 * 我既讨厌写注释，又讨厌看别人不写注释的代码。好了我现在开始看一年前的代码了。
 */
public class FrMain extends Fragment implements View.OnClickListener, ListItem.Listener {
    public static final int DOWNLOAD_ASSIGNMENT_TEXT_COMPLETE = 1;
    public static final int IMAGE_DOWNLOAD_COMPLETE = 2;

    int sort_type = 0;
    public static int distance = 0;

    @BindView(R.id.main_scrollView)
    ScrollView scrollView;
    @BindView(R.id.list_container)
    LinearLayout mainContainer;
    @BindView(R.id.fr_main_spinner_distance)
    Spinner spinner_distance;
    @BindView(R.id.fr_main_spinner_sort)
    Spinner spinner_sort;

    static boolean usingPtr;
    UniversalImageDownloader downloader;
    MainAssignmentsListAdapter adapter;

    Gson gson = new Gson();
    LatLng original;
    View frMain;
    Map<Integer, View> Id_View;
    public static boolean hasScrollToTop;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        frMain = inflater.inflate(R.layout.main_fr_main, container, false);
        frMain.getViewTreeObserver().addOnGlobalLayoutListener(() -> ((Main) getActivity()).setScrollView(scrollView));
        ButterKnife.bind(this, frMain);
        init();
        return frMain;
    }

/*    //某个活动返回此界面前放置了此刷新标志，读取并消除标志
    @Override
    public void onResume() {
        super.onResume();
        if (SpUtil.readIntAndChange(getActivity()) == util.DATA_CHANGED) {
            DownloadAssignmentText();
        }
    }*/

    public void init() {
        downloader = new UniversalImageDownloader(getActivity());
        initSpinner();
        DownloadAssignmentText();
        getLocation();
        context().getListener().getAssignment((r)->{

        });
        RecyclerView recyclerView;
    }

    private void initSpinner() {
        spinner_distance.setAdapter(new MainDistanceSpinnerAdapter(getActivity(), getResources().getStringArray(R.array.distances)));
        spinner_distance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (distance == i) return;
                distance = i;
                Refresh();
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
                Refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    float[][] dis_scope = {{0, 1000f}, {1000f, 2000f}, {2000f, 5000f}, {5000f, 200000f}};

    private List<AssignmentModule> filterAssignmentByDistance(List<AssignmentModule> assignments) {
        if (distance == 0 || original == null) return assignments;
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < assignments.size(); i++) {
            AssignmentModule assignment = assignments.get(i);
            if (assignment.latitude == -1 || assignment.longitude == -1) {
                stack.add(i);
                continue;
            }
            LatLng latLng = new LatLng(assignment.latitude, assignment.longitude);
            float dis = AMapUtils.calculateLineDistance(original, latLng);
            Log.d("main", "distance " + dis);
            if (!(dis_scope[distance][0] < dis && dis < dis_scope[distance][1]))
                stack.add(i);
            SpUtil.putString("database", String.valueOf(assignment.id), String.valueOf(dis), getActivity());
        }
        while (!stack.isEmpty()) {
            int i = stack.pop();
            assignments.remove(i);
        }
        //processInputStream("{\"list\":"+gson.toJson(assignments)+"}");
        return assignments;
    }

    //如果adapter不为空，为刷新
    public void processInputStream(String s) {
        if (s.length() == 0 || s.charAt(0) == '<') return;
        AssignmentListReceiver receiver = gson.fromJson(s, AssignmentListReceiver.class);
        if (receiver == null) return;
        //new Thread(()->receiver.list = filterAssignmentByDistance(receiver.list)).start();
        receiver.list = filterAssignmentByDistance(receiver.list);
        getActivity().runOnUiThread(() -> {
            //view 没有复用
            if (receiver.list.size() == 0) mainContainer.removeAllViews();
            else {
                if (adapter == null) {
                    adapter = new MainAssignmentsListAdapter(receiver.list, getActivity());
                } else {
                    mainContainer.removeAllViews();
                    adapter.setData(receiver.list);
                    //assignmentsListView.invalidateViews();
                    Flowable.just(util.obtain_message(Main.STATE_REFRESH_FINISH)).subscribe(((Main) getActivity()).consumer);
                    // ((Main) getActivity()).getHandler().sendEmptyMessage(Main.STATE_REFRESH_FINISH);
                }
                for (int i = 0; i < receiver.list.size(); i++) {
                    mainContainer.addView(adapter.inflateView(i, mainContainer));
                }
            }
        });
    }

    public void Refresh() {
        if (sort_type == 0) DownloadAssignmentText();//默认
        if (sort_type == 1) DownloadAssignmentTextOrderByPriceAsc();
        if (sort_type == 2) DownloadAssignmentTextOrderByPriceDesc();
        adapter.downloader.onRefresh();
        //usingPtr = true;
    }

    void DownloadAssignmentText() {
        OkHttpUtil okhttp = OkHttpUtil.inst(this::processInputStream);
        okhttp.addPart("sql", "select * from assignment order by id DESC limit 20");
        okhttp.post("query_data_common");
    }

    void DownloadAssignmentTextOrderByPriceAsc() {
        OkHttpUtil okhttp = OkHttpUtil.inst(this::processInputStream);
        okhttp.addPart("sql", "select * from assignment order by price ASC limit 20");
        okhttp.post("query_data_common");
    }

    void DownloadAssignmentTextOrderByPriceDesc() {
        OkHttpUtil okhttp = OkHttpUtil.inst(this::processInputStream);
        okhttp.addPart("sql", "select * from assignment order by price DESC limit 20");
        okhttp.post("query_data_common");
    }

    private void getLocation() {
        AMapLocationClient locationClient = new AMapLocationClient(getActivity());
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        locationClient.setLocationOption(mLocationOption);
        locationClient.setLocationListener((amapLocation) -> {
            if (amapLocation != null)
                if (amapLocation.getErrorCode() == 0) {
                    original = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                } else {
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
        });
        locationClient.startLocation();
    }


    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void onDestroy() {
        clear();
        super.onDestroy();
    }

    public FrMain inst() {
        return FrMain.this;
    }

    private void clear() {
        if (Id_View != null)
            Id_View.clear();
        Id_View = null;
    }

    public FrMainHandler frMainHandler = new FrMainHandler(this);

    public static class FrMainHandler extends Handler {
        private WeakReference<FrMain> ref;

        public FrMainHandler(FrMain fragment) {
            ref = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FrMain inst = ref.get();
            if (inst != null)
                inst.handleMsg(msg);
        }
    }

    private void handleMsg(Message msg) {
        switch (msg.what) {
            case 1:
                //processInputStream();
                break;
            case IMAGE_DOWNLOAD_COMPLETE:
                LoadImage(msg.getData());
                break;
            default:
                break;
        }
    }


    private void LoadImage(Bundle data) {
        int[] imageId = {R.id.main_item_image0, R.id.main_item_image1, R.id.main_item_image2};
        SimpleDraweeView draweeView;
        View parent = Id_View.get(data.getInt("asm_id"));
        if (parent == null)
            return;
        draweeView = parent.findViewById(imageId[data.getInt("pos")]);
        if (draweeView == null)
            return;
        draweeView.setImageURI(Uri.fromFile(new File(data.getString("path"))));
    }

    @Override
    public void onItemTouched(int position) {
        //Main inst = mainRef.get();
    }

    private class AssignmentListReceiver {
        List<AssignmentModule> list;
    }

    public Main context(){
        return (Main) super.getActivity();
    }
}
