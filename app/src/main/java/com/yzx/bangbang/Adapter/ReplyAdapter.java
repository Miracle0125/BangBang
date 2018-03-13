package com.yzx.bangbang.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import com.yzx.bangbang.module.Reply;
import com.yzx.bangbang.R;

public class ReplyAdapter extends BaseAdapter {
    List<Reply> list;
    Context context;

    public ReplyAdapter(Context context, List<Reply> list) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.assign_detail_list_item, parent, false);
            holder.tv_name = (TextView) convertView.findViewById(R.id.reply_name);
            holder.tv_price = (TextView) convertView.findViewById(R.id.reply_exp_price);
            holder.tv_message = (TextView) convertView.findViewById(R.id.reply_message);
            holder.iv_reply_portrait = (ImageView) convertView.findViewById(R.id.reply_portrait);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();   //?
        }
        holder.tv_name.setText(list.get(position).getName());
        holder.tv_price.setText(list.get(position).getPrice());
        holder.tv_message.setText(list.get(position).getMessage());
        holder.iv_reply_portrait.setBackgroundResource(R.drawable.main_icon_portrait);

        return convertView;
    }

    private static class ViewHolder {
        ImageView iv_reply_portrait;
        TextView tv_name, tv_price, tv_message;
    }
}
