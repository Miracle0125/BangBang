package com.yzx.bangbang.Fragment.Main;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yzx.bangbang.Activity.AssignDetail;
import com.yzx.bangbang.Activity.Main;
import com.yzx.bangbang.model.Mysql.AssignmentModule;
import com.yzx.bangbang.R;
import com.yzx.bangbang.Service.NetworkService;
import com.yzx.bangbang.utils.NetWork.OkHttpUtil;

import java.util.List;
import java.util.Set;

public class FrNews extends Fragment implements View.OnClickListener {
    LinearLayout frNews;
    LayoutInflater inflater;
    //List<String> titles;
    Gson gson = new Gson();


    //count 大于0的  都加进去
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_fr_news, container, false);
        this.inflater = inflater;
        frNews = (LinearLayout) v.findViewById(R.id.main_fr_news_container);
        return v;
    }

    public void Resume(){
        if (frNews!=null)
            frNews.removeAllViews();
        update();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (frNews!=null)
            frNews.removeAllViews();
        update();
/*        for (int i = 0; i < frNews.getChildCount(); i++) {
            View v = frNews.getChildAt(i);
            updateView((int) v.getTag(), v);
        }*/
    }

    private void initView() {

    }

    private void update() {
        if (NetworkService.id_diff==null) return;
        Set<Integer> set = NetworkService.id_diff.keySet();
        for (int id : set) {
            int diff = NetworkService.id_diff.get(id);
            if (diff > 0) {
                downloadAssignment(id);
            } else if (diff == 0 && NetworkService.id_count.get(id) > 0) {
                downloadAssignment(id);
            }
        }
    }

    private void downloadAssignment(int asm_id) {
        OkHttpUtil okhttp = OkHttpUtil.inst(s -> {
            if (s.length() == 0 || s.charAt(0) == '<') return;
            ReceiveList receiveList = gson.fromJson(s, ReceiveList.class);
            getActivity().runOnUiThread(() -> {
                View v = inflate(receiveList.list.get(0));
                frNews.addView(v);
            });
        });
        okhttp.addPart("sql", "", "select * from assignment where id = " + asm_id, OkHttpUtil.MEDIA_TYPE_JSON);
        okhttp.post("query_data_common");
    }

    private View inflate(AssignmentModule a) {
        View v = inflater.inflate(R.layout.main_fr_news_item, frNews, false);
        TextView tv_title = ((TextView) v.findViewById(R.id.main_fr_news_title));
        tv_title.setText(a.title);
        updateView(a.id, v);
        v.setTag(a.id);
        v.setOnClickListener(FrNews.this);
        return v;
    }

    private void updateView(int asm_id, View v) {
        TextView tv = ((TextView) v.findViewById(R.id.main_fr_news_head));
        TextView tv_title = ((TextView) v.findViewById(R.id.main_fr_news_title));
        int diff = NetworkService.id_diff.get(asm_id);
        if (diff > 0) {
            tv_title.setTextColor(getResources().getColor(R.color.main));
            tv.setTextColor(getResources().getColor(R.color.main));
            tv.setText(diff + "个新的帮众");
        } else {
            tv_title.setTextColor(getResources().getColor(R.color.cloud_gray));
            tv.setTextColor(getResources().getColor(R.color.cloud_gray));
            tv.setText(NetworkService.id_count.get(asm_id) + "个帮众");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_fr_news_item:
                int asm_id = (int) view.getTag();
                NetworkService.checkIfHasRecord(asm_id, NetworkService.id_count.get(asm_id) + NetworkService.id_diff.get(asm_id));
              /*  NetworkService.inst.queryMessage(new ArrayList<Integer>(asm_id));*/
                updateNtfCircle();
                Intent i = new Intent(getActivity(), AssignDetail.class);
                i.putExtra("asm_id", asm_id);
                getActivity().startActivity(i);
                break;
        }
    }

    private void updateNtfCircle() {
        TextView tv = Main.get().ntf_news;
        if (tv.getVisibility() == View.VISIBLE) {
            int i = Integer.valueOf(tv.getText().toString());
            if (--i > 0) {
                tv.setText(String.valueOf(i));
            } else {
                tv.setVisibility(View.GONE);
            }
        }
    }

    private class ReceiveList {
        List<AssignmentModule> list;
    }
}
