package com.yzx.bangbang.adapter.chat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yzx.bangbang.R;
import com.yzx.bangbang.activity.ChatActivity;
import com.yzx.bangbang.utils.sql.DAO;
import com.yzx.bangbang.utils.util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import model.ChatRecord;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<ChatRecord> messages = new ArrayList<>();
    private int user_id = util.get_user_id();
    private ChatActivity context;

    public ChatAdapter(ChatActivity context) {
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //-1 for image
        int layout[] = {R.layout.chat_item_text_l, R.layout.chat_item_text_r, -1};
        View v = context.getLayoutInflater().inflate(layout[viewType], parent, false);
        if (viewType == 0) return new TextHolderLeft(v);
        else return new TextHolderRight(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof TextHolder){
            TextHolder h  = (TextHolder) holder;
            h.text.setText(messages.get(position).getBody());
            h.date.setText(messages.get(position).getDate());
        }
    }


    public class TextHolder extends RecyclerView.ViewHolder{
        public TextHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        @BindView(R.id.text)
        TextView text;
        @BindView(R.id.date)
        TextView date;
    }

    public class TextHolderRight extends TextHolder {
        public TextHolderRight(View itemView) {
            super(itemView);
        }
    }

    public class TextHolderLeft extends TextHolder {
        public TextHolderLeft(View itemView) {
            super(itemView);
        }
    }

    public class ImageHolder extends RecyclerView.ViewHolder {

        public ImageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getPoster() == user_id ? TYPE_TEXT_LEFT : TYPE_TEXT_RIGHT;
    }

    public static final int TYPE_TEXT_LEFT = 0;
    public static final int TYPE_TEXT_RIGHT = 1;
    public static final int TYPE_IMAGE = 2;
}
