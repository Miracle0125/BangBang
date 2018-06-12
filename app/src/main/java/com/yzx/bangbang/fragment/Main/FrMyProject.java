package com.yzx.bangbang.fragment.Main;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yzx.bangbang.R;
import com.yzx.bangbang.activity.AssignmentDetail;
import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.adapter.main.AssignmentsAdapter;
import com.yzx.bangbang.presenter.MainPresenter;
import com.yzx.bangbang.utils.util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import model.Assignment;


public class FrMyProject extends Fragment {

    private void init() {
        ButterKnife.bind(this, v);
        presenter = context().presenter;

        adapter = new AssignmentsAdapter(context());
        adapter.setOnClickListener(onClickListener);
        list.setLayoutManager(new LinearLayoutManager(context()));
        list.setAdapter(adapter);
    }

    private void refresh() {
        presenter.get_user_assignment(context().user_id, r -> {
            set_text_visibility(r.isEmpty());
            adapter.setData(r);
            adapter.notifyDataSetChanged();
        });
    }

    View.OnClickListener onClickListener = v -> {
        Intent intent = new Intent(context(), AssignmentDetail.class);
        intent.putExtra("assignment", (Assignment) v.getTag());
        startActivity(intent);
    };

    private void set_text_visibility(boolean empty) {
        if (empty) {
            text_sentence.setVisibility(View.VISIBLE);
        } else text_sentence.setVisibility(View.GONE);
    }

    @OnClick(R.id.button_new_asm)
    void click(View v) {
        if (v.getId() == R.id.button_new_asm)
            Flowable.just(util.obtain_message(Main.ACTION_NEW_ASSIGNMENT)).compose(context().<Message>bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(context().consumer);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.main_fr_my_project, container, false);
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


    View v;
    AssignmentsAdapter adapter;
    MainPresenter presenter;
    @BindView(R.id.text_sentence)
    View text_sentence;
    @BindView(R.id.list)
    RecyclerView list;

}
