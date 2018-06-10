package com.yzx.bangbang.adapter.main;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yzx.bangbang.R;
import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.model.Contact;
import com.yzx.bangbang.utils.netWork.Retro;
import com.yzx.bangbang.utils.util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/4/11.
 */

public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public View.OnClickListener onClickListener;
    private List<Contact> contacts = new ArrayList<>();
    private Main main;
    private int divider_count;
    private int[] view_types, data_pos, divider_pos;
    private static final int TYPE_CONTACT = 0;
    private static final int TYPE_DIVIDER = 1;
    private static final int TYPE_BOTTOM = 2;

    private static final int layout[] = {R.layout.main_fr_message_contact, R.layout.main_fr_message_contact_divider};

    public ContactsAdapter(Main main) {
        this.main = main;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == TYPE_CONTACT)
            return new ContactHolder(main.getLayoutInflater().inflate(layout[i], viewGroup, false));
        else if (i == TYPE_DIVIDER)
            return new DividerHolder(main.getLayoutInflater().inflate(layout[i], viewGroup, false));
        else return new BottomHolder(get_bottom_view());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof ContactHolder) {
            ContactHolder h = (ContactHolder) holder;
            h.v.setTag(contacts.get(data_pos[i]).person_id);
            h.v.setOnClickListener(onClickListener);
            h.host_name.setText(contacts.get(data_pos[i]).person_name);
            h.host_portrait.setImageURI(Retro.get_portrait_uri(contacts.get(data_pos[i]).person_id));
        } else if (holder instanceof DividerHolder) {
            DividerHolder h = (DividerHolder) holder;
            int first_letter = contacts.get(data_pos[i + 1]).first_letter;
            h.letter.setText(first_letter == 26 ? "#" : String.valueOf((char) ('A' + first_letter)));
        }
    }


    @Override
    public int getItemCount() {
        if (contacts.isEmpty()) return 0;
        return divider_count + contacts.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return view_types[position];
    }

    //返回值没有意义，只是为了rxjava调用
    private int init_view_types() {
        if (contacts.isEmpty()) return 0;
        if (view_types == null || view_types.length < 28 + contacts.size() + 1) {
            view_types = new int[28 + contacts.size() + 1];
            data_pos = new int[view_types.length];
            divider_pos = new int[view_types.length];
        }
        view_types[0] = TYPE_DIVIDER;
        view_types[1] = TYPE_CONTACT;
        divider_pos[contacts.get(0).first_letter] = 1;
        int pos = 2;
        for (int i = 1, n = contacts.size(); i < n; i++) {
            if (contacts.get(i).first_letter != contacts.get(i - 1).first_letter) {
                divider_pos[contacts.get(i).first_letter] = pos + 1;
                view_types[pos++] = TYPE_DIVIDER;
                divider_count++;
            }
            view_types[pos] = TYPE_CONTACT;
            data_pos[pos++] = i;
        }
        view_types[pos] = TYPE_BOTTOM;
        return 0;
    }

    //初始化的时候+1,现在-1。因为要处理默认值的情况
    public int get_scroll_pos(int first_letter) {
        if (divider_pos == null) return -1;
        //  Log.e("contact", "first_letter " + String.valueOf((char) ('A' + first_letter)) + " get " + divider_pos[first_letter]);
        return divider_pos[first_letter] == 0 ? -1 : divider_pos[first_letter] - 1;
    }

    public class DividerHolder extends RecyclerView.ViewHolder {
        private DividerHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @BindView(R.id.letter)
        TextView letter;
    }

    public class ContactHolder extends RecyclerView.ViewHolder {
        private ContactHolder(View itemView) {
            super(itemView);
            v = itemView;
            ButterKnife.bind(this, itemView);
        }

        View v;
        @BindView(R.id.host_portrait)
        SimpleDraweeView host_portrait;
        @BindView(R.id.host_name)
        TextView host_name;
    }

    private class BottomHolder extends RecyclerView.ViewHolder {
        public BottomHolder(View itemView) {
            super(itemView);
        }
    }


    @SuppressLint("SetTextI18n")
    private LinearLayout get_bottom_view() {
        LinearLayout linearLayout = new LinearLayout(main);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, util.dp2px(40)));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(Color.parseColor("#DCDCDC"));
        TextView textView = new TextView(main);
        textView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        textView.setGravity(Gravity.CENTER);
        textView.setText(contacts.size() + "位联系人");
        linearLayout.addView(textView);
        return linearLayout;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
        Flowable.just(init_view_types())
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> notifyDataSetChanged());
    }
}
