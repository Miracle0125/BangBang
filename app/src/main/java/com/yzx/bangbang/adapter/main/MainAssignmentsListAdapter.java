//package com.yzx.bangbang.adapter.main;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.facebook.drawee.view.SimpleDraweeView;
//import com.yzx.bangbang.activity.AssignmentDetail;
//import com.yzx.bangbang.activity.Main;
//import com.yzx.bangbang.Fragment.Main.FrMain;
//import com.yzx.bangbang.model.Mysql.AssignmentModule;
//import com.yzx.bangbang.model.SimpleIndividualInfo;
//import com.yzx.bangbang.R;
//import com.yzx.bangbang.utils.NetWork.UniversalImageDownloader;
//import com.yzx.bangbang.utils.SpUtil;
//import com.yzx.bangbang.utils.util;
//import com.yzx.bangbang.view.mainView.ListItem;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import io.reactivex.Flowable;
//
//public class MainAssignmentsListAdapter implements ListItem.Listener {
//    List<AssignmentModule> list;
//    Context context;
//    public UniversalImageDownloader downloader;
//    FrMain frMain;
//
//    public MainAssignmentsListAdapter(List<AssignmentModule> list, Context context) {
//        this.list = list;
//        this.context = context;
//        downloader = new UniversalImageDownloader(context);
//        frMain = (FrMain) ((Main) context).fm.findFragmentByClass(FrMain.class);
//    }
//
//    public void setData(List<AssignmentModule> list) {
//        this.list = list;
//    }
//
//
//    public int getCount() {
//        return list.size();
//    }
//
//    public Object getItem(int i) {
//        return list.get(i);
//    }
//
//    public long getItemId(int i) {
//        return 0;
//    }
//
//    List<Integer> loadedItem = new ArrayList<>();
//
//
//    //这个方法会被调用多次 ，需要规避它
//    public View inflateView(int i, ViewGroup viewGroup) {
//        View v;
//        AssignmentModule assignment = list.get(i);
//        ViewHolder h;
//        h = new ViewHolder();
//        if (assignment.images >= 1) {
//            v = LayoutInflater.from(context).inflate(R.layout.main_list_item_with_images, viewGroup, false);
//        } else
//            v = LayoutInflater.from(context).inflate(R.layout.main_list_item, viewGroup, false);
//        initView(h, v);
//        loadImage(v, h, assignment);
//        bindData(v, assignment);
//        loadData(h, assignment);
//        return v;
//    }
//
//    private void loadImage(View v, ViewHolder h, AssignmentModule assignment) {
//        if (assignment.images >= 1) {
//            //v = LayoutInflater.from(context).inflate(R.layout.main_list_item_with_images, viewGroup, false);
//            h.image0 = (SimpleDraweeView) v.findViewById(R.id.image0);
//            downloader.downloadAssignmentImages(assignment.id, 0, h.image0);
//            h.image0.setVisibility(View.VISIBLE);
//            if (assignment.images >= 2) {
//                h.image1 = (SimpleDraweeView) v.findViewById(R.id.image1);
//                downloader.downloadAssignmentImages(assignment.id, 1, h.image1);
//                h.image1.setVisibility(View.VISIBLE);
//            }
//            if (assignment.images >= 3) {
//                h.image2 = (SimpleDraweeView) v.findViewById(R.id.image2);
//                downloader.downloadAssignmentImages(assignment.id, 2, h.image2);
//                h.image2.setVisibility(View.VISIBLE);
//            }
//        }
//    }
//
//    private void bindData(View v, AssignmentModule assignment) {
//        ((ListItem) v).registerListener(this);
//        ((ListItem) v).setAssignmentId(assignment.id);
//    }
//
//    private void initView(ViewHolder h, View v) {
//        h.tv_date = (TextView) v.findViewById(R.id.item_date);
//        h.tv_employer = (TextView) v.findViewById(R.id.item_username);
//        h.tv_title = (TextView) v.findViewById(R.id.item_title);
//        h.tv_price = (TextView) v.findViewById(R.id.item_price);
//        h.tv_replies = (TextView) v.findViewById(R.id.item_num_reply);
//        h.portrait = (SimpleDraweeView) v.findViewById(R.id.portrait);
//        h.tv_distance = (TextView) v.findViewById(R.id.distance);
//    }
//
//    private void loadData(ViewHolder h, AssignmentModule assignment) {
//        h.tv_date.setText(util.CustomDate(assignment.date));
//        h.tv_employer.setText(assignment.employer_name);
//        h.tv_title.setText(assignment.title);
//        h.tv_price.setText(util.s(assignment.price));
//        h.tv_price.setTextColor(util.CustomColor(assignment.price));
//        h.tv_replies.setText(util.s(assignment.repliers));
//        if (FrMain.distance != 0) {
//                String dis = SpUtil.getString("database", assignment.id, context);
//                h.tv_distance.setText("距离" + dis + "m");
//        }
//        downloader.downLoadPortrait(assignment.employer_id, h.portrait);
//        h.portrait.setTag(new SimpleIndividualInfo(assignment.employer_id, assignment.employer_name));
//        h.portrait.setOnClickListener((view) ->
//                Flowable.just(util.obtain_message(Main.ACTION_CLICK_PORTRAIT,view.getTag()))
//                        .subscribe(((Main) context).consumer));
//    }
//
//    @Override
//    public void onItemTouched(int asm_id) {
//        Intent intent = new Intent(context, AssignmentDetail.class);
//        intent.putExtra("asm_id", asm_id);
//        context.startActivity(intent);
//    }
//
//    private class ViewHolder {
//        TextView tv_employer,
//                tv_title,
//                tv_date,
//                tv_price,
//                tv_replies,
//                tv_distance;
//        SimpleDraweeView image0, image1, image2, portrait;
//    }
//
//    private class Cache {
//        int id;
//        TextView view;
//
//        public Cache(int id,
//                     TextView view) {
//            this.id = id;
//            this.view = view;
//        }
//    }
//}
