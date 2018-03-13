package com.yzx.bangbang.Fragment.AD;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yzx.bangbang.Activity.AssignDetail;
import com.yzx.bangbang.Activity.ChatActivity;
import com.yzx.bangbang.module.ReplierInfo;
import com.yzx.bangbang.module.SimpleIndividualInfo;
import com.yzx.bangbang.R;
import com.yzx.bangbang.Utils.NetWork.OkHttpUtil;
import com.yzx.bangbang.Utils.NetWork.UniversalImageDownloader;

import java.util.List;


public class FrReplierInfo extends Fragment implements View.OnClickListener {
    View v, btn_back;
    TextView toolbar_title;
    RelativeLayout scroll_view_container;
    List<ReplierInfo> replierInfoList;
    Adapter adapter;
    AlertDialog dialog;
    int chosen_user_id;
    //网络延迟原因，fragment里也设置一个boolean
    private boolean fulfill;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.common_template0, container, false);
        init();
        return v;
    }

    private void init() {
        replierInfoList = ((AssignDetail) getActivity()).replierInfoList;
        if (replierInfoList == null) return;
        adapter = new Adapter(getActivity().getLayoutInflater(), (ViewGroup) v.findViewById(R.id.scroll_view_container));
        initView();
        if (get().chosen)
            getChosenUserId(get().assignment.id);
        else adaptItem();
    }

    private void initView() {
        //v.setAlpha(1);
        //v.getBackground().setAlpha(255);
        toolbar_title = (TextView) v.findViewById(R.id.toolbar_title);
        toolbar_title.setText("帮众");
        scroll_view_container = (RelativeLayout) v.findViewById(R.id.scroll_view_container);
        btn_back = v.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
    }

    private void adaptItem() {
        for (ReplierInfo info : replierInfoList) {
            View v = adapter.inflate(info);
            v.findViewById(R.id.btn_message).setOnClickListener(this);
            v.findViewById(R.id.btn_choose).setOnClickListener(this);
            v.setTag(info);
            scroll_view_container.addView(v);
        }
        updateItemView();
    }

    private void updateItemView() {
        if (get().chosen) {
            for (int i = 0; i < replierInfoList.size(); i++) {
                ReplierInfo info = (ReplierInfo) scroll_view_container.getChildAt(i).getTag();
                if (info.user_id == chosen_user_id) {
                    TextView tv = (TextView) v.findViewById(R.id.btn_fulfill);
                    tv.setVisibility(View.VISIBLE);
                    if (get().fulfill||fulfill) tv.setText("已完成");
                    else
                        tv.setOnClickListener(this);
                    SimpleDraweeView view = (SimpleDraweeView) v.findViewById(R.id.btn_choose);
                    view.setImageURI(Uri.parse("res://" + get().getPackageName() + "/" + R.drawable.main_icon_success));
                }
            }
        }
    }

    private int parseString(String s, String key) {
        if (s.charAt(0) != '{') return -1;
        int start = 0;
        int end = start = s.indexOf(key) + key.length() + 3;
        System.out.println("" + end);
        while (Character.isDigit(s.charAt(end)))
            end++;
        return Integer.valueOf(s.substring(start, end));
    }

    @Override
    public void onClick(View view) {
        ReplierInfo info;
        switch (view.getId()) {
            case R.id.btn_back:
                exitFragment();
                break;
            case R.id.btn_message:
                info = (ReplierInfo) ((View) view.getParent()).getTag();
                if (info == null) return;
                Intent i = new Intent(getActivity(), ChatActivity.class);
                i.putExtra("info", new SimpleIndividualInfo(info.user_id, info.user_name));
                getActivity().startActivity(i);
                break;
            case R.id.btn_choose:
                info = (ReplierInfo) ((View) view.getParent()).getTag();
                if (info == null) return;
                showAdoptDialog(info.user_id, info.user_name);
                break;
            case R.id.btn_fulfill:
                showFulfillDialog();
                break;
        }
    }

    private void showAdoptDialog(int user_id, String name) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.ad_dialog, null);
        UniversalImageDownloader d = ((AssignDetail) getActivity()).downloader;
        if (d != null)
            d.setPortraitUsingCache(user_id, (SimpleDraweeView) v.findViewById(R.id.ad_dialog_portrait));
        ((TextView) v.findViewById(R.id.ad_dialog_name)).setText(name);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("确定要采用他吗？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", null);
        builder.setView(v);
        dialog = builder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener((view) -> chooseReplier(user_id));
    }

    private void showFulfillDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("确认需求已完成？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", null);
        dialog = builder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener((view) -> fulfillAssignment());
    }

    private void getChosenUserId(int asm_id) {
        OkHttpUtil okhttp = OkHttpUtil.inst((s) -> {
            if (s.length() == 0 || s.charAt(0) == '<') return;
            chosen_user_id = parseString(s, "chosen");
            get().runOnUiThread(this::adaptItem);
        });
        okhttp.addPart("sql", "select chosen from `event` where `success` = '1' and `type` = '1' and `asm_id` = '" + asm_id + "'");
        okhttp.post("query_data_common");
    }

    private void chooseReplier(int user_id) {
        OkHttpUtil okhttp = OkHttpUtil.inst((s) -> {
            if (s.equals("success")) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "选择帮众成功", Toast.LENGTH_SHORT).show();
                    if (dialog != null) dialog.hide();
                    exitFragment();
                });
                get().checkIfHasChosen();
            }
        });
        okhttp.addPart("sql", "update `event` set `success` = '1',`chosen` = '" + user_id + "' where asm_id = '" + get().assignment.id + "'");
        okhttp.post("update_data_common");
    }

    private void fulfillAssignment() {
        OkHttpUtil okhttp = OkHttpUtil.inst((s) -> {
            if (s.equals("success")) {
                get().checkIfHasFulfill();
                fulfill = true;
                get().runOnUiThread(()->{
                    updateItemView();
                    Toast.makeText(getActivity(), "验收成功，需求已结束！", Toast.LENGTH_SHORT).show();
                    if (dialog != null) dialog.hide();
                });
            }
        });
        okhttp.addPart("sql", "update `event` set `fulfill` = '1' where asm_id = '" + get().assignment.id + "'");
        okhttp.post("update_data_common");
    }

    private void exitFragment() {
        ((AssignDetail) getActivity()).inst().handler.sendEmptyMessage(AssignDetail.ACTION_REMOVE_FRAGMENT);
    }

    private AssignDetail get() {
        return ((AssignDetail) getActivity());
    }

    private class Adapter {
        LayoutInflater inflater;
        ViewGroup container;

        public Adapter(LayoutInflater inflater, ViewGroup container) {
            this.inflater = inflater;
            this.container = container;
        }

        View inflate(ReplierInfo info) {
            ViewGroup item = (ViewGroup) inflater.inflate(R.layout.ad_replier_info_item, container, false);
            TextView v = (TextView) item.findViewById(R.id.user_name);
            v.setText(info.user_name);
            v = (TextView) item.findViewById(R.id.date);
            v.setText(info.date);
            v = (TextView) item.findViewById(R.id.message);
            v.setText(info.message);
            UniversalImageDownloader d = ((AssignDetail) getActivity()).downloader;
            if (d != null)
                d.downLoadPortrait(info.user_id, (SimpleDraweeView) item.findViewById(R.id.portrait));
            return item;
        }
    }
}
