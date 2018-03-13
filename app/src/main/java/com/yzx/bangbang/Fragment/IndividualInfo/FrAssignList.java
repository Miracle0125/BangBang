package com.yzx.bangbang.Fragment.IndividualInfo;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.yzx.bangbang.Activity.AssignDetail;
import com.yzx.bangbang.Activity.IndvInfo;
import com.yzx.bangbang.Adapter.AssignListAdapter;
import com.yzx.bangbang.module.SimpleIndividualInfo;
import com.yzx.bangbang.R;
import com.yzx.bangbang.Utils.NetWork.OkHttpUtil;
import java.util.List;

public class FrAssignList extends Fragment {
    Gson gson = new Gson();
    View v, btn_back;
    ListView listView;
    int indv_id = -1;
    List<Assignment> list;
    TextView toolbarTitle;
    SimpleIndividualInfo info;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.al_layout, container, false);
        init();
        return v;
    }

    private void init() {
        if ((info = ((IndvInfo) getActivity()).info) == null) return;
        indv_id = info.id;
        initView();
        getData();
    }

    private void initView() {
        btn_back = v.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(view -> {
            ((IndvInfo) getActivity()).fm.removeCurrent();
            ((IndvInfo) getActivity()).updateFragmentBackground();
        });
        toolbarTitle = (TextView) v.findViewById(R.id.al_toolbar_textView);
        if (info != null) toolbarTitle.setText(info.name + "的需求");
    }

    private void getData() {
        if (indv_id == -1)
            return;
        new Thread(() -> {
            OkHttpUtil okhttp = OkHttpUtil.inst(s -> {
                if (s.charAt(0) == '<')
                    return;
                Receive receive = gson.fromJson(s, Receive.class);
                list = receive.list;
                if (list != null)
                    getActivity().runOnUiThread(this::loadData);
            });
            okhttp.addPart("employer_id", String.valueOf(indv_id));
            okhttp.post("indv_assign");
        }).start();
    }

    private void loadData() {
        assert list != null;
        listView = (ListView) v.findViewById(R.id.al_listView);
        listView.setAdapter(new AssignListAdapter(getActivity(), list));
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getActivity(), AssignDetail.class);
            intent.putExtra("asm_id", list.get(i).id);
            startActivity(intent);
        });
    }

    private class Receive {
        List<Assignment> list;
    }
}
