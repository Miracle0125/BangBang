//package com.yzx.bangbang.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.yzx.bangbang.R;
//import com.yzx.bangbang.utils.util;
//import com.yzx.bangbang.view.mainView.ListItem;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MainAssignAdapter {
//    ImageView portrait, reply_icon;
//    TextView tv_employer,
//            tv_title,
//            tv_date,
//            tv_price,
//            tv_replies;
//
//    static LayoutInflater inflater;
//
//    public View getView() {
//        return v;
//    }
//
//    private List<ImageView> item_imageViews;
//    private ViewGroup container;
//    ListItem v;
//    int numOfImages;
//    boolean isReload;
//
//    public MainAssignAdapter(Context context, ViewGroup container) {
//        if (inflater == null)
//            inflater = LayoutInflater.from(context);
//        this.container = container;
//
//    }
//
//    public ListItem inflate(Assignment assignment, ListItem view) {
//        if (view == null)
//            v = (ListItem) inflater.inflate(R.layout.main_list_item, container, false);
//        else {
//            v = view;
//            isReload = true;
//        }
//        numOfImages = assignment.images;
//
//        if (numOfImages > 0) {
//            //临时用来管理
//            item_imageViews = new ArrayList<>();
//            item_imageViews.add((ImageView) v.findViewById(R.id.image0));
//            item_imageViews.add((ImageView) v.findViewById(R.id.image1));
//            item_imageViews.add((ImageView) v.findViewById(R.id.image2));
//            if (!v.layoutExpanded)
//                LayoutExpandForImage();
//            setImageBackground();
//        } else if (isReload) {
//            RelativeLayout container = (RelativeLayout) v.findViewById(R.id.image_container);
//            if (container.getVisibility() == View.VISIBLE) {
//                container.setVisibility(View.GONE);
//                shrinkLayout();
//            }
//        }
//
//        //main_icon_portrait = (ImageView) v.findViewById(R.id.item_portrait);
//        //reply_icon = (ImageView) v.findViewById(R.id.item_reply_icon);
//        tv_date = (TextView) v.findViewById(R.id.item_date);
//        tv_employer = (TextView) v.findViewById(R.id.item_username);
//        tv_title = (TextView) v.findViewById(R.id.item_title);
//        tv_price = (TextView) v.findViewById(R.id.item_price);
//        tv_replies = (TextView) v.findViewById(R.id.item_num_reply);
//
//        //main_icon_portrait.setBackgroundResource(R.drawable.main_icon_portrait);
//        //reply_icon.setBackgroundResource(R.drawable.main_icon_reply);
//        tv_date.setText(util.CustomDate(assignment.date));
//        tv_employer.setText(assignment.getEmployer_name());
//        tv_title.setText(assignment.getTitle());
//        tv_price.setText(util.s(assignment.price));
//        //tv_price.setTextColor(Color.parseColor("#CC0000"));
//        tv_price.setTextColor(util.CustomColor(assignment.price));
//        tv_replies.setText(util.s(assignment.repliers));
//        if (item_imageViews != null)
//            item_imageViews.clear();
//
//        return v;
//    }
//
//    private void LayoutExpandForImage() {
//        //make container visible ,and move it to the new position
//        RelativeLayout container = (RelativeLayout) v.findViewById(R.id.image_container);
//        container.setVisibility(View.VISIBLE);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) container.getLayoutParams();
//        params.addRule(RelativeLayout.BELOW, R.id.item_title);
//        container.setLayoutParams(params);
//        RelativeLayout reply_bar = (RelativeLayout) v.findViewById(R.id.main_fr_reply_bar);
//        params = (RelativeLayout.LayoutParams) reply_bar.getLayoutParams();
//        params.addRule(RelativeLayout.BELOW, R.id.image_container);
//        reply_bar.setLayoutParams(params);
//        v.layoutExpanded = true;
//
//    }
//
//    //if there has already some imageViews  ,alter it
//    private void setImageBackground() {
//        if (isReload) {
//            for (int i = 2; i > numOfImages - 1; i--) {
//                item_imageViews.get(i).setVisibility(View.GONE);
//            }
//        }
//        for (int i = 0; i < numOfImages; i++) {
//            item_imageViews.get(i).setVisibility(View.VISIBLE);
//        }
//    }
//
//    private void shrinkLayout() {
//        RelativeLayout reply_bar = (RelativeLayout) v.findViewById(R.id.main_fr_reply_bar);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) reply_bar.getLayoutParams();
//        params.addRule(RelativeLayout.BELOW, R.id.item_title);
//        reply_bar.setLayoutParams(params);
//    }
//}
