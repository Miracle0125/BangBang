package com.yzx.bangbang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.util;

import java.util.List;

import model.Assignment;

public class AssignListAdapter extends BaseAdapter {
    LayoutInflater inflater;
    List<Assignment> list;
    Context context;

    public AssignListAdapter(Context context, List<Assignment> list) {
        this.list = list;
        this.context = context;
        inflater  = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View v, ViewGroup viewGroup) {
        ViewHolder h = null;
        if (v == null) {
            h = new ViewHolder();
            v = inflater.inflate(R.layout.al_list_item, null);
            //((AssignListItem)v).setData(list.get(i));
            h.title = (TextView) v.findViewById(R.id.title);
            h.date = (TextView) v.findViewById(R.id.date);
            h.price = (TextView) v.findViewById(R.id.price);
            h.repliers = (TextView) v.findViewById(R.id.repliers);
            v.setTag(h);
        } else
            h = (ViewHolder) v.getTag();
        h.title.setText(list.get(i).getTitle());
        h.date.setText(util.transform_date(list.get(i).getDate()));
        h.price.setText(""+list.get(i).getPrice());
        h.repliers.setText("帮众  "+list.get(i).getRepliers());
        return v;
    }

    private class ViewHolder {
        TextView title, date, price, repliers;
    }
}
