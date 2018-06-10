package com.yzx.bangbang.adapter.main;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yzx.bangbang.R;
import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.utils.netWork.RetroFunctions;
import com.yzx.bangbang.utils.util;
import com.yzx.bangbang.widget.Portrait;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import model.ChatRecord;

public class ChatRecordsAdapter extends RecyclerView.Adapter {

    public ChatRecordsAdapter(Main context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = context.getLayoutInflater().inflate(R.layout.main_fr_message_chat_record_item, parent, false);
        return new RecentConversationHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        RecentConversationHolder h = (RecentConversationHolder) holder;
        int poster = chatRecords.get(pos).getPoster(), receiver = chatRecords.get(pos).getReceiver();
        int opposite = poster == user_id ? receiver : poster;
        RetroFunctions.get_username_by_id(context, opposite,
                r -> h.opposite_name.setText(r));
        h.portrait.setData(opposite);
        h.recent_message.setText(chatRecords.get(pos).getBody());
    }

    public class RecentConversationHolder extends RecyclerView.ViewHolder {

        public RecentConversationHolder(View itemView) {
            super(itemView);
            this.v = itemView;
            ButterKnife.bind(this, v);
        }

        View v;
        @BindView(R.id.portrait)
        Portrait portrait;
        @BindView(R.id.opposite_name)
        TextView opposite_name;
        @BindView(R.id.recent_message)
        TextView recent_message;
    }

    @Override
    public int getItemCount() {
        return chatRecords.size();
    }

    private Main context;
    public List<ChatRecord> chatRecords = new ArrayList<>();
    private int user_id = util.get_user_id();

}
