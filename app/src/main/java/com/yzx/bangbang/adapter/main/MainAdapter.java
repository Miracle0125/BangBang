package com.yzx.bangbang.adapter.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.Fragment.Main.FrMain;
import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.SpUtil;
import com.yzx.bangbang.utils.util;
import java.util.List;
import model.Assignment;


public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<Assignment> data;
    private Main main;

    public MainAdapter(Context context){
        main= (Main) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int i) {
        h.tv_date.setText(util.CustomDate(data.get(i).getDate()));
        h.tv_employer.setText(data.get(i).getEmployer_name());
        h.tv_title.setText(data.get(i).getTitle());
        h.tv_price.setText(util.s(data.get(i).getPrice()));
        h.tv_price.setTextColor(util.CustomColor(data.get(i).getPrice()));
        h.tv_replies.setText(util.s(data.get(i).getRepliers()));
        if (FrMain.distance != 0) {
            String dis = SpUtil.getString("database", data.get(i).getId(), main);
            h.tv_distance.setText("距离" + dis + "m");
        }
        //downloader.downLoadPortrait(assignment.employer_id, h.portrait);
//        h.portrait.setTag(new SimpleIndividualInfo(assignment.employer_id, assignment.employer_name));
//        h.portrait.setOnClickListener((view) ->
//                Flowable.just(util.obtain_message(Main.ACTION_CLICK_PORTRAIT,view.getTag()))
//                        .subscribe(((Main) context).consumer));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_employer,
                tv_title,
                tv_date,
                tv_price,
                tv_replies,
                tv_distance;
        SimpleDraweeView image0, image1, image2, portrait;

        public ViewHolder(View v) {
            super(v);
            tv_date = v.findViewById(R.id.item_date);
            tv_employer = v.findViewById(R.id.item_username);
            tv_title = v.findViewById(R.id.item_title);
            tv_price = v.findViewById(R.id.item_price);
            tv_replies = v.findViewById(R.id.item_num_reply);
            portrait = v.findViewById(R.id.item_portrait);
            tv_distance = v.findViewById(R.id.main_item_distance);
        }
    }

    public void setData(List<Assignment> data) {
        this.data = data;
    }

}
