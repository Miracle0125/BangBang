package com.yzx.bangbang.fragment.IndividualInfo;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.yzx.bangbang.activity.AssignmentDetail;
import com.yzx.bangbang.activity.IndvInfo;
import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.model.Mysql.AssignmentModule;
import com.yzx.bangbang.model.SimpleIndividualInfo;
import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.NetWork.OkHttpUtil;
import com.yzx.bangbang.utils.util;
import java.util.List;

public class FrCollection extends Fragment implements View.OnClickListener {
    Gson gson = new Gson();
    View v, btn_back;
    TextView toolbar_title;
    RelativeLayout scroll_view_container;
    Adapter adapter;
    SimpleIndividualInfo info;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.common_template0, container, false);
        init();
        return v;
    }

    private void init() {
        if ((info = ((IndvInfo) getActivity()).info) == null) return;
        adapter = new Adapter(getActivity().getLayoutInflater(), (ViewGroup) v.findViewById(R.id.scroll_view_container));
        initView();
        loadCollectedAssignmentId();
    }

    private void initView() {
        scroll_view_container = (RelativeLayout) v.findViewById(R.id.scroll_view_container);
        btn_back = v.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(view -> {
            ((IndvInfo) getActivity()).fm.removeCurrent();
            ((IndvInfo) getActivity()).updateFragmentBackground();
        });
        toolbar_title = (TextView) v.findViewById(R.id.toolbar_title);
        if (info != null) toolbar_title.setText(info.name + "的收藏");
    }

    List<Integer> assignmentId;
    private void loadCollectedAssignmentId() {
        OkHttpUtil okhttp = OkHttpUtil.inst(s -> {
            if (s.length() == 0 || s.charAt(0) == '<') return;
            assignmentId = util.parseSingleColumnJsonInteger(s, "asm_id");
            downloadAssignment(assignmentId);
        });
        okhttp.addPart("sql", "select asm_id from event where user_id = '" + Main.user.getId() + "' and type = 3");
        okhttp.post("query_data_common");
    }

    private void downloadAssignment(List<Integer> list){
        if (list==null||list.size()==0)return;
        OkHttpUtil okhttp = OkHttpUtil.inst(s -> {
            if (s.length() == 0 || s.charAt(0) == '<') return;
            Receiver receiver = gson.fromJson(s,Receiver.class);
            addItems(receiver.list);
        });
        String sql = "select * from assignment where id = ";
        StringBuilder sb = new StringBuilder(sql);
        sb.append(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            int id = list.get(i);
            sb.append(" or id = ").append(id);
        }
        okhttp.addPart("sql", sb.toString());
        okhttp.post("query_data_common");
    }

    private void addItems(List<AssignmentModule> list){
        if (list==null) return;
        getActivity().runOnUiThread(()->{
            for (AssignmentModule a : list){
                scroll_view_container.addView(adapter.inflate(a));
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.al_list_item:
                int asm_id = ((AssignmentModule)view.getTag()).id;
                Intent i = new Intent(getActivity(), AssignmentDetail.class);
                i.putExtra("asm_id",asm_id);
                getActivity().startActivity(i);
                break;
        }
    }

    private class Receiver {
        List<AssignmentModule> list;
    }

    private class Adapter {
        LayoutInflater inflater;
        ViewGroup container;
        public Adapter(LayoutInflater inflater, ViewGroup container) {
            this.inflater = inflater;
            this.container = container;
        }

        View inflate(AssignmentModule assignment) {
            View item = inflater.inflate(R.layout.al_list_item, container, false);
            TextView v = (TextView) item.findViewById(R.id.title);
            v.setText(assignment.title);
            v = (TextView) item.findViewById(R.id.date);
            v.setText(util.CustomDate(assignment.date));
            v = (TextView) item.findViewById(R.id.price);
            v.setText(String.valueOf(assignment.price));
            v = (TextView) item.findViewById(R.id.repliers);
            v.setText("帮众 "+assignment.repliers);
            item.setTag(assignment);
            item.setOnClickListener(FrCollection.this);
            return item;
        }
    }
}
