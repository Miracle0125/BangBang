package com.yzx.bangbang.Fragment.IndividualInfo;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.yzx.bangbang.Activity.IndvInfo;
import com.yzx.bangbang.Activity.Main;
import com.yzx.bangbang.module.EventModule;
import com.yzx.bangbang.module.SimpleIndividualInfo;
import com.yzx.bangbang.R;
import com.yzx.bangbang.Utils.NetWork.OkHttpUtil;
import com.yzx.bangbang.Utils.NetWork.UniversalImageDownloader;

import java.util.List;

/**
 * Created by Administrator on 2016/12/12.
 */
//关注 与被关注兼容
public class FrConcern extends Fragment implements View.OnClickListener {
    Gson gson = new Gson();
    View v, btn_back;
    TextView toolbar_title;
    RelativeLayout scroll_view_container;
    Adapter adapter;
    SimpleIndividualInfo info;
    boolean isHisConcernList;
    UniversalImageDownloader downloader;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.common_template0, container, false);
        init();
        return v;
    }

    private void init() {
        if ((info = ((IndvInfo) getActivity()).info) == null) return;
        isHisConcernList = get().isConcernList;
        downloader = new UniversalImageDownloader(getActivity());
        adapter = new Adapter(getActivity().getLayoutInflater(), (ViewGroup) v.findViewById(R.id.scroll_view_container));
        initView();
        loadList();
    }

    private void initView() {
        scroll_view_container = (RelativeLayout) v.findViewById(R.id.scroll_view_container);
        btn_back = v.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(view -> {
            ((IndvInfo) getActivity()).fm.removeCurrent();
            ((IndvInfo) getActivity()).updateFragmentBackground();
        });
        toolbar_title = (TextView) v.findViewById(R.id.toolbar_title);
        if (info != null) {
            if (isHisConcernList) {
                if (info.id == Main.user.getId()) {
                    toolbar_title.setText("我关注的人");
                } else {
                    toolbar_title.setText("他关注的人");
                }
            }else{
                if (info.id == Main.user.getId()) {
                    toolbar_title.setText("关注我的人");
                } else {
                    toolbar_title.setText("关注他的人");
                }
            }
        }
    }

    private void loadList(){
        String sql1 = "select * from event where user_id = " + Main.user.getId() + " and type = 2";
        String sql2 = "select * from event where obj_user_id = " + Main.user.getId() + " and type = 2";
        OkHttpUtil okhttp = OkHttpUtil.inst(s->{
            if(s.length()==0||s.charAt(0)=='<') return;
            Receiver receiver = gson.fromJson(s,Receiver.class);
            addItem(receiver.list);
        });
        if (isHisConcernList)
        okhttp.addPart("sql",sql1 );
        else okhttp.addPart("sql",sql2 );
        okhttp.post("query_data_common");
    }

    private void addItem(List<EventModule> list){
        if (list==null) return;
        getActivity().runOnUiThread(()->{
            for (EventModule eventModule : list){
                scroll_view_container.addView(adapter.inflate(new SimpleIndividualInfo(eventModule.obj_user_id,eventModule.obj_user_name)));
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.portrait:
                SimpleIndividualInfo info = (SimpleIndividualInfo) view.getTag();
                Intent i = new Intent(getActivity(),IndvInfo.class);
                i.putExtra("info",info);
                getActivity().startActivity(i);
                break;
        }
    }

    private class Receiver {
        List<EventModule> list;
    }

    private IndvInfo get() {
        return ((IndvInfo) getActivity());
    }

    private class Adapter {
        LayoutInflater inflater;
        ViewGroup container;

        public Adapter(LayoutInflater inflater, ViewGroup container) {
            this.inflater = inflater;
            this.container = container;
        }

        View inflate(SimpleIndividualInfo info) {
            View item = inflater.inflate(R.layout.individual_concern_item, container, false);
            TextView v = (TextView) item.findViewById(R.id.name);
            v.setText(info.name);
   /*          View btn = item.findViewById(R.id.indv_btn_concern);
           if (!isHisConcernList) {
                btn.setVisibility(View.GONE);
            }else {
                btn.setOnClickListener(FrConcern.this);
            }*/
            SimpleDraweeView portrait = (SimpleDraweeView) item.findViewById(R.id.portrait);
            portrait.setTag(info);
            portrait.setOnClickListener(FrConcern.this);
            downloader.downLoadPortrait(info.id,portrait);
            return item;
        }
    }
}
