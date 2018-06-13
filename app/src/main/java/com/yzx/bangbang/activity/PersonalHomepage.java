package com.yzx.bangbang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yzx.bangbang.R;
import com.yzx.bangbang.model.UserRecord;
import com.yzx.bangbang.presenter.HomepageProxy;
import com.yzx.bangbang.utils.netWork.RetroFunctions;
import com.yzx.bangbang.utils.util;
import com.yzx.bangbang.view.personal_homepage.CircleWithNumber;
import com.yzx.bangbang.widget.Portrait;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * person_id is required
 */
public class PersonalHomepage extends HomepageProxy {

    private void init() {
        setContentView(R.layout.homepage_layout);
        ButterKnife.bind(this);
        person_id = getIntent().getIntExtra("person_id", -1);
        if (person_id == -1) return;
        IS_USER = person_id == util.get_user_id();
        initViews();

        RetroFunctions.get_username_by_id(this, person_id, r -> name.setText(r));
        RetroFunctions.get_user_record(this, person_id, this::update_rates);
    }

    private void initViews() {
        portrait.setData(person_id);
        if (IS_USER) {
            button_chat.setVisibility(View.GONE);
            button_add_contact.setVisibility(View.GONE);
        }
        String[] specials = getResources().getStringArray(R.array.personal_homepage_rates_spec);
        for (int i = 0; i < specials.length; i++) {
            CircleWithNumber circleWithNumber = (CircleWithNumber) rates.getChildAt(i);
            circleWithNumber.set_spec(specials[i]);
        }
    }

    private void update_rates(UserRecord record) {
        int[] num_rates = {float_2_int(record.num_complete / (float) record.num_accept),
                float_2_int(record.num_in_budget / (float) record.num_accept),
                float_2_int(record.num_in_time / (float) record.num_accept),
                -1};
        for (int i = 0; i < num_rates.length; i++) {
            CircleWithNumber circleWithNumber = (CircleWithNumber) rates.getChildAt(i);
            circleWithNumber.set_rate(num_rates[i]);
        }
    }

    private int float_2_int(float in) {
        return Math.round(in * 100);
    }

    @OnClick({R.id.button_chat, R.id.button_add_contact, R.id.toolbar_back})
    public void click(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.toolbar_back:
                finish();
                break;
            case R.id.button_chat:
                if (person_id == -1) return;
                intent = new Intent(this, ChatActivity.class);
                intent.putExtra("other_id", person_id);
                break;
            case R.id.button_add_contact:
                if (person_id == -1) return;
                add_to_contact(util.get_user_id(), person_id, r -> util.toast_binary(this, r));
                break;
            default:
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }


    private boolean IS_USER;
    private int person_id;
    //    @BindView(R.id.button_chat)
//    Button button_chat;
    @BindView(R.id.rates)
    LinearLayout rates;
    @BindView(R.id.portrait)
    Portrait portrait;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.button_add_contact)
    Button button_add_contact;
    @BindView(R.id.button_chat)
    Button button_chat;
}
