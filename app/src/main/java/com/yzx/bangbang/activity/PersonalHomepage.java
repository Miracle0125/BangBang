package com.yzx.bangbang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yzx.bangbang.R;
import com.yzx.bangbang.presenter.HomepageProxy;
import com.yzx.bangbang.utils.netWork.RetroFunctions;
import com.yzx.bangbang.utils.util;
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
        portrait.setData(person_id);
        RetroFunctions.get_username_by_id(this, person_id, r -> name.setText(r));
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
                add_to_contact(util.get_user_id(),person_id,r-> util.toast_binary(this,r));
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


    private int person_id;
    //    @BindView(R.id.button_chat)
//    Button button_chat;
    @BindView(R.id.portrait)
    Portrait portrait;
    @BindView(R.id.name)
    TextView name;
}
