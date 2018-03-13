package com.yzx.bangbang.Fragment.Main;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.yzx.bangbang.Activity.ChatActivity;
import com.yzx.bangbang.Activity.Main;
import com.yzx.bangbang.model.Mysql.ChatRecord;
import com.yzx.bangbang.model.SimpleIndividualInfo;
import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.NetWork.OkHttpUtil;
import com.yzx.bangbang.utils.NetWork.UniversalImageDownloader;
import com.yzx.bangbang.utils.util;

import java.util.List;

public class FrMessage extends Fragment implements View.OnClickListener {

    Gson gson = new Gson();
    UniversalImageDownloader downloader;
    ViewGroup container;
    View v;
    Adapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.main_fr_message, container, false);
        init();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (container != null)
            container.removeAllViews();
        get_convr_id();
    }

    private void init() {
        downloader = new UniversalImageDownloader(get());
        container = (ViewGroup) v.findViewById(R.id.main_fr_msg_container);
        adapter = new Adapter(get().getLayoutInflater(), container, get());
    }


    private void get_convr_id() {
        OkHttpUtil okhttp = OkHttpUtil.inst((s) -> {
            if (s.length() == 0 || s.charAt(0) == '<') return;
            TempList tempList = gson.fromJson(s, TempList.class);
            for (Convr convr : tempList.list) {
                getChatRecord(convr.convr_id);
            }
        });
        okhttp.addPart("sql", "select distinct(convr_id) from `chat_record` where `user_id` = '" + Main.user.getId() + "' or `obj_user_id` = '" + Main.user.getId() + "'");
        okhttp.post("query_data_common");
    }

//    class ss(val name:String){
//
//    }

    private void getChatRecord(String convr_id) {
        OkHttpUtil okhttp = OkHttpUtil.inst((s) -> {
            if (s.length() == 0 || s.charAt(0) == '<') return;
            ChatRecordContainer container = gson.fromJson(s, ChatRecordContainer.class);
            get().runOnUiThread(() -> {
                addItem(container.list.get(0));
            });
        });
        okhttp.addPart("sql", "select * from `chat_record` where `convr_id` = '" + convr_id + "' order by date desc limit 1");
        okhttp.post("query_data_common");
    }

    private void addItem(ChatRecord chatRecord) {
        container.addView(adapter.inflate(chatRecord));
    }

    private class Adapter {
        LayoutInflater inflater;
        Context context;
        ViewGroup parent;

        public Adapter(LayoutInflater inflater, ViewGroup parent, Context context) {
            this.inflater = inflater;
            this.context = context;
            this.parent = parent;
        }

        public View inflate(ChatRecord chatRecord) {
            View v = inflater.from(context).inflate(R.layout.main_fr_message_item, parent, false);
            TextView tv = (TextView) v.findViewById(R.id.main_fr_msg_name);
            String obj_user_name;
            if (chatRecord.user_name.equals(Main.user.getName())) {
                tv.setText(chatRecord.obj_user_name);
                obj_user_name = chatRecord.obj_user_name;
            } else {
                tv.setText(chatRecord.user_name);
                obj_user_name = chatRecord.user_name;
            }
            tv = (TextView) v.findViewById(R.id.main_fr_msg_date);
            tv.setText(util.CustomDate(util.getDate(chatRecord.date)));
            tv = (TextView) v.findViewById(R.id.main_fr_msg_content);
            tv.setText(chatRecord.message);
            int obj_user_id;
            if (chatRecord.user_id == Main.user.getId())
                obj_user_id = chatRecord.obj_user_id;
            else
                obj_user_id = chatRecord.user_id;
            downloader.downLoadPortrait(obj_user_id, (SimpleDraweeView) v.findViewById(R.id.main_fr_msg_portrait));
            v.setTag(new SimpleIndividualInfo(obj_user_id, obj_user_name));
            v.setOnClickListener(FrMessage.this);
            return v;
        }
    }

    @Override
    public void onClick(View view) {
        SimpleIndividualInfo info = (SimpleIndividualInfo) view.getTag();
        if (info == null) return;
        Intent i = new Intent(getActivity(), ChatActivity.class);
        i.putExtra("info", info);
        getActivity().startActivity(i);
    }

    private Main get() {
        return ((Main) getActivity());
    }

    private class ChatRecordContainer {
        List<ChatRecord> list;
    }

    private class TempList {
        List<Convr> list;
    }

    private class Convr {
        String convr_id;
    }


}
