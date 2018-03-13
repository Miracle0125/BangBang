package com.yzx.bangbang.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;




public class AssignAdapter extends BaseAdapter {

    List<Assignment> list;
    Context context;

    public AssignAdapter(Context context, List<Assignment> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override              //参考KuaiHu
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.main_list_item, parent, false);
            viewHolder.portrait = (ImageView) convertView.findViewById(R.id.item_portrait);
            viewHolder.reply_icon = (ImageView) convertView.findViewById(R.id.item_reply_icon);
            viewHolder.tv_date = (TextView) convertView.findViewById(R.id.item_date);
            viewHolder.tv_employer = (TextView) convertView.findViewById(R.id.item_username);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.item_title);
            viewHolder.tv_price = (TextView) convertView.findViewById(R.id.item_price);
            viewHolder.tv_replies = (TextView) convertView.findViewById(R.id.item_num_reply);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();   //?
        }

        viewHolder.portrait.setBackgroundResource(R.drawable.main_icon_portrait);
        viewHolder.reply_icon.setBackgroundResource(R.drawable.main_icon_reply);
        viewHolder.tv_date.setText(CustomDate(position));
        viewHolder.tv_employer.setText(list.get(position).getEmployer_name());
        viewHolder.tv_title.setText(list.get(position).getTitle());
        viewHolder.tv_price.setText(util.s(list.get(position).price));
        viewHolder.tv_replies.setText(list.get(position).repliers);

        return convertView;
    }

    private static class ViewHolder {
        ImageView portrait,reply_icon;
        TextView tv_employer,
                tv_title,
                tv_date,
                tv_price,
                tv_replies;
    }

    String CustomDate(int position) {
        String date_str = list.get(position).date;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date_display = "";
        try {
            Date date = format.parse(date_str);
            Date now = new Date();
            Log.d("ss", now.toString());
            long t = now.getTime() - date.getTime();
            long day_long = t / (24 * 60 * 60 * 1000);
            long hour_long = t / (60 * 60 * 1000);
            long minute = t / (60 * 1000);
            if (day_long >= 1 && day_long < 7) {
                date_display = String.valueOf((int) day_long) + "天前";
            } else if (day_long < 1 && hour_long >= 1) {
                date_display = String.valueOf((int) hour_long) + "小时前";
            } else if (hour_long < 1) {
                date_display = String.valueOf((int) minute)+"分钟前";
            } else {
                format = new SimpleDateFormat("yyyy-MM-dd");
                date_display = format.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date_display;
    }


}


