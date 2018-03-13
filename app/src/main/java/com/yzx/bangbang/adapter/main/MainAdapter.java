package com.yzx.bangbang.adapter.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.facebook.drawee.view.SimpleDraweeView;
import com.yzx.bangbang.fragment.Main.FrMain;
import com.yzx.bangbang.R;
import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.utils.SpUtil;
import com.yzx.bangbang.utils.util;

import java.util.List;

import model.Assignment;


public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<Assignment> data;
    private Main main;

    public MainAdapter(Context context) {
        main = (Main) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = main.getLayoutInflater().inflate(R.layout.main_list_item, viewGroup, false);
        return new ViewHolder(v, data.get(i).getImages() > 0);
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
            LatLng user_pos = (LatLng) SpUtil.getObject(SpUtil.LATLNG);
            if (user_pos != null) {
                double lat = data.get(i).getLatitude();
                double lng = data.get(i).getLongitude();
                if (lat != 0 && lng != 0) {
                    float dis = AMapUtils.calculateLineDistance(user_pos, new LatLng(lat, lng));
                    h.tv_distance.setText("距离" + dis + "m");
                }
            }
        }
        int num_images = data.get(i).getImages();
        if (num_images > 0) {
            SimpleDraweeView images[] = {h.image0, h.image1, h.image2};
            for (int j = 0; j < num_images; j++)
                images[j].setImageURI(util.get_image_uri(data.get(i).getId() + "_" + j));
        }

        //downloader.downLoadPortrait(assignment.employer_id, h.portrait);
//        h.portrait.setTag(new SimpleIndividualInfo(assignment.employer_id, assignment.employer_name));
//        h.portrait.setOnClickListener((view) ->
//                Flowable.just(util.obtain_message(Main.ACTION_CLICK_PORTRAIT,view.getTag()))
//                        .subscribe(((Main) context).consumer));
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_employer,
                tv_title,
                tv_date,
                tv_price,
                tv_replies,
                tv_distance;
        SimpleDraweeView image0, image1, image2, portrait;

        public ViewHolder(View v, boolean have_image) {
            super(v);
            tv_date = v.findViewById(R.id.item_date);
            tv_employer = v.findViewById(R.id.item_username);
            tv_title = v.findViewById(R.id.item_title);
            tv_price = v.findViewById(R.id.item_price);
            tv_replies = v.findViewById(R.id.item_num_reply);
            portrait = v.findViewById(R.id.portrait);
            tv_distance = v.findViewById(R.id.distance);
            if (have_image) {
                ViewGroup viewGroup = v.findViewById(R.id.image_container);
                viewGroup.setVisibility(View.VISIBLE);
                image0 = v.findViewById(R.id.image0);
                image1 = v.findViewById(R.id.image1);
                image2 = v.findViewById(R.id.image2);
            }
        }
    }

    public void setData(List<Assignment> data) {
        this.data = data;
    }

}
