package com.yzx.bangbang.fragment.Main;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yzx.bangbang.R;
import com.yzx.bangbang.activity.AssignmentDetail;
import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.adapter.main.NotifyAdapter;
import com.yzx.bangbang.interfaces.network.IMain;
import com.yzx.bangbang.model.Notify;
import com.yzx.bangbang.utils.netWork.Retro;
import com.yzx.bangbang.utils.sql.SA;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.schedulers.Schedulers;

public class FrNotify extends Fragment {
    private NotifyAdapter adapter;
    private Main main;
    @BindView(R.id.list)
    RecyclerView recyclerView;
    List<Notify> notifies;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_layout0, container, false);
        ButterKnife.bind(this, v);
        init();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }


    private void init() {
        main = (Main) getActivity();
        adapter = new NotifyAdapter(main);
        adapter.setOnClickListener(onClickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(main));
        recyclerView.setAdapter(adapter);
    }

    @SuppressWarnings("all")
    private void refresh() {
        notifies = (List<Notify>) SA.query(SA.TYPE_NOTIFIES);
        if (notifies == null)
            return;
        adapter.setNotifies(notifies);
        adapter.notifyDataSetChanged();
    }

    View.OnClickListener onClickListener = (v -> {
        Integer i = (Integer) v.getTag();
        if (i == null)
            return;
        Notify notify = notifies.get(i);
        notify.read = 1;
        adapter.notifyDataSetChanged();
        read_notify(notify.id);
        if (Notify.CODE_NEW_BID <= notify.what && notify.what <= Notify.CODE_UNABLE_TO_FINISH) {
            Intent intent = new Intent(main, AssignmentDetail.class);
            intent.putExtra("asm_id", notify.relate_id);
            main.startActivity(intent);
        }
    });

    private void read_notify(int id) {
        Retro.single().create(IMain.class)
                .read_notify(id)
                .subscribeOn(Schedulers.io())
                .compose(main.bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe(r -> {
                });
    }


}
