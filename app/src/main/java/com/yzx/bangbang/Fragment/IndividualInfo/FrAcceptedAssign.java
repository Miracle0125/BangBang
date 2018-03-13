package com.yzx.bangbang.Fragment.IndividualInfo;

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
import com.yzx.bangbang.model.EventModule;
import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.NetWork.OkHttpUtil;
import com.yzx.bangbang.utils.Params;
import com.yzx.bangbang.view.indvInfo.AcceptedAssignItem;

import java.util.List;

public class FrAcceptedAssign extends Fragment implements View.OnClickListener{
    Gson gson = new Gson();
    View v,btn_back;
    TextView toolbar_title;
    RelativeLayout scroll_view_container;
    Adapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.common_template0, container, false);
        init();
        return v;
    }

    private void init(){
        adapter = new Adapter(getActivity().getLayoutInflater(), (ViewGroup) v.findViewById(R.id.scroll_view_container));
        initView();
    }

    private void initView(){
        toolbar_title = (TextView) v.findViewById(R.id.toolbar_title);
        toolbar_title.setText("已接需求");
        scroll_view_container = (RelativeLayout) v.findViewById(R.id.scroll_view_container);
        btn_back = v.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        new Thread(this::getData).start();
    }

    private void getData() {
        OkHttpUtil okhttp = OkHttpUtil.inst(s -> {
            Receiver receiver = gson.fromJson(s,Receiver.class);
            if (receiver==null) return;
            for (EventModule event :receiver.list){
                getActivity().runOnUiThread(() -> scroll_view_container.addView(adapter.inflate(event)));
            }
        });
        int user_id = ((IndvInfo)getActivity()).info.id;
        //okhttp.addPart("user_id", String.valueOf(user_id));
        okhttp.addPart("query_event", String.valueOf(Params.EVENT_TYPE_ACCEPT_ASSIGNMENT) + "," + String.valueOf(user_id));
        okhttp.post( "query_event");
    }

    @Override
    public void onClick(View view) {
        switch ( view.getId()){
            case R.id.btn_back:
                ((IndvInfo)getActivity()).fm.removeCurrent();
                ((IndvInfo)getActivity()).updateFragmentBackground();
                break;
            case R.id.accepted_asm_item:
                Intent intent;
                intent = new Intent(getActivity(), AssignmentDetail.class);
                intent.putExtra("asm_id", ((AcceptedAssignItem) view).data.asm_id);
                getActivity().startActivity(intent);
                break;
        }
    }

    private class Adapter{
        LayoutInflater inflater;
        ViewGroup container;
        public Adapter(LayoutInflater inflater,ViewGroup container){
            this.inflater = inflater;
            this.container = container;
        }
        View inflate(EventModule event){
            AcceptedAssignItem item = (AcceptedAssignItem) inflater.inflate(R.layout.individual_assign_item_template,container,false);
            TextView v= (TextView) item.findViewById(R.id.title);
            v.setText(event.asm_title);
            v= (TextView) item.findViewById(R.id.date);
            v.setText(event.date);
            v= (TextView) item.findViewById(R.id.price);
            v.setText(String.valueOf(event.price));
            item.setData(event);
            item.setOnClickListener(FrAcceptedAssign.this);
            return item;
        }
    }
    private class Receiver {
        List<EventModule> list;
    }
}
