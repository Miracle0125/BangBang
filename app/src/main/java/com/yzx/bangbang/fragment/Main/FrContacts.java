package com.yzx.bangbang.fragment.Main;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yzx.bangbang.R;
import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.activity.PersonalHomepage;
import com.yzx.bangbang.adapter.main.ContactsAdapter;
import com.yzx.bangbang.fragment.sign_in.TestUtils;
import com.yzx.bangbang.model.Contact;
import com.yzx.bangbang.presenter.MainPresenter;
import com.yzx.bangbang.utils.util;
import com.yzx.bangbang.view.mainView.AlphabetView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FrContacts extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_fr_message_contacts_layout, container, false);
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
        presenter = context().presenter;
        adapter = new ContactsAdapter(context());
        adapter.onClickListener=onClickListener;
        //context().presenter.get_contacts(context().user.getId(), r -> adapter.setContacts(r));
        // adapter.setContacts(TestUtils.get_test_contacts());
        recyclerView.setLayoutManager(new LinearLayoutManager(context()));
        recyclerView.setAdapter(adapter);
        alphabet_view.listener = onTouchListener;
    }

    private void refresh(){
        presenter.get_contacts(util.get_user_id(), r -> adapter.setContacts(r));
    }

    View.OnClickListener onClickListener = v->{
        Intent intent = new Intent(context(), PersonalHomepage.class);
        intent.putExtra("person_id",(int)v.getTag());
        startActivity(intent);
    };

    AlphabetView.OnTouchListener onTouchListener = new AlphabetView.OnTouchListener() {
        @Override
        public void onTouch(int letter) {
            String s;
            if (letter == 0) {
                s = "↑";
                recyclerView.scrollToPosition(0);
            } else if (letter == AlphabetView.LETTER_NUMBER - 1)
                s = "#";
            else {
                letter--;
                s = String.valueOf((char) ('A' + letter));
            }
            text_big_letter.setText(s);
            text_big_letter.setVisibility(View.VISIBLE);

            if (s.equals("↑")) return;
            int pos = adapter.get_scroll_pos(letter);
            if (pos != -1)
                ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(pos, 0);
        }

        @Override
        public void onLeave() {
            text_big_letter.setVisibility(View.INVISIBLE);
        }
    };

    private Main context() {
        return (Main) getActivity();
    }

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.alphabet_view)
    AlphabetView alphabet_view;
    @BindView(R.id.text_big_letter)
    TextView text_big_letter;

    MainPresenter presenter;
    ContactsAdapter adapter;
}
