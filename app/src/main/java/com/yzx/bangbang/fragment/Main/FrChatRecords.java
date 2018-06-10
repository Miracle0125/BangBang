package com.yzx.bangbang.fragment.Main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.yzx.bangbang.R;
import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.adapter.main.ChatRecordsAdapter;
import com.yzx.bangbang.presenter.MainPresenter;
import com.yzx.bangbang.utils.util;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FrChatRecords extends Fragment {

    private void init() {
        ButterKnife.bind(this, v);
        presenter = context().presenter;
        adapter = new ChatRecordsAdapter(context());
        list.setLayoutManager(new LinearLayoutManager(context()));
        list.setAdapter(adapter);
    }

    private void refresh() {
        presenter.get_recent_conversations(user_id, r -> {
            adapter.chatRecords = r;
            adapter.notifyDataSetChanged();
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.main_fr_chat_records, container, false);
        init();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private Main context() {
        return (Main) getActivity();
    }

    @BindView(R.id.list)
    RecyclerView list;

    View v;
    MainPresenter presenter;
    ChatRecordsAdapter adapter;
    int user_id = util.get_user_id();
}
