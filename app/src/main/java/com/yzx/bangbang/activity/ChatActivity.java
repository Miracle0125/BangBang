package com.yzx.bangbang.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.yzx.bangbang.R;
import com.yzx.bangbang.adapter.chat.ChatAdapter;
import com.yzx.bangbang.presenter.ChatProxy;
import com.yzx.bangbang.utils.ui.ui_utils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends ChatProxy {

    private void init() {
        super.adapter = new ChatAdapter(this);
        super.other_id = getIntent().getIntExtra("other_id", -1);
        ButterKnife.bind(this);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        ui_utils.fluctuating_bottom(getWindow().getDecorView(), edit_bar);
        edit.addTextChangedListener(textWatcher);
        button_send.setOnClickListener(v -> send(edit.getText().toString()));
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().equals("")) {
                edit.setBackground(getResources().getDrawable(R.drawable.icon_send_gray));
                edit.setClickable(false);
            } else {
                edit.setBackground(getResources().getDrawable(R.drawable.icon_send));
                edit.setClickable(true);
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        init();
    }

    //ChatPresenter presenter = new ChatPresenter(this);
    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.edit_bar)
    ViewGroup edit_bar;
    @BindView(R.id.edit)
    public EditText edit;
    @BindView(R.id.button_send)
    ImageView button_send;
}
